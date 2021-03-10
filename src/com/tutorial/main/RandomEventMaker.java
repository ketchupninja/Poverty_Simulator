package com.tutorial.main;

import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Random;

//this class handles random events
public class RandomEventMaker extends GameObject {
	private Random r;
	public static int rng;
	Menu eventMenu;
	
	public RandomEventMaker(float x, float y) {
		super(0, 0, ID.RandomEventMaker);
		Game.handler.addObject(this);
		r = new Random();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tick() throws FontFormatException, IOException {
		if (Game.gameState == Game.STATES.Playing) {
			rng = r.nextInt(100000); //every tick (60 times per sec) generate random number between 1 and 100,000
			if (rng >= 1 && rng <= 20) {
				eventMenu = new Menu(Game.library.eventFindMoneyArchive);
				Game.handler.addObject(eventMenu);
				Game.thisGame.addKeyListener(eventMenu);
			}
		}
	}

	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

}
