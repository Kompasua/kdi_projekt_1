// Ghost.java
// Used for PaKman

import java.awt.Color;
import java.awt.Point;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

public class Ghost extends Actor
{
    private PaKman pakman;

    public Ghost(PaKman pakman)
    {
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
    public void act()
    {	 
    	nextStep();
    	// When moving westwards, mirror the sprite so it looks in the proper direction
        if (getDirection() > 150 && getDirection() < 210)
            setHorzMirror(false);
        else
            setHorzMirror(true);
        if (!isMoveValid())
            turn(180);
        if (isInGrid());
        	//move();
    }
    private boolean canMove(Location location)
    {
      Color c = getBackground().getColor(location);
      return (!c.equals(Color.gray));
    }
    
    private void nextStep(){
    	Location next = getLocation().getNeighbourLocation(Location.EAST);
    	if (next != null && canMove(next))
        {
          setLocation(next);
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
