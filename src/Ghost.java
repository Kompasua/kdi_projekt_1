/**
 * Ghost.java
 * Used for PaKman
 * @author Anton Bubnov
 * @version 01.12.2014
 */

import java.util.ArrayList;
import java.util.Random;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

public class Ghost extends Actor {
    private PaKman pakman;
    private Location last = null; // Previous location of ghost.
    private Location locsize = null; //Used to get width and height of maze.
    private int random = 0; //Counter for Ghost random steps.
    private int mode = 0; //Hunting\fleeing mode. Can be 0 or 180 respectively.
    // Current direction of ghost movement. 
    // Based on previous and current locations.
    private double direction = 0;

    public Ghost(PaKman pakman) {
        super(false, "sprites/ghost.gif", 2);
        this.pakman = pakman;
        //Get current level to get size of maze
        Level level = pakman.getLevel();
        locsize = level.getSize(); //Get size of level
        reset();
    }
    
    /**
     * @return pakman
     */
    public PaKman getPakman() {
        return pakman;
    }

    /**
     * Set pakman. Pakman must not be NULL.
     * @param pakman 
     */
    public void setPakman(PaKman pakman) {
        if (pakman == null){
            throw new IllegalArgumentException("Argument must not be NULL!");
        }else{
            this.pakman = pakman;
        }
    }

    /**
     * @return last location of pakman
     */
    public Location getLast() {
        return last;
    }

    /**
     * Set last location.
     * Location must not be NULL.
     * @param last location
     */
    public void setLast(Location last) {
        if (last == null){
            throw new IllegalArgumentException("Argument must not be NULL!");
        }else{
            this.last = last;
        }
    }

    /**
     * @return current number of random steps.
     */
    public int getRandom() {
        return random;
    }

    /**
     * Set number of random steps.
     * Must be a positive integer or 0.
     * @param random
     */
    public void setRandom(int random) {
        if (random < 0){
            throw new IllegalArgumentException("Argument must be "
                    + "positive or 0.");
        }else{
            this.random = random;
        }
    }

    /**
     * @return current mode (0 for hunting, 180 for fleeing)
     */
    public int getMode() {
        return mode;
    }

    /**
     * Set hunting or fleeing mode
     * @param mode (should be 0 or 180)
     */
    public void setMode(int mode) {
        if (mode!= 0 || mode !=180){
            throw new IllegalArgumentException("Argument must be 0 if "
                    + "hunting mode and 180 if fleeing mode.");
        }else{
            this.mode = mode;
        }
    }

    /**
     * @return current direction of pakman movement
     */
    public double getDirection() {
        return direction;
    }

    /**
     * @param direction of pakman movement
     */
    public void setDirection(double direction) {
        if ( (direction > 360 || direction < 0) && direction % 90 != 0 ){
            throw new IllegalArgumentException("Argument must be "
                    + "0, 90, 180 0r 270");
        }else{
            this.direction = direction;
        }
    }

    /**
     * Called when the level is initialized or reset.
     * Set default values.
     */
    public void reset() {
        random = 0; //Discard random mode.
        mode = 0; //Set hunting mode.
        direction = 0; //Set default direction.
    }

    /**
     * Called once in every iteration of the game loop to 
     * calculate the actions of this ghost.
     */
    public void act() {
        nextStep(); //Ghost make one step.
        // When moving westwards, mirror the sprite so it looks in the proper
        // direction
        if (direction > 150 && direction < 210)
            setHorzMirror(false);
        else
            setHorzMirror(true);
    }

    /**
     * Check if cell is available for move of ghost.
     * @param direction to near location where ghost would like to move
     * @return true if sell is PASSAGE, else false.
     */
    private boolean canMove(double dir) {
        Location location = getLocation().getNeighbourLocation(
                stabilizeDir(dir));
        // Check if this location not out of maze
        if (location.getY() < locsize.getY()
                && location.getX() < locsize.getX() && location.getY() >= 0
                && location.getX() >= 0) {
            Tile cell = pakman.getLevel().getTile(location);
            if (cell == Tile.PASSAGE) {
                return true;
            }
        }
        return false;
    }

    /**
     * If possible and if we must'nt make random step,
     * then make step in near location (by direction).
     * If movement in this direction is impossible then call goRandom() method.
     * @param _direction for next step of ghost
     */
    private void makeStep(double _direction) {
        //Location where ghost would like to move.
        Location next = getLocation().getNeighbourLocation(
                stabilizeDir(_direction));
        if (canMove(stabilizeDir(_direction)) && random == 0) {
            last = getLocation(); //Save current location
            setLocation(next);
            // Seems it the same, but it is not.
            direction = last.getDirectionTo(getLocation());
            gameGrid.refresh();
        } else {
            goRandom();
        }
    }

    /**
     * Make next step depending on pakman location (east, west, north, south). 
     * If pakman location is south-west, east-west and so on, then make
     * step in the same direction. If no one of described variants 
     * is possible, then make one random step.
     * Mode variable is used to reverse direction if in fleeing mode. 
     */
    private void nextStep() {
        //Direction to pakman location in this step.
        double pakmanLoc = getLocation().getDirectionTo(pakman.wherePakman()); 
        
        if (pakmanLoc > 45 && pakmanLoc < 135) {
            makeStep(90 - mode);
            return;
        }
        if (pakmanLoc > 135 && pakmanLoc < 225) {
            makeStep(180 - mode);
            return;
        }
        if (pakmanLoc > 225 && pakmanLoc < 315) {
            makeStep(270 - mode);
            return;
        }
        if ((pakmanLoc > 315 && pakmanLoc < 360)
                || (pakmanLoc > 0 && pakmanLoc < 45)) {
            makeStep(0 - mode);
            return;
        }
        if (canMove(direction)) {
            makeStep(direction);
            return;
        }
        //Set only one random step if we're not in 10 
        // random steps mode.
        if (random == 0){
            random = 1;
        }
        goRandom();
    }

    /**
     * Stabilize direction of movement for Location class methods. 
     * Check if direction in range from 0 to 359.
     * If not, then change it so to be in this range.
     * Else return same direction.
     * @param direction of pakman movement (not stabilized)
     * @return direction of pakman movement (stabilized)
     */
    private double stabilizeDir(double direction) {
        if (direction < 0)
            return 360 - direction * (-1);
        if (direction == 360)
            return 0;
        if (direction > 360)
            return direction - 360;
        return direction;
    }

    /**
     * Collect possible directions for move.
     * First collect directions in straight, right and left
     * directions. If move in those directions is not possible
     * then turn over (go back).
     * @return Array with possible directions
     */
    private ArrayList<Double> getNextSteps() {
        ArrayList<Double> directions = new ArrayList<Double>();
        if (canMove(direction)) {
            directions.add(direction);
        }
        if (canMove(stabilizeDir(direction + 90))) {
            directions.add(stabilizeDir(direction + 90));
        }
        if (canMove(stabilizeDir(direction - 90))) {
            directions.add(stabilizeDir(direction - 90));
        }
        if (directions.size() == 0 && canMove(stabilizeDir(direction - 180))) {
            directions.add(stabilizeDir(direction - 180));
            return directions;
        }
        return directions;
    }

    /**
     * Make random step in available directions (get them from
     * lookAround method).
     * Direction of step depends from random.
     * If we have 3 possible directions, then choose 
     * one of them with chance 25% for each of right or 
     * left turn and 50% for straight move.
     * Else go in available two or one direction with 
     * chance 50% or 100% respectively. 
     */
    private void goRandom() {
        //If we're not in random mode, then "activate" it. 
        if (random == 0) {
            random = 10;
        } else {
            random--; //Decrease random steps counter
            //Get available for move directions.
            ArrayList<Double> directions = getNextSteps();
            //Store possible directions number.
            int variants = directions.size();
            Random generator = new Random();
            last = getLocation(); //Save current location.

            //Choose random step move.
            if (variants == 3) {
                /*
                 * Generate random number in range from 0 to 
                 * possible directions number plus one.
                 * Plus one is used to increase chance of moving 
                 * straight to 50%.
                 */
                int i = generator.nextInt(variants + 1);
                switch (i) {
                case 0:
                    //Second element from Array - right
                    setLocation(getLocation().getNeighbourLocation(
                            directions.get(1)));
                    break;
                case 1:
                    //Third element from Array - left
                    setLocation(getLocation().getNeighbourLocation(
                            directions.get(2)));
                    break;
                default:
                    //First element from Array - straight
                    setLocation(getLocation().getNeighbourLocation(
                            directions.get(0)));
                }
            } else {
                /*
                 * If we have only one possible direction, then go in it. 
                 * If we have two, then generate random from 0 to 1 to 
                 * make chance of right\left 50% respectively.
                 */
                int i = generator.nextInt(variants);
                switch (i) {
                case 0:
                    //First element from Array - right, if left if possible,
                    //else left.
                    setLocation(getLocation().getNeighbourLocation(
                            directions.get(0)));
                    break;
                case 1:
                    //Second element from Array - left, if right if possible,
                    //else null.
                    setLocation(getLocation().getNeighbourLocation(
                            directions.get(1)));
                }
            }
            //Get direction of current move.
            direction = last.getDirectionTo(getLocation());
            gameGrid.refresh();
        }
    }

    /**
     * Toggle hunting/fleeing mode.
     */
    public void toggleHunting() {
        // Change hunting/fleeing mode
        if (mode == 0)
            mode = 180;
        else
            mode = 0;
        //Cancel random mode.
        random = 0;
        // Toggle sprite
        show(1 - getIdVisible());
    }

}
//EOF