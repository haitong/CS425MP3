import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class Node implements Runnable{
	private int cs_int;
	private int next_req;
	private int total_exec_time;
	private int option;
	private int node_id;
	private long end_time;
	private int lock_index;
	private List<SharedObj> shared_obj = new ArrayList<SharedObj>(); 
	private List<Integer>  voting_set = new ArrayList<Integer> ();
	private Mutex mutex;

	public Node(Mutex m, int id){
		mutex = m;
		cs_int = m.getCsInt();
		next_req=m.getNextReq();
		total_exec_time=m.getTotalExecTime();
		option=m.getOption();
		node_id = id;
		shared_obj = m.getSharedObj();
		lock_index = -1;
	}

	public void requestLock(){
		for(int vote : voting_set){
			SharedObj shared = shared_obj.get(vote);
			synchronized(shared){
				// When the thread send a message to the other thread
				if(option == 1){
					DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
					Date dateobj = new Date();
					System.out.println(df.format(dateobj) + "   " + vote + "   " + node_id + "   Requesting lock");
				}
				while(shared.isAvailable() == 0){
					try{
						shared.wait();
					}
					catch(InterruptedException e){
						 e.printStackTrace(); 
					}
					if(option == 1){
						DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
						Date dateobj = new Date();
						System.out.println(df.format(dateobj) + "   " + node_id + "   " + vote + "   Granting lock");
					}

					// check whether should terminate
					long curr_time = System.currentTimeMillis();
//					System.out.println("Node " + node_id + ",Current time " + curr_time + ", end time " + end_time);
					if(curr_time > end_time){
						return;
					}
				}
				lock_index +=1;
				long curr_time = System.currentTimeMillis();
				if(curr_time > end_time){
					return;
				}
			}
		}
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
		Date dateobj = new Date();
		System.out.println(df.format(dateobj) + "   " + node_id + "   " + voting_set);
	}

	public void holdLock(){
		long curr_time = System.currentTimeMillis();
		if(curr_time > end_time){
			return;
		}
		try{
			Thread.sleep(cs_int);
		}
		catch(Exception e){
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}

	public void releaseLock(){
		for(int i = lock_index; i >=0; i--){
			SharedObj shared = shared_obj.get(voting_set.get(i));
			synchronized(shared){
				shared.setAvailable();
				if(option == 1){
					DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
					Date dateobj = new Date();
					System.out.println(df.format(dateobj) + "   " + voting_set.get(i) + "  " + node_id+"   Releasing lock");
				}
				shared.notify();
			}
		}
		
		long curr_time = System.currentTimeMillis();
		if(curr_time >= end_time){
			for(SharedObj shared : shared_obj){
				synchronized(shared){
					shared.notify();
				}
			}
		}

		try{
			Thread.sleep(next_req);
		}
		catch(Exception e){
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
		lock_index = -1;
	}

	public void getVotingSet(){
		int row = node_id/3;
		int col = node_id%3;

		for(int i=0; i < 3; i++){
			if(i != col)
				voting_set.add(row * 3 + i);
		}
		for(int i=0; i < 3; i++){
			if(i != row)
				voting_set.add(i * 3 + col);
		}

		voting_set.add(node_id);
		// Order the nodes in priority
		Collections.sort(voting_set);
		if(option == 1)
			System.out.println("For node " + node_id + ", its voting set are " + voting_set);
	}

	@Override
	public void run(){
		System.out.println("Node " + node_id + " is running");
		long curr_time = System.currentTimeMillis();
		end_time = curr_time + 1000 * total_exec_time;
		getVotingSet();
		while(curr_time < end_time){
			requestLock();
			holdLock();
			releaseLock();
			curr_time = System.currentTimeMillis();
		}
		System.out.println("Node " + node_id +" exiting");
	}
}












