package simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class World{

	enum Scenario {
		MAINTAIN,
		RAMP_UP_DOWN,
		LOW_LUMINOSITY,
		OUR_LUMINOSITY,
		HIGH_LUMINOSITY,
		SOLAR_FLARE,
	}

	// singleton instance
	private static World instance = null;

	private static CSVWriter writer = new CSVWriter();

	private static Random rand = new Random();
	
	// interface inputs
	public double solarLuminosity; // solar-luminosity
	public double surfaceAlbedo; //albedo-of-surface
	public int startPercentBlack; //start-%-black
	public int startPercentWhite; //start-%-white
	public int startPercentRabbit; //start-%-rabbit
	public double blackAlbedo; //albedo-of-black
	public double whiteAlbedo; //albedo-of-white

	// global variables
	public static double globalTemp = 0; // set global-temperature 0
	public static int numBlacks = 0; //num-blacks
	public static int numWhites = 0; //num-whites
	public static int numRabbits = 0; //rabbit extension
	public static int ticks = 0;
	public static int run = 0;

	// map inside a map representing x,y grid for patches
	private HashMap<Coordinate, Patch> patches = new HashMap<Coordinate, Patch>();
	// rabbits roaming in the map
	private ArrayList<Rabbit> rabbits = new ArrayList<Rabbit>();

	// private constructor for singleton
	private World() {}

	// get singleton instance
	public static World getInstance(){
		if (instance == null) {
			instance = new World();
		}
		return instance;
	}

	// setup procedure
	public void setup(double sl, double sa, int sb, int sw, int sr, double ba, double wa) {
		this.solarLuminosity = sl;
		this.surfaceAlbedo = sa;
		this.startPercentBlack = sb;
		this.startPercentWhite = sw;
		this.startPercentRabbit = sr;
		this.blackAlbedo = ba;
		this.whiteAlbedo = wa;

		switch (Params.scenario) {
		case MAINTAIN:
			// Do nothing
			break;
		case SOLAR_FLARE:
			this.solarLuminosity = 0.9;
			break;
		case RAMP_UP_DOWN:
			this.solarLuminosity = 0.8;
			break;
		case LOW_LUMINOSITY:
			this.solarLuminosity = 0.6;
			break;
		case OUR_LUMINOSITY:
			this.solarLuminosity = 1.0;
			break;
		case HIGH_LUMINOSITY:
			this.solarLuminosity = 1.4;
			break;
		}

		run++;
		ticks = 0;
		globalTemp = 0;
		numBlacks = 0;
		numWhites = 0;
		numRabbits = 0;

		this.generatePatches();
		this.generateRabbits();

		this.seedRandomly(Daisy.Color.BLACK); // seed-blacks-randomly + ask daisies [set age random max-age]
		this.seedRandomly(Daisy.Color.WHITE); // seed-whites-randomly + ask daisies [set age random max-age]
		this.calculatePatchesTemp(); // ask patches [calc-temperature]
		this.setGlobalTemperature(); // set global-temperature (mean [temperature] of patches)

		recordData();
	}


	// go procedure
	public void go() {
		
		// rabbits taking one action per tick
		this.rabbitsAct();
		this.calculatePatchesTemp(); // ask patches [calc-temperature]
		this.diffuseTemperature(); // diffuse temperature .5

		this.checkPatchesSurvivability(); // ask daisies [check-survivability]
		this.setGlobalTemperature(); //set global-temperature (mean [temperature] of patches)

		if(Params.scenario.equals(Scenario.RAMP_UP_DOWN)) {
			if(ticks > 200 && ticks <= 400) {
				this.solarLuminosity += 0.005;
			}

			if(ticks > 600 && ticks <= 850) {
				this.solarLuminosity -= 0.0025;
			}
		}
		
		if(Params.scenario.equals(Scenario.SOLAR_FLARE)) {
			int adjustedTicks = ticks % 1500;
			
			if(adjustedTicks > 0 && adjustedTicks <= 100) {
				this.solarLuminosity += 0.003;
			}

			if(adjustedTicks > 750 && adjustedTicks <= 850) {
				this.solarLuminosity -= 0.003;
			}
		}

		ticks++;
		recordData();
	}


	/////////////////////////////////////////////////
	//////////////  Netlogo Procedures //////////////
	/////////////////////////////////////////////////

	// ask daisies [check-survivability]
	private void checkPatchesSurvivability() {
		ArrayList<Coordinate> patchesToCheck = getAllCoordinates();
		HashMap<Coordinate,Patch> copyPatches = this.deepCopyPatches(this.patches);
		
		patchesToCheck.removeIf((c)->!copyPatches.get(c).hasDaisy());
		
		Collections.shuffle(patchesToCheck);
		for(Coordinate coords : patchesToCheck) {
			this.checkSurvivability(coords);
		}

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
				ArrayList<Coordinate> neighbours = sourceCoordinate.generateNeighbours(1);
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

	public void seedRandomly(Daisy.Color color){
		int colorPercent;
		if (color == Daisy.Color.BLACK) {
			colorPercent = this.startPercentBlack;
		}else {
			colorPercent = this.startPercentWhite;
		}

		// unclear how netlogo handles decimal rounding
		int quota = Math.round(((float)colorPercent/100) * this.totalPatches());

		int count = 0;
		while (count < quota) {
			// pick a random patch
			int randomX = ThreadLocalRandom.current().nextInt(Params.xStart, Params.xEnd + 1);
			int randomY = ThreadLocalRandom.current().nextInt(Params.yStart, Params.yEnd + 1);
			Patch selectedPatch = patches.get(new Coordinate(randomX, randomY));

			// if empty, sprout daisy & randomize daisy age
			if (!selectedPatch.hasDaisy()) {
				selectedPatch.sproutDaisy(color);
				selectedPatch.getDaisy().randomizeAge();
				count += 1;
			}
		}
	}

	// ask patches [calc-temperature]
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

	/////////////////////////////////////////////////
	///////////////  Helper Functions ///////////////
	/////////////////////////////////////////////////

	// instantiating empty patches inside grid
	private void generatePatches() {
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate coordinate = new Coordinate(x,y);
				patches.put(coordinate, new Patch(coordinate));
			}
		}
	}

	// creating a single rabbit at a coordinate
	public void createRabbit(Coordinate coordinate) {
		int randomAge = ThreadLocalRandom.current().nextInt(0, Params.maxRabbitAge + 1);
		Rabbit rabbit = new Rabbit(coordinate, randomAge);
		this.rabbits.add(rabbit);
	}

	// creating rabbits based on start percent rabbit
	private void generateRabbits() {
		int quota = Math.round(((float)startPercentRabbit/100) * this.totalPatches());
		rabbits.clear();

		ArrayList<Coordinate> allPatches = getAllCoordinates();
		
		Collections.shuffle(allPatches);
		
		int count = 0;
		while (count < quota) {
			this.createRabbit(allPatches.remove(0));
			count += 1;
		}
	}

	// each rabbit will take one action: move, eat or reproduce
	private void rabbitsAct() {
		ArrayList<Rabbit> newbornRabbits = new ArrayList<Rabbit>();
		
		Collections.shuffle(rabbits);
		
		Iterator<Rabbit> it = rabbits.iterator();
		
		while(it.hasNext()) {
			Rabbit rabbit = it.next();
			
			Coordinate currCoord = rabbit.getCoordinate();
			
			Patch rabbitPatch = patches.get(currCoord);
			
			// Get nearby patches with daisy
			List<Patch> foodPatches = currCoord.generateNeighbours(1).stream()
					.map((c)->patches.get(c)).filter((p)->p.hasDaisy())
					.collect(Collectors.toList());
			
			// Add the current patch if it has daisy too
			if(rabbitPatch.hasDaisy())
				foodPatches.add(rabbitPatch);
			
			ArrayList<Coordinate> validCoords = currCoord.generateNeighbours(1);
			
			// Convert all rabbits into a list of coordinates
			List<Coordinate> rabbitsCoords = rabbits.stream().map((r)->r.getCoordinate())
					.collect(Collectors.toList());

			// Check for new born rabbits as well
			rabbitsCoords.addAll(newbornRabbits.stream().map((r)->r.getCoordinate())
					.collect(Collectors.toList()));
			
			// Get neighbor coordinates with no rabbit
			validCoords.removeIf((c)->rabbitsCoords.contains(c));
			Collections.shuffle(validCoords);
					
			Random rand = new Random();
			float randomFloat = rand.nextFloat();
			
			// rabbits reproduce when they have excess energy and by chance
			if (randomFloat <= Rabbit.REPRODUCE_CHANCE && rabbit.getEnergyLevel() 
					>= Rabbit.REPRODUCE_REQUIREMENT	&& validCoords.size() > 0) {
				newbornRabbits.add(rabbit.reproduce(validCoords.get(0)));
			// rabbits eat when there's daisy
			} else if (foodPatches.size() > 0 && rabbit.getEnergyLevel() < Rabbit.MAX_ENERGY){
				//System.out.println("Rabbit ate daisy.");
				Collections.shuffle(foodPatches);
				rabbit.eat(foodPatches.get(0));
			// rabbits move in search of daisy
			} else {
				// Always get called to make sure rabbits will use energy even if not 'moving'
				rabbit.move(validCoords.size() > 0 ? validCoords.get(0) : currCoord);
			}
			
			// rabbits die after running out of energy
			if (rabbit.getEnergyLevel() <= 0 || rabbit.getAge() > Params.maxRabbitAge) {
//							System.out.println("Rabbit dies.");
				World.numRabbits -= 1;
				it.remove();
			}
		}
		
		this.rabbits.addAll(newbornRabbits);
	}

	// deep copying patches
	private HashMap<Coordinate, Patch> deepCopyPatches(HashMap<Coordinate,Patch> patches) {
		HashMap<Coordinate, Patch> copyPatches = new HashMap<Coordinate,Patch>();
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate key = new Coordinate(x,y);
				Patch value = patches.get(key);
				Patch deepCopy = new Patch(key);
				deepCopy.setTemperature(value.getTemperature());
				deepCopy.setDaisy(value.getDaisy());
				copyPatches.put(key, deepCopy);
			}
		}
		return copyPatches;
	}

	// get patch neighbours of a coordinate
	private ArrayList<Patch> getPatchNeighbours(Coordinate coordinate){
		ArrayList<Coordinate> neighbourCoordinates = coordinate.generateNeighbours(1);
		ArrayList<Patch> patchNeighbours = new ArrayList<Patch>();
		for (int i=0; i<neighbourCoordinates.size(); i++) {
			Patch patchNeighbour = patches.get(neighbourCoordinates.get(i));
			patchNeighbours.add(patchNeighbour);
		}
		return patchNeighbours;
	}

	// check-survivability (for a single patch)
	public void checkSurvivability(Coordinate coordinate) {
		double seedThreshold = 0;
		Patch patch = patches.get(coordinate);
		Daisy daisy = patch.getDaisy();

		daisy.setAge(daisy.getAge()+1);
		if (daisy.getAge() < Params.maxAge) {
			seedThreshold = ((0.1457 * patch.getTemperature()) - (0.0032 * 
					(Math.pow(patch.getTemperature(), 2))) - 0.6443);
			float randomFloat = rand.nextFloat();
			if (randomFloat < seedThreshold) {
				// making an array list of neighbours without daisy
				ArrayList<Patch> patchNeighbours = this.getPatchNeighbours(coordinate);
				for (int i=patchNeighbours.size()-1; i>=0; i--) {
					Patch patchNeighbour = patchNeighbours.get(i);
					if (patchNeighbour.hasDaisy()) {
						patchNeighbours.remove(i);
					}
				}

				// randomly select a random neighbour without daisy
				if (patchNeighbours.size() > 0) {
					int randomInt = rand.nextInt(patchNeighbours.size());
					Patch seedingPlace = patchNeighbours.get(randomInt);
					if (daisy.getColor() == Daisy.Color.BLACK) {
						seedingPlace.sproutDaisy(Daisy.Color.BLACK);
					}else {
						seedingPlace.sproutDaisy(Daisy.Color.WHITE);
					}
				}
			}
		} else {
			patch.daisyDies(); //die
		}
	}

	private int totalPatches() {
		// calculating no. of seedings required
		int row = Math.abs(Params.xStart) + Math.abs(Params.xEnd) + 1;
		int column = Math.abs(Params.yStart) + Math.abs(Params.yEnd) + 1;
		return row * column;
	}
	
	private ArrayList<Coordinate> getAllCoordinates(){
		ArrayList<Coordinate> allPatches = new ArrayList<Coordinate>();
		
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				allPatches.add(new Coordinate(x,y));
			}
		}

		return allPatches;
	}

	private void recordData() {
		writer.recordData(run, ticks, globalTemp, numWhites, numBlacks, numRabbits, solarLuminosity,
				startPercentBlack, startPercentWhite, blackAlbedo, whiteAlbedo,
				surfaceAlbedo);
	}

	public void writeToFile(String fileName) {
		writer.writeToFile(fileName);
	}

	@Override
	public String toString() {
		return String.format("Run: %d | Tick: %d | Global Temperature: %.2f | White: %d | "
				+ "Black: %d | Rabbit: %d | Luminosity: %.3f | Total Population: %d ", 
				run, ticks, globalTemp, numWhites, numBlacks, numRabbits, 
				solarLuminosity, numWhites + numBlacks + numRabbits);
	}

}
