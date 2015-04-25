import java.util.*;
import java.io.*;

public class Mutex{
	private static int cs_int;
	private static int next_req;
	private static int total_exec_time;
	private static int option;
	private int TOTAL_NUM;
	private List<SharedObj> shared_obj = new ArrayList<SharedObj>();  
	private List<Thread> thread = new ArrayList<Thread>();  

    
    public Mutex(String[] input){
        this.start(input);
    }

    public void start(String[] input){
		TOTAL_NUM = 9;
		cs_int = Integer.parseInt(input[0]);
		next_req = Integer.parseInt(input[1]);
		total_exec_time = Integer.parseInt(input[2]);
		option = Integer.parseInt(input[3]);
		System.out.println("Input parameters: cs_int=" + cs_int + ", next_req=" + next_req +", total_time="+total_exec_time + ", option=" + option);

		// set up 9 shared obj for synchronizations
		for(int i=0; i < TOTAL_NUM; i++){
			shared_obj.add(new SharedObj(i));
		}

		// set up 9 nodes threads
		for(int i=0; i < TOTAL_NUM; i++){
			Node n = new Node(this,i);
			Thread t = new Thread(n);
			thread.add(t);
		}
		for(Thread t : thread){
			t.start();
		}
		try{
			Thread.sleep(1000 * total_exec_time);
		}
		catch(Exception e){
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
		for(Thread t : thread){
			try{
				t.join();
			}
			catch(Exception e){
				e.printStackTrace(); 
			}
		}
		System.out.println("Main thread exiting");
    }
   
	public synchronized int getCsInt(){
		return cs_int;
	}

	public synchronized int getNextReq(){
		return next_req;
	}
	
	public synchronized int getTotalExecTime(){
		return total_exec_time;
	}
	
	public synchronized int getOption(){
		return option;
	}

	public synchronized  List<SharedObj> getSharedObj(){
		return shared_obj;
	}

	public static void main(String[] args){
        System.out.println("Starting Mutex...");
        Mutex mutex = new Mutex(args);
    }
}
