
public class Coordinate {
	private int xcor;
	private int ycor;
	
	public Coordinate(int xcor, int ycor) {
		this.xcor = xcor;
		this.ycor = ycor;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true; // same object return true
        if (!(o instanceof Coordinate)) return false; // different class return false
        Coordinate coordinate = (Coordinate) o;
        return xcor == coordinate.xcor && ycor == coordinate.ycor; // equal values return true
    }

    @Override
    public int hashCode() {
        int result = xcor;
        result = 31 * result + ycor;
        return result;
    }
	
	public int getX() {
		return this.xcor;
	}
	
	public int getY() {
		return this.ycor;
	}
}
