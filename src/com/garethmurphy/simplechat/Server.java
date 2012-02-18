package com.garethmurphy.simplechat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private ServerSocket socket;
	private List<Thread> clientListenThreads = new ArrayList<Thread>();
	private List<Socket> connectionSockets = new ArrayList<Socket>();

	public static void main(String[] args) {
		new Server().start();
	}
	
	public void start() {
		startListeningOnPort(5000);
		while(true) {
			Socket clientSocket = waitAndReceiveConnection();
			connectionSockets.add(clientSocket);
			setupClientListenThread(clientListenThreads.size(), clientSocket);
		}
	}
	
	private void startListeningOnPort(int port) {
		try {
			socket = new ServerSocket(port);
		} catch(IOException e) {
			System.err.println("Couldn't open server socket.");
			e.printStackTrace();
		}
	}
	
	private Socket waitAndReceiveConnection() {
		try {
			Socket clientSocket = socket.accept();
			System.out.println("Connected to " + clientSocket.getInetAddress().toString());
			return clientSocket;
		} catch(IOException e) {
			System.err.println("Error on receiving incoming connection.");
			e.printStackTrace();
		}
		return null;
	}
	
	private void setupClientListenThread(int index, Socket clientSocket) {
		ClientListener clientListener = new ClientListener(index, clientSocket);
		Thread clientThread = new Thread(clientListener);
		clientListenThreads.add(clientThread);
		clientThread.start();
	}
	
	class ClientListener implements Runnable {
		private int indexInLists;
		private Socket clientSocket;
		private BufferedReader inStream;
		private String userName;
		
		public ClientListener(int index, Socket clientSocket) {
			indexInLists = index;
			this.clientSocket = clientSocket;
			setupInStream();			
			// TODO: Give proper option to set name
			userName = "User " + index;
		}
		
		private void setupInStream() {
			try {
				InputStreamReader rawInStream = new InputStreamReader(clientSocket.getInputStream());
				inStream = new BufferedReader(rawInStream);
			} catch(IOException e) {
				System.err.println("Problem getting client incoming stream.");
				e.printStackTrace();
			}
		}
		
		public void run() {
			listenForAndForwardMsgs();
		}
		
		private void listenForAndForwardMsgs() {
			try {
				String msg;
				while((msg = inStream.readLine()) != null) {
					// TODO: Implement empty message checking
					sendMessage(userName, msg);
				}
			} catch(IOException e) {
				System.err.println("Problem on message receive/forward.");
				e.printStackTrace();
			}
		}
	}
	
	public void sendMessage(String userName, String msg) {
		
		for (Socket s : connectionSockets) {
			PrintWriter socketOutput = getSocketOutWriter(s);
			socketOutput.println(userName + ": " + msg + "\n"); 
			socketOutput.flush();
		}
	}
	
	private PrintWriter getSocketOutWriter(Socket socket) {
		try {
			PrintWriter socketOutput = new PrintWriter(socket.getOutputStream());
			return socketOutput;
		} catch(IOException e) {
			System.err.println("Error on getting socket OutputStream.");
			e.printStackTrace();
		}
		return null;
	}
}