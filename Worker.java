import java.io.FileWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

// Worker handle a client to send and receive message from client.
public class Worker implements Runnable {

	private Socket socket; // to connect to server
	private DataInputStream in; // take data from server
	private DataOutputStream out; // pass data to server
	private List<Socket> listenClient; // list of all connecting clients
	private List<String> nameList; // list of all user names
	private List<String> messageHistory; // as message history
	private String name;
	private FileWriter messageHistoryFile;

	final String GREEN_TEXT = "\u001B[32m";
	final String RED_TEXT = "\u001B[31m";
	final String RESET_TEXT = "\u001B[0m";


	public Worker(Socket s, List<Socket> listenClient, List<String> nameList, List<String> messageHistory) throws IOException {
		this.socket = s;
		this.listenClient = listenClient;
		this.nameList = nameList;
		this.messageHistory = messageHistory;
		messageHistoryFile = new FileWriter("message-history.txt", true);
	}

	public void run() {
		try {
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				doService();
			} finally {
				nameList.remove(name);
				listenClient.remove(socket);
				socket.close();
				messageHistoryFile.close();
			}
		} catch (IOException exception) {
			nameList.remove(name);
			System.out.println(RED_TEXT + name + " just left the chat" + RESET_TEXT);
			try {
				for (Socket s : listenClient) {
					DataOutputStream tempOut = new DataOutputStream(s.getOutputStream());
					String message = RED_TEXT + name + " just left the chat" + RESET_TEXT;
					tempOut.writeUTF(message);
					tempOut.flush();
				}
			} catch (IOException e) {
			}
		}
	}

	public void doService() throws IOException {
		boolean isInLogin = true;
		while (isInLogin) {
			String userName = in.readUTF();
			if (!nameList.contains(userName)) {
				this.name = userName;
				out.writeUTF("SUCCEED");
				String users = usersMessage();
				out.writeUTF(users);
				out.flush();
				nameList.add(userName);
				isInLogin = false;
			} else {
				out.writeUTF("FAILED");
				out.flush();
			}
		}

		String messages = "";
		for (String str : messageHistory) {
			messages += str + "\n";
		}
		out.writeUTF(messages);
		out.flush();

		for (Socket s : listenClient) {
			String message = GREEN_TEXT + name + " just joined the chatroom!" + RESET_TEXT;
			if (s != socket) {
				// if s is not this socket, then don't print it (a user don't chat to themselves)
				DataOutputStream tempOut = new DataOutputStream(s.getOutputStream());
				tempOut.writeUTF(message);
				tempOut.flush();
			}
		}

		while (true) {
			String message = in.readUTF();
			messageHistory.add(message); 
			messageHistoryFile.append(message + "\n");
			for (Socket s : listenClient) {
				if (s != socket) {
					// if s is not this socket, then don't print it (a user don't chat to themselves)
					DataOutputStream tempOut = new DataOutputStream(s.getOutputStream());
					tempOut.writeUTF(message);
					tempOut.flush();
				}
			}

		}
	}

	private String usersMessage() {
		String users = "";
		if (nameList.size() > 0) {
			for (int i = 0; i < nameList.size(); i++) {
				users += nameList.get(i);
				if (i < nameList.size() - 2) {
					users += ", ";
				} else if (i == nameList.size() - 2) {
					users += " and ";
				}
			}
			if (nameList.size() == 1) {
				users += " is ";
			} else {
				users += " are ";
			}
		} else {
			users += "No one is ";
		}
		users += "in this chatroom";
		return users;
	}
}
