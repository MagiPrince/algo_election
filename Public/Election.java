import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Election {
    public static void main(String args[]) throws Exception{
    	//Setup part
        //Args: 0 = port, 1 = neighbours file name, 2 = STATUS
        if(args.length != 3){
            System.out.println("Usage: java BFSTree PORT NEIGHBOURFILE INIT|WAIT");
            return;
        }
        Integer port = Integer.parseInt(args[0]);
        String state = args[2];
        if(!state.equals("INIT") && !state.equals("WAIT")){
            System.out.println("Invalid status "+state+". Should be INIT or WAIT.");
            return;
        }
        String filename = args[1];
        List<String> neighbours = getNeighbours(filename);

        Node node = new Node(port,neighbours,state.equals("INIT"));
    	Server server = new Server(node);
    	(new Thread(server)).start();
        //Wait 1s for the server to start
        Thread.sleep(1000);
        node.start();
    }

    static List<String> getNeighbours(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines;
    }
}