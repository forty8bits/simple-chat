import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;


public class GuiClient {
	
	// Change these settings for your own server setup!
	private String ip = "127.0.0.1";
	private int port = 5000;
	
	private BufferedReader in;
	private PrintWriter out;

	private JFrame frame;
	private JTextField textField;
	private JTextPane textPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiClient window = new GuiClient();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GuiClient() {
		initialize();
		try {
			setupSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String msg = textField.getText();
				sendMsg(msg);
				textField.setText("");
				textField.requestFocus();
			}
		});
		frame.getContentPane().add(textField, BorderLayout.SOUTH);
		textField.setColumns(10);
		
		textPane = new JTextPane();
		frame.getContentPane().add(textPane, BorderLayout.CENTER);
	}
	
	private void setupSocket() throws IOException {
		Socket socket = new Socket(ip, port);
		
		// Set up input...
		InputStreamReader reader = new InputStreamReader(
				socket.getInputStream());
		in = new BufferedReader(reader);
		
		Thread listenThread = new Thread(new MsgListener());
		listenThread.start();
		
		// And output...
		out = new PrintWriter(socket.getOutputStream());
	}
	
	private void sendMsg(String msg) {
		out.println(msg);
		out.flush();
	}
	
	class MsgListener implements Runnable {
		@Override
		public void run() {
			String msg;
			
			try {
				while((msg = in.readLine()) != null) {
					textPane.setText(textPane.getText() + "\n" + msg);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}			
		}
	}
}
