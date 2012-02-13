package com.garethmurphy.simplechat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

	public static void main(String[] args) {		
		try {
			Socket socket = new Socket("127.0.0.1", 5000);
			
			// Set up input...
			InputStreamReader reader = new InputStreamReader(
					socket.getInputStream());
			BufferedReader in = new BufferedReader(reader);
			
			new ChatClient().startListener(in);
			
			// And output...
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			
			while(true) {
				Scanner input = new Scanner(System.in);
				String msg = input.next() + input.nextLine();
				out.println(msg);
				out.flush();
			}
			
			
		} catch(IOException ex) {
			ex.printStackTrace();
		}

	}
	
	public void startListener(BufferedReader in) {
		Thread inThread = new Thread(new MsgListener(in));
		inThread.start();
	}
	
	class MsgListener implements Runnable{
		BufferedReader in;
		
		public MsgListener(BufferedReader in) {
			this.in = in;
		}
		
		@Override
		public void run() {
			String msg;
			
			try {
				while((msg = in.readLine()) != null) {
					System.out.println(msg);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
