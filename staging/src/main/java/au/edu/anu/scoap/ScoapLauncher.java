package au.edu.anu.scoap;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import au.edu.anu.scoap.commands.ScoapCommand;
import au.edu.anu.scoap.commands.ScoapCommandException;

/**
 * Main Class to query scoap and load them in to 
 * @author Genevieve Turner
 *
 */
public class ScoapLauncher {
	public static void main(String[] args) {
		ScoapCommand command = new ScoapCommand();
		
		CmdLineParser parser = new CmdLineParser(command);
		
		try {
			parser.parseArgument(args);
			command.run();
		}
		catch (CmdLineException | ScoapCommandException e) {
			System.err.println(e.getMessage());
			parser.printSingleLineUsage(System.out);
			System.out.println("");
			parser.printUsage(System.out);
		}
	}
}
