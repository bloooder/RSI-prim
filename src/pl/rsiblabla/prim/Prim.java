package pl.rsiblabla.prim;

import java.util.ArrayList;

public class Prim {
	public static class Result {
		public int nodeNr;
		public int linkNr;
		
		public Result(int nodeNr, int linkNr) {
			this.nodeNr = nodeNr;
			this.linkNr = linkNr;
		}
	}
	
	public static Result getLightestNode(SubGraph graph) {
		Result result = getLightestNode(graph);
		result.linkNr = result.linkNr + graph.nodeNrOffset;
		return result;
	}
	
	public static Result getLightestNode(Graph graph, ArrayList<Integer> visitedNodes) {
		int bestWeight = Integer.MAX_VALUE;
		int bestNode = 0;
		int bestLink = 0;
		for (int i = 0; i < graph.nodes.length; i++) {
			if(!visitedNodes.contains(i))
				continue;
			GraphNode node = graph.nodes[i];
			for (int j = 0; j < node.links.length; j++) {
				GraphLink link = node.links[j];
				//TODO: not completed
			}
		}
		return null;
	}
}
