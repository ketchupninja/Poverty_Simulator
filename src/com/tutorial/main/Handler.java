package com.tutorial.main;

import java.awt.FontFormatException;
import java.awt.Graphics;
import java.io.IOException;
import java.util.LinkedList;

public class Handler {
	//Handler is the thing that takes "code objects" and turns them into objects that matter to the player.
	//It makes them visible and makes them do stuff.
	public LinkedList<MoneyEffect> effectQueue = new LinkedList<MoneyEffect>();
	private int queueTimer = 0;
	public LinkedList<GameObject> object = new LinkedList<GameObject>();
	//object is actually a list of GameObjects
	//Makes all game objects do stuff by calling their tick method.
	public void tick() throws FontFormatException, IOException {
		effectTick();
		boolean foundListener = false;
		for (int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if (tempObject.getId().equals(ID.BuildingListener)) {
				BuildingKeyListener smepObject = (BuildingKeyListener) tempObject;
				int uX = (int) tempObject.getX() - (int) Player.getPlayerX() + smepObject.parentBuilding.getBuildingSizeX() /2;
				int uY = (int) tempObject.getY() - (int) Player.getPlayerY() + smepObject.parentBuilding.getBuildingSizeY() /2;
				if (Game.getPlayerDist(uX, uY) < 175) { //if player within range of that BL
					Game.activeListener = (BuildingKeyListener) tempObject;
					foundListener = true;
				}
			}
			if (Game.gameState == Game.STATES.Playing) {
				if ( !(tempObject instanceof Menu) && tempObject.getId() != ID.SleepEffect) {
					//replace "choiceMenu" above with generic "menu" after i merge classes
					tempObject.tick(); //make each "regular" object tick when not in menu
				}
			} else if (Game.gameState == Game.STATES.Menu && (tempObject instanceof Menu)) {				
				//replace with generic (i don't know what i meant by this - future Matt)
				tempObject.tick(); //make each menu tick when in menu
			} 
		}
		
		if (foundListener == false) {
			Game.activeListener = null;
		}
	}
	
	private void effectTick() {
		queueTimer--;
		if (effectQueue.size() > 0) {
			queueTimer = Game.clamp(queueTimer, 0, 100);
			if (queueTimer == 0) {
				MoneyEffect toAdd = effectQueue.remove(0);
				Game.handler.addObject(toAdd);
				queueTimer = 20; //delay in ticks before next msg will appear
			}
		}
	}
	//Makes all game objects visible by calling their render method.
	public void render(Graphics g) {
		for (int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			
			if ( !(tempObject instanceof Menu)&& tempObject.getId() != ID.SleepEffect && tempObject.getId() != ID.Reticle) {
				tempObject.render(g); //tick all non-menus in playtime.
			}
			
			if (Game.gameState == Game.STATES.Menu && (tempObject instanceof Menu)) {
				tempObject.render(g);
			} else if (Game.gameState == Game.STATES.Sleeping && tempObject.getId() == ID.SleepEffect) {
				tempObject.render(g);
			}
		}
	}
	
	//Adds an object to the handler, and makes it "real" so that it can be visible and do stuff.
	public void addObject(GameObject object) {
		this.object.add(object);
		
	}
	
	//Removes an object from the handler, so it will no longer be visible or do stuff.
	public void removeObject(GameObject object) {
		 this.object.remove(object);
	}
	
}
