package pl.rsiblabla.prim;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Master {
	public static ArrayList<Socket> slaves = new ArrayList<Socket>();

	public static void run() {
		try {
			addSlave("127.0.0.1");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}
