package simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class Rabbit {
	private Coordinate coordinate;
	private int energyLevel;
	private int age;
	public static int REPRODUCE_ENERGY = 50;
	public static int REPRODUCE_REQUIREMENT = 100;
	public static int MOVE_ENERGY = 1;
	public static int EAT_ENERGY = 1;
	
	public Rabbit() {
		int randomX = ThreadLocalRandom.current().nextInt(Params.xStart, Params.xEnd + 1);
		int randomY = ThreadLocalRandom.current().nextInt(Params.yStart, Params.yEnd + 1);
		this.coordinate = new Coordinate(randomX, randomY);
		this.energyLevel = 3;
		this.age = 1;
		World.numRabbits += 1;
	}
	
	public Rabbit(Coordinate coordinate) {
		this.coordinate = coordinate;
		this.energyLevel = 3;
		this.age = 1;
		World.numRabbits += 1;
	}
	
	// moving randomly on grid
	public void move(Coordinate coords) {
		// randomly choose neighbour coordinate as new coordinate
		this.coordinate = coords;
		this.energyLevel -= Rabbit.MOVE_ENERGY;
		this.age += 1;
	}
	
	// eating daisy from patch
	public void eat(Patch patchWithDaisy) {
		//System.out.println("Daisy color eaten: " + patchWithDaisy.getDaisy().getColor());
		patchWithDaisy.daisyDies();
		this.energyLevel += Rabbit.EAT_ENERGY;
		this.age += 1;
	}
	
	public Rabbit reproduce(Coordinate coords) {
		this.energyLevel -= Rabbit.REPRODUCE_ENERGY;
		this.age += 1;
		return new Rabbit(coords);
	}
	
	public int getEnergyLevel() {
		return this.energyLevel;
	}
	
	public int getAge() {
		return this.age;
	}
	
	public Coordinate getCoordinate() {
		return this.coordinate;
	}
}
