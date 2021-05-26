
public class Patch {
	private double temperature;
	private Daisy daisy;

	public void sproutDaisy(Daisy.Color color) { //set-as-black and set-as-white
		this.daisy = new Daisy(0, color);
	}
	
	
	public void calcTemperature(){ // calc-temperature
		double absorbedLuminosity = 0;
		double localHeating = 0;
		
		if (!this.hasDaisy()) {
			absorbedLuminosity = ((1 - World.getInstance().surfaceAlbedo) * World.getInstance().solarLuminosity);
		} else {
			absorbedLuminosity = ((1 - daisy.getAlbedo()) * World.getInstance().solarLuminosity);
		}
		
		if (absorbedLuminosity > 0) {
			localHeating = 72 * Math.log(absorbedLuminosity + 80);
		} else {
			localHeating = 80;
		}
		
		temperature = temperature + localHeating / 2;
	}
	
	public Daisy getDaisy() {
		return this.daisy;
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
	
	//check if patch contains daisy
	public boolean hasDaisy() {
		if (daisy == null) {
			return false;
		}
		return true; 
	}
}
