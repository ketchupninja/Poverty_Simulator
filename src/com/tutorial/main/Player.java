package com.tutorial.main;

import java.awt.Color;

import java.awt.Graphics;
//import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

public class Player extends GameObject{

	
	Random r = new Random();

	private static final float DRAW_PLAYER_X = ((Game.WIDTH) / 2) - 32;
	public static float getDRAW_PLAYER_X() {
		return DRAW_PLAYER_X;
	}
	
	private static final float DRAW_PLAYER_Y = ((Game.HEIGHT) /2) - 32;
	public static float getDRAW_PLAYER_Y() {
		return DRAW_PLAYER_Y;
	}

	private Handler playerHandler;
	private static float playerX;
	//the player's position in the game world
	private static float playerY;
	private static float playerXPast;
	//the player's position in game world in previous tick
	private static float playerYPast;
	public static float startX = -150;
	public static float startY = -200;
	public Player(int x, int y, ID id, Handler handler) {
		super(x, y, id);
		playerX = startX;
		playerY = startY;
		playerXPast = x;
		playerYPast = y; //if you spawn player in a building they wont be able to move
		// (don't do that)
		this.playerHandler = handler;
	}
	public Rectangle getBounds() {
		return new Rectangle ((int) DRAW_PLAYER_X, (int) DRAW_PLAYER_Y, 16, 16); 
		/*this uses draw player x/y because we are using it to deal with collision
		and things collide with the player in the center of the screen,
		where player is drawn */
	}
	public void tick() {
		playerXPast = playerX; //store info for collision
		playerYPast = playerY;
		playerX += velX; //move the player if they're moving
		playerY += velY;
		for (int i = 0; i < playerHandler.object.size(); i++) { //check all objects
			GameObject tempObjP = playerHandler.object.get(i); 
			if (tempObjP.getId() == ID.Building && Game.debug == false) { //if that obj is building
				resolveCollision(this, tempObjP); //resolve collisions
			}
		}
	}
	
	private void resolveCollision(Player player, GameObject obstacle) {
		if (getBounds().intersects(obstacle.getBounds())) { //if there is a collision
			playerX = playerXPast;
			playerY = playerYPast;
			//rewind time to fix it
		}
	}

	public void render(Graphics g) {
		
		//Graphics2D g2d = (Graphics2D) g;
		
		g.setColor(Color.blue);
		if (PlayerInput.biking) {
			g.setColor(Color.black);
		}
		g.fillRect((int) DRAW_PLAYER_X, (int) DRAW_PLAYER_Y,
				16, 16);
		//for now the player is just a rectangle. eventually we will have some kind of sprite.
		//g2d.draw(getBounds()); draw hitbox
		
		//g.setColor(Color.white); //player color
		//g.fillRect(DRAW_PLAYER_X, DRAW_PLAYER_Y, 16, 16); //player object size (replace with actual
	}
	
	public static float getPlayerX() {
		return playerX;
	}
	
	public static float getPlayerY() {
		return playerY;
	}
	
	public static void setPlayerX(float setX) {
		playerX = setX;
	}
	
	public static void setPlayerY(float setY) {
		playerY = setY;
	}
}
