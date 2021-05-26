
public class Daisy {
	public enum Color{
		WHITE,
		BLACK
	}
	
	private int age; // Age
	private final Color color; // color of daisy
	private final double albedo;
	
	public Daisy(int age, Color color) {
		this.age = age;
		this.color = color;
		if (color == Color.WHITE) {
			albedo = World.getInstance().whiteAlbedo;
		} else {
			albedo = World.getInstance().blackAlbedo;
		}
	}
	
	public void checkSurvivability() {
		// check-survivability
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public Color getColor() {
		return color;
	}
	
	public double getAlbedo() {
		return albedo;
	}
}
