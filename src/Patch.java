public class Patch {
	private double temperature;
	private Daisy daisy;
	
	//set-as-black & set-as-white
	public void sproutDaisy(Daisy.Color color) { 
		this.daisy = new Daisy(0, color);
		if (color == Daisy.Color.BLACK) {
			//System.out.println("Adding to numBlacks.");
			World.numBlacks += 1;
		} else {
			World.numWhites += 1;
			//System.out.println("Adding to numWhites.");
		}
	}
	
	// calc-temperature (for a single patch)
	public void calcTemperature(){ 
		double absorbedLuminosity = 0;
		double localHeating = 0;
		
		if (!this.hasDaisy()) {
			absorbedLuminosity = ((1 - World.getInstance().surfaceAlbedo) * World.getInstance().solarLuminosity);
		} else {
			absorbedLuminosity = ((1 - daisy.getAlbedo()) * World.getInstance().solarLuminosity);
		}
		
		if (absorbedLuminosity > 0) {
			localHeating = 72 * Math.log(absorbedLuminosity) + 80;
		} else {
			localHeating = 80;
		}
		temperature = (temperature + localHeating) / 2;
	}
	
	public Daisy getDaisy() {
		return this.daisy;
	}
	
	public void setDaisy(Daisy daisy) {
		this.daisy = daisy;
	}
	
	public void daisyDies() {
		if (this.daisy.getColor() == Daisy.Color.BLACK) {
			//System.out.println("Deducting from numBlacks.");
			World.numBlacks -= 1;
		} else {
			World.numWhites -= 1;
			//System.out.println("Deducting from numWhites.");
		}
		this.daisy = null;
		
	}
	
	public void addToTemperature(double add) {
		this.temperature = this.temperature + add;
	}
	
	public double getTemperature() {
		return this.temperature;
	}
	
	public void setTemperature(double temp) {
		this.temperature = temp;
	}
	
	public boolean hasDaisy() {
		if (daisy == null) {
			return false;
		}
		return true; 
	}
}
