package com.tutorial.main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

//This is the thing that display "+$x" or "-$x" when player gains or loses money.

public class SleepEffect extends GameObject {
	
	private int tickCount;
	private float alpha = 0;
	
	public SleepEffect(float x, float y, ID id) {
		super(x, y, id);
		tickCount = 0;
		//System.out.println("sleep Effect constructor");
	}

	@Override
	public void tick() {
		//System.out.println("SleepEffect tick");
		//System.out.println(tickCount);
		if (tickCount < 35) {
			alpha += .03;	
		} else if (tickCount > 70) {
			alpha -= .03;
		}
		if (tickCount > 110) {
			Game.gameState = Game.STATES.Menu;
			//System.out.println("deleting sleep effect");
			Game.handler.removeObject(this);
			MenuLibrary.sleepEffect = null;
		}
		tickCount++;
		alpha = Game.clamp(alpha, (float) 0 , (float) 1);
		/* if (this.y > 150) {
			System.out.println("\"aight imma head out\" - moneyeffect");
			Game.handler.removeObject(this);
		} */
	}
	
	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(makeTransparent(alpha));
		
		//System.out.println("sleep effect render");
		g.setColor(Color.black);
		Font effectFont = new Font("arial", 1, 16);
		g.setFont(effectFont);
		//System.out.println("soul String money effect = " + moneyChangeStr);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		
		g2d.setComposite(makeTransparent(1));		
	}

	public static int getEnergyRestored() {
		boolean beforeMidnight = true;
		if (HUD.hoursSinceMidnight <= 6) {
			beforeMidnight = false;
		}
		int nextMorning;
		if (beforeMidnight) {
			nextMorning = (int) ((8 * 60 * HUD.gameSlowFactor) + (HUD.day * 1440 * HUD.gameSlowFactor) - 1);
		} else {
			nextMorning = (int) ((8 * 60 * HUD.gameSlowFactor) + ((HUD.day - 1) * 1440 * HUD.gameSlowFactor) - 1);
		}
		
		double timeSlept = Math.abs(nextMorning - HUD.rawTimeElapsed);
		double energyPerTick = 100 / (8 * 60 * (double) HUD.gameSlowFactor); //it should take 8 hours of sleep to get full energy
		int energyRestored = (int) (timeSlept * energyPerTick);
		//System.out.println("timeSlept = " + timeSlept + "enPerTick = " + energyPerTick +
				//"enRestored" + energyRestored + "beforeMidnight = " + beforeMidnight + "rawTime = " + HUD.rawTimeElapsed + "nextMorning = " + nextMorning);
		return energyRestored;
	}
	private AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}
	
	@Override
	public Rectangle getBounds() {
		// don't put anything here
		return null;
	}

}