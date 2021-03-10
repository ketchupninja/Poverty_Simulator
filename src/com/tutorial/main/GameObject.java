package com.tutorial.main;

import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;

public abstract class GameObject { 
	/* Anything that we want to be rendered and do things each game tick are probably going to extend GameObject.
	 * Because the handler that calls tick and render on everything each 1/60 of a second does so based on a 
	 * list of GameObjects it has. 
	 */

	/* Coordinates. These won't be useful for every GameObject. */
	protected float x, y;

	/* Each GameObject gets an ID from the enum in the "ID" class. 
	 * This is useful for only running certain code if we're dealing with a certain kind of object.
	 *  
	 * Example: We only want to render menus if the gameState is Menu. So we only render 
	 * objects with ID "Menu" when gameState is "Menu".
	 */
	protected ID id; 
	
	protected float velX;
	protected float velY;
	
	/* Used for the speedControl functions. */
	protected float currentXDistance; //Current x distance to destination.
	protected boolean moveXComplete = false; //Tells if we're done moving the object.
	protected int totalXDistance;
	
	protected float currentYDistance;
	protected boolean moveYComplete = false;
	protected int totalYDistance;
	
	/* Constructor. */
	public GameObject(float x, float y, ID id) { 
		this.x = x; 
		this.y = y;
		this.id = id;
	}
	
	public abstract void tick() throws FontFormatException, IOException; //these methods exist for GameObject's
	public abstract void render(Graphics g); 
	public abstract Rectangle getBounds();
	
	public void setX(float x) { //not abstract -> in player class but hidden
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public void setId(ID id) {
		this.id = id;
	}
	
	public ID getId() {
		return id;
	}

	public float getVelX() {
		return velX;
	}

	public void setVelX(float setVelX) {
		this.velX = setVelX;
	}

	public float getVelY() {
		return velY;
	}

	public void setVelY(float setVelY) {
		this.velY = setVelY;
	}
	
	public void speedControlX(int destination) {
		currentXDistance = destination - x;
		if (currentXDistance > totalXDistance) {
			totalXDistance = (int) currentXDistance;
		}
		int acceleration = totalXDistance / 20;
		//currently only works horizontally
				float topSpeed = (float) Math.sqrt(acceleration * Math.abs(currentXDistance));
				float decelDistance = (float) ((Math.pow(topSpeed, 2)) / (2 * acceleration));

				if (currentXDistance > decelDistance) { 
					velX = Math.min(velX + acceleration, topSpeed);
				} else if (currentYDistance < -decelDistance) {
					velX = Math.max(velX - acceleration,  -topSpeed);
				} else {
					if (velX < 0) {
						velX = velX + acceleration;
					} else if (velX > 0) {
						velX = velX - acceleration;
					}
				}
				if (currentXDistance <= acceleration && currentXDistance > 0) {
					moveXComplete = true;
					velX = 0;
					x = destination;
				}
		
	}
	
public void speedControlY(int destination) {
	currentYDistance = destination - y;
	
	if (currentYDistance > totalYDistance) {
		totalYDistance = (int) currentYDistance;
	}
	
	int acceleration = totalYDistance / 20;
		//currently only works horizontally
		float topSpeed = (float) Math.sqrt(acceleration * Math.abs(currentYDistance));
		float decelDistance = (float) ((Math.pow(topSpeed, 2)) / (2 * acceleration));
		if (currentYDistance > decelDistance) {
			velY = Math.min(velY + acceleration, topSpeed); 
		} else if (currentYDistance < -decelDistance) {
			velY = Math.max(velY - acceleration,  -topSpeed);
		} else {
			if (velY < 0) {
				velY = velY + acceleration;
			} else if (velY > 0) {
				velY = velY - acceleration;
			}
		}
		if (currentYDistance <= acceleration && currentYDistance > 0) {
			moveYComplete = true;
			velY = 0;
			y = destination;
		}
		
	}
	
	

}
