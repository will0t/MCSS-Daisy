package simulator;

public class Patch {

	enum PatchType {
		WHITE_DAISY,
		BLACK_DAISY,
		EMPTY,
	}
	
	public PatchType patchType;
	public Options options;
	public double temperature;
	public int maxAge;
	public int x;
	public int y;
	
	public Patch(Options options, int x, int y) {
		this.options = options;
		this.patchType = PatchType.EMPTY;
		this.temperature = 0.0;
		this.x = x;
		this.y = y;
	}
	
	public void calculateTemperature() {
		double absorbedLuminosity = 0.0;
		double localHeating = 0.0;
		
		switch (patchType) {
		case EMPTY:
			absorbedLuminosity = (1 - options.surfaceAlbedo) * options.luminosity;
			break;
		case WHITE_DAISY:
			absorbedLuminosity = (1 - options.whiteAlbedo) * options.luminosity;
			break;
		case BLACK_DAISY:
			absorbedLuminosity = (1 - options.blackAlbedo) * options.luminosity;
			break;
		default:
			break;
		}
		
		
		
	}
	
	public void emptyPatch() {
		this.patchType = PatchType.EMPTY;
	}
	
	public void plantWhite(int maxAge) {
		this.maxAge = maxAge;
		this.patchType = PatchType.WHITE_DAISY;
	}
	
	public void plantBlack(int maxAge) {
		this.maxAge = maxAge;
		this.patchType = PatchType.BLACK_DAISY;
	}
	
	public boolean isEmpty() {
		return patchType == PatchType.EMPTY;
	}
}
