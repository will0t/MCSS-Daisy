package simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class World{

	enum Scenario {
		MAINTAIN,
		RAMP_UP_DOWN,
		LOW_LUMINOSITY,
		OUR_LUMINOSITY,
		HIGH_LUMINOSITY,
	}

	// singleton instance
	private static World instance = null;

	private static CSVWriter writer = new CSVWriter();

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

		this.generatePatches();
		// generating rabbits that roam the map
		this.generateRabbits();

		//for (Rabbit rabbit: rabbits) {
		//	System.out.println(rabbit + " at " + rabbit.getCoordinate().getX() + "," + rabbit.getCoordinate().getY());
		//}
		//System.out.println("Total rabbits: " + rabbits.size());

		this.seedRandomly(Daisy.Color.BLACK); // seed-blacks-randomly + ask daisies [set age random max-age]
		this.seedRandomly(Daisy.Color.WHITE); // seed-whites-randomly + ask daisies [set age random max-age]
		this.calculatePatchesTemp(); // ask patches [calc-temperature]
		this.setGlobalTemperature(); // set global-temperature (mean [temperature] of patches)

		recordData();
	}


	// go procedure
	public void go() {
		// DEBUG USE: To sanity check patch temperature
		/*
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate coordinate = new Coordinate(x,y);
				Patch patch = patches.get(coordinate);
				System.out.println("Temperature: " + patch.getTemperature());
			}
		}
		System.out.println("Global temperature: " + World.globalTemp);
		*/

		System.out.println("No. of black daisies:" + World.numBlacks);
		System.out.println("No. of white daisies:" + World.numWhites);
		System.out.println("No. of rabbits:" + World.numRabbits);

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

		ticks++;
		recordData();
	}


	/////////////////////////////////////////////////
	//////////////  Netlogo Procedures //////////////
	/////////////////////////////////////////////////

	// ask daisies [check-survivability]
	private void checkPatchesSurvivability() {
		ArrayList<Coordinate> patchesToCheck = new ArrayList<Coordinate>();
		HashMap<Coordinate,Patch> copyPatches = this.deepCopyPatches(this.patches);
		for (int x=Params.xStart; x<=Params.xEnd; x++) {
			for (int y=Params.yStart; y<=Params.yEnd; y++) {
				Coordinate coordinate = new Coordinate(x,y);
				// only check survivability when patch has daisy
				if (copyPatches.get(coordinate).hasDaisy()) {
					patchesToCheck.add(coordinate);
				}
			}
		}

		Collections.shuffle(patchesToCheck);
		for(Coordinate coords : patchesToCheck) {
			this.checkSurvivability(coords);
		}

	}

	// diffuse temperature .5
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

	// seed-black-randomly & seed-white-randomly + ask daisies [set age random max-age]
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
		Rabbit rabbit = new Rabbit(coordinate);
		this.rabbits.add(rabbit);
	}

	// creating rabbits based on start percent rabbit
	private void generateRabbits() {
		int quota = Math.round(((float)startPercentRabbit/100) * this.totalPatches());

		int count = 0;
		while (count < quota) {
			int randomX = ThreadLocalRandom.current().nextInt(Params.xStart, Params.xEnd + 1);
			int randomY = ThreadLocalRandom.current().nextInt(Params.yStart, Params.yEnd + 1);
			this.createRabbit(new Coordinate(randomX, randomY));
			count += 1;
		}
	}

	// each rabbit will take one action: move, eat or reproduce
	private void rabbitsAct() {
		ArrayList<Rabbit> newbornRabbits = new ArrayList<Rabbit>();
		for (int i=rabbits.size()-1; i>=0; i--) {
			Rabbit rabbit = rabbits.get(i);
			Patch rabbitPatch = patches.get(rabbit.getCoordinate());
			// rabbits reproduce when they have excess energy
			if (rabbit.getEnergyLevel() >= Rabbit.REPRODUCE_REQUIREMENT) {
				newbornRabbits.add(rabbit.reproduce());
			// rabbits eat when there's daisy
			} else if (rabbitPatch.hasDaisy()){
				//System.out.println("Rabbit ate daisy.");
				rabbit.eat(rabbitPatch);
			// rabbits move in search of daisy
			} else {
				rabbit.move();
			}

			// rabbits die after running out of energy
			if (rabbit.getEnergyLevel() == 0) {
				System.out.println("Rabbit dies.");
				rabbits.remove(i);
				World.numRabbits -= 1;
			}
		}

		for (Rabbit newborn: newbornRabbits) {
			//System.out.println("Adding newborn baby.");
			this.rabbits.add(newborn);
		}
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
		ArrayList<Coordinate> neighbourCoordinates = coordinate.generateNeighbours();
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
			seedThreshold = ((0.1457 * patch.getTemperature()) - (0.0032 * (Math.pow(patch.getTemperature(), 2))) - 0.6443);
			Random rand = new Random();
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

				// DEBUG USE: checking if neighbours in list have daisy
				/* for (int i=0; i<patchNeighbours.size(); i++) {
				//	System.out.println("Has daisy: " + patchNeighbours.get(i).hasDaisy());
				}
				*/

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

	private void recordData() {
		writer.recordData(run, ticks, globalTemp, numWhites, numBlacks, solarLuminosity,
				startPercentBlack, startPercentWhite, blackAlbedo, whiteAlbedo,
				surfaceAlbedo);
	}

	public void writeToFile(String fileName) {
		writer.writeToFile(fileName);
	}

	@Override
	public String toString() {
		return String.format("Run: %d | Tick: %d | Global Temperature: %.2f | White Population: %d | "
				+ "Black Population: %d | Solar Luminosity: %.3f | Total Population: %d ", run,
				ticks, globalTemp, numWhites, numBlacks, solarLuminosity, numWhites + numBlacks);
	}

}
