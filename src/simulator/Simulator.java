package simulator;

import java.util.InputMismatchException;
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

		int scenario = queryInt(sc, "Scenario (1-5): ", 1, 5);
		Params.scenario = Scenario.values()[scenario-1];
		System.out.println(Params.scenario.toString() + " selected");

		double sl = 1.0;

		// Only maintain luminosity scenario uses this input
		if(scenario <= 1) {
			 sl = queryDouble(sc, "Solar luminosity (0.001-3.000): ", 0.001, 3.000);
		}

		double sa = queryDouble(sc, "Albedo of surface (0.0-1.0): ", 0.0, 1.0);

		int sb = queryInt(sc, "Start % black (0-50): ", 0, 50);

		int sw = queryInt(sc, "Start % white (0-50): ", 0, 50);

		int sr = queryInt(sc, "Start % rabbit (0-100): ", 0, 100);

		double wa = queryDouble(sc, "Albedo of whites (0.0-1.0): ", 0.0, 1.0);

		double ba = queryDouble(sc, "Albedo of blacks (0.0-1.0): ", 0.0, 1.0);

		int iteration = queryInt(sc, "Enter max iteration (>0): ", 0, Integer.MAX_VALUE);

		int runs = queryInt(sc, "Enter repetition times (>0): ", 0, Integer.MAX_VALUE);

		// Creating world and setup procedure
		World world = World.getInstance();

		System.out.println("Running simulations...");

		for(int i = 0; i < runs; i++) {
			world.setup(sl, sa, sb, sw, sr, ba, wa);
			// go procedure
			int count = 0;
			while(count < iteration) {
				world.go();
				System.out.println(world);
				count += 1;
			}
		}

		world.writeToFile("output.csv");
	}

	private static int queryInt(Scanner sc, String message,
			int lb, int ub) {
		while(true) {
			try {
				System.out.print(message);
				int result = sc.nextInt();

				if(result < lb || result > ub) {
					continue;
				}

				return result;
			} catch (InputMismatchException e) {
				sc.nextLine();
			}
		}
	}

	private static double queryDouble(Scanner sc, String message,
			double lb, double ub) {
		while(true) {
			try {
				System.out.print(message);
				double result = sc.nextDouble();

				if(result < lb || result > ub) {
					continue;
				}

				return result;
			} catch (InputMismatchException e) {
				sc.nextLine();
			}
		}
	}
}
