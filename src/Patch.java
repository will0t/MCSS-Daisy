
public class Patch {
	private double temperature;
	private Daisy daisy;
	private World world = World.getInstance();
	
	public void sproutBlackDaisy() { //set-as-black
		this.daisy = new Daisy(0, Daisy.Color.BLACK);
	}
	
	public void sproutWhiteDaisy() { //set-as-white
		this.daisy = new Daisy(0, Daisy.Color.WHITE);
	}
	
	
	public void calcTemperature(){ // calc-temperature in NL
		double absorbedLuminosity = 0;
		double localHeating = 0;
		
		if (!this.hasDaisy()) {
			absorbedLuminosity = ((1 - world.surfaceAlbedo) * world.solarLuminosity);
		} else {
			absorbedLuminosity = ((1 - daisy.getAlbedo()) * world.solarLuminosity);
		}
		
		if (absorbedLuminosity > 0) {
			localHeating = 72 * Math.log(absorbedLuminosity + 80);
		} else {
			localHeating = 80;
		}
		
		temperature = temperature + localHeating / 2;
	}
	
	private boolean hasDaisy() {
		return true; //check if patch contains daisy
	}
}
