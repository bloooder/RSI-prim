package pl.rsiblabla.prim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class IO {
	public static Graph loadGraph(File file) throws FileNotFoundException {
		Graph result = new Graph();
		Scanner sc = new Scanner(file);
		int nodeAmount = Integer.parseInt(sc.nextLine());
		result.nodes = new GraphNode[nodeAmount];
		for (int i = 0; i < result.nodes.length; i++) {
			GraphNode node = new GraphNode();
			String linkStr = sc.nextLine();
			ArrayList<GraphLink> links = new ArrayList<GraphLink>();
			String[] linkStrInts = linkStr.split(" ");
			for (int j = 0; j < linkStrInts.length; j += 2) {
				int nodeNr = Integer.parseInt(linkStrInts[j]);
				int weight = Integer.parseInt(linkStrInts[j+1]);
				GraphLink link = new GraphLink();
				link.destinationNodeNr = nodeNr;
				link.weight = weight;
				links.add(link);
			}
			node.links = links;
			result.nodes[i] = node;
		}
		sc.close();
		return result;
	}
	
	public static void saveGraph(Graph graph, File file) throws IOException {
		PrintWriter out = new PrintWriter(file);
		StringBuilder strBldr = new StringBuilder();
		out.println(graph.nodes.length);
		for(GraphNode node : graph.nodes) {
			if(node.links.size() == 0) {
				out.println();
				continue;
			}
			strBldr.setLength(0);
			for(GraphLink link : node.links) {
				strBldr.append(link.destinationNodeNr);
				strBldr.append(" ");
				strBldr.append(link.weight);
				strBldr.append(" ");
			}
			strBldr.deleteCharAt(strBldr.length()-1);
			out.println(strBldr.toString());
		}
		out.close();
	}
}
