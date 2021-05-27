package simulator;


public class Options {
	enum Scenario {
		MAINTAIN,
		RAMP_UP_DOWN,
		LOW_LUMINOSITY,
		OUR_LUMINOSITY,
		HIGH_LUMINOSITY,
	}
	
	Scenario scenario;
	int startWhitePercent;
	int startBlackPercent;
	double whiteAlbedo;
	double blackAlbedo;
	double surfaceAlbedo;
	double luminosity;
	
	public Options() {
		scenario = Scenario.MAINTAIN;
		startWhitePercent = 20;
		startBlackPercent = 20;
		whiteAlbedo = 0.75;
		blackAlbedo = 0.25;
		surfaceAlbedo = 0.4;
		luminosity = 1.0;
	}
}
