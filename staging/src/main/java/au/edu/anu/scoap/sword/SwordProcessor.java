package au.edu.anu.scoap.sword;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swordapp.client.Deposit;
import org.swordapp.client.DepositReceipt;
import org.swordapp.client.EntryPart;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SWORDCollection;
import org.swordapp.client.SWORDWorkspace;
import org.swordapp.client.ServiceDocument;
import org.swordapp.client.SwordResponse;
import org.swordapp.client.UriRegistry;

import au.edu.anu.scoap.sword.data.BitstreamInfo;
import au.edu.anu.scoap.sword.data.SwordRequestData;
import au.edu.anu.scoap.sword.data.SwordRequestDataProvider;
import au.edu.anu.scoap.sword.exception.WorkflowException;
import au.edu.anu.scoap.sword.task.AddToMediaResourceTask;
import au.edu.anu.scoap.sword.task.ChangeInProgressTask;
import au.edu.anu.scoap.sword.task.DepositTask;
import au.edu.anu.scoap.sword.task.Md5CalcTask;
import au.edu.anu.scoap.sword.task.ReplaceTask;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class SwordProcessor {
	private static final Logger log = LoggerFactory.getLogger(SwordProcessor.class);

	private final SWORDClient swordClient;
	private final SwordServerInfo serverInfo;
	private final SwordRequestDataProvider provider;

	public SwordProcessor(SWORDClient swordClient, SwordServerInfo serverInfo, SwordRequestDataProvider provider) {
		this.swordClient = Objects.requireNonNull(swordClient);
		this.serverInfo = Objects.requireNonNull(serverInfo);
		this.provider = Objects.requireNonNull(provider);
	}

	
	/**
	 * Performs the following in order:
	 * 
	 * <ol>
	 * <li>Retrieves Service Document from a Swordv2 Service
	 * <li>Iterates over the SwordRequestData collection returned by provider
	 * and creates and submits Sword requests
	 * </ol>
	 * 
	 * @throws WorkflowException
	 */
	public void process() throws WorkflowException {
		// retrieve service document
		ServiceDocument sd = retrieveServiceDoc();

		int nTotal = provider.getSwordRequests().size();
		if (nTotal == 0) {
			log.info("No Sword Requests to be processed.");
		}

		int nSuccess = 0;
		int nErrors = 0;
		for (SwordRequestData iData : provider.getSwordRequests()) {
			try {
				String editLink = null;
				String editMediaLink = null;

				if ((iData.getCollectionName() != null)
						&& (iData.getMetadata() != null && !iData.getMetadata().isEmpty())) {
					// create a resource if collection name and title is
					// provided
					Set<String> ariesRecords = iData.getMetadata().get("ariespublication");
					log.info("Uploading records {}", ariesRecords);
					SWORDCollection collection = getCollectionNamed(sd, iData.getCollectionName());
					Deposit metadataDeposit = createMetadataDeposit(iData.getMetadata());
					DepositReceipt depositReceipt = depositResource(collection, metadataDeposit);
					editLink = depositReceipt.getEditLink().getHref();
					editMediaLink = depositReceipt.getEditMediaLink().getHref();
					iData.setEditLink(editLink);
					iData.setEditMediaLink(editMediaLink);
				} else if (iData.getEditLink() != null && (iData.getMetadata() != null && !iData.getMetadata().isEmpty())) {
					// read resource url from command line parameters.
					Set<String> ariesRecords = iData.getMetadata().get("ariespublication");
					log.info("Editing records {}", ariesRecords);
					editLink = iData.getEditLink();
					Deposit metadataDeposit = createMetadataDeposit(iData.getMetadata());
					replaceResource(editLink, metadataDeposit);
					editMediaLink = iData.getEditMediaLink();
				}
				else if (iData.getMetadata() == null || iData.getMetadata().isEmpty()) {
					log.debug("No metadata to upload");
				}
				else {
					log.debug("No location to send metadata to specified");
				}

				// submit files to media link url
				if (editMediaLink != null) {
					Map<BitstreamInfo, SwordResponse> bsDepositResponses = depositFiles(editMediaLink,
							iData.getBitstreams());
					
					// check for errors when uploading bitstreams
					Set<BitstreamInfo> erroredBitstreams = new LinkedHashSet<>(bsDepositResponses.size());
					for (Entry<BitstreamInfo, SwordResponse> resp : bsDepositResponses.entrySet()) {
						if (resp.getValue() == null || resp.getValue().getStatusCode() != 201) {
							erroredBitstreams.add(resp.getKey());
						}
					}
					if (!erroredBitstreams.isEmpty()) {
						throw new WorkflowException("Error uploading files: " + erroredBitstreams.toString());
					}
				}

				// change in progress flag if required
				if (editLink != null && !iData.isInProgress()) {
					changeInProgress(editLink);
				}

				// let provider know that the data was successfully uploaded
				provider.updateRequestStatus(iData, true);
				nSuccess++;
			} catch (WorkflowException e) {
				nErrors++;
				log.error(e.getMessage(), e);
			}
		}

		String summaryMsg = "{} success. {} errors. {} total.";
		if (nErrors > 0) {
			log.warn(summaryMsg, nSuccess, nErrors, nTotal);
//			throw new WorkflowException();
		} else {
			log.info(summaryMsg, nSuccess, nErrors, nTotal);
		}
	}

	private ServiceDocument retrieveServiceDoc() throws WorkflowException {
		ServiceDocument sd = null;
		try {
			log.info("Retrieving service document from {}...", this.serverInfo.getServiceDocUrl());
			sd = swordClient.getServiceDocument(this.serverInfo.getServiceDocUrl(), this.serverInfo.createAuth());
			log.info("Retrieved service document from {}", this.serverInfo.getServiceDocUrl());
			log.info("Sword version: {}, Max upload size: {}. Available workspaces: {}", sd.getVersion(),
					Long.valueOf(sd.getMaxUploadSize()), Integer.valueOf(sd.getWorkspaces().size()));
			logAllCollections(sd);
		} catch (Exception e) {
			log.error("Error retrieving service document from {}", this.serverInfo.getServiceDocUrl());
			throw new WorkflowException(e);
		}
		return sd;
	}

	private Deposit createMetadataDeposit(Map<String, Set<String>> metadata) throws WorkflowException {
		EntryPart ep = new EntryPart();
	
		for (Entry<String, Set<String>> metadataEntry : metadata.entrySet()) {
			for (String value : metadataEntry.getValue()) {
				try {
					QName qName = createQName(metadataEntry.getKey());
	//				log.debug("QName: {}, Key: {}, Value: {}", qName, metadataEntry.getKey(), value);
					ep.addSimpleExtension(qName, value);
				}
				catch (Exception e) {
					// There were issues in creating the qname, the record probably shouldn't be processed in this case...
					throw new WorkflowException(e);
				}
			}
		}
		Deposit resourceDeposit = new Deposit(ep, null, null, null, null, null, null, true, false);
		return resourceDeposit;
	}


	private DepositReceipt depositResource(SWORDCollection collection, Deposit resource) throws WorkflowException {
		DepositTask depTask = new DepositTask(swordClient, this.serverInfo, collection, resource);
		DepositReceipt depositReceipt = null;
		int maxRetries = 3;
		try {
		for (int i = 0; i < maxRetries && (depositReceipt == null || depositReceipt.getStatusCode() != 201); i++) {
			try {
				depositReceipt = depTask.call();
				log.info("Created resource at {} with status code {}.", depositReceipt.getLocation(),
						Integer.valueOf(depositReceipt.getStatusCode()));
			} catch (Exception e) {
				if (i < maxRetries - 1) {
					log.error("Error creating item retrying in 5 seconds, re-try " + (i+1), e);
					Thread.sleep(5000);
				}
				else {
					throw new WorkflowException(e);
				}
			}
		}
		}
		catch (Exception e) {
			//Don't care if wait is interrupted ...
			throw new WorkflowException(e);
		}
		
		return depositReceipt;
	}
	
	private SwordResponse replaceResource(String uri, Deposit resource) throws WorkflowException {
		ReplaceTask replaceTask = new ReplaceTask(swordClient, this.serverInfo, uri, resource);
		SwordResponse swordResponse = null;
		int maxRetries = 3;
		try {
			for (int i = 0; i < maxRetries && (swordResponse == null || swordResponse.getStatusCode() != 200); i++) {
				try {
					swordResponse = replaceTask.call();
					log.info("Replaced resource at {} with status code {}.", uri, swordResponse.getStatusCode());
				}
				catch (Exception e) {
					if (i < maxRetries - 1) {
						log.error("Error editing item via " + uri + " retrying in 5 seconds, re-try " + (i+1), e);
						Thread.sleep(5000);
					}
					else {
						throw new WorkflowException(e);
					}
				}
			}
		}
		catch (Exception e) {
			//Don't care if wait is interrupted ...
			throw new WorkflowException(e);
		}
		return swordResponse;
	}

	private Map<BitstreamInfo, SwordResponse> depositFiles(String editMediaLink, Collection<BitstreamInfo> bitstreams) {
		Map<BitstreamInfo, SwordResponse> responses;
		if (bitstreams != null) {
			responses = new LinkedHashMap<>(bitstreams.size());
			int count = 1;
			for (BitstreamInfo bitstreamInfo : bitstreams) {
				InputStream bitstream = null;
				try {
					long sizeBytes = bitstreamInfo.getSize();
					String fmtSize = MessageFormat.format("{0,number,#,000} KB",
							new Object[] { Long.valueOf(sizeBytes / 1024L) });

					// calculate MD5
					log.info("Calculating MD5 {} size={} ...", bitstreamInfo.getFilename(), fmtSize);
//					bitstream = createInputStream(bitstreamInfo.getFile());
					bitstream = bitstreamInfo.getFile();
					Md5CalcTask md5Task = new Md5CalcTask(bitstream);
					String md5 = Hex.encodeHexString(md5Task.call()).toLowerCase();
					IOUtils.closeQuietly(bitstream);

					log.info("Uploading {} size={}:MD5={} [{}/{}]...", new Object[] { bitstreamInfo.getFilename(),
							fmtSize, md5, Integer.valueOf(count), Integer.valueOf(bitstreams.size()) });

					SwordResponse bsDepositReceipt = null;
					int maxRetries = 3;
					for (int i = 0; i < maxRetries && (bsDepositReceipt == null || bsDepositReceipt.getStatusCode() != 201); i++){
						// create bitstream deposit object
						Deposit bsDeposit = new Deposit();
						String depositFilename = bitstreamInfo.getFilenameToDeposit();
						log.info("Deposit file name: {}", depositFilename);
						bsDeposit.setFilename(depositFilename);
						bsDeposit.setInProgress(true);
						bsDeposit.setMd5(md5);
//						String mimeType = Files.probeContentType(bitstreamInfo.getFile());
						String mimeType = bitstreamInfo.getMimeType();
						if (mimeType != null) {
							bsDeposit.setMimeType(mimeType);
						} else {
							bsDeposit.setMimeType("application/octet-stream");
						}
						bsDeposit.setPackaging(UriRegistry.PACKAGE_BINARY);
						
						bitstream = bitstreamInfo.getFile();
//						bitstream = createInputStream(bitstreamInfo.getFile());
						bsDeposit.setFile(bitstream);
						try {
							AddToMediaResourceTask atmrTask = new AddToMediaResourceTask(swordClient, this.serverInfo,
									editMediaLink, bsDeposit);
							bsDepositReceipt = atmrTask.call();
						} 
						catch (Exception e) {
							if (i < maxRetries - 1) {
								log.error("Error adding files to "+ editMediaLink +" retrying in 5 seconds, retry " + (i+1), e);
								Thread.sleep(5000);
							}
							else {
								throw(e);
							}
						}
					}
					responses.put(bitstreamInfo, bsDepositReceipt);
					log.info("Uploaded {}. Status:{}, MD5:{}", bitstreamInfo.getFilename(),
							Integer.valueOf(bsDepositReceipt.getStatusCode()), bsDepositReceipt.getContentMD5());
				} catch (Exception e) {
					responses.put(bitstreamInfo, null);
					log.error("Unable to upload " + bitstreamInfo.getFilepath() + ". " + e.getMessage(), e);
					log.error(e.getMessage(), e);
				} finally {
					IOUtils.closeQuietly(bitstream);
					count++;
				}
			}
		} else {
			responses = new LinkedHashMap<>(0);
		}
		return responses;
	}

	private SwordResponse changeInProgress(String editLink) throws WorkflowException {
		SwordResponse response = null;
		try {
			ChangeInProgressTask cipTask = new ChangeInProgressTask(swordClient, serverInfo, editLink);
			response = cipTask.call();
		} catch (Exception e) {
			log.error("Unable to change In Progress flag at {}", editLink);
			throw new WorkflowException(e);
		}
		return response;
	}

	private void logAllCollections(ServiceDocument sd) {
		if (log.isTraceEnabled()) {
			for (SWORDWorkspace iWorkspace : sd.getWorkspaces()) {
				log.trace("Workspace [{}] {} items.", iWorkspace.getTitle(), iWorkspace.getCollections().size());
				for (SWORDCollection iCollection : iWorkspace.getCollections()) {
					log.trace("\t{}", iCollection.getTitle());
				}
			}
		}
	}


	/**
	 * Get the collection with the specified name.
	 * 
	 * @param sd
	 *            ServiceDocument
	 * @param collectionName
	 *            Name of collection to retrieve
	 * @return Collection as SWORDCollection
	 * @throws WorkflowException
	 */
	private SWORDCollection getCollectionNamed(ServiceDocument sd, String collectionName) throws WorkflowException {
		SWORDCollection collection = null;
		if (collectionName != null) {
			for (SWORDWorkspace iWorkspace : sd.getWorkspaces()) {
				for (SWORDCollection iColl : iWorkspace.getCollections()) {
					if (iColl.getTitle().equalsIgnoreCase(collectionName)) {
						collection = iColl;
						break;
					}
				}
			}
			if (collection == null) {
				log.error("Collection [{}] not found. Check user's write permissions.", collectionName);
				throw new WorkflowException("No collection named [" + collectionName + "]");
			}
		}
		return collection;
	}


	private QName createQName(String fqFieldname) {
		QName term = null;

		if (fqFieldname.indexOf('.') == -1) {
			term = new QName(UriRegistry.DC_NAMESPACE, fqFieldname);
		} else {
			String[] parts = fqFieldname.split("\\.", 2);
			String namespaceName = parts[0];
			String field = parts[1];

			if (namespaceName.equalsIgnoreCase("dc")) {
				term = new QName(UriRegistry.DC_NAMESPACE, field);
			} else if (namespaceName.equalsIgnoreCase("dcterms")) {
				term = new QName(UriRegistry.DC_NAMESPACE, field);
			} else if (namespaceName.equalsIgnoreCase("anu")) {
				term = new QName("http://anu.edu.au/dspace/", field);
			}
		}
		return term;
	}

//	private InputStream createInputStream(Path file) throws IOException {
//		long totalBytes = Files.size(file);
//		ProgressInputStream fileStream = new ProgressInputStream(Files.newInputStream(file), totalBytes);
//		fileStream.addPropertyChangeListener(new PropertyChangeListener() {
//			public void propertyChange(PropertyChangeEvent evt) {
//				if (evt.getPropertyName().equals("percentComplete")) {
//					int oldValue = ((Integer) evt.getOldValue()).intValue();
//					int newValue = ((Integer) evt.getNewValue()).intValue();
//					if (newValue > oldValue) {
//						System.out.print(newValue + "%\r");
//						if (newValue == 100) {
//							System.out.println();
//						}
//					}
//				}
//			}
//		});
//		return fileStream;
//	}
}