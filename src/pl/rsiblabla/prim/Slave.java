package pl.rsiblabla.prim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Slave {
	public static ServerSocket portListener;
	public static Socket masterConnection;
	public static SubGraph subGraph;
	public static boolean endProgram = false;
	
	public static ArrayList<Integer> visitedNodes = new ArrayList<Integer>();
	
	public static void run() {
		try {
			openConnection();
			getSubGraph();
			while(!endProgram)
				listenMaster();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				masterConnection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void listenMaster() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(masterConnection.getInputStream()));
		String message = in.readLine();
		if(message == "GIBMELINKPLOX") {
			String outMessage = searchForLightestLink();
			//TODO: send back result
		} else if (message == "THEEND") {
			endProgram = true;
		} else {
			throw new IOException("Incorrect message from master: " + message);
		}
	}

	private static String searchForLightestLink() {
		int bestWeight = Integer.MAX_VALUE;
		for (int i = 0; i < subGraph.nodes.length; i++) {
			//TODO: not completed
		}
		return null;
	}

	private static void openConnection() throws IOException {
		System.out.println("Waiting for connection...");
		portListener = new ServerSocket(Global.connectionPort);
		masterConnection = portListener.accept();
		System.out.println("Accepting connection from " + masterConnection.getInetAddress().getHostAddress());
	}

	public static void getSubGraph() throws IOException, ClassNotFoundException {
		ObjectInputStream objInStream = new ObjectInputStream(masterConnection.getInputStream());
		System.out.println("Downloading subgraph");
		subGraph = (SubGraph) objInStream.readObject();
		System.out.println("Subgraph downloaded!");
	}
}
