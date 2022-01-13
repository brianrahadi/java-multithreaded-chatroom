import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

// SpeakClient ran on a thread by client and it will read typed messages and send it to server
public class SpeakClient implements Runnable {

	private Scanner userIn; // to take user input later to be passed to server
	private Socket socket; // to connect to server
	private DataOutputStream out; // pass the user input
	private String name;
	public static final String GREEN_TEXT = "\u001B[32m";
	public static final String RESET_TEXT = "\u001B[0m";

	public SpeakClient(Scanner scanner, Socket socket, String name) {
		userIn = scanner;
		this.socket = socket;
		this.name = name;
	}

	public void run() {
		try {
			out = new DataOutputStream(socket.getOutputStream());
			doService();
			socket.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public void doService() throws IOException {
		while (true) {
			String message = userIn.nextLine();
			message = name + ": " + message;

			out.writeUTF(GREEN_TEXT + message + RESET_TEXT);
			out.flush();
		}
	}
}
