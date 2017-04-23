import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Options options = new Options();
		options.addOption("name", true, "input name");
		options.addOption("email", true, "input email address");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {

		}
		String name = "",email = "";
		if (cmd.hasOption("name")) {
			name = cmd.getOptionValue("name");
		} else {
			name = "The user does not provide name";
		}
		if (cmd.hasOption("email")) {
			email = cmd.getOptionValue("email");
		} else {
			email = "The user does not provide email address";
		}

		System.out.println("name: " + name + " email: " + email);
	}

}
