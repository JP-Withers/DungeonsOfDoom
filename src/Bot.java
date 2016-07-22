

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * This class plays the game as a very basic bot that will just move around
 * randomly and pick up anything that it lands on.
 * 
 * Note that there is a (very slim) chance that it will go on forever because
 * it's just moving at random. If you have a non-randomly moving bot from the
 * first piece of coursework then it might be better to use that instead of this
 * one!
 * 
 * Or of course you could just put your movement methods in place of
 * randomMove()
 * 
 * The bot extends CommandLineUser to "prove" it isn't cheating. This also means
 * that that the bot could easily be modified to work over the network.
 */
public class Bot{
	// The bot needs to know what is has since it will act like a player on the
	// map.
	private boolean hasLantern = false;
	private boolean hasSword = false;
	private boolean hasArmour = false;
	private BufferedReader input;
	private BufferedWriter output;
	private Socket botSocket;/*Socket connects bot to server*/
	private boolean isTurn = false;/*checks if it's the bot's turn or not*/

	private char[][] currentLookReply;

	/**
	 * Constructs a new instance of the Bot.
	 * 
	 * @param game
	 *            The instance of the game, to run on. This is only needed for
	 *            the parent class, as the Bot sends text commands to it.
	 */
	public Bot(String hostIP, int portNumber){
		/*Constructs a bot class with arguments which connect it to the server, using hostIP and portNumber */
		try {
			botSocket = new Socket(hostIP, portNumber);
			input = new BufferedReader(new InputStreamReader(botSocket.getInputStream())); //Stream to read from the server
			output = new BufferedWriter(new OutputStreamWriter(botSocket.getOutputStream()));//stream to write to the server
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void processCommand(String toProcess){
		/*converted Proccess command method so that instead of implementing
		 * the CommanLineListner method it writes to the server through the port*/
		try {
			output.write(toProcess+"\n");
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Controls the playing logic of the bot
	 */
	public void startBot() {
		/*Starts the Bot playing the game*/
		processCommand("HELLO R2D2");
		streamRead();
		while (true) {
			if(isTurn == true){
				lookAtMap();
				pickupIfAvailable();
				makeRandomMove();
			}

			// Pause
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e){
				// This will not happen with the current app
			}
		}
	}
	
	public void streamRead(){
		/*Method creates a thread which allows input from the server to be read. 
		 * Determines if the bot's turn has finished; assigns isTurn field accordingly
		 * .
		 * */
		Thread readThread = new Thread(){
			public void run(){
				while(true){
					String recieved = "";
					try{
						recieved = input.readLine();
						if(recieved.equals("STARTTURN")){
							isTurn = true;
						}
						if(recieved.equals("ENDTURN")){
							isTurn = false;
						}
						if(recieved == null){
							System.out.println("Server down!");
							System.exit(0);
						}
						if(recieved.equals("LOOKREPLY")){
							int lookSize = 5;
							if(hasLantern == true){
								lookSize = 7;
							}
							String[] look = new String[lookSize+1];
							look[0] = new String(recieved);
							for(int r = 0; r < lookSize; r++){
								look[r+1] = new String(input.readLine());
							}
							handleLookReply(look);
						}
						else{
							doOutputMessage(recieved);
						}
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					catch(SocketException e){
						System.out.println("Server is down!");
						System.exit(0);
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
	};
		readThread.start();
	}
	/**
	 * Allows the bot to receive and act on messages send from the game.
	 * For now, we just handle the LOOKREPLY and WIN.
	 * 
	 * @param the message sent by the game
	 */
	protected void doOutputMessage(String message) {
		if (!message.equals("")) {
			// Print the message for the benefit of a human observer
			System.out.println(message);

			final String[] lines = message.split(System
					.getProperty("line.separator"));
			final String firstLine = lines[0];
			 if (firstLine.equals("WIN")) {
				System.out.println("SHOUT I won the game");
				processCommand("SHOUT I won the game");

				System.exit(0);

			} else if (firstLine.startsWith("FAIL")) {
				throw new RuntimeException("Bot entered invalid command");
			}
		}
	}

	/**
	 * Issues a LOOK to update what the bot can see. Returns when it is updated.
	 **/
	private void lookAtMap() {
		this.currentLookReply = null;

		// Have a look at the map
		System.out.println("LOOK");
		processCommand("LOOK");

		// For now the game is not concurrent, but it's worth checking the look
		// reply has been updated. This will also help with a network, where
		// it'll take
		// a while to process.
		while (this.currentLookReply == null) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handles the LOOKREPLY from the game, updating the bot's array.
	 * 
	 * @param lines
	 *            the lines returned as part of the LOOKREPLY
	 */
	private void handleLookReply(String[] lines) {
		if (lines.length < 2) {
			throw new RuntimeException("FAIL: Invalid LOOKREPLY dimensions");
		}
		for(int a = 0; a<lines.length; a++){
			System.out.println(lines[a]);
		}
		final int lookReplySize = lines[1].length();
		if (lines.length != lookReplySize + 1) {
			throw new RuntimeException("FAIL: Invalid LOOKREPLY dimensions");
		}

		this.currentLookReply = new char[lookReplySize][lookReplySize];

		for (int row = 0; row < lookReplySize; row++) {
			for (int col = 0; col < lookReplySize; col++) {
				this.currentLookReply[row][col] = lines[row + 1].charAt(col);
			}
		}
	}

	/**
	 * Picks up anything the bot is standing on, if possible
	 */
	private void pickupIfAvailable() {
		switch (getCentralSquare()) {
			// We can't pick these up if we already have them, so don't even try
			case 'A' :
				if (!this.hasArmour) {
					System.out.println("PICKUP");
					processCommand("PICKUP");
					// We assume that this will be successful, but we could
					// check
					// the reply from the game.
					this.hasArmour = true;
				}
				break;

			case 'L' :
				if (!this.hasLantern) {
					System.out.println("PICKUP");
					processCommand("PICKUP");
					this.hasLantern = true;
				}
				break;

			case 'S' :
				if (!this.hasSword) {
					System.out.println("PICKUP");
					processCommand("PICKUP");
					this.hasSword = true;

					System.out.println("SHOUT I am a killer robot now");
					processCommand("SHOUT I am a killer robot now");
				}
				break;

			// We'll always get some gold or health
			case 'G' :
				System.out.println("PICKUP");
				processCommand("PICKUP");

				System.out.println("SHOUT I got some gold");
				processCommand("SHOUT I got some gold");
				break;

			case 'H' :
				System.out.println("PICKUP");
				processCommand("PICKUP");
				break;

			default :
				break;
		}
	}

	/**
	 * Makes a random move, not into a wall
	 */
	private void makeRandomMove() {
		try {
			final char dir = generateRandomMove();
			final String moveString = "MOVE " + dir;
			System.out.println(moveString);
			processCommand(moveString);

		} catch (final IllegalStateException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Return a direction to move in. Note that we do checks to see what it in
	 * the square before sending the request to move to the game logic.
	 * 
	 * @return direction in which to move
	 */
	private char generateRandomMove() {
		// First, ensure there is a move
		if (!isMovePossible()) {
			System.out.println("SHOUT I am stuck and so will terminate.");
			processCommand("SHOUT I am stuck and so will terminate.");

			throw new IllegalStateException(
					"The bot is stuck in a dead end and cannot move anymore!");
		}

		final Random random = new Random();
		while (true) {
			final int dir = (int) (random.nextFloat() * 4F);

			switch (dir) {
				case 0 : // N
					if (getSquareWithOffset(0, -1) != '#') {
						return 'N';
					}
					break;

				case 1 : // E
					if (getSquareWithOffset(1, 0) != '#') {
						return 'E';
					}
					break;

				case 2 : // S
					if (getSquareWithOffset(0, 1) != '#') {
						return 'S';
					}
					break;

				case 3 : // W
					if (getSquareWithOffset(-1, 0) != '#') {
						return 'W';
					}
					break;
			}
		}
	}

	/**
	 * Obtains the square in the centre of the LOOKREPLY, i.e. that over which
	 * the bot is standing
	 * 
	 * @return the square under the bot
	 */
	private char getCentralSquare() {
		// Return the square with 0 offset
		return getSquareWithOffset(0, 0);
	}

	/**
	 * Obtains a square in of the LOOKREPLY with an offset to the bot
	 * 
	 * @return the square corresponding to the bot and offset
	 */
	private char getSquareWithOffset(int xOffset, int yOffset) {
		final int lookReplySize = this.currentLookReply.length;
		final int lookReplyCentreIndex = lookReplySize / 2; // We rely on
		// truncation

		return this.currentLookReply[lookReplyCentreIndex + yOffset][lookReplyCentreIndex
				+ xOffset];
	}

	/**
	 * Check if the there is a possible move from the centre of the vision field
	 * to another tile
	 * 
	 * @return true if the bot is not encircled with walls, false otherwise
	 */
	private boolean isMovePossible() {
		if ((getSquareWithOffset(-1, 0) != '#')
				|| (getSquareWithOffset(0, 1) != '#')
				|| (getSquareWithOffset(1, 0) != '#')
				|| (getSquareWithOffset(0, -1) != '#')) {
			return true;
		}

		return false;
	}
	
	public static void main (String[] args){
		Bot computer = new Bot("127.0.0.2",55002);
		computer.startBot();
	}
}
