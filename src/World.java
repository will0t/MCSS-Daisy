import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class World{
	// singleton instance
	private static World instance = null;
	
	// interface inputs
	public double solarLuminosity; // solar-luminosity
	public double surfaceAlbedo; //albedo-of-surface
	public int startPercentBlack; //start-%-black
	public int startPercentWhite; //start-%-white
	public double blackAlbedo; //albedo-of-black
	public double whiteAlbedo; //albedo-of-white
	
	// map inside a map representing x,y grid for patches
	private HashMap<Coordinate, Patch> patches 
	= new HashMap<Coordinate, Patch>(); 
	
	// private constructor for singleton
	private World() {
	} 
	
	// get singleton instance
	public static World getInstance(){ 
		if (instance == null) {
			instance = new World();
		}
		return instance;
	}
	
	// setup procedure
	public void setup(double sl, double sa, int sb, int sw, double ba, double wa) {
		this.solarLuminosity = sl;
		this.surfaceAlbedo = sa;
		this.startPercentBlack = sb;
		this.startPercentWhite = sw;
		this.blackAlbedo = ba;
		this.whiteAlbedo = wa;
		
		this.generatePatches();
		this.seedRandomly(Daisy.Color.BLACK);
		this.seedRandomly(Daisy.Color.WHITE);
		//this.randomizeAge();
		// calculate temperature of each patch (NO DIFFUSION)
		//this.setGlobalTemperature();
	}
	
	
	// go procedure
	public void go() { 
	}
	
	// instantiating empty patches inside map
	private void generatePatches() { 
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate coordinate = new Coordinate(x,y);
				patches.put(coordinate, new Patch());
			}
		}
	}
		
	public void seedRandomly(Daisy.Color color){ //seed-black-randomly & seed-white-randomly
		int colorPercent;
		if (color == Daisy.Color.BLACK) {
			colorPercent = this.startPercentBlack;
		}else {
			colorPercent = this.startPercentWhite;
		}
		
		int row = Math.abs(Params.xStart) + Math.abs(Params.xEnd) + 1;
		int column = Math.abs(Params.yStart) + Math.abs(Params.yEnd) + 1;
		int totalPatches = row * column;
		int quota = Math.round(((float)colorPercent/100) * totalPatches); //unclear how netlogo handles decimals
		
		int count = 0;
		while (count < quota) {
			int randomX = ThreadLocalRandom.current().nextInt(Params.xStart, Params.xEnd + 1);
			int randomY = ThreadLocalRandom.current().nextInt(Params.yStart, Params.yEnd + 1);
			
			Patch selectedPatch = patches.get(new Coordinate(randomX, randomY));
			System.out.println(selectedPatch);
			if (!selectedPatch.hasDaisy()) {
				selectedPatch.sproutDaisy(color);
				count += 1; 
			}
		}
	}

	public void randomizeAge() { //ask daisies [set age random max-age]	
	}
	
	public void setGlobalTemperature() { //set global-temperature (mean [temperature] of patches)
		Params.globalTemp = 0;
	}
}
