// Ghost.java
// Used for PaKman

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
		Level level = pakman.getLevel(); //Current level to get size of maze
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
	 * @return true if sell is PASSAGE, false if WALL
	 */
	private boolean canMove(double dir) {
		Location location = getLocation().getNeighbourLocation(
				dirStabilizer(dir));
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
	 * If move in this direction is impossible then call goRandom() method.
	 * @param _direction for next step of ghost
	 */
	private void makeStep(double _direction) {
		//Location where ghost would like to move.
		Location next = getLocation().getNeighbourLocation(dirStabilizer(_direction));
		if (canMove(dirStabilizer(_direction)) && random == 0) {
			last = getLocation();
			setLocation(next);
			// Seems it the same, but it is not.
			direction = last.getDirectionTo(getLocation());
			gameGrid.refresh();
		} else {
			goRandom();
		}
	}
	
	/**
	 * Make next depending on pakman location (east,west,north, south). 
	 * If pakman location is south-west, east-west and so on, then make
	 * step in the same direction. 
	 * If no one of described variants is possible, 
	 * then make one random step.
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
	 * When not, then change it so to be in this range.
	 * Else return same direction.
	 * @param direction of pakman movement (not stabilized)
	 * @return direction of pakman movement (stabilized)
	 */
	private double dirStabilizer(double direction) {
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
		if (canMove(dirStabilizer(direction + 90))) {
			directions.add(dirStabilizer(direction + 90));
		}
		if (canMove(dirStabilizer(direction - 90))) {
			directions.add(dirStabilizer(direction - 90));
		}
		if (directions.size() == 0 && canMove(dirStabilizer(direction - 180))) {
			directions.add(dirStabilizer(direction - 180));
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
			random--;
			//Get available for move directions.
			ArrayList<Double> directions = getNextSteps();
			//Store possible directions number.
			int variants = directions.size();
			Random generator = new Random();
			int i = generator.nextInt(variants);
			last = getLocation(); //Save current location.

			//Choose random step move.
			if (variants == 3) {
				i = generator.nextInt(variants + 1);
				switch (i) {
				case 0:
					setLocation(getLocation().getNeighbourLocation(
							directions.get(1)));
					break;
				case 1:
					setLocation(getLocation().getNeighbourLocation(
							directions.get(2)));
					break;
				default:
					setLocation(getLocation().getNeighbourLocation(
							directions.get(0)));
				}
			} else {
				switch (i) {
				case 0:
					setLocation(getLocation().getNeighbourLocation(
							directions.get(0)));
					break;
				case 1:
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
		// Toggle sprite
		show(1 - getIdVisible());
	}

}
//EOF