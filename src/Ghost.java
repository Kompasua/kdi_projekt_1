// Ghost.java
// Used for PaKman

import java.util.ArrayList;
import java.util.Random;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

public class Ghost extends Actor
{
    private PaKman pakman;
    private Location last = null; //Previous location of ghost
    private Level level = null; 
    private Location locsize = null;
    private int random = 0;
    //Current direction of ghost movement. Based on previous and current locations
    private double direction = 0;
    Random generator = new Random();
    
    public Ghost(PaKman pakman) {
        super(false, "sprites/ghost.gif", 2);
        this.pakman = pakman;
        level = pakman.getLevel();
        locsize = level.getSize();
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
    	nextStepHunt();
    	// When moving westwards, mirror the sprite so it looks in the proper direction
    	if (direction > 150 && direction < 210)
            setHorzMirror(false);
        else
            setHorzMirror(true);
    }
    
    /**
     * Check if cell is available for move of ghost.  
     * @param location where ghost would like to move
     * @return true if sell is PASSAGE, false if WALL
     */
    private boolean canMove(Location location) {
    	//Check if this location not out of maze
    	if (location.getY() < locsize.getY() && location.getX() < locsize.getX() &&
    		location.getY() >=0 && location.getX() >= 0){
	    	Tile cell = pakman.getLevel().getTile(location);
	    	if (cell == Tile.PASSAGE){
	    		return true;
	    	}
    	}
    	return false;
    }
    
    private boolean canMove(double dir) {
    	Location location = getLocation().getNeighbourLocation(dir);
    	//Check if this location not out of maze
    	if (location.getY() < locsize.getY() && location.getX() < locsize.getX() &&
    		location.getY() >=0 && location.getX() >= 0){
	    	Tile cell = pakman.getLevel().getTile(location);
	    	if (cell == Tile.PASSAGE){
	    		return true;
	    	}
    	}
    	return false;
    }
    
    private void makeStep(double _direction){
    	Location next = getLocation().getNeighbourLocation(_direction);
		if (next != null && canMove(next) && random == 0) {
    		last = getLocation();
    		setLocation(next);
    	    gameGrid.refresh();
    	}else{
        	goRandom();
        }
    }
    
    private void nextStepHunt(){
    	double pakmanLoc = getLocation().getDirectionTo(pakman.wherePakman()); //Pakman location in this step 
    	if (pakmanLoc > 45 && pakmanLoc < 135){
    		makeStep(90);
    		return;
    	}
    	if (pakmanLoc > 135 && pakmanLoc < 225){
    		makeStep(180);
    		return;
    	}
    	if (pakmanLoc > 225 && pakmanLoc < 315){
    		makeStep(270);
    		return;
    	}
    	if ( (pakmanLoc > 315 && pakmanLoc < 360) || (pakmanLoc > 0 && pakmanLoc < 45) ){
    		makeStep(0);
    		return;
    	}
    	if (canMove(direction)){
    		makeStep(direction);
    		return;
    	}
    	if (random == 0)
	    	random = 1;
    	goRandom();
    }
        
    private double dirStabilizer(double direction){
    	if (direction < 0)
    		return 360 - direction*(-1);
    	if (direction == 360)
    		return 0;
    	return direction;
    }
    
    private ArrayList<Double> getNextSteps(){
    	ArrayList<Double> directions = new ArrayList<Double>();
    	if (canMove(direction)){
    		directions.add(direction);
    	}
    	if (canMove( dirStabilizer(direction+90) )){
    		directions.add(dirStabilizer(direction+90));
    	}
    	if (canMove( dirStabilizer(direction-90) )){
    		directions.add(dirStabilizer(direction-90));
    	}
    	if (directions.size() == 0 && canMove( dirStabilizer(direction-180) )){
    		directions.add( dirStabilizer(direction-180) );
    		return directions;
    	}
    	return directions;
    }
    
    private void goRandom(){
    	if (random == 0){
    		random = 10;
    	}else{
    		random--;
    		Location next = getLocation().getNeighbourLocation(direction);
    		Location right = getLocation().getNeighbourLocation(direction+90);
    		Location left = getLocation().getNeighbourLocation(direction-90);
    		ArrayList<Double> directions = getNextSteps();
    		int variants = directions.size();
    		int i = generator.nextInt(variants);
    		last = getLocation();
    		
    		if (variants == 3){
    			i = generator.nextInt(variants+1);
    			switch (i){
    			case 0: setLocation( getLocation().getNeighbourLocation(directions.get(1)) );
    			break;
    			case 1: setLocation( getLocation().getNeighbourLocation(directions.get(2)) );
    			break;
    			default: setLocation( getLocation().getNeighbourLocation(directions.get(0)) );
    			}
    		}else{
    			switch (i){
    			case 0: setLocation( getLocation().getNeighbourLocation(directions.get(0)) );
    			break;
    			case 1: setLocation( getLocation().getNeighbourLocation(directions.get(1)) );
    			}
    		}
	    	direction = last.getDirectionTo(getLocation()); 
	    	gameGrid.refresh();
    	}
    }
    
    /**
     * Toggle hunting/fleeing mode.
     */
    public void toggleHunting() {
        // Toggle sprite
        show(1 - getIdVisible());
    }
 
}
