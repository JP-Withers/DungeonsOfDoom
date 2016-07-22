import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



/*The graphical User Interface for the user. 
 * Allows the User to play on the DoD server
 * interface includes: 
 * - A pane to view the look from;
 * - Panel of buttons from which actions are performed by the user;
 * - Panel for chat areas and name input;
 * - a button to quit the server; 
 *  */


public class UserGUI extends JFrame implements ActionListener {
	
	
	
	
	
	
	/*Main Method instantiates and runs A ClientGUI object
	 * [USED IF 'ServerGUI' FAILS TO INSTANTIATE 'UserGUI' ] */
	
	public static void main(String[] args){
		LocalClient user;
		try {
			user = new LocalClient("127.0.0.1",55002);
			UserGUI dungeonInterface = new UserGUI(user);
			dungeonInterface.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	/*The different components of my GUI */
	
	private ImageIcon wall = new ImageIcon(getClass().getResource("Flemish_Garden_Wall_bond.png"));
	private ImageIcon coin = new ImageIcon(getClass().getResource("retro_coin.png"));
	private ImageIcon armour = new ImageIcon(getClass().getResource("armour.png"));
	private ImageIcon exit = new ImageIcon(getClass().getResource("exit.png"));
	private ImageIcon sword = new ImageIcon(getClass().getResource("sword.png"));
	private ImageIcon lantern = new ImageIcon(getClass().getResource("lantern.png"));
	private ImageIcon nullSquare = new ImageIcon(getClass().getResource("nullSquare.png"));
	private ImageIcon blank = new ImageIcon(getClass().getResource("blank.png"));
	private JButton north = new JButton("NORTH");
	private JButton east = new JButton("EAST");
	private JButton south = new JButton("SOUTH");
	private JButton west = new JButton("WEST");
	private JButton pickup = new JButton("PICKUP");
	private JButton sendChatInput = new JButton("ENTER");
	private JButton quit = new JButton("QUIT");
	private JTextArea chatArea = new JTextArea();
	private JTextField chatInput = new JTextField();
	private JTextField nameInput = new JTextField();
	private JLabel labelName = new JLabel("name");
	private JLabel  labelChatInput= new JLabel("name");
	private JLabel labelChatArea = new JLabel("name");
	private BufferedReader input;
	private BufferedWriter output;
	private LocalClient user;
	private JLabel label[][] = new JLabel[7][7];
	private JScrollPane chatScrollPane = new JScrollPane(chatArea);
	private String userName = "";
	private JFrame frmGame;
	
	
	
	
	
	
	
	/*Constructs a UserGUI for the User*/
	
	public UserGUI(LocalClient lc){
			frmGame = new JFrame();
			user = lc;
			frmGame.setTitle("Dungeon of Doom");
			frmGame.setSize(750,700);
			frmGame.getContentPane().setLayout(null);
			lookPanelSetup();
			buttonPanelSetup();
			chatPanelSetup();
			frmGame.setVisible(true);
			getInput();
	}
	
	
	
	
	
	
	
	
	/*Method dedicated to updating the lookPanel with the LOOKREPLY received from the server
	 *Iterates through LOOKREPLY checking each character. 
	 *Uses if functions to check each character of LOOKREPLY load the respective image to the GUI */
	
	private void updateLook(String[] lookreply){
		String[] lReply = lookreply;
			int l = 0;
			for(int a = 1; a < lReply.length ; a++){
				for(int b = 0; b<lReply[a].length(); b++){
					char tile = lReply[a].charAt(b);
					if(tile == 'X'){
						label[a][b+1].setIcon(wall);	
					}
					if(tile == '#'){
						label[a][b+1].setIcon(wall);	
					}else
					if(tile == 'G'){
						label[a][b+1].setIcon(coin);	
					}else
					if(tile == 'E'){
						label[a][b+1].setIcon(exit);	
					}else
					if(tile == '.'){
						label[a][b+1].setIcon(blank);	
					}else
					if(tile == 'A'){
						label[a][b+1].setIcon(armour);	
					}else
					if(tile == 'S'){
						label[a][b+1].setIcon(sword);	
					}else if(tile == 'L'){
						label[a][b+1].setIcon(lantern);	
					}
					
					
					
				}
			}
	}
	
	
	
	
	
	
	/*Sets up a 7x7 array of JLabels to add to the JFrame 
	 * Sets the location for each component */
	
	private void lookPanelSetup(){
		for(int a = 0; a < 7 ; a++){
			for(int b = 0; b<7; b++){
				label[a][b] = new JLabel(wall);
				label[a][b].setBounds(b*70, a*70, 70, 70);
				frmGame.getContentPane().add(label[a][b]);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	/*Sets up the all the buttons to be used within the UserGUI
	 * Sets the location of each of the components described within 
	 * adds ActionListeners/ActionCommands to buttons 
	 * Adds components to JFrame */
	
	private void buttonPanelSetup(){
		quit.setBounds(500, 530, 100, 30);
		north.setBounds(105,500,100,30);
		east.setBounds(205,530,100,30);
		south.setBounds(105,560,100,30);
		west.setBounds(5,530,100,30);
		pickup.setBounds(105,530,100,30);
		north.addActionListener(this);
		north.setActionCommand("MOVE N");
		east.addActionListener(this);
		east.setActionCommand("MOVE E");
		south.addActionListener(this);
		south.setActionCommand("MOVE S");
		west.addActionListener(this);
		west.setActionCommand("MOVE W");
		pickup.addActionListener(this);
		pickup.setActionCommand("PICKUP");
		
		quit.addActionListener(this);
		quit.setActionCommand("QUIT");
		frmGame.getContentPane().add(north);
		frmGame.getContentPane().add(east);
		frmGame.getContentPane().add(south);
		frmGame.getContentPane().add(west);
		frmGame.getContentPane().add(pickup);
		frmGame.getContentPane().add(quit);
	}
	
	
	
	
	/*Sets up components related to chat: chatArea; chatInput; nameInput; chatScrollPane
	 * Sets location of components contained within
	 * Adds components to JFrame */
	
	
	public void chatPanelSetup(){
		sendChatInput.addActionListener(this);
		chatArea.setEditable(false);
		sendChatInput.setActionCommand("SHOUT");
		nameInput.setActionCommand("HELLO");
		nameInput.setBounds(500, 5, 200, 30);
		chatScrollPane.setBounds(500, 40, 200, 400);
		chatInput.setBounds(500, 445, 200, 30);
		sendChatInput.setBounds(500, 480, 200, 30);
		frmGame.getContentPane().add(nameInput);
		frmGame.getContentPane().add(chatScrollPane);
		frmGame.getContentPane().add(chatInput);
		frmGame.getContentPane().add(sendChatInput);
		
	}
	
	
	
	
	/*This method uses an inner class to create a separate Thread for reading from the server.
	 * Uses LocalClients getLine() function to read from server.
	 *  Splits input from server into string[] by "\n" characters.
	 *  if it's a LOOKREPLY calls updateLook() function;
	 *  all other input from server is appended to JTextArea */
	
	public void getInput(){
		Thread readThread = new Thread(){
			public void run(){
				while(true){
					String[] a = user.getLine().split("\n");
					if(a[0].equals("LOOKREPLY")){
						updateLook(a);
					}
					else{
						for(int z = 0; z < a.length; z++){
							chatArea.append(a[z] + "\n");
						}
					}
				}
			}
		};
		readThread.start();
	}
	
	
	
	/*method used to hard call the LOOK action*/
	public void look(){
		user.streamWrite("LOOK");
	}
	
	
	
	/*Implemented actionPeformed() method from the interface ActionListener
	 * uses each components ActionCommand to determine action and source
	 * if ActionCommand == QUIT - exits the UserGUI
	 * Otherwise, writes ActionCommand to Server using LocalClient.  
	 * */
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("QUIT")){
			System.exit(0);
		}else if(e.getActionCommand().equals("SHOUT")){ 
			user.streamWrite("SHOUT "+nameInput.getText()+": "+chatInput.getText());
		}
		else{
			user.streamWrite(e.getActionCommand());
			look();
		}
	}
	

}

