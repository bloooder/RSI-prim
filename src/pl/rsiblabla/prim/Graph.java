package pl.rsiblabla.prim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.naming.LimitExceededException;

public class Graph implements Serializable {
	private static final long serialVersionUID = -1045936618203421854L;
	
	GraphNode[] nodes;
	
	public SubGraph[] divideIntoSubgraphs(int parts) {
		SubGraph[] result = new SubGraph[parts];
		for (int i = 0; i < parts; i++) {
			SubGraph subGraph = new SubGraph();
			subGraph.totalNodeAmount = nodes.length;
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
		for (int i = 0; i < nodes; i++)
			newGraph.nodes[i] = new GraphNode();
		
		LinkedList<Integer> nodeStack = new LinkedList<Integer>();
		ArrayList<Integer> lostNodes = new ArrayList<Integer>(nodes);
		for (int i = 0; i < nodes; i++)
			lostNodes.add(i);
		
		int firstNode = rand.nextInt(nodes);
		nodeStack.addFirst(firstNode);
		lostNodes.remove((Integer)firstNode);
		
		while(nodeStack.size() != 0) {
			int actualIndex = nodeStack.removeFirst();
			GraphNode node = newGraph.nodes[actualIndex];
			int linkAmount = rand.nextInt(maxLinksPerNode) + 1;
			
			for (int i = 0; i < linkAmount; i++) {
				int destNodeIndex = 0;
				if(lostNodes.size() == 0) {
					boolean goodIndex = false;
					while(!goodIndex) {
						goodIndex = true;
						destNodeIndex = rand.nextInt(nodes);
						if(destNodeIndex == actualIndex) {
							goodIndex = false;
							continue;
						}
						for(GraphLink link : node.links)
							if(link.destinationNodeNr == destNodeIndex) {
								goodIndex = false;
								break;
							}
					}
				} else {
					destNodeIndex = lostNodes.remove(rand.nextInt(lostNodes.size()));
					nodeStack.addLast(destNodeIndex);
				}
				
				GraphLink link = new GraphLink();
				link.destinationNodeNr = destNodeIndex;
				link.weight = rand.nextInt(maxWeight);
				node.links.add(link);
				
				GraphLink symmetricLink = new GraphLink();
				symmetricLink.destinationNodeNr = actualIndex;
				symmetricLink.weight = link.weight;
				newGraph.nodes[destNodeIndex].links.add(symmetricLink);
			}
		}
		return newGraph;
	}
}
