package simulator;

public class Simulator {	
	
	public static void main(String[] args) {
		Options options = new Options();
		World world = new World(options);
			
		System.out.println(world);
	}

}
