package simulator;

import java.util.concurrent.ThreadLocalRandom;

public class Rabbit {
	private Coordinate coordinate;
	private int energyLevel;
	private int age;
	public static int REPRODUCE_ENERGY = 5;
	public static int REPRODUCE_REQUIREMENT = 7;
	public static float REPRODUCE_CHANCE = 0.7f;
	public static int MAX_ENERGY = 10;
	public static int MOVE_ENERGY = 1;
	public static int EAT_ENERGY = 1;
	
	public Rabbit(Coordinate coordinate, int age) {
		this.coordinate = coordinate;
		this.energyLevel = 3;
		this.age = age;
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
		patchWithDaisy.daisyDies();
		
		this.energyLevel += Rabbit.EAT_ENERGY;
		this.age += 1;
	}
	
	public Rabbit reproduce(Coordinate coords) {
		this.energyLevel -= REPRODUCE_ENERGY;
		this.age += 1;
		int randomAge = ThreadLocalRandom.current().nextInt(0, Params.maxRabbitAge + 1);
		return new Rabbit(coords, randomAge);
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
