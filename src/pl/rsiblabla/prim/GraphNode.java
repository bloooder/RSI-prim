package pl.rsiblabla.prim;

import java.io.Serializable;
import java.util.ArrayList;

public class GraphNode implements Serializable {
	private static final long serialVersionUID = -4709077554896520535L;
	
	ArrayList<GraphLink> links = new ArrayList<GraphLink>();
}