package pl.rsiblabla.prim;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Slave {
	public static Socket masterConnection;
	public static ServerSocket portListener;
	public static SubGraph subGraph;
	
	static {
		try {
			portListener = new ServerSocket(Global.connectionPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void run() {
		try {
			setMaster();
			subGraph = getSubGraph();
			portListener.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static SubGraph getSubGraph() {
		System.out.println("Waiting for subgraph");
		
		return null;
	}

	public static void setMaster() throws IOException {
		System.out.println("Waiting for master...");
		masterConnection = portListener.accept();
		System.out.println("Accepting connection from " + masterConnection.getInetAddress().getHostAddress());
	}
}
