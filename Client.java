import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Client is the main program that will be used to chat in the terminal. It has a login/ signup mechanism and run the ListenClient and SpeakClient class.
public class Client {
	private static String name; // name of user
	private static Scanner userIn; // take username
	private static PrintWriter userOut; // passed to ListenClient and SpeakClient to display chat
	private static Socket socket; // to connect the network
	private static DataInputStream serverIn; // read results  from server(succeed)
	private static DataOutputStream serverOut; // to pass username to server
	public static final String GREEN_TEXT = "\u001B[32m";
	public static final String RED_TEXT = "\u001B[31m";
	public static final String RESET_TEXT = "\u001B[0m";

	public static void main(String[] args) {
		final String publicIp = "ENTERPUBLICIPADDRESSHERE"; // type your publicIp here
		final int PORT = 3000; // type port here
		userIn = new Scanner(System.in);
		userOut = new PrintWriter(System.out);

		try {
			socket = new Socket(publicIp, PORT);
			serverIn = new DataInputStream(socket.getInputStream());
			serverOut = new DataOutputStream(socket.getOutputStream());
			
			userLogin();

			new Thread(new ListenClient(userOut, socket, name)).start();
			new Thread(new SpeakClient(userIn, socket, name)).start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void successMessage() throws IOException {
		String usersInfo = serverIn.readUTF();
		System.out.println(GREEN_TEXT + "Succeed to connect!" + RESET_TEXT);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
		}
		String printedMessage = GREEN_TEXT + "\nEntering the Chatroom!\n" + usersInfo + "\n---------------------------------------------------\n" + RESET_TEXT;
		System.out.println(printedMessage);
	}

	public static void userLogin() throws IOException {
		System.out.println("Welcome to Java Multithreaded Chat App!");
		while (true) {
			System.out.print("Enter username: ");
			String userName = userIn.nextLine();

			serverOut.writeUTF(userName);
			serverOut.flush();

			String result = serverIn.readUTF();
			if (result.equals("SUCCEED")) {
				successMessage();
				name = userName;
				return; 
			} else if (result.equals("FAILED")) {
				System.out.println(RED_TEXT + "\nUsername is already chosen! Pick another unique username" + RESET_TEXT);
				System.out.println();
			}
		}
	}
}