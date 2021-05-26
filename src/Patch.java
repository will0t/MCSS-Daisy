
public class Patch {
	private double temperature;
	private Daisy daisy;

	public void sproutBlackDaisy() { //set-as-black
		this.daisy = new Daisy(0, Daisy.Color.BLACK);
	}
	
	public void sproutWhiteDaisy() { //set-as-white
		this.daisy = new Daisy(0, Daisy.Color.WHITE);
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
	
	//check if patch contains daisy
	private boolean hasDaisy() {
		if (daisy == null) {
			return false;
		}
		return true; 
	}
}
