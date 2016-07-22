
import java.net.*;
import java.io.*;

	class DungeonClient extends CommandLineUser implements Runnable {
		private Socket clientSocket;
		private Thread clientThread;
		private BufferedReader input;
		private PrintWriter output;
		
		public DungeonClient(Socket passed, GameLogic game){
			/*Constructs a platform for handling each independent client that joins the sever.
			 * Each client receives its own thread for concurrency.
			 * Each client receives a GameLogic argument 'game' which is the same for each DungeonClient, so each client can play on the same game */
			super(game);
			clientSocket = passed;
			clientThread = new Thread(DungeonClient.this);
			try{
				input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				output = new PrintWriter(clientSocket.getOutputStream(), true);
			}
			catch(IOException e){
				System.out.println("Sorry, there was in IOException!");
			}
			addPlayer();
		} 


		public void start(){
			/*method used to start the thread using the Runnable interface.*/
			clientThread.start();
		}
		
		
		public void run(){
			/*Run method implemented. implemented from the interface runnable in the CommandLineUser class.
			 * Allows the thread to operate.*/
			String userInput = "";
			try {
				while(true){
					userInput = input.readLine();
					System.out.println("value of userinput: "+userInput);
					if(userInput == null){
						System.out.println("Client disconnected");
						clientSocket.close();
						break;
					}
					processCommand(userInput);
					userInput = "";
				}
			} 
			catch(SocketException e){
				System.out.println("User has terminated the connection!");
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

		@Override
		protected void doOutputMessage(String message) {
			//sends output through server back to the LocalClient
			if (message.equals("WIN")){
				output.println("The Player has won!");
			}
			System.out.println("~~~~~~: "+message);
			output.println(message);
			
		}
	}