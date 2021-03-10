package com.tutorial.main;

import java.awt.Color;
//import java.awt.Font;
import java.awt.Graphics;

//These buildings get KeyHandlers, special art, and menus. 
public class ImportantBuilding extends Building {
	
	private BuildingKeyListener listener;
	//private boolean inRange = false;
	
	private ChoiceProcessorInterface function;
	public ChoiceProcessorInterface getFunction() {
		return function; //this is a getter.
	}
	
	protected MenuDataArchive menuDataFromBuilding;
	public MenuDataArchive getMenuDataFromBuilding() {
		return this.menuDataFromBuilding;
	}
	
	/* This decides whether or not the building will let you in if your clothes are disgusting. 
	 * If it is elitist, you will not be allowed in while wearing disgusting clothes. */
	private boolean elitist;
	public boolean getElitist() {
		return this.elitist;
	}
	
	//these indicate the times of opening and closing, in hours after midnight, 24-hour time. 
	//example: 8 = am, 16 = 4 pm
	private double openTime = 0;
	private double closeTime = 25;
	//if these aren't given in the constructor, the building will be open 24/7
	
	public ImportantBuilding(int x, int y, ID id, int sizeX, int sizeY,
			MenuDataArchive setMenuDataInBuilding, String imagepath, boolean elitist) { //constructor
		super(x, y, id, sizeX, sizeY, imagepath);
		universalConstructor(setMenuDataInBuilding);
		this.elitist = elitist;
	}

	public ImportantBuilding(int x, int y, ID id, int sizeX, int sizeY,
			MenuDataArchive setMenuDataInBuilding, String imagepath, int setOpenTime, int setCloseTime, boolean elitist) { //constructor
		super(x, y, id, sizeX, sizeY, imagepath);
		universalConstructor(setMenuDataInBuilding);
		this.openTime = setOpenTime;
		this.closeTime = setCloseTime;
		this.elitist = elitist;
	}
	
	public void tick() {
		
	}
	
	public void setMenu(MenuDataArchive setMenu) {
		this.menuDataFromBuilding = setMenu;
	}
	public void addListener() {
		BuildingKeyListener aListener = new BuildingKeyListener(x, y, this);
		this.listener = aListener;
		Game.thisGame.addKeyListener(aListener);
		Game.handler.addObject(listener);
	}
	/* Variables that need to be set up for every single Important Building
	 * regardless of which constructor is used.*/
	private void universalConstructor(MenuDataArchive setMenuDataInBuilding) {
		this.menuDataFromBuilding = setMenuDataInBuilding;
		this.addListener();
		
	}
	
	public void render(Graphics g) {
		g.setColor(Color.yellow);
		g.drawImage(img,(int) x - (int) Player.getPlayerX(), (int) y - (int) Player.getPlayerY(), null);
		//g.fillRect((int) x - (int) Player.getPlayerX(), (int) y - (int) Player.getPlayerY(),
		//		getBuildingSizeX(), getBuildingSizeY());
	}
	
	//checks to see if the building is open or closed, based on current time.
	protected boolean buildingOpen() {
		double now = HUD.hoursSinceMidnight;
		if (now >= openTime && now <= closeTime) {
			return true;
		}
		return false;
	}
}
