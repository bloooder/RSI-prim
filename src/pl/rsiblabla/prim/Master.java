package pl.rsiblabla.prim;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Master {
	public static ArrayList<Socket> slaves = new ArrayList<Socket>();
	public static Graph graph;
	public static SubGraph subGraph;

	public static void run() {
		graph = Graph.generateGraph(10, 5, 10);
		try {
			addSlave("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		SubGraph[] subgraphs = graph.divideIntoSubgraphs(slaves.size()+1);
		
		try {
			sendSubGraphs(subgraphs);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				for(Socket socket : slaves)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void sendSubGraphs(SubGraph[] subgraphs) throws IOException {
		subGraph = subgraphs[0];
		for (int i = 1; i < subgraphs.length; i++) {
			ObjectOutputStream out = new ObjectOutputStream(slaves.get(i-1).getOutputStream());
			out.writeObject(subgraphs[i]);
		}
	}

	public static void addSlave(String ip) throws UnknownHostException {
		try {
			Socket connection = new Socket(ip, Global.connectionPort);
			slaves.add(connection);
		} catch (UnknownHostException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Graph generateMST() {
		return null;
	}

}
