package pl.rsiblabla.prim;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Slave {
	public static ServerSocket portListener;
	public static Socket masterConnection;
	public static BufferedReader in;
	public static PrintWriter out;
	public static SubGraph subGraph;
	public static boolean endProgram = false;
	
	public static boolean[] visitedNodes;
	
	public static void run() {
		try {
			openConnection();
			getSubGraph();
			initConnectionStreams();
			while(!endProgram)
				listenMaster();
		} catch (EOFException e) {
			System.out.println("Connection closed. Exiting application.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			try {
				masterConnection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void initConnectionStreams() throws IOException {
		in = new BufferedReader(new InputStreamReader(masterConnection.getInputStream()));
		out = new PrintWriter(masterConnection.getOutputStream());
	}

	private static void listenMaster() throws IOException {
		
		String message = in.readLine();
		if(message == "GIBMELINKPLOX") {
			String outMessage = searchForLightestLink();
			sendToMaster(outMessage);
		} else if (message.startsWith("USED")) {
				String[] splitted = message.split(" ");
				visitedNodes[Integer.parseInt(splitted[1])] = true;
		} else if (message == "THEEND") {
			endProgram = true;
		} else {
			throw new IOException("Incorrect message from master: " + message);
		}
	}

	private static void sendToMaster(String outMessage) {
		out.println(outMessage);
		out.flush();
	}

	public static String searchForLightestLink() {
		int bestWeight = Integer.MAX_VALUE;
		int nodeNr = 0;
		int linkNr = 0;
		for (int i = 0; i < subGraph.nodes.length; i++) {
			GraphNode node = subGraph.nodes[i];
			//check if all links used
			if(subGraph.usedLinks[i] == node.links.length)
				continue;
			for (int j = 0; j < node.links.length; j++) {
				GraphLink link = node.links[j];
				//check if link is redundant
				if(visitedNodes[link.destinationNodeNr])
					continue;
				if(link.weight < bestWeight) {
					nodeNr = i;
					linkNr = j;
					bestWeight = link.weight;
				}
			}
		}
		nodeNr += subGraph.nodeNrOffset;
		return nodeNr + " " + linkNr;
	}

	private static void openConnection() throws IOException {
		System.out.println("Waiting for connection...");
		portListener = new ServerSocket(Global.connectionPort);
		masterConnection = portListener.accept();
		System.out.println("Accepting connection from " + masterConnection.getInetAddress().getHostAddress());
	}

	public static void getSubGraph() throws IOException, ClassNotFoundException, EOFException {
		ObjectInputStream objInStream = new ObjectInputStream(masterConnection.getInputStream());
		System.out.println("Downloading subgraph");
		subGraph = (SubGraph) objInStream.readObject();
		System.out.println("Subgraph downloaded!");
		visitedNodes = new boolean[subGraph.totalNodeAmount];
	}
}