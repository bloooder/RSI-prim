package pl.rsiblabla.prim;

import java.io.Serializable;
import java.util.Random;

public class Graph implements Serializable {
	private static final long serialVersionUID = -1045936618203421854L;
	
	GraphNode[] nodes;
	
	public SubGraph[] divideIntoSubgraphs(int parts) {
		SubGraph[] result = new SubGraph[parts];
		for (int i = 0; i < parts; i++) {
			SubGraph subGraph = new SubGraph();
			int startIndex = i * nodes.length / parts;
			int endIndex = ((i+1) * nodes.length / parts) - 1;
			subGraph.nodeNrOffset = startIndex;
			int nodeAmount = endIndex-startIndex+1;
			subGraph.nodes = new GraphNode[nodeAmount];
			for (int j = startIndex; j <= endIndex; j++)
				subGraph.nodes[j-startIndex] = nodes[j];
			subGraph.usedLinks = new int[nodeAmount];
			result[i] = subGraph;
		}
		return result;
	}
	
	public static Graph generateGraph(int nodes, int maxLinksPerNode, int maxWeight) {
		Random rand = new Random(System.currentTimeMillis());
		Graph newGraph = new Graph();
		newGraph.nodes = new GraphNode[nodes];
		for (int i = 0; i < nodes; i++) {
			GraphNode newNode = new GraphNode();
			newNode.links = new GraphLink[rand.nextInt(maxLinksPerNode)];
			for (int j = 0; j < newNode.links.length; j++) {
				GraphLink newLink = new GraphLink();
				newLink.destinationNodeNr = rand.nextInt(nodes);
				newLink.weight = rand.nextInt(maxWeight);
				newNode.links[j] = newLink;
			}
			newGraph.nodes[i] = newNode;
		}
		//TODO: search for alone nodes and connect them
		return newGraph;
	}
}
