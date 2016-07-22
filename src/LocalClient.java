import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

	
public class LocalClient {
		
	private Socket clientSocket;
	private BufferedReader input;
	private BufferedWriter output;
	private String toSend;
		
	/*Constructs a client which connects to a server with IP=hostIP; port=portNumber*/
	public LocalClient (String hostIP, int portNumber) throws IOException {
			clientSocket = new Socket(hostIP, portNumber);
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			
	}
		/*Reads from the Buffer;
		 * Reads both multiple and single lines of input*/
	public String getLine(){
		String toSend = "";
		try {
			String line = input.readLine();
			if(input.ready() == false){
				toSend  = toSend +line;
			}
			while(input.ready()){
				toSend = toSend + line+ "\n";
				line = input.readLine();
			}
			System.out.println("toSend: "+toSend);
			return toSend;
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toSend;
	}

	/*Method creates a thread which allows input from the server to be read whilst 
	 * continuing to allow the Command Line to take input and user to access server */
	public void commandLineRead(){
		Thread readThread = new Thread(){
			public void run(){
				while(true){
					String recieved = "";
					try{
						recieved = input.readLine();
						if(recieved == null){
							System.out.println("Server down!");
							System.exit(0);
						}
						System.out.println(recieved);
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
		
	/*Writes to the Buffer; sends output to the Server*/
	public void streamWrite(String toWrite){
		try {
			output.write(toWrite+"\n");
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*sets up a buffer to read from the command line interface.
	 * sends the inputs to the server and reads responses sent back from the server */
	public void runCL(){
		BufferedReader readerCL = new BufferedReader(new InputStreamReader(System.in));
		String inputCL;
		commandLineRead();
		while(true){
			try {
				inputCL = readerCL.readLine();
				if(inputCL.equals("EXIT")){
					clientSocket.close();
					System.exit(0);
				}
				System.out.println("Input from the CL: "+inputCL);
				streamWrite(inputCL);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*constructs a LocalClient to connect to server of the constructor's arguments. runs the client.*/
	public static void main(String[] args){
		try {
			LocalClient client = new LocalClient("127.0.0.1",55002);
			client.runCL();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
