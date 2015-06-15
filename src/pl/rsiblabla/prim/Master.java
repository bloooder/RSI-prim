package pl.rsiblabla.prim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;

public class Master {
	public static ArrayList<Socket> slaves = new ArrayList<Socket>();
	public static Graph graph;
	
	public static BufferedReader stdIn;

	public static void run() {
		createLocalSlave();
		stdIn = new BufferedReader(new InputStreamReader(System.in));
		boolean endProgram = false;
		
		while(!endProgram) {
			printStatus();
			System.out.println("\nMENU:");
			System.out.println("1. Add slave");
			System.out.println("2. Generate graph");
			System.out.println("3. Load graph");
			System.out.println("4. Generate MST");
			System.out.println("5. Exit");
			
			int choice = 0;
			try {
				choice = Integer.parseInt(stdIn.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				continue;
			}
			
			switch (choice) {
			case 1:
				addSlaveMenu();
				break;
			case 2:
				generateGraphMenu();
				break;
			case 3:
				loadGraphMenu();
				break;
			case 4:
				generateMSTMenu();
				break;
			case 5:
				exitApplication();
				break;
			default:
				continue;
			}
		}
	}

	private static void createLocalSlave() {
		Thread local = new Thread(new Runnable() {
			@Override public void run() {
				Slave.run();
			}
		});
		local.setName("localSlave");
		local.start();
		try {
			addSlave("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private static void addSlaveMenu() {
		System.out.println("Type slave's ip address (type 'back' to return):");
		String address = null;
		try {
			address = stdIn.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(address == "back")
			return;
		
		try {
			addSlave(address);
		} catch (UnknownHostException e) {
			System.out.println("Niepoprawny adres!");
			addSlaveMenu();
		}
	}
	
	private static void generateGraphMenu() {
		int nodeAmount;
		int maxLinks;
		int maxWeight;
		while(true) {
			System.out.println("Type number of nodes (type 'back' to return):");
			try {
				String str = stdIn.readLine();
				if(str.equals("back"))
					return;
				nodeAmount = Integer.parseInt(str);
				break;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				continue;
			}
		}
		while(true) {
			System.out.println("Type max number of links per node (type 'back' to return):");
			try {
				String str = stdIn.readLine();
				if(str.equals("back"))
					return;
				maxLinks = Integer.parseInt(str);
				break;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				continue;
			}
		}
		while(true) {
			System.out.println("Type max weight (type 'back' to return):");
			try {
				String str = stdIn.readLine();
				if(str.equals("back"))
					return;
				maxWeight = Integer.parseInt(str);
				break;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				continue;
			}
		}
		graph = Graph.generateGraph(nodeAmount, maxLinks, maxWeight);
	}
	
	private static void loadGraphMenu() {
		while(true) {
			System.out.println("Type path to file (type 'back' to return):");
			try {
				String str = stdIn.readLine();
				if(str.equals("back"))
					return;
				File file = new File(str);
				graph = IO.loadGraph(file);
				break;
			} catch (FileNotFoundException e) {
				System.out.println("File not found!");
				continue;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				continue;
			}
		}
	}
	
	private static void generateMSTMenu() {
		File file;
		int startNode = 0;
		while(true) {
			System.out.println("Choose file path (type 'back' to return):");
			try {
				String str = stdIn.readLine();
				if(str.equals("back"))
					return;
				file = new File(str);
				break;
			} catch (FileSystemException e) {
				System.out.println("Incorrect path!");
				continue;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		while(true) {
			System.out.println("Choose start node nr (type 'back' to return):");
			try {
				String str = stdIn.readLine();
				if(str.equals("back"))
					return;
				startNode = Integer.parseInt(str);
				if(startNode < 0 || startNode >= graph.nodes.length)
					throw new NumberFormatException();
				break;
			} catch (NumberFormatException e) {
				System.out.println("Incorrect value!");
				continue;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Graph mst = generateMST(startNode);
			IO.saveGraph(mst, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void exitApplication() {
		for(Socket socket : slaves)
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		System.exit(0);
	}

	private static void printStatus() {
		System.out.print("\n\n\nLoaded graph: ");
		if(graph == null)
			System.out.println("none");
		else
			System.out.println(graph.nodes.length + " nodes");
		System.out.println("Active connections:");
		if(slaves.size() == 0)
			System.out.println("none");
		else
			for(Socket socket : slaves)
				System.out.println(socket.getInetAddress().getHostAddress());
	}
	
	private static void sendSubGraphs(SubGraph[] subgraphs) throws IOException {
		//local slave
		Slave.subGraph = subgraphs[0];
		//network slaves
		for (int i = 0; i < subgraphs.length; i++) {
			ObjectOutputStream out = new ObjectOutputStream(slaves.get(i).getOutputStream());
			out.writeObject(subgraphs[i]);
			out.flush();
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
	
	public static Graph generateMST(int startNode) throws IOException {
		SubGraph[] subgraphs = graph.divideIntoSubgraphs(slaves.size());
		sendSubGraphs(subgraphs);
		
		Graph mst = new Graph();
		mst.nodes = new GraphNode[graph.nodes.length];
		for (int i = 0; i < mst.nodes.length; i++)
			mst.nodes[i] = new GraphNode();
		
		for (Socket slave : slaves)
			sendMessage("STARTNODE " + startNode, slave);
		
		int linkAmount = graph.nodes.length-1;
		for (int i = 0; i < linkAmount; i++) {
			NetGraphLink bestLink = findLightestLink();
			if(bestLink.nodeNr == -1)
				throw new IOException("Link not found???");
			GraphLink newLink = new GraphLink();
			newLink.destinationNodeNr = bestLink.destNr;
			for(GraphLink link : graph.nodes[bestLink.nodeNr].links)
				if(link.destinationNodeNr == newLink.destinationNodeNr) {
					newLink.weight = link.weight;
					break;
				}
			mst.nodes[bestLink.nodeNr].links.add(newLink);
			
			for(Socket slave : slaves)
				sendMessage("USEDLINK " + bestLink.nodeNr + " " + bestLink.destNr, slave);
		}
		
		return mst;
	}
	
	public static NetGraphLink findLightestLink() throws IOException {
		NetGraphLink best = NetGraphLink.EMPTY_LINK; 
		for(Socket slave : slaves)
			sendMessage("GIBMELINKPLOX", slave);
		
		for(Socket slave : slaves) {
			String message = getMessage(slave);
			NetGraphLink link = new NetGraphLink(message);
			if(link.weight < best.weight)
				best = link;
		}
		
		return best;
	}

	private static void sendMessage(String string, Socket slave) throws IOException {
		PrintWriter out = new PrintWriter(slave.getOutputStream());
		out.println(string);
		out.flush();
	}
	
	private static String getMessage(Socket slave) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(slave.getInputStream()));
		return in.readLine();
	}

	public static class NetGraphLink {
		public static final NetGraphLink EMPTY_LINK = new NetGraphLink(-1, Integer.MAX_VALUE);
		int nodeNr;
		int destNr;
		int weight;
		
		private NetGraphLink(int nodeNr, int weight) {
			this.nodeNr = nodeNr;
			this.weight = weight;
		}
		
		public NetGraphLink(String string) {
			String[] splitted = string.split(" ");
			nodeNr = Integer.parseInt(splitted[0]);
			destNr = Integer.parseInt(splitted[1]);
			for(GraphLink link : graph.nodes[nodeNr].links)
				if(link.destinationNodeNr == destNr) {
					weight = link.weight;
					break;
				}
		}
	}
}
