// Ghost.java
// Used for PaKman

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.Location.CompassDirection;

public class Ghost extends Actor
{
    private PaKman pakman;
    private Location last;
    boolean first = true;
    
    public Ghost(PaKman pakman) {
        super(false, "sprites/ghost.gif", 2);
        this.pakman = pakman;
        reset();
    }
    
    /**
     * Called when the level is initialized or reset.
     */
    public void reset() {

    }
    
    /**
     * Called once in every iteration of the game loop to calculate the actions
     * of this ghost.
     */
    public void act() {
    	if (first == true){
    		goRandom();
    		first = false;
    	}else{
    		nextStep();
    	}
    	// When moving westwards, mirror the sprite so it looks in the proper direction
        if (getDirection() > 150 && getDirection() < 210)
            setHorzMirror(false);
        else
            setHorzMirror(true);
        //If end of the map and move is impossible, then turn over
        if (!isMoveValid())
            turn(180);
    }
    
    /**
     * Check if cell is available for move of ghost.  
     * @param location where ghost would like to move
     * @return true if sell is PASSAGE, false if WALL
     */
    private boolean canMove(Location location) {
    	Tile cell = pakman.getLevel().getTile(location);
    	if (cell == Tile.PASSAGE){
    		return true;
    	}
    	return false;
    }
    
    private void nextStep(){
    	Location next = getLocation().getNeighbourLocation(last.getDirectionTo(getLocation()));
    	//Debug block
    	System.out.println("Last: "+last.toString());
    	System.out.println("Now: "+getLocation());
    	System.out.println("Next: "+next.toString());
    	
    	if (next != null && canMove(next)) {
    		if (lookAround().size() > 2){
    			System.out.println(lookAround().toString()); //DELETE
    			goRandom();
    		}else{
    			setLocation(next);
    	        gameGrid.refresh();
    	        System.out.println("NO random"); //DELETE
    		}
        }else{
        	goRandom();
        }
    }
    
    private ArrayList<Location> lookAround(){
    	Location east = getLocation().getNeighbourLocation(Location.EAST);
    	Location west = getLocation().getNeighbourLocation(Location.WEST);
    	Location north = getLocation().getNeighbourLocation(Location.NORTH);
    	Location south = getLocation().getNeighbourLocation(Location.SOUTH);
    	
    	ArrayList<Location> directions = new ArrayList<Location>();
    	if (canMove(east))
    		directions.add(east);
    	if (canMove(west))
    		directions.add(west);
    	if (canMove(north))
    		directions.add(north);
    	if (canMove(south))
    		directions.add(south);
    	return directions;
    }
    
    private void goRandom(){
    	System.out.println("Random");
    	
    	Random generator = new Random(); 
    	int i = generator.nextInt(lookAround().size());
    	setLocation((Location) lookAround().get(i));
    	last = getLocation();
    	gameGrid.refresh();
    }
    
    /**
     * Toggle hunting/fleeing mode.
     */
    public void toggleHunting() {
        // Toggle sprite
        show(1 - getIdVisible());
    }
 
}
