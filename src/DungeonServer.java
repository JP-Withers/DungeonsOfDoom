
import java.net.*;
import java.text.ParseException;
import java.io.*;

	public class DungeonServer  {
		/*A server created to accept clients to play the Dungeon of Doom.
		 * the Server can accept multiple clients*/
		
		private ServerSocket server;
		private GameLogic game; // the game which each client will play on
		
	public DungeonServer(int port){
		/*Constructor sets the server up and defaults class fields. 
		 * Assigns the programme to run on the argument port*/
	   	try{
	       server = new ServerSocket(port);
	       game = new GameLogic("SmallMap.txt");
	    }
	   	catch(IOException | ParseException e){
		   System.out.println("Sorry, there was an error in construction! ");
	   	}
	}
	
	public void setup(){
		/*method keeps server listening for clients. 
		 * Each accepted client gets a DungeonClient Thread constructed for it*/
		try{
			Socket serverSocket;
			boolean serverRunning = true;
			while(serverRunning == true){
				 serverSocket = server.accept();
				 System.out.println("Connected to the server!");
				 DungeonClient connectedClient = new DungeonClient(serverSocket, game);
				 connectedClient.start();
			}
		}
		catch(IOException e){
			System.out.println("Sorry, there was an IOException!");
		}
	}
	   
	   public static void main(String [] args){
		   /*Main method constructs the server and runs it*/
	      int port = 55002;
	      DungeonServer theServer = new DungeonServer(port);
	      theServer.setup();
	      
	   }
	   
	 
	}
