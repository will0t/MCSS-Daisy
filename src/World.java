import java.util.ArrayList;
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
	
	// global variables
	public static double globalTemp = 0; // global-temperature
	public static int numBlacks = 0; //num-blacks
	public static int numWhites = 0; //num-whites
	
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
		// calculate temperature of each patch (NO DIFFUSION)
		this.calculatePatchesTemp();
		this.setGlobalTemperature();
	}
	
	
	// go procedure
	public void go() { 
		this.diffuseTemperature();
	}
	
	private void diffuseTemperature() {
		// make a deep copy of the patches hashmap
		HashMap<Coordinate, Patch> copyPatches = new HashMap<Coordinate,Patch>();
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate key = new Coordinate(x,y);
				Patch value = patches.get(key);
				Patch deepCopy = new Patch();
				deepCopy.setTemperature(value.getTemperature());
				copyPatches.put(key, deepCopy);
			}
		}
		
		// loop through each patch of deep copied patches
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate sourceCoordinate = new Coordinate(x,y);
				Patch sourcePatch = patches.get(sourceCoordinate);
				Patch copyPatch = copyPatches.get(sourceCoordinate);
								
				// calculate 1/8 of temperature diffused
				double amountToDiffuse = copyPatch.getTemperature() * 0.5 / 8;
				System.out.println("Amount to diffuse: "+amountToDiffuse);
				
				// diffuse it in the actual patches
				ArrayList<Coordinate> neighbours = sourceCoordinate.generateNeighbours();
				for (int i=0; i<neighbours.size(); i++) {
					Coordinate neighbourCoordinate = neighbours.get(i);
					Patch neighbourPatch = patches.get(neighbourCoordinate);
					// adding to neighbour
					neighbourPatch.addToTemperature(amountToDiffuse);
					// deducting from source
					sourcePatch.addToTemperature(-amountToDiffuse);
				}
			}
		}		
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
			if (!selectedPatch.hasDaisy()) {
				selectedPatch.sproutDaisy(color);
				randomizeAge(selectedPatch.getDaisy());
				count += 1; 
			}
		}
	}

	public void randomizeAge(Daisy daisy) { //ask daisies [set age random max-age]	
		int randomAge = ThreadLocalRandom.current().nextInt(0, Params.maxAge + 1);
		daisy.setAge(randomAge);
	}
	
	private void calculatePatchesTemp() {
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate coordinate = new Coordinate(x,y);
				patches.get(coordinate).calcTemperature();
			}
		}
	}
	
	public void setGlobalTemperature() { //set global-temperature (mean [temperature] of patches)
		int count = 0;
		double total = 0;
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate coordinate = new Coordinate(x,y);
				total += patches.get(coordinate).getTemperature();
				System.out.println("Temperature added: " + patches.get(coordinate).getTemperature());
				count += 1;
			}
		}
		World.globalTemp = total / count;
		System.out.println("Global temperature: " + World.globalTemp);
	}
}
