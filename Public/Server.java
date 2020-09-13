import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
public class Server implements Runnable{
	ServerSocket serverSocket;
	int port;
	Node node;
	public Server(Node n){
		port = n.port;
		node = n;
		try{
			serverSocket = new ServerSocket(port);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void run(){
		System.out.println("Server listening on port "+port);
		try{
			while(!node.hasFinished()){
				Socket clientSocket = serverSocket.accept();
				node.onMessageReceived(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine());
			}
			System.out.println("Server closed.");
			serverSocket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}