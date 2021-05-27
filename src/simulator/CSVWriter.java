package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CSVWriter {
	private ArrayList<String> data = new ArrayList<>();
	
	public CSVWriter() {
	}
	
	public void recordData(int run, int tick, double temperature, 
			int whiteNum, int blackNum, double luminosity, double blackPercent, 
			double whitePercent, double blackAlbedo, double whiteAlbedo, double surfaceAlbedo) {
		String line = String.format("%d,%d,%.2f,%d,%d,%.3f,%d,%.2f,%.2f,%.2f,%.2f,%.2f", 
				run, tick, temperature, whiteNum, blackNum, luminosity, whiteNum + blackNum, 
				whitePercent, blackPercent, whiteAlbedo, blackAlbedo, surfaceAlbedo);
		data.add(line);
	}
	
	public void writeToFile(String fileName) {
		try(PrintWriter writer = new PrintWriter(new File(fileName))){
			writer.println("run,tick,temperature,white_daisy_num,"
					+ "black_daisy_num,luminosity,population,"
					+ "start_white_%,start_black_%,white_albedo,"
					+ "black_albedo,surface_albedo");
			for(int i = 0; i < data.size(); i++) {
				writer.println(data.get(i));
			}
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
