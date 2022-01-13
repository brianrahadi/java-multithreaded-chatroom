import java.io.PrintWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

// ListenClient ran on a thread by client will take messages from server (readUTF) and send it to client's terminal (print)

public class ListenClient implements Runnable {

	private PrintWriter userOut; // to pass the chat to terminal
	private Socket socket; // to connect to server
	private DataInputStream in; // to take data from server (do not need out as not sending to server)

	public ListenClient(PrintWriter userOut, Socket socket, String name) {
		this.userOut = userOut;
		this.socket = socket;
	}

	public void run() {

		try {
			try {
				this.in = new DataInputStream(socket.getInputStream());
				printMessage();
			} finally {
				socket.close();
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}

	}

	public void printMessage() throws IOException {
		String message = in.readUTF();
		userOut.print(message);
		userOut.flush();

		while (true) {
			message = in.readUTF();
			userOut.println(message);
			userOut.flush();
		}
	}
}
