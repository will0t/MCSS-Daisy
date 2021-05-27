import java.util.Scanner;

public class Simulator {
	public static void main(String[] args) {
		// Retrieving interface inputs
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter the following values.");
		System.out.print("Solar luminosity: ");
		double sl = sc.nextDouble();
		System.out.print("Albedo of surface: ");
		double sa = sc.nextDouble();
		System.out.print("Start % black: ");
		int sb = sc.nextInt();
		System.out.print("Start % white: ");
		int sw = sc.nextInt();
		System.out.print("Albedo of whites: ");
		double wa = sc.nextDouble();
		System.out.print("Albedo of blacks: ");
		double ba = sc.nextDouble();
		System.out.print("Enter max iteration: ");
		int iteration = sc.nextInt();
		
		// Creating world and setup procedure
		World world = World.getInstance(); 
		world.setup(sl, sa, sb, sw, ba, wa);
		
		System.out.println("Running simulations...");
		// go procedure
		int count = 0;
		while(count < iteration) {
			world.go();
//			System.out.println("Current iteration: " + Integer.toString(count));
			System.out.println(world);
			count += 1;
		}
		world.writeToFile("output.csv");
	}
}
