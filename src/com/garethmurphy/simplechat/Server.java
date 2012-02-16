package com.garethmurphy.simplechat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	private int port = 5000;

	private ArrayList<Socket> connections = new ArrayList<Socket>();
	private ArrayList<Thread> threads = new ArrayList<Thread>();

	public static void main(String[] args) {
		new Server().start();
	}
	
	public void start() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			int currentConnectionIndex = 0;
			
			while(true) {	
				Socket clientSocket = serverSocket.accept();
				connections.add(clientSocket);
				
				threads.add(new Thread(new ClientListener(
						currentConnectionIndex, clientSocket)));
				
				threads.get(currentConnectionIndex).start();
				
				currentConnectionIndex++;
				
				System.out.println("Connected to: " 
						+ clientSocket.getInetAddress().toString());
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	class ClientListener implements Runnable {
		
		private BufferedReader inStream;
		private String name;
		
		public ClientListener(int index, Socket socket) {
			try {
				
				InputStreamReader rawInStream = new InputStreamReader(
						socket.getInputStream());
				
				inStream = new BufferedReader(rawInStream);
			} catch(IOException ex) {
				ex.printStackTrace();
			}
			
			// TODO: Give proper option to set name
			name = "User " + index;
		}
		
		public void run() {
			String msg;
			try {
				while((msg = inStream.readLine()) != null) {
					// TODO: Implement empty message checking
					sendMessage(name, msg);
				}
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void sendMessage(String name, String msg) {
		
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