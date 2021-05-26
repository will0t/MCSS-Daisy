import java.util.HashMap;

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
	private HashMap<Integer, HashMap<Integer, Patch>> patches 
	= new HashMap<Integer, HashMap<Integer, Patch>>(); 
	
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
		//this.seedBlackRandomly();
		//this.seedWhiteRandomly();
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
				HashMap<Integer, Patch> innerMap = new HashMap<Integer, Patch>();
				innerMap.put(y, new Patch());
				patches.put(x, innerMap);
			}
		}
	}
	
	public void seedBlackRandomly(){ //seed-black-randomly
	}
	
	public void seedWhiteRandomly() { //seed-white-randomly	
	}
	
	public void randomizeAge() { //ask daisies [set age random max-age]	
	}
	
	public void setGlobalTemperature() { //set global-temperature (mean [temperature] of patches)
		Params.globalTemp = 0;
	}
}
