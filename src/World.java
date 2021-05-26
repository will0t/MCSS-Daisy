import java.util.Map;

public class World{
	// interface inputs
	public double solarLuminosity; // solar-luminosity
	public double surfaceAlbedo; //albedo-of-surface
	public int startPercentBlack; //start-%-black
	public int startPercentWhite; //start-%-white
	public double blackAlbedo; //albedo-of-black
	public double whiteAlbedo; //albedo-of-white
	
	private static World instance = null;
	
	private Map<Integer, Map<Integer, Patch>> patches; //Map inside a map representing coordinates
	// patches.get(x).get(y);
	
	private World() { //setup procedure
		this.seedBlackRandomly();
		this.seedWhiteRandomly();
		this.randomizeAge();
		// calculate temperature of each patch (NO DIFFUSION)
		this.setGlobalTemperature();
	} 
	
	public static World getInstance(){ //inputs for private constructor needed
		if (instance == null) {
			instance = new World();
		}
		return instance;
	}
	
	public void go() { //go procedure
	}
	
	private void generatePatches() { 
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
