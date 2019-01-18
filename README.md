# scoaptodspace

This program adds records from [Scoap3](https://repo.scoap3.org/) to DSpace.  This is coded for The Australian National University instance however it should be possible to implement in your own institution via editing build.properties and ~/dspace/au/edu/anu/scoap/dspace/DSpaceObject.java.

## Upload
Example command to upload articles from Scoap3

	java -jar /home/dspace/admin/scoap3.jar -s ${earliest_date} -e ${latest_date}