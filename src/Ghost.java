// Ghost.java
// Used for PaKman

import ch.aplu.jgamegrid.*;
import java.awt.Color;
import java.util.*;

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
    
        
        // When moving westwards, mirror the sprite so it looks in the proper direction
        if (getDirection() > 150 && getDirection() < 210)
            setHorzMirror(false);
        else
            setHorzMirror(true);
    }
    
    
    /**
     * Toggle hunting/fleeing mode.
     */
    public void toggleHunting() {
        // Toggle sprite
        show(1 - getIdVisible());
    }
 
}