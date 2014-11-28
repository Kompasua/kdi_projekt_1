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
    private Location last = null; //Previous location of ghost
    //Current direction of ghost movement. Based on previous and current locations
    private double direction = 0;
    Random generator = new Random();
    
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
    	nextStep();
    	
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
    	Location next = getLocation().getNeighbourLocation(direction);
    	//Debug block
    	System.out.println("Now: "+getLocation());
    	System.out.println("Next: "+next.toString());
    	
    	if (next != null && canMove(next)) {
    		if (lookAround().size() > 2){
    			System.out.println(lookAround().toString()); //DELETE
    			goRandom();
    		}else{
    			last = getLocation();
    			setLocation(next);
    			//Actually must be the same, because we moving in the same direction. Delete after prove. 
    			direction = last.getDirectionTo(getLocation()); 
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
    	System.out.println("Random"); //DELETE
    	 
    	int i = generator.nextInt(lookAround().size());
    	last = getLocation();
    	setLocation((Location) lookAround().get(i));
    	direction = last.getDirectionTo(getLocation()); 
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
