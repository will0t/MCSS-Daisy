import java.util.ArrayList;
import java.util.Scanner;

public class Simulator {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter the following values.");
		System.out.print("Solar luminosity: ");
		double sl = sc.nextDouble();
		System.out.print("Albedo of surface: ");
		double sa = sc.nextDouble();
		System.out.print("Albedo of whites: ");
		double wa = sc.nextDouble();
		System.out.print("Albedo of blacks: ");
		double ba = sc.nextDouble();
		System.out.print("Start % black: ");
		int sb = sc.nextInt();
		System.out.print("Start % white: ");
		int sw = sc.nextInt();
		System.out.print("Enter max iteration: ");
		int iteration = sc.nextInt();
		
		World world = World.getInstance(); 
		world.setup(sl, sa, sb, sw, ba, wa); // setup procedure
		
		int count = 0;
		while(count < iteration) {
			world.go();
			System.out.println("Current iteration: " + Integer.toString(count));
			count += 1;
			// writer.writeToCSV(world);
		}
	}
}
