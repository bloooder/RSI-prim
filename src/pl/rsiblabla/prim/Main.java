package pl.rsiblabla.prim;
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
		//TODO: ask for master/slave
		return true;
	}
}
