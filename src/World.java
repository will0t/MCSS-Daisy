import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class World{
	// singleton instance
	private static World instance = null;
	
	private static CSVWriter writer = new CSVWriter();
	
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
		this.calculatePatchesTemp();
		this.setGlobalTemperature();
		
		recordData();
	}
	
	
	// go procedure
	public void go() { 
		// To sanity check patch temperature
		//for (int x=Params.xStart; x<=Params.xEnd; x++) {
		//	for (int y=Params.yStart; y<=Params.yEnd; y++) {
		//		Coordinate coordinate = new Coordinate(x,y);
		//		Patch patch = patches.get(coordinate);
		//		System.out.println("Temperature: " + patch.getTemperature());
		//	}
		//}
		//System.out.println("Global temperature: " + World.globalTemp);
		
		this.calculatePatchesTemp();
		this.diffuseTemperature();
		recordData();
		
		// ask daisies [check-survivability]
		this.checkPatchesSurvivability()
		
		this.setGlobalTemperature();
		// TODO: scenarios not done yet
	}
	
	// check-survivability
	public void checkSurvivability(Coordinate coordinate) {
		double seedThreshold = 0;
		Patch seedingPlace;
		Patch patch = patches.get(coordinate);
		Daisy daisy = patch.getDaisy();
		
		daisy.setAge(daisy.getAge()+1);
		if (daisy.getAge() < Params.maxAge) {
			seedThreshold = ((0.1457 * patch.getTemperature()) - (0.0032 * (Math.pow(patch.getTemperature(), 2))) - 0.6443);
			Random rand = new Random();
			float randomFloat = rand.nextFloat();
			if (randomFloat < seedThreshold) {
				// making an array list of neighbours without daisy
				ArrayList<Patch> patchNeighbours = this.getPatchNeighbours(coordinate);
				for (int i=patchNeighbours.size()-1; i>=0; i--) {
					Patch patchNeighbour = patchNeighbours.get(i);
					if (patchNeighbour.hasDaisy()) {
						System.out.println("Has neighbour, removing.");
						patchNeighbours.remove(i);
					}
				}
				
				// checking if neighbours in list have daisy
				//for (int i=0; i<patchNeighbours.size(); i++) {
				//	System.out.println("Has daisy: " + patchNeighbours.get(i).hasDaisy());
				//}
				
				// randomly select a random neighbour without daisy
				if (patchNeighbours.size() > 0) {
					int randomInt = rand.nextInt(patchNeighbours.size());
					seedingPlace = patchNeighbours.get(randomInt);
					if (daisy.getColor() == Daisy.Color.BLACK) {
						seedingPlace.sproutDaisy(Daisy.Color.BLACK);
					}else {
						seedingPlace.sproutDaisy(Daisy.Color.WHITE);
					}
				}
			}
		} else {
			daisy = null; //die
		}
	}
	
	private void checkPatchesSurvivability() {
		HashMap<Coordinate,Patch> copyPatches = this.deepCopyPatches(this.patches);
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate coordinate = new Coordinate(x,y);
				if (copyPatches.get(coordinate).hasDaisy()) {
					this.checkSurvivability(coordinate);	
				}
			}
		}
	}
	
	// get patch neighbours of a coordinate
	private ArrayList<Patch> getPatchNeighbours(Coordinate coordinate){
		ArrayList<Coordinate> neighbourCoordinates = coordinate.generateNeighbours();
		ArrayList<Patch> patchNeighbours = new ArrayList<Patch>();
		for (int i=0; i<neighbourCoordinates.size(); i++) {
			Patch patchNeighbour = patches.get(neighbourCoordinates.get(i));
			patchNeighbours.add(patchNeighbour);
		}
		return patchNeighbours;
	}
	
	private void diffuseTemperature() {
		// make a deep copy of the patches hashmap
		HashMap<Coordinate,Patch> copyPatches = this.deepCopyPatches(this.patches);
		
		// loop through each patch of deep copied patches
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate sourceCoordinate = new Coordinate(x,y);
				Patch sourcePatch = patches.get(sourceCoordinate);
				Patch copyPatch = copyPatches.get(sourceCoordinate);
								
				// calculate 1/8 of temperature diffused
				double amountToDiffuse = copyPatch.getTemperature() * 0.5 / 8;
				
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
	
	// deep copying patches
	private HashMap<Coordinate, Patch> deepCopyPatches(HashMap<Coordinate,Patch> patches) {
		HashMap<Coordinate, Patch> copyPatches = new HashMap<Coordinate,Patch>();
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate key = new Coordinate(x,y);
				Patch value = patches.get(key);
				Patch deepCopy = new Patch();
				deepCopy.setTemperature(value.getTemperature());
				deepCopy.setDaisy(value.getDaisy());
				copyPatches.put(key, deepCopy);
			}
		}
		return copyPatches;
	}
	
	//seed-black-randomly & seed-white-randomly
	public void seedRandomly(Daisy.Color color){ 
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
	
	//ask daisies [set age random max-age]	
	public void randomizeAge(Daisy daisy) { 
		int randomAge = ThreadLocalRandom.current().nextInt(0, Params.maxAge + 1);
		daisy.setAge(randomAge);
	}
	
	private void recordData() {
		writer.recordData(globalTemp, numWhites, numBlacks, solarLuminosity, 
				startPercentBlack, startPercentWhite, blackAlbedo, whiteAlbedo, 
				surfaceAlbedo);
	}
	
	public void writeToFile(String fileName) {
		writer.writeToFile(fileName);
	}
	
	private void calculatePatchesTemp() {
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate coordinate = new Coordinate(x,y);
				patches.get(coordinate).calcTemperature();
			}
		}
	}
	
	//set global-temperature (mean [temperature] of patches)
	public void setGlobalTemperature() { 
		int count = 0;
		double total = 0;
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate coordinate = new Coordinate(x,y);
				total += patches.get(coordinate).getTemperature();
				count += 1;
			}
		}
		World.globalTemp = total / count;
	}
}
