package simulator;
import java.util.ArrayList;
import java.util.Random;

import simulator.Patch.PatchType;

public class World {
	private static final Random random = new Random();
	public static final int WORLD_SIZE = 29;
	
	private Patch[][] grid;
	private double globalTemp;
	private int ticks;
	
	public World(Options options) {
		globalTemp = 0.0;
		ticks = 0;
		
		grid = new Patch[WORLD_SIZE][WORLD_SIZE];
		for(int y = 0; y < WORLD_SIZE ; y++) {
			for(int x = 0; x < WORLD_SIZE ; x++) {
				grid[x][y] = new Patch(options, x, y);
			}
		}
		int total_patches = WORLD_SIZE * WORLD_SIZE;
		int whiteDaisyNum = (int)(total_patches*(options.startWhitePercent/100.0f));
		int blackDaisyNum = (int)(total_patches*(options.startBlackPercent/100.0f));
		
		while(whiteDaisyNum > 0) {
			getRandomPatch().plantWhite(random.nextInt(25) + 1);
			whiteDaisyNum--;
		}
		
		while(blackDaisyNum > 0) {
			getRandomPatch().plantBlack(random.nextInt(25) + 1);
			blackDaisyNum--;
		}
	}
	
	public Patch getRandomPatch() {
		ArrayList<Patch> emptyPatches = new ArrayList<Patch>();
		
		for(int y = 0; y < WORLD_SIZE ; y++) {
			for(int x = 0; x < WORLD_SIZE ; x++) {
				if (grid[x][y].isEmpty())
					emptyPatches.add(grid[x][y]);
			}
		}
		
		return emptyPatches.get(random.nextInt(emptyPatches.size()));
	}

	private void calculateTemp() {
		
		
		for(int y = 0; y < WORLD_SIZE ; y++) {
			for(int x = 0; x < WORLD_SIZE ; x++) {
				grid[x][y].calculateTemperature();
			}
		}
	}
	
	public void step() {
		ticks++;
		
	}
	
	@Override
	public String toString() {
		String text = "";
		
		for(int y = 0; y < WORLD_SIZE ; y++) {
			for(int x = 0; x < WORLD_SIZE ; x++) {
				String typeTxt = " ";
				switch (grid[x][y].patchType) {
				case WHITE_DAISY:
					typeTxt = "W";
					break;
				case BLACK_DAISY:
					typeTxt = "B";
					break;
				case EMPTY:
					typeTxt = " ";
					break;
				default:
					break;
				}
				text += String.format("[%s]", typeTxt);
			}
			text += "\n";
		}
		
		text += "Current tick : " + ticks;
		
		return text;
	}
}
