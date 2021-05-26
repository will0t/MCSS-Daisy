import java.util.ArrayList;

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
	
	public boolean outOfGrid() {
		if (this.xcor < Params.xStart || this.xcor > Params.xEnd
				|| this.ycor < Params.yStart || this.ycor > Params.yEnd) {
			return true;
		}
		return false;
	}
	
	public ArrayList<Coordinate> generateNeighbours(){
		ArrayList<Coordinate> neighbours = new ArrayList<Coordinate>();
		for (int x=-1; x<=1; x++) {
			for (int y=-1; y<=1; y++) {
				Coordinate neighbour = new Coordinate(this.xcor+x, this.ycor+y);
				// check if out of grid and if coordinate equal to centre
				if (!neighbour.outOfGrid() && !(x == 0 && y == 0)) {
					neighbours.add(neighbour);
				}
			}
		}
		return neighbours;
	}
}
