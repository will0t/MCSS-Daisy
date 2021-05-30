package simulator;

import java.util.concurrent.ThreadLocalRandom;

/*
 * Represents the daisies in the DaisyWorld
 */
public class Daisy {
	// available color of daisies
	public enum Color{
		WHITE,
		BLACK
	}
	
	private int age;
	private final Color color;
	private final double albedo;
	
	public Daisy(int age, Color color) {
		this.age = age;
		this.color = color;
		if (color == Color.WHITE) {
			albedo = World.getInstance().whiteAlbedo;
		} else {
			albedo = World.getInstance().blackAlbedo;
		}
	}
	
	//ask daisies [set age random max-age]	(for a single daisy)
	public void randomizeAge() { 
		int randomAge = ThreadLocalRandom.current().nextInt(0, Params.maxAge + 1);
		this.setAge(randomAge);
	}
	
	public int getAge() {
		return this.age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public Color getColor() {
		return color;
	}
	
	public double getAlbedo() {
		return albedo;
	}
}
