import java.io.IOException;
import java.net.Socket;
import java.io.PrintWriter;
public class Client {
	private final int port;
	private Socket socket;
	public Client(String ip,int portNumber){
		port = portNumber;
		try{
			socket = new Socket(ip,portNumber);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public void send(String message) throws Exception{
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		out.println(message);
		out.flush();
		close();
	}

	public void close(){
		try{
			socket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}