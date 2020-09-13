import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.net.InetAddress;

public class Node {
	private final List<String> neighbours;
	final String ip;
	final Integer id;
	final Integer port;
	private boolean candidat;
	private String owner;
	private Map<String, Boolean> tried = new HashMap<>();
	private boolean finished;

	private int t;
	private int counter;
	private long realTime;
	private int n = 1;

	private ArrayList<Integer> pokemaster;

	public Node(Integer port, List<String> neighboursList, boolean candidat) throws Exception {
		this.ip = InetAddress.getLocalHost().getHostAddress();
		this.port = port;
		this.id = toId(ip);
		this.neighbours = neighboursList;
		this.candidat = candidat;
		this.owner = "";
		this.neighbours.forEach( node -> {
			tried.put(node, false);
		});
		this.finished = false;
		pokemaster = new ArrayList<>();
	}

	public void start(){
		if(this.candidat){
			this.owner = this.ip;
		}
		synchronizeStart();
		this.realTime = new Date().getTime();
		checkTime();
		this.realTime += 5000;
		this.t = 0;

		if(this.candidat){
			ArrayList<String> pokemons = select(n);
			pokemons.forEach(p -> sendMessage(p, "Pokemon"));
		}
		this.t+=1;
		elect();
		checkTime();
		if (candidat) synchronizeStop();
	}

	private void checkTime(){
		try{
			Thread.sleep(this.realTime + 5000 - new Date().getTime());
		}
		catch(InterruptedException ex){
			Thread.currentThread().interrupt();
		}
	}

	private void elect(){
		while(!this.hasFinished()){
			checkTime();
			if (t%2 == 0){
				if (this.candidat){
					if (counter == 0){
						if(t<2 * Math.log(this.neighbours.size()+1)+1){
							ArrayList<String> pokemons = select( n );
							pokemons.forEach(p -> sendMessage(p, "Pokemon"));
						}
					}
					else{
						System.out.println("stopping");
						this.candidat = false;
					}
				}
				if(t>2 * Math.log(this.neighbours.size()+1)+1){
					this.finished = true;
				}
			}


			else{
				if (! pokemaster.isEmpty()){
					Integer best = Collections.max(this.pokemaster);
					if (this.owner.equals("") || toId(this.owner) < best){
						this.owner = toIp(best);
						sendMessage(toIp(best), "Ack");
						if (this.candidat){
							this.candidat = false;
						}
					}
					this.pokemaster = new ArrayList<>();
				}	
			}
			this.realTime += 5000;
			this.t+=1;
		}
	}

	public ArrayList<String> select(int number){

		this.counter = 0;
		ArrayList<String> pokemons = new ArrayList<>();
		tried.entrySet().stream().filter(e -> !e.getValue()).map(e -> e.getKey() ).limit(number).forEach(key -> {
			pokemons.add(key);
			tried.replace(key, false, true);
			this.counter ++;
		} );

		n*=2;
		return pokemons;
	}

	public void onMessageReceived(String message){
		String[] parts = message.split(":");
		String ipSrc = parts[0];
		Integer source = toId(ipSrc);
		String msg = parts[1];
		System.out.println("RECEIVED "+msg+" from "+ipSrc);
		switch(msg){
			case "Pokemon":
				pokemaster.add(source);
				break;
			case "Ack":
				this.counter --;
				break;
			default:
				break;
		}
	}
	public boolean hasFinished(){
		return this.finished;
	}

	private void synchronizeStart(){
		broadcast("Sync");
	}	
	
	private void synchronizeStop(){
		broadcast("I'm the boss");
		sendMessage(ip, "I'm the boss");
	}

	private void sendMessage(String neighbour,String message){
		try{
			System.out.println("SENDING "+message+" to "+neighbour);
			(new Client(neighbour, port)).send(ip+":"+message);
		}catch (Exception e){
			try{
				Thread.sleep(5000);
			}
			catch(InterruptedException ex){
				Thread.currentThread().interrupt();
			}
			sendMessage(neighbour, message);
		}
	}

	private void broadcast(String message){
		List<Thread> threads = new ArrayList<Thread>();
		for(String neighbour : this.neighbours){
			Thread t = new Thread(){
				public void run(){
					sendMessage(neighbour,message);
				}
			};
			threads.add(t);
			t.start();
		}
		for(Thread t : threads){
			try{
				t.join();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private String toIp(Integer id){
		return "129.194.184." + id;
	}

	private Integer toId(String ip){
		return Integer.valueOf(ip.substring(ip.lastIndexOf('.') + 1));
	}

	@Override
	public String toString(){
		return "Node " + id;
	}
}