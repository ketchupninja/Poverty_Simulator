package com.tutorial.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Game extends Canvas implements Runnable{
	 
	private static final long serialVersionUID = 1550691097823471818L;
	public static MenuLibrary library;
	public static Game thisGame;
	public static void main(String args[]) throws UnsupportedAudioFileException, IOException, LineUnavailableException, FontFormatException {
		thisGame = new Game();
	}
	public static final int WIDTH = 1200, HEIGHT = WIDTH /12 * 9; //resolution setup
	
	private Thread threadName; //threadName is instance of Thread
	private boolean running = false; //this is a single thread game; that could be relevant.


	public static Handler handler;
	public static EventMaker eventMaker;
	public static Inventory inventory;
	//this being static shouldn't be an issue; there's only one handler anyway, we pass it to everything.
	public static HUD hud;
	public static Finance finance;
	public static Menu mainMenu;
	public static JobFunction jobFunction;
	public static boolean mainMenuOpen = true;
	public static boolean mute = false;
	public static boolean debug = false;
	
	public static PlayerInput playerMovementThing;
	public static BuildingKeyListener activeListener;
	
	static GameObject thePlayer;
	static ImportantBuilding burgerBuilding;
	GameObject apartmentBuilding;
	GameObject collegeBuilding;
	public static ImportantBuilding walmartBuilding;
	GameObject hospitalBuilding;
	//the NPC is actually a building in disguise, don't tell the player
	GameObject torenBuilding;
	GameObject laundromatBuilding;
	GameObject gasStationBuilding;
	GameObject coffeeShopBuilding;
	GameObject clothingStoreBuilding;
	public static String moneyChangeCause;
	
	public enum STATES { //determines if game is in a menu or not
		Menu, //in a menu
		Playing, //not in a menu
		Sleeping, //sleep fade in/out is playing
	};
	
	public static void checkGameState() {
		if (Game.gameState == Game.STATES.Menu) {
			System.out.println("Menu.");
		} else {
			System.out.println("Playing.");
		}
	}
	
	public static STATES gameState = STATES.Menu; //initial state of game, should be menu at first to bring up main menu
	//this being static should not be a problem; if we have more than one "game" at a time, something is wrong.
	
	public Game() throws UnsupportedAudioFileException, IOException, LineUnavailableException, FontFormatException { //Game class constructor
		handler = new Handler();
		hud = new HUD();
		library = new MenuLibrary();
		inventory = new Inventory();
		finance = new Finance();
		Game.handler.addObject(finance);
		eventMaker = new EventMaker(0 ,0);
		jobFunction = new JobFunction();
		
		//initializing an instance of a bunch of classes that have important stuff.
		
		new Window(WIDTH, HEIGHT, "Poverty Simulator", this); //creates an instance of Window class for the name, sets size and name
		setUpMainMenu(); //set up main menu
		System.out.println("DOES THIS HAPPEN BEFORE YOU LEAVE THE MENU?");


		} 

	
	public void gameMapSetup() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		//HARDCODE THE POSITION/SIZE/STUFF OF ALL MAB OBJECTS RIGHT HERE
		thePlayer = new Player((Game.WIDTH/2) - 32, (Game.HEIGHT/2) - 32, ID.Player, Game.handler);
		
		burgerBuilding = new ImportantBuilding(-725, -80, ID.Building, 120, 87, library.burgerArchive, "office.png", false);
		apartmentBuilding = new ImportantBuilding(300, 300, ID.Building, 116, 125, library.apartmentArchive, "apartment.png", false);
		collegeBuilding = new ImportantBuilding(-325, -1740 , ID.Building, 119, 88, library.collegeArchive, "office.png", true);
		walmartBuilding = new ImportantBuilding(586, -1700, ID.Building, 152, 223, library.walmartArchive, "fancyOffice.png", true);
		hospitalBuilding = new ImportantBuilding(898, -90, ID.Building, 152, 223, library.hospitalArchive, "fancyOffice.png", false);
		torenBuilding = new ImportantBuilding(300, -60, ID.Building, 32, 32, library.torenArchive, "torenNPC.png", false);
		//Game.handler.addObject(new Player((Game.WIDTH/2) - 32, (Game.HEIGHT/2) - 32, ID.Player, Game.handler));
		laundromatBuilding = new ImportantBuilding(1387, -1770, ID.Building, 120, 87, library.laundromatArchive, "office.png", false);
		Game.handler.addObject(new Building(1387, -1940, ID.Building, 150, 175, "genericBuilding.png"));
		
		gasStationBuilding = new ImportantBuilding(1220, 255, ID.Building, 220, 87, library.gasStationArchive, "office.png", false);
		
		coffeeShopBuilding = new ImportantBuilding(140, -1700, ID.Building, 152, 223, library.coffeeShopArchive, "fancyOffice.png", false);
		clothingStoreBuilding = new ImportantBuilding(-740, -1750, ID.Building, 152, 223, library.clothingStoreArchive, "fancyOffice.png", false);
		placeGenericBuildings();
		
		
		Game.handler.addObject(thePlayer);
		Game.handler.addObject(burgerBuilding);
		Game.handler.addObject(apartmentBuilding);
		Game.handler.addObject(collegeBuilding);
		Game.handler.addObject(walmartBuilding);
		Game.handler.addObject(hospitalBuilding);
		Game.handler.addObject(torenBuilding);
		Game.handler.addObject(laundromatBuilding);
		Game.handler.addObject(gasStationBuilding);
		Game.handler.addObject(coffeeShopBuilding);
		Game.handler.addObject(clothingStoreBuilding);
		
		Game.handler.addObject(inventory);
		
        jobFunction.setList();
        
		playerMovementThing = new PlayerInput(handler);
		this.addKeyListener(playerMovementThing); //allow game to respond to inputs for player movement
		//Game.handler.addObject(new ImportantBuilding(300, 300, ID.Building, 150, 150, BuildingKeyListener.walmartArchive));
	}
	
	//this is used to keep a float variable between a max and minimum.
	public static float clamp(float variable, float min, float max) {
		if (variable >= max) {
			return max;
		} else if (variable <= min) {
			return min;
		}
		return variable; //if within range already, do nothing
	}
	
	//same thing as the first clamp but for integers
	public static int clamp(int variable, int min, int max) {
		//System.out.println("Clamping in progress...");
		if (variable >= max) {
			return max;
		} else if (variable <= min) {
			return min;
		}
		return variable; //if within range already, do nothing
	}
	public synchronized void start() {
		threadName = new Thread(this); //this = this instance of our Game class
		threadName.start();
		running = true;
	}
	
	public synchronized void stop() {
		try {
			threadName.join(); //stops the thread, basically
			running = false;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() { //the game loop, try not to mess with this
		this.requestFocus(); //added to make the game focus (take input) whenever it's selected/alt tabbed to. 
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1) {
				try {
					tick();
				} catch (FontFormatException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				delta--;
			}
			if (running)
				render();
			frames++;
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				//prints FPS to console each seconds
				if (Game.debug) {
					System.out.println("FPS: " + frames);	
				}
				frames = 0;
			}
		}
		stop();
	}

	private void tick() throws FontFormatException, IOException {

		handler.tick(); //when the handler ticks, everyone ticks.
		if (playerMovementThing != null) {
			playerMovementThing.tick();
		}
		if (gameState == STATES.Playing) {
			hud.tick();
		}
	}
	
	private void render() {
		
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) { //weird stuff, don't worry about it, idk what it does.
			this.createBufferStrategy(3);
			return;	
		} 
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(Color.gray);
		g2d.fillRect(0,0, WIDTH, HEIGHT);
		//THIS FILLS IN THE BACKGROUND COLOR
		
		handler.render(g2d); //when the handler renders, everyone does
		hud.render(g2d);

		g.dispose();
		bs.show();
		if (mainMenuOpen) {
			Font okFont = new Font("monospaced", 1, 20);
			g.setFont(okFont);
			g.drawString("Use W and S keys to navigate menu options.", 70, 480);
			g.drawString("Press E to choose an option. Press F when prompted to interact.", 35, 510);
		}
	}
	
	public static float getPlayerDist(float a, float b) { //returns distance from player to point x,y
		float xDist = (((Game.WIDTH) / 2) - 32 - a);
		float yDist = (((Game.HEIGHT) / 2) - 32 - b);
		xDist = xDist * xDist; //square it
		yDist = yDist * yDist;
		float distance = (float) Math.sqrt(xDist + yDist);
		return distance;
	}
	
	public void setUpMainMenu() throws FontFormatException, IOException {
		mainMenu = new Menu(library.mainMenuArchive); //make mainMenu a new instance of Menu class
		Game.handler.addObject(mainMenu);
		this.addKeyListener(mainMenu); //this has to be here for main menu to take player input
	}
	

	private void placeGenericBuildings() {
		for (int i = 100, loops = 0; loops < 10; i += 160) { //left side of the apartment
			Game.handler.addObject(new Building(240 - i, 300 - 47, ID.Building, 150, 175, "genericBuilding.png"));
			loops++;
		}
		for (int i = 100, loops = 0; loops < 5; i -= 160) { //right side of the apartment
			Game.handler.addObject(new Building(525 - i, 300 - 47, ID.Building, 150, 175, "genericBuilding.png"));
			loops++;
		}
		for (int i = 100, loops = 0; loops < 9; i += 175) { //column to right of apartment
			Game.handler.addObject(new Building(587, 20 - i, ID.Building, 150, 175, "genericBuilding.png"));
			loops++;
		}
		for (int i = 275, loops = 0; loops < 8; i += 175) { //second column to right of apartment
			Game.handler.addObject(new Building(900, 20 - i, ID.Building, 150, 175, "genericBuilding.png"));
			loops++;
		}
		for (int i = 100, loops = 0; loops < 9; i += 175) { //buildings directly above apartment
			Game.handler.addObject(new Building(140, 20 - i, ID.Building, 150, 175, "genericBuilding.png"));
			loops++;
		}
		for (int i = 100, loops = 0; loops < 10; i += 175) { //second vertical row the left of apartment
			Game.handler.addObject(new Building(-340, 20 - i, ID.Building, 150, 175, "genericBuilding.png"));
			loops++;
		}
		for (int i = 100, loops = 0; loops < 8; i += 175) { //third column to the left of apartment
			Game.handler.addObject(new Building(-740, -155 - i, ID.Building, 150, 175, "genericBuilding.png"));
			loops++;
		}
		for (int i = 100, loops = 0; loops < 13; i += 175) { //left bounding wall of the map
			Game.handler.addObject(new Building(-1297, 175 - i, ID.Building, 150, 175, "genericBuilding.png"));
			loops++;
		}
		for (int i = 100, loops = 0; loops < 11; i += 175) { //right bounding wall of the map
			Game.handler.addObject(new Building(1387, 175 - i, ID.Building, 150, 175, "genericBuilding.png"));
			loops++;
		}
		for (int i = 260, loops = 0; loops < 16; i += 160) { //north map bound
			Game.handler.addObject(new Building(1500 - i, -2100, ID.Building, 150, 175, "genericBuilding.png"));
			loops++;
		}
	}
	
	
}
