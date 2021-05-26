import java.util.Scanner;

public class Simulator {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter max iteration: ");
		int iteration = sc.nextInt();
		
		World world = World.getInstance(); // setup procedure
		
		int count = 0;
		while(count < iteration) {
			world.go();
			System.out.println("Current count: " + Integer.toString(count));
			count += 1;
			// writer.writeToCSV(world);
		}
	}
}
