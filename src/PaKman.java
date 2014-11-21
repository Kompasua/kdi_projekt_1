// PaKman.java
// Simple PaKman implementation
//import PaKActor;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.GGKeyListener;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;

public class PaKman extends GameGrid implements GGKeyListener
{
    protected PaKActor pacActor;
    private Ghost pinky;
    private Level theLevel;

    public PaKman() {
        super(30, 33, 20, true);    // Need to set the winsize, because it cannot be changed.
        pacActor = new PaKActor(this);
        setSimulationPeriod(100);
        setTitle("PaKman");
        addKeyListener(this);

        setupLevel(new Level());

        // Show and activate the game window
        show();
        activate();
    }

    
    public void reset() {
        removeAllActors();
        setupLevel(new Level());
    }
    
    
    /**
     * Return PaKman's location.
     * @returns pakman's location
     */
    public Location wherePakman() {
        return pacActor.getLocation();
    }

    
    /**
     * Return the current level.
     * @return the current level
     */
    public Level getLevel() {
        return theLevel;
    }       
    

    /**
     * Setup the given level:
     * <ul>
     *  <li> draw the maze; </li>
     *  <li> put pakman at its starting position; </li>
     *  <li> create the ghost(s) at their starting positions; </li>
     *  <li> initializes internae</li>
     * </ul>
     */
    public void setupLevel(Level level) {
        theLevel = level;
        setNbHorzCells(level.getSize().x);
        setNbVertCells(level.getSize().y);
        level.drawLevel(this);
        addActor(pacActor, level.getPakmanStart());
        addActor(pinky = new Ghost(this), level.getGhostStart());
        // pakman acts last, to ensure correct collision detection
        setActOrder(Ghost.class, PaKActor.class);
    }

    
    /**
     * Check whether pakman and a ghost collide.
     * If so, call gameover().
     */
    public void checkCollision() {
        if (pacActor.getLocation().equals(pinky.getLocation())) {
            addActor(new Actor("sprites/explosion3.gif"), pacActor.getLocation());
            gameOver();
        }
    }
    
    
    /**
     * Display a game over message and pause the game.
     */
    private void gameOver() {
        GGBackground bg = getBg();
        bg.setPaintColor(Color.red);
        bg.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        bg.drawText("Game Over",
            new Point(toPoint(new Location(6, 15))));
        refresh();
        doPause();
    }
    
    
    public static void main(String[] args) {
        new PaKman();
    }

    //===================================== Actor collision listener methods ======================
    /* Not used: GameGrid collision detector doesn't detect collision when two actor move
       simultaneously and cross each other.
    public int collide(Actor actor1, Actor actor2) {
        Location loc = pacActor.getLocation();
        //pacActor.removeSelf();
        addActor(new Actor("sprites/explosion3.gif"), loc);
        gameOver();
        return 0;
    }*/
    
    //========================================== Key listener methods =============================
    
    /** KeyListener method (no function for us) */
    public boolean keyPressed(KeyEvent event) {
        return false;
    }

    /**
     * KeyListener method.
     * Here we act on the (press and) release of keys.
     * Currently implemented:
     * f toggles hunting/fleeing mode.
     */
    public boolean keyReleased(KeyEvent event) {
        switch (event.getKeyChar()) {
            case 'f': pinky.toggleHunting(); return true;
        }
        
        return false;
    }
}
