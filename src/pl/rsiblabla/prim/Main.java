package pl.rsiblabla.prim;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		boolean master = askForMaster();
		
		if (master) {
			Master.run();
		} else {
			Slave.run();
		}
	}

	private static boolean askForMaster() {
		while(true) {
			System.out.println("(M)aster or (S)lave?");
			String key = null;
			try {
				key = Global.stdIn.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(key.toUpperCase().equals("M"))
				return true;
			if(key.toUpperCase().equals("S"))
				return false;
		}
	}
}
