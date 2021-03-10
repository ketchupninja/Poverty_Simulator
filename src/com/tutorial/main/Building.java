package com.tutorial.main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Building extends GameObject {
	
	protected Image img;
	protected final int buildingSizeX; //each building has its own size which wont change
	public int getBuildingSizeX() {
		return buildingSizeX;
	}

	protected final int buildingSizeY;
	public int getBuildingSizeY() {
		return buildingSizeY;
	}

	public Building(int x, int y, ID id, int sizeX, int sizeY, String imagepath) { //constructor for building 
		super(x, y, id); //call parent constructor	
		this.buildingSizeX = sizeX;
		this.buildingSizeY = sizeY;
		try {
			img = ImageIO.read(getClass().getResource(imagepath));
		} catch (IOException e) {
			//exception handling (not really)
		}
	}
	
	public Rectangle getBounds() {
		return new Rectangle ((int) x - (int) Player.getPlayerX(),(int) y - (int) Player.getPlayerY(),
				buildingSizeX, buildingSizeY); 
	}
	
	@Override
	public void tick() {
	}

		

	@Override
	public void render(Graphics g) { //what should be rendered for enemy each tick
		
		g.drawImage(img,(int) x - (int) Player.getPlayerX(), (int) y - (int) Player.getPlayerY(), null);
		//in this class, "x" and "y" refer to the building's 'true' coordinates, not display coordinates.
		
	}
	
}
