import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class Node {
	private final List<String> neighbours;
	final Integer id;
	private boolean candidat;
	private Integer owner;
	private Map<Integer, Boolean> tried = new HashMap<>();
	private boolean finished;

	private int t;
	private int counter;
	private long realTime;
	private int n = 1;

	private ArrayList<Integer> pokemaster;
	
	public Node(Integer port, List<String> neighboursList, boolean candidat){
		this.id = port;
		this.neighbours = neighboursList;
		this.candidat = candidat;
		this.owner = -1;
		this.neighbours.forEach( node -> {
			tried.put(Integer.valueOf(node), false);
		});
		this.finished = false;
		pokemaster = new ArrayList<>();
	}

	public void start(){
		if(this.candidat){
			this.owner = this.id;
		}
		synchronizeStart();
		this.realTime = new Date().getTime();
		checkTime();
		this.realTime += 5000;
		this.t = 0;

		if(this.candidat){
			ArrayList<Integer> pokemons = select(n);
			pokemons.forEach(p -> sendMessage(p.toString(), "Pokemon"));
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
							ArrayList<Integer> pokemons = select( n );
							pokemons.forEach(p -> sendMessage(p.toString(), "Pokemon"));
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
					if (this.owner < best){
						this.owner = best;
						sendMessage(best.toString(), "Ack");
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

	public ArrayList<Integer> select(int number){

		this.counter = 0;
		ArrayList<Integer> pokemons = new ArrayList<>();
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
		Integer source = Integer.valueOf(parts[0]);
		String msg = parts[1];
		System.out.println("RECEIVED "+msg+" from "+source);
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
		sendMessage(id.toString(), "I'm the boss");
	}

	private void sendMessage(String neighbour,String message){
		try{
			System.out.println("SENDING "+message+" to "+neighbour);
			(new Client("127.0.0.1",Integer.parseInt(neighbour))).send(id+":"+message);
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

	@Override
	public String toString(){
		return "Node " + id;
	}
}