package au.edu.anu.scoap.commands;

import org.kohsuke.args4j.CmdLineParser;

/**
 * Utility class for the command line arguments
 * 
 * @author Genevieve Turner
 *
 */
public class CommandUtil {
	public static final String SCOAP = "scoap";

	/**
	 * Print the usage command
	 * 
	 * @param command The command being executed
	 * @param clazz The class associated with the command
	 */
	public static void printUsage(Class<?> clazz) {
		try {
			CmdLineParser parser = new CmdLineParser(clazz.newInstance());
			System.out.print(CommandUtil.SCOAP);
			parser.printSingleLineUsage(System.out);
			System.out.println("");
			parser.printUsage(System.out);
		}
		catch (IllegalAccessException e) {
			System.err.println("Exception accessing class " + clazz.getName() + ". " + e.getMessage());
		}
		catch (InstantiationException e) {
			System.err.println("Exception instantiationg class " + clazz.getName() + ". " + e.getMessage());
		}
	}
}
