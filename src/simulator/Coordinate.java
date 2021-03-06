package simulator;

import java.util.ArrayList;

/*
 * Represents the coordinates in DaisyWorld
 */
public class Coordinate {
	private int xcor;
	private int ycor;
	
	public Coordinate(int xcor, int ycor) {
		this.xcor = xcor;
		this.ycor = ycor;
	}
	
	// overriding equals and hashCode to make HashMap recognize different coordinate
	// objects with same values as equals
	@Override
    public boolean equals(Object o) {
        if (this == o) return true; // same object return true
        if (!(o instanceof Coordinate)) return false; // different class return false
        Coordinate coordinate = (Coordinate) o;
        return xcor == coordinate.xcor && ycor == coordinate.ycor; // equal values return true
    }

    @Override
    public int hashCode() {
        int result = 29 * xcor + ycor;
        return result;
    }
	
	public int getX() {
		return this.xcor;
	}
	
	public int getY() {
		return this.ycor;
	}
	
	// check if coordinate is out of map
	public boolean outOfGrid() {
		if (this.xcor < Params.xStart || this.xcor > Params.xEnd
				|| this.ycor < Params.yStart || this.ycor > Params.yEnd) {
			return true;
		}
		return false;
	}
	
	// Adjust the coordinate so that the grid wraps around
	private void adjustCoordinate(Coordinate coords) {
		if(coords.getX() > Params.xEnd)
			coords.xcor -= 29;
		
		if(coords.getX() < Params.xStart)
			coords.xcor += 29;
		
		if(coords.getY() > Params.yEnd)
			coords.ycor -= 29;
		
		if(coords.getY() < Params.yStart)
			coords.ycor += 29;
	}
	
	// generate neighbours of this coordindate
	public ArrayList<Coordinate> generateNeighbours(int radius){
		ArrayList<Coordinate> neighbours = new ArrayList<Coordinate>();
		for (int x=-radius; x<=radius; x++) {
			for (int y=-radius; y<=radius; y++) {
				// Skip the original coordinate
				if(x == 0 && y == 0) continue;
				
				Coordinate neighbour = new Coordinate(this.xcor+x, this.ycor+y);
				adjustCoordinate(neighbour);
				
				neighbours.add(neighbour);
			}
		}
		return neighbours;
	}
}
