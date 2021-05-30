package simulator;

/*
 * Represents the patches in DaisyWorld which may contain daisies
 */
public class Patch {
	private double temperature;
	private Daisy daisy;
	private Coordinate coordinate;
	
	public Patch(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	//set-as-black & set-as-white
	public void sproutDaisy(Daisy.Color color) { 
		this.daisy = new Daisy(0, color);
		if (color == Daisy.Color.BLACK) {
			World.numBlacks += 1;
		} else {
			World.numWhites += 1;
		}
	}
	
	// calc-temperature (for a single patch)
	public void calcTemperature(){ 
		double absorbedLuminosity = 0;
		double localHeating = 0;
		
		// formulas are all taken from Netlogo
		if (!this.hasDaisy()) {
			absorbedLuminosity = ((1 - World.getInstance().surfaceAlbedo) 
					* World.getInstance().solarLuminosity);
		} else {
			absorbedLuminosity = ((1 - daisy.getAlbedo()) 
					* World.getInstance().solarLuminosity);
		}
		
		if (absorbedLuminosity > 0) {
			localHeating = 72 * Math.log(absorbedLuminosity) + 80;
		} else {
			localHeating = 80;
		}
		temperature = (temperature + localHeating) / 2;
	}
	
	public Coordinate getCoordinate() {
		return this.coordinate;
	}
	
	public Daisy getDaisy() {
		return this.daisy;
	}
	
	public void setDaisy(Daisy daisy) {
		this.daisy = daisy;
	}
	
	// kill of daisy in patch
	public void daisyDies() {
		if (this.daisy.getColor() == Daisy.Color.BLACK) {
			World.numBlacks -= 1;
		} else {
			World.numWhites -= 1;
		}
		this.daisy = null;
		
	}
	
	// changing the temperature of patch
	public void addToTemperature(double add) {
		this.temperature += add;
	}
	
	public double getTemperature() {
		return this.temperature;
	}
	
	public void setTemperature(double temp) {
		this.temperature = temp;
	}
	
	public boolean hasDaisy() {
		return daisy != null;
	}
}
