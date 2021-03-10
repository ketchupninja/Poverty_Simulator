package com.tutorial.main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
/* This class stores most of the player's "stat" variables
 * e.g. health, money, food, energy.
 * It also handles all the time variables
 */
public class HUD {
	/* This slows the game down the higher it is.
	 * Specifically, game speed is divided by this number.
	 * Hunger loss and such should be tied to this number, meaning
	 * that if you change this number, the game will function
	 * exactly the same, just faster or slower.
	 * Probably will be around 8 in the final release. 
	 */
	static final int gameSlowFactor = 6;
	
	/* Keeps tracks of time since start of the game.
	 * Specifically, time since Day 1, 12:00 AM.
	 * (There is no Day 0)
	 * Initial value is set to 10 AM.
	 * Updates by 1 60 times per second.
	 */
	public static long rawTimeElapsed = 10 * 60 * gameSlowFactor;
	
	/* Indicates if the player is sick, so they lose health each tick. */
	public static boolean isSick = false;
	
	/* Player hunger loss per tick. */
	public final static double METABOLISM = .03;
	/* Player energy loss per tick. 
	 * Needs to stay at .09 to run out after about 18 hours at max. 
	 */
	public final static double ENERGY_LOSS = .09;
	/* Player health loss due to low hunger. */
	public static double hungerPain = .1;	
	
	/* Ticks since the last midnight, i.e. ticks since start of day. */
	public static int ticksSinceMidnight;
	/* Hours since midnight. Used by buildings when checking current time. */
	public static double hoursSinceMidnight;
	/* Player statistics. */
	public static double STARTING_MONEY = 15.00;
	public static double money = STARTING_MONEY;
	public static double health = 100;
	public static double hunger = 100.00;
	public static double energy = 100.00;
	
	
	public Menu deathMenu;
	/* Stores amount of money player had the previous tick. 
	 * Used to generate a MoneyEffect when player gains/loses money.
	 */
	private static double moneyPast = STARTING_MONEY;
	
	private final int BARX = 150; //x value of start of hunger/health bars
	private final int HEALTHY = 5; //y value to draw health bar at 
	
	private final int BARSIZEY = 18; //y size of hunger/health bars
	private final int HUNGERY = 27; //y value to draw hunger rectangles at
	
	private final int ENERGYY = 49;
	private final int BARLENGTH = 200; //length of health and health bas
	
	
	private double sickPain = .01;
	
	private double hungerPercent; 
	private double healthPercent; //used for math for healthFullnessX
	private double energyPercent;
	
	private int hungerFullnessX; //used for drawing hunger/health bars based on current value
	private int healthFullnessX;
	private int energyFullnessX;
	
	private String hungerStringAlert = ""; //used to alert the player when they lose hp due to hunger
	private String moneyStringAlert = ""; //used to alert the player when they are in debt (negative money)

	/* Used for the "F" or "Closed." text when player is near a building. */
	private Font promptFont = new Font("arial", 1, 28);
	/* Used for the top right HUD. */
	private Font hudFont = new Font("arial", 1, 16);

	/* Cost of rent player pays each day at 8 AM. */
	private double rentCost = 10.00;
	
	/* Used to draw stuff transparently. 1 = Normal, 0 = invisible. */
	private float alpha;
	
	public static float hungerFullnessDelayedX; //used to draw the orange health bar, which will slowly react to changes
	public static float hungerLoss = 0; //used to draw the red hunger bar that indicates sudden loss
	/* Time variables. */
	public static double day;
	private double hour;
	private double min;
	
	/* Used if player restarts game after dying. */
	public static void resetStats() {
		rawTimeElapsed = 10 * 60 * gameSlowFactor;
		health = 100;
		hunger = 100;
		money = STARTING_MONEY;
	}

	@SuppressWarnings("unused")
	public void tick() throws FontFormatException, IOException {
		

		
		rawTimeElapsed++; //keeps track of actual time since inception of universe
		
		ticksSinceMidnight = (int) (rawTimeElapsed % (1440 * HUD.gameSlowFactor));
		hoursSinceMidnight = ( (double) ticksSinceMidnight / (60 * HUD.gameSlowFactor));
		
		day = (rawTimeElapsed / (1440 * gameSlowFactor)) + 1; //for player
		hour = (rawTimeElapsed / (60 * gameSlowFactor)) % 24; //for math, loops 0 - 23
		min = (rawTimeElapsed / gameSlowFactor) % 60; //for player, loops 0 - 59
		
		int today8AM = (int) ((8 * 60 * HUD.gameSlowFactor) + ((HUD.day - 1) * 1440 * HUD.gameSlowFactor));
		if (day % Finance.PERIOD_LENGTH == 0 && rawTimeElapsed == today8AM) {
			Finance.financeUpdate();
			System.out.println("Finance update.");
		}
			
		statLossManage();
		deathCheck();
		drawBarVariableManage();

		painManage();

		
		//put in a function updateMoney();
		if (money != moneyPast) { //if money has changed
			MoneyEffect moneyEffect = new MoneyEffect(10, 85, ID.MoneyEffect, money - moneyPast);
			System.out.println("money effect spawned - HUD");
		}
		moneyPast = money;
		
		hungerGainLossManage();
		moneyAlertManage();
		dailyCostManage();
	}
	

	private void statLossManage() {
		hunger -= (METABOLISM / gameSlowFactor);
		energy -= (ENERGY_LOSS / gameSlowFactor);
		
		energy = Game.clamp( (float) energy, (float) 0.0, (float) 100.0);
		hunger = Game.clamp( (float) hunger, (float) 0.0, (float) 100.0);
		health = Game.clamp( (float) health, (float) 0.0, (float) 100.0);
	}
	
	private void drawBarVariableManage() {
		
		healthPercent = health / 100;
		hungerPercent = hunger / 100;
		energyPercent = energy / 100;
		
		healthFullnessX = (int) (BARLENGTH * healthPercent);
		hungerFullnessX = (int) (BARLENGTH * hungerPercent);
		energyFullnessX = (int) (BARLENGTH * energyPercent);
	}
	
	//this converts the rawtimeElapsed to a a date time output for hours/minutes display.
	private String hourPretty(double hour, double min) {
	String dateString = Double.toString(hour) + Double.toString(min);
	SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
	try {
		Date date3 = sdf.parse(dateString);
		SimpleDateFormat sdf2 = new SimpleDateFormat("hh.mm aa");
		String dotVer = sdf2.format(date3);
		return dotVer.replace(".", ":");
	} catch(ParseException e) {
		e.printStackTrace();
	}
	return "this is bad";
}

	private void deathCheck() throws FontFormatException, IOException {
		//System.out.println("health = " + health);
		if (HUD.health == 0) {
			//delete save file
			JSONObject gamedata = new JSONObject();
	        PrintWriter pw = null;
	        try {
	            pw = new PrintWriter("gamedata.json");
	        } catch (FileNotFoundException e3) {
	            // TODO Auto-generated catch block
	            e3.printStackTrace();
	        }
	        pw.write(((Object) gamedata).toString());
	        pw.flush();
	        pw.close();
	        //die
			Game.mute = true;
			deathMenu = new Menu(Game.library.deathArchive);
			Game.handler.addObject(deathMenu);
			Game.thisGame.addKeyListener(deathMenu);
		}
	}

	/* Makes the player pay costs they have to do each day.
	 * Right now, it's rent and possibly interest on debt.
	 */
	private void dailyCostManage() {
		int today8AM = (int) ((8 * 60 * HUD.gameSlowFactor) + ((HUD.day - 1) * 1440 * HUD.gameSlowFactor));

		if (rawTimeElapsed == today8AM) {
			Game.moneyChangeCause = "Rent Payment";
			money -= rentCost;
		}
	}
	
	private void moneyAlertManage() {
		if (money >= 0) {
			moneyStringAlert = "";
		} else if (Finance.getBankrupt()) {
			moneyStringAlert = "BANKRUPT";
		} else {
			moneyStringAlert = "In Debt";
		}
	}
	private void painManage() {
		if (isSick) {
			health -= (sickPain);
		}
		if (hunger < 50 && hunger > 1) {
			hungerPain = .025;
			hungerStringAlert = "(!)";
		} else if (hunger < 1) {
			hungerPain = .1;
			hungerStringAlert = "(!!!)";
		} else {
			hungerStringAlert = " ";
			hungerPain = 0;
		}
		health -= (hungerPain / gameSlowFactor);
	}
	
	private void hungerGainLossManage() {
		if (hungerFullnessX > hungerFullnessDelayedX) {
			hungerFullnessDelayedX += .12;
		}
		if (hungerFullnessX < hungerFullnessDelayedX) {
			hungerFullnessDelayedX = hungerFullnessX;
		}
		
		if (hungerLoss > 0) {
			hungerLoss -= .12;
		}
	}
	
	private AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(makeTransparent(alpha));
		if (!Game.mainMenuOpen) {		
			drawDarkness(g);
			drawHudBackground(g);
			drawHudWords(g);
			drawBarOutlines(g);
			drawBarBackgrounds(g);
			drawBarFullness(g);	
			drawBarMarkings(g);
			drawInteractPrompt(g);
			handleSleepEffect(g);
			if (Game.debug) {
				g.setFont(hudFont);
				g.drawString("PlayerX " + Player.getPlayerX(), 0, 100);
				g.drawString("PlayerY " + Player.getPlayerY(), 0, 125);
				
				g.drawString("nextMorn = " + (int) ((8 * 60 * HUD.gameSlowFactor) + (HUD.day * 1440 * HUD.gameSlowFactor) - 1), 0, 150);
				g.drawString("rawTimeElapsed" + " " + rawTimeElapsed, 0, 175);

				g.drawString("ticksSinceMidnight" + ticksSinceMidnight, 0, 200);
				g.drawString("hoursSinceMidnight" + hoursSinceMidnight, 0, 225);
				g.drawString(energy + "= energy", 0, 250);
				g.drawString(hunger + "= hunger", 0, 275);
				
				g.drawString("dayThresholdTicks = " + 1440 * HUD.gameSlowFactor, 0, 300);
			}
		}
	}
	
	private void decideTransparency(Graphics2D g2d) {
		g2d.setComposite(makeTransparent(alpha));
	}
	/* Draw a 'lighting effect' based on time of day. */
	private void drawDarkness(Graphics g) {
		/* Sin input goes through a cycle every day, AKA every 1440 ticks.
		 * the 2 * PI in the denominator exists because of how trigonometry works.*/
		double sinInput = (double) HUD.rawTimeElapsed / ((double) HUD.gameSlowFactor * (1440 / (2 * Math.PI)));
		/* Offset makes sure it's bright in the morning and dark at night. */
		double offset = (360 * HUD.gameSlowFactor) / ((double) HUD.gameSlowFactor * (1440 / (2 * Math.PI)));
		double darknessFactor = .5 + (.5 * Math.sin(offset + sinInput));
		Graphics2D g2d = (Graphics2D) g;
		alpha = (float) darknessFactor;
		alpha = Game.clamp(alpha, (float) 0.0, (float) .65);
		/* if (Game.debug) {
			g.drawString("darknessOpaquess = " + alpha, 0, 500);
		} */
		decideTransparency(g2d);
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		
	}
	private void drawHudBackground(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		alpha = (float) .7;
		g2d.setComposite(makeTransparent(alpha));
		g.setColor(Color.black);
		g.fillRoundRect(-15,-15, 445, 90, 30, 30); //the hud background black bar
	}
	
	private void drawHudWords(Graphics g) {
		String moneyString = String.format("%.2f", money);
		g.setFont(hudFont);
		Graphics2D g2d = (Graphics2D) g;
		alpha = (float) 1;
		g2d.setComposite(makeTransparent(alpha));
		g.setColor(Color.white);
		g.drawString("Day " + (int) day + "/ " + hourPretty(hour, min), 6, 22);
		g.drawString("$" + moneyString, 14, 42); 
		
		g.drawString("Health ", BARLENGTH + BARX + 4, HEALTHY + BARSIZEY - 3);
		g.setColor(Color.red);
		g.drawString(hungerStringAlert, BARX - 35, HUNGERY + BARSIZEY - 3);
		g.drawString(moneyStringAlert, 14, 62);
		g.setColor(Color.white);
		g.drawString("Fullness", BARLENGTH + BARX + 4, HUNGERY + BARSIZEY - 2);
		g.drawString("Energy", BARLENGTH + BARX + 4, ENERGYY + BARSIZEY - 2);
	}
	
	private void drawBarOutlines(Graphics g) {
		g.setColor(Color.white); //draw outlines 
		g.drawRect(BARX, HEALTHY, BARLENGTH, BARSIZEY); //health bar outline
		g.drawRect(BARX - 1, HEALTHY - 1, BARLENGTH, BARSIZEY); //health bar outline
		g.drawRect(BARX, HUNGERY, BARLENGTH, BARSIZEY); //hunger bar outline
		g.drawRect(BARX - 1, HUNGERY - 1, BARLENGTH, BARSIZEY); //hunger bar outline
		g.drawRect(BARX, ENERGYY, BARLENGTH, BARSIZEY);
		g.drawRect(BARX - 1, ENERGYY - 1, BARLENGTH, BARSIZEY);
	}
	
	private void drawBarBackgrounds(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(BARX, HEALTHY, BARLENGTH, BARSIZEY); //health bar background
		g.fillRect(BARX, HUNGERY, BARLENGTH, BARSIZEY); //hunger bar background
		g.fillRect(BARX, ENERGYY, BARLENGTH, BARSIZEY);
	}
	
	private void drawBarFullness(Graphics g) {

		
		g.setColor(Color.red);
		g.fillRect(BARX, HEALTHY, healthFullnessX, BARSIZEY); //health bar
		g.setColor(Color.yellow);
		g.fillRect(BARX, ENERGYY, energyFullnessX, BARSIZEY); //energy bar
		g.setColor(Color.green); //hunger bar instant
		g.fillRect(BARX, HUNGERY, hungerFullnessX, BARSIZEY);
		g.setColor(Color.orange); //hunger bar delayed
		g.fillRect(BARX, HUNGERY, (int) hungerFullnessDelayedX, BARSIZEY);
		g.setColor(Color.red);
		g.fillRect((int) (BARX + hungerFullnessDelayedX), HUNGERY, (int) (hungerLoss * 2), BARSIZEY); //hunger loss indicator
	}
	
	private void drawBarMarkings(Graphics g) {
		g.setColor(Color.black);
		//these lines show the player when their stuff is at 25%, 50%, and 75%
		//there are two lines to increase thickness
		g.drawLine(BARX + BARLENGTH/4, HUNGERY, BARX + BARLENGTH/4, HUNGERY + BARSIZEY - 1);
		g.drawLine(BARX + BARLENGTH/4 + 1, HUNGERY, BARX + BARLENGTH/4 + 1, HUNGERY + BARSIZEY - 1);
		//the minus one is there to stop the line from going over the border and looking bad
		g.drawLine(BARX + BARLENGTH/2, HUNGERY, BARX + BARLENGTH/2, HUNGERY + BARSIZEY - 1);
		g.drawLine(BARX + BARLENGTH/2 + 1, HUNGERY, BARX + BARLENGTH/2 + 1, HUNGERY + BARSIZEY - 1);
		
		g.drawLine(BARX + (3 * BARLENGTH) /4, HUNGERY, BARX + (3 * BARLENGTH) /4, HUNGERY + BARSIZEY - 1);
		g.drawLine(BARX + (3 * BARLENGTH) /4 + 1, HUNGERY, BARX + (3 * BARLENGTH)/4 + 1, HUNGERY + BARSIZEY - 1);
		
		//same thing but for hunger
		g.drawLine(BARX + BARLENGTH/4, HEALTHY, BARX + BARLENGTH/4, HEALTHY + BARSIZEY - 1);
		g.drawLine(BARX + BARLENGTH/4 + 1, HEALTHY, BARX + BARLENGTH/4 + 1, HEALTHY + BARSIZEY - 1);
		//the minus one is there to stop the line from going over the border and looking bad
		g.drawLine(BARX + BARLENGTH/2, HEALTHY, BARX + BARLENGTH/2, HEALTHY + BARSIZEY - 1);
		g.drawLine(BARX + BARLENGTH/2 + 1, HEALTHY, BARX + BARLENGTH/2 + 1, HEALTHY + BARSIZEY - 1);
		
		g.drawLine(BARX + (3 * BARLENGTH) /4, HEALTHY, BARX + (3 * BARLENGTH) /4, HEALTHY + BARSIZEY - 1);
		g.drawLine(BARX + (3 * BARLENGTH) /4 + 1, HEALTHY, BARX + (3 * BARLENGTH)/4 + 1, HEALTHY + BARSIZEY - 1);
	
		//ENERGY
		g.drawLine(BARX + BARLENGTH/4, ENERGYY, BARX + BARLENGTH/4, ENERGYY + BARSIZEY - 1);
		g.drawLine(BARX + BARLENGTH/4 + 1, ENERGYY, BARX + BARLENGTH/4 + 1, ENERGYY + BARSIZEY - 1);
		//the minus one is there to stop the line from going over the border and looking bad
		g.drawLine(BARX + BARLENGTH/2, ENERGYY, BARX + BARLENGTH/2, ENERGYY + BARSIZEY - 1);
		g.drawLine(BARX + BARLENGTH/2 + 1, ENERGYY, BARX + BARLENGTH/2 + 1, ENERGYY + BARSIZEY - 1);
		
		g.drawLine(BARX + (3 * BARLENGTH) /4, ENERGYY, BARX + (3 * BARLENGTH) /4, ENERGYY + BARSIZEY - 1);
		g.drawLine(BARX + (3 * BARLENGTH) /4 + 1, ENERGYY, BARX + (3 * BARLENGTH)/4 + 1, ENERGYY + BARSIZEY - 1);

	}
	
	private void drawInteractPrompt(Graphics g) {
		if (Game.activeListener != null && Game.activeListener.inRadius && Game.gameState == Game.STATES.Playing) {
			g.setFont(promptFont);
			String interactPrompt;
			if (Game.activeListener.parentBuilding.buildingOpen()) { //if it's open
				interactPrompt = "F";
			} else {
				interactPrompt = "Closed"; //if it's closed don't display the prompt
			}
			g.drawString(interactPrompt, (int) (Player.getDRAW_PLAYER_X()) - 2, ((int) (Player.getDRAW_PLAYER_Y()) - 16)); //interaction prompt
		}
	}
	
	private void handleSleepEffect(Graphics g) {
		if (Game.gameState == Game.STATES.Sleeping) {
			MenuLibrary.sleepEffect.tick();
			if (MenuLibrary.sleepEffect != null) {
				MenuLibrary.sleepEffect.render(g);
			}
		}
	}
}
