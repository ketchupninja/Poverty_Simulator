package com.tutorial.main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;

import javax.imageio.ImageIO;
/* This is the arrow that is drawn in most menus to show the choice the player has selected. */
public class Reticle extends GameObject {
	
	/* The image of the arrow. */
	private Image arrow;
	
	/* The y value the arrow should move toward, if it is not already there. */
	private float targetY;
	
	public Reticle(float x, float y) {
		super(x, y, ID.Reticle);
		this.targetY = y;
		
		try {
			arrow = ImageIO.read(getClass().getResource("arrow.png"));
		} catch (IOException e) {
		}
	}

	@Override
	public void tick() {
		y += velY;
		speedControlY((int) targetY);
		if (Game.gameState != Game.STATES.Menu) {
			Game.handler.removeObject(this);
		}
	}

	public void setTargetY(int setTargetY) {
		this.targetY = setTargetY;
	}
	
	@Override
	public void render(Graphics g) {
		g.drawImage(arrow, (int) x, (int) y, null);
	}

	@Override
	public Rectangle getBounds() {
		return null;
	}
	

}
