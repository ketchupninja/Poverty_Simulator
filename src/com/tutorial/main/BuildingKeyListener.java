package com.tutorial.main;

import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;


public class BuildingKeyListener extends GameObject implements KeyListener {

	//The Building object that this listener belongs to.
	public ImportantBuilding parentBuilding;
	
	//Boolean on whether or not player is close enough to interact.
	public boolean inRadius = false;
	
	//Interaction radius.
	private final int RADIUS = 175;
	
	//Coordinates for drawing visible interaction radius.
	private int drawX;
	private int drawY; 
	//Stores user key input
	private int key;
	
	/* The entire BuildingKeyListener class shares this buildingMenu. Whenever a building menu is opened, it is instantiated.
	 * Whenever a building menu is closed, it is returned to a null reference.
	 */
	private static Menu buildingMenu; 
	public Menu getBuildingMenu() {
		return buildingMenu;
	}
	
	/* Constructor. The X and Y are inherited from its parent ImportantBuilding. */
	BuildingKeyListener(float setX, float setY, ImportantBuilding setParent) {
		super(setX, setY, ID.BuildingListener);
		this.parentBuilding = setParent;
	}


	@Override
	public void tick() {
		//Update the coordinates the radius is drawn at each tick. Only used when debug is on. 
		drawX = (int) x - (int) Player.getPlayerX() - RADIUS + parentBuilding.getBuildingSizeX() / 2;
		drawY = (int) y - (int) Player.getPlayerY() - RADIUS + parentBuilding.getBuildingSizeY() / 2;

		//Update coordinates used to calculate distance from this building every tick.
		int myX = (int) x - (int) Player.getPlayerX() + parentBuilding.getBuildingSizeX() /2;
		int myY = (int) y - (int) Player.getPlayerY() + parentBuilding.getBuildingSizeY() /2;
		
		/* Decide if player in radius. */
		if (Game.getPlayerDist(myX, myY) < RADIUS) {
			inRadius = true;
		} else {
			inRadius = false;
		}
	}

	@Override
	/* This will draw the interact radius around buildings if you have debug mode on. */
	public void render(Graphics g) {
		g.setColor(Color.cyan);
		if (Game.debug) {
			g.drawOval(drawX, drawY, 2*RADIUS, 2*RADIUS);
		}
	}

	
	@Override
	public void keyPressed(KeyEvent e) {
		key = e.getKeyCode();
		/* If you're close enough AND you press F AND the game is not already in a menu */
		if (inRadius && key == KeyEvent.VK_F && Game.gameState == Game.STATES.Playing) {
			/* If the building you're trying to get into is open at this time of day */
			if (parentBuilding.buildingOpen()) {
				/* You're allowed in if the building isn't elitist OR you're respectable. */
				if (!this.parentBuilding.getElitist() || Inventory.respectable()) {
					try {
						/* Create a menu, and get its data from the MenuDataArchive of the building
						 * that this BuildingKeyListener is attached to.*/
						MenuDataArchive parentArchive = this.parentBuilding.getMenuDataFromBuilding();
						buildingMenu = new Menu(parentArchive);
						Game.handler.addObject(buildingMenu);
						Game.thisGame.addKeyListener(buildingMenu);
					} catch (FontFormatException | IOException e1) {
						e1.printStackTrace();
					}
					
				} else { /* You got kicked out for having disgusting clothes on. */
					try {
						buildingMenu = new Menu(Game.library.kickedOutArchive);
					} catch (FontFormatException | IOException e1) {
						e1.printStackTrace();
					}
					Game.handler.addObject(buildingMenu);
					Game.thisGame.addKeyListener(buildingMenu);
				}
				

			} else {
				/* If everything else is good to open the menu, but the building is closed. */
				System.out.println("Closed, idiot.");
			}
		}	
	}





	/* Methods down here only exist because all GameObjects have to have them. Don't try to call them or anything. */
	
	@Override
	public void keyReleased(KeyEvent e) {
		
	}


	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public Rectangle getBounds() {
		return null;
	}
	
}
