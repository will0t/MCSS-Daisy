package simulator;

import simulator.World.Scenario;

public class Params {
	public static Scenario scenario = Scenario.RAMP_UP_DOWN;
	
	// representing the grid and coordinates
	public final static int xStart = -14;
	public final static int xEnd = 14;
	public final static int yStart = -14;
	public final static int yEnd = 14;
	
	// storing constants
	public final static int maxAge = 25; //max-age
	public final static int maxRabbitAge = 15;
}
