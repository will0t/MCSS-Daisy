package simulator;

import java.util.Scanner;

import simulator.World.Scenario;

public class Simulator {
	public static void main(String[] args) {
		// Retrieving interface inputs
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter the following values.");
		System.out.println("1. Maintain Luminosity:");
		System.out.println("2. Ramp-Up Ramp-Down ");
		System.out.println("3. Low Luminosity:");
		System.out.println("4. Our Luminosity:");
		System.out.println("5. High Luminosity");
		
		while(true) {
			try {
				System.out.print("Scenario (1-5):");
				int scenario = sc.nextInt();
				
				if(scenario < 0 || scenario > 5) {
					continue;
				}
				
				Params.scenario = Scenario.values()[scenario-1];
				break;
			} catch (Exception e) {
				sc.nextLine();
			}
		}
		
		System.out.println(Params.scenario.toString() + " selected");
		
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
