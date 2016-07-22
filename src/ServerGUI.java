import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/*Connects a user to the Server*/


public class ServerGUI extends JFrame implements ActionListener{
	
	JLabel serverIP = new JLabel();
	JLabel serverPort = new JLabel();
	JLabel connectionState = new JLabel();
	JTextField ipField = new JTextField();
	JTextField portField = new JTextField();
	JButton connectButton = new JButton("Connect");
	JFrame serverFrame;
	UserGUI userInterface;
	
	
	
	public ServerGUI(){
		serverFrame = new JFrame();
		serverFrame.setTitle("Connect to a Dungeon Server");
		serverFrame.setLayout(new GridLayout(0,2));
		serverFrame.setSize(500,100);
		serverFrame.setVisible(true);
		serverIP.setText("Server IP: ");
		serverPort.setText("Server Port-Number: ");
		connectionState.setText("Press Connect when ready");
		connectButton.addActionListener(this);
		serverFrame.add(serverIP);
		serverFrame.add(ipField);
		serverFrame.add(serverPort);
		serverFrame.add(portField);
		serverFrame.add(connectionState);
		serverFrame.add(connectButton);
	}
	
	public void setInvisible(){
		serverFrame.setVisible(false);
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == connectButton){
			try{
				LocalClient c = new LocalClient (ipField.getText(), Integer.parseInt(portField.getText()));
				userInterface = new UserGUI(c);
				setInvisible();
			}
			catch (IOException ex) {
				connectionState.setText("Failed to connect to server! try again later");
				System.exit(0);
			}
		}
	}
	
	public static void main(String[] args){
		ServerGUI server = new ServerGUI();
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
