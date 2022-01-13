import java.io.IOException;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

	public static void main(String[] args) throws IOException {
		final int PORT = 3000;
		List<String> nameList = new ArrayList<>();
		List<String> messageHistory = new ArrayList<>();
		List<Socket> listenClient = new ArrayList<>();
		ServerSocket server = new ServerSocket(PORT);
		final String GREEN_TEXT = "\u001B[32m";
		final String RESET_TEXT = "\u001B[0m";

		File messageHistoryFile = new File("message-history.txt");

		if (messageHistoryFile.exists()) {
			Scanner sc = new Scanner(messageHistoryFile);
			while (sc.hasNextLine()) {
				messageHistory.add(sc.nextLine());
			}
		}

		System.out.println("Waiting to find client");
		try {
			while (true) {
				Socket s = server.accept();
				System.out.println(GREEN_TEXT + "A Client is connected." + RESET_TEXT);
				listenClient.add(s);

				Worker worker = new Worker(s, listenClient, nameList, messageHistory);
				new Thread(worker).start();
			}
		} catch (IOException e) {
		} finally {
			server.close();
		}
	}
}
