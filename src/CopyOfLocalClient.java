import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

	
public class CopyOfLocalClient {
		
	private Socket clientSocket;
	private BufferedReader input;
	private BufferedWriter output;
		
		public CopyOfLocalClient(String hostIP, int portNumber){
			/*Constructs a client which connects to a server with IP=hostIP; port=portNumber*/
			try {
				clientSocket = new Socket(hostIP, portNumber);
				input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				
			} catch (IOException e) {
				e.printStackTrace();
				System.err.print("Failed to connect to server! terminating programme");
				System.exit(0);
			}
		}

		public void streamRead(){
			/*Method creates a thread which allows input from the server to be read whilst 
			 * continuing to allow the Command Line to take input and user to access server */
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
		
		public void streamWrite(String toWrite){
			try {
				output.write(toWrite+"\n");
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void runCL(){
			/*sets up a buffer to read from the command line interface.
			 * sends the inputs to the server and reads responses sent back from the server */
			BufferedReader readerCL = new BufferedReader(new InputStreamReader(System.in));
			String inputCL;
			streamRead();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public static void main(String[] args){
			/*constructs a LocalClient to connect to server of the constructor's arguments. runs the client.*/
			CopyOfLocalClient localClient = new CopyOfLocalClient("127.0.0.1",55002);
			localClient.runCL();
		}
}
