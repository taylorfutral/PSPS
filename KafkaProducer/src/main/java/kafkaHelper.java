import java.lang.ProcessBuilder;
import java.lang.ProcessBuilder.Redirect;
import java.lang.Process;
import java.lang.StringBuilder;
import java.lang.InterruptedException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.util.Date;

// Assume Linux environment

/*Reference:
*https://www.mkyong.com/java/how-to-execute-shell-command-from-java/
*/

public class kafkaHelper{

	private static String LOG_FILENAME_BASE = "log";

	public kafkaHelper() {
	}

	public ArrayList<String> getCommandList() {
		ArrayList<String> commandList = new ArrayList<String>();
		commandList.add("ls ./");
		commandList.add("cd ..");
		commandList.add("pwd");
		return commandList;
	}

	public void runCommand(String command, boolean writeToFile) {
		ProcessBuilder pb = new ProcessBuilder();

		// Run a shell command
		// String command = "ls ./GeneralConsumer";
		System.out.println("Running: "+command);

		pb.command("bash", "-c", command);

		try {
			Date date = new Date();
			File log = new File(LOG_FILENAME_BASE+"_"+date.toString());
			pb.redirectErrorStream(true);
			pb.redirectOutput(Redirect.appendTo(log));

			Process process = pb.start();
			StringBuilder output = new StringBuilder();	
			BufferedReader reader;	
			if (writeToFile) {
				reader = new BufferedReader(new FileReader(log));
			} else {
				reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			}	

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

			int exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
				System.out.println(output);
				// System.exit(0);
			} else {
				// abnormal
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
	}

	public void runCommands(ArrayList<String> commandList) {
		for (String command : commandList) {
			runCommand(command);
		}
		return;
	}


	public static void main(String[] args) throws Exception {
		kafkaHelper kh = new kafkaHelper();
		ArrayList<String> commandList = kh.getCommandList();
		kh.runCommands(commandList, false);
	}
}