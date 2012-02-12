import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
	
	/* These ArrayLists will keep track of all open socket connections and their
	 * corresponding Threads. Each connection gets its own thread.
	 */	
	private ArrayList<Socket> connections = new ArrayList<Socket>();
	private ArrayList<Thread> threads = new ArrayList<Thread>();

	public static void main(String[] args) {
		
		// Simply start the server listening on the appropriate port.
		new ChatServer().startServer();
	}
	
	public void startServer() {
		try {
			// You can pick any arbitrary unused port 
			ServerSocket serverSock = new ServerSocket(5000);
			
			// Keeps track of index of latest client connection in the ArrayList
			int i = 0;
			
			// Infinite listener loop
			while(true) {
				
				/* The 'accept()' method blocks; it sits and waits for a
				 * connection to come in, so this isn't as non-performant as
				 * you'd think.
				 */
				Socket clientSock = serverSock.accept();
				
				connections.add(clientSock);
				
				threads.add(new Thread(new ClientListener(i, clientSock)));
				threads.get(i).start();
				
				i++; // Don't forget to bump the index count!
				System.out.println("Got a connection!");
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	// An inner class implementing the 'Runnable' interface to pass to a thread
	class ClientListener implements Runnable {
		
		private BufferedReader clientIn;
		int msgNum = 1;
		private String name;
		
		public ClientListener(int i, Socket sock) {
			try {
				
				InputStreamReader reader = new InputStreamReader(
						sock.getInputStream());
				
				clientIn = new BufferedReader(reader);
			} catch(IOException ex) {
				ex.printStackTrace();
			}
			
			// Just give an automatic name for now
			name = "User " + i;
		}
		
		public void run() {
			String msg;
			try {
				while((msg = clientIn.readLine()) != null) {
					if (msg != "") {
						tellEveryone(name, msg);
					}
				}
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void tellEveryone(String name, String msg) {
		
		for (Socket s : connections) {
			try {
				PrintWriter writer = new PrintWriter(s.getOutputStream());
				writer.println(name + ": " + msg + "\n");
				
				// The PrintWriter is buffered, so needs to be flushed 
				writer.flush();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
