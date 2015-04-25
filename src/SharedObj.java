import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;



public class SharedObj{
	private int node_id;
	private int available;

	public  SharedObj(int id){
		node_id  = id;
		available = 1;
	}

	public synchronized int getNodeID(){
		return node_id;
	}

	public synchronized int isAvailable(){
		if(available > 0){
			available = 0;
			return 1;
		}
		return 0;
	}
	
	public synchronized void setAvailable(){
		available = 1;
	}
}
