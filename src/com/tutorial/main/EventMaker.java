package com.tutorial.main;

import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

//this class handles random events
public class EventMaker extends GameObject {
	private Random r;
	public static int rng;
	public static boolean justWokeUp = false;
	Menu eventMenu;
	
	public EventMaker(float x, float y) {
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
				activateEvent(Game.library.eventFindMoneyArchive);
			}
			
			if (!HUD.isSick && rng >= 25 && rng <= 27) {
				activateEvent(Game.library.eventGetSickArchive);
			}
		}
		if (justWokeUp) {
			rng = r.nextInt(10);
			if (Inventory.hasBike && rng == 4) {
				activateEvent(Game.library.eventBikeStolenArchive);
			}
		}
		justWokeUp = false;
		
        ArrayList jobList = Game.jobFunction.jobList;
        for (int i = 0; i < jobList.size(); i++) {
            //Every time a strike is added, popup menu
            Job job = (Job) jobList.get(i);
            if (job.strikes > job.strikeTracker) {
                System.out.println("Your strikes in this job increased");
                eventMenu = new Menu(Game.library.eventStrikesArchive);
                Game.handler.addObject(eventMenu);
                Game.thisGame.addKeyListener(eventMenu);
                job.strikeTracker++;
            }
        }
	}

	private void activateEvent(MenuDataArchive event) throws FontFormatException, IOException {
		System.out.println("event happening");
		eventMenu = new Menu(event);
		Game.handler.addObject(eventMenu);
		Game.thisGame.addKeyListener(eventMenu);
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
