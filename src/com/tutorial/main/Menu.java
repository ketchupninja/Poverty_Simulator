package com.tutorial.main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Menu extends GameObject implements KeyListener {
	
	//these are for the whole menu itself
	protected final int DRAW_MENU_Y = (Game.HEIGHT / 12) + 25; //all menus are drawn at the same spot
	protected final int MENU_SIZE_X = 650;
	protected final int DRAW_MENU_X = (Game.WIDTH - MENU_SIZE_X) / 2; //constants that decide where to draw the menu
	protected final int MENU_SIZE_Y = 400;
	
	//these are for the player's option to pick from the menu
	//even if they only have one option (See NoChoiceMenu)
	protected final int DRAW_OPTION_X = DRAW_MENU_X + 75;
	protected final int DRAW_OPTION_Y = DRAW_MENU_Y + 200;
	protected final int DRAW_OPTION_SIZE_X = 410;
	protected final int DRAW_OPTION_SIZE_Y = 40;

	//these are the coords to draw the string for each choice at. (should be inside each option's rectangle)
	protected final int OPTSTRX = DRAW_OPTION_X + 5;
	protected final int OPTSTRY = DRAW_OPTION_Y + 180;
	
	protected String prompt; //this is the information the menu gives the player. i.e. "you are in a burger shop, what do?"
	
	protected int reticleY; //the y value to draw the reticle at
	protected final int RETICLEX = DRAW_OPTION_X - 50;
	
	protected final ChoiceProcessorInterface menuFunction;
	//menuFunction is a functional interface (basically a function.) Each menu gets its own function that will decide 
	//what code gets run based on the user's choice. 
	
	protected String[] choiceIdArray;
	
	protected Image arrow;
	
	private final int Y_GAP = 50; //the amount of space between each choice in the menu
	private int numChoices; //number of choices in the menu. goes between 2 - 4. 
	private String[] choiceStrings; //user-facing choice strings, i.e. "Buy a burger"
	private int selectedChoice; //the menu option that the player currently has selected 
	
	private Reticle reticle;
	private Font bookAntiqua;
	private Image img;
	public int getSelectedChoice() {
		return selectedChoice;
	}
	private String finalChoice;
	private String title;
	private boolean moveable; //indicates whether or not this menu should use the "Cruise" function to fly on screen
	private int key;
	private float alpha = 1; //used to draw things transparently
	private MenuDataArchive archive;
	private int ticksSinceCruise;
	
	private String clothesCurrentString;
	
	private Color currentQualityColor;
	private Font bookAntiquaRaw;
	private Font bookAntiqua16;
	private Font bookAntiqua25;
	
	AudioInputStream stream;
    Clip clip;
    Sounds menuOpenSound;
    File filePathSound = new File("").getAbsoluteFile();
    
	public Menu(MenuDataArchive inputArchive) throws FontFormatException, IOException {
		super(-2 *Game.WIDTH, 0, inputArchive.getMenuType()); //GameObject constructor.

		String fName = "BookAntiqua.ttf";
	    InputStream is = Menu.class.getResourceAsStream(fName);
	    bookAntiqua = Font.createFont(Font.TRUETYPE_FONT, is);
	    
		if (this.getId() == ID.ChoiceMenu || this.getId() == ID.InventoryMenu || this.getId() == ID.PauseMenu) {
			this.moveable = true;
		} else {
			this.moveable = false;
		}
		
		this.title = inputArchive.getMenuTitle();
		this.archive = inputArchive;
		this.prompt = archive.getPromptString();
		this.menuFunction = archive.getArchFunction();
		this.numChoices = archive.getOptionStrings().length;
		this.choiceStrings = new String[numChoices];
		this.choiceIdArray = new String[numChoices];
		
		for (int i = 0; i < numChoices; i++) {
			int setIndex = numChoices - 1 - i;
			choiceStrings[setIndex] = archive.getOptionStrings()[i][0];
		}
		for (int i = 0; i < numChoices; i++) {
			choiceIdArray[i] = archive.getOptionStrings()[i][1];
		}
		
		if (this.getId() != ID.InventoryMenu) {
			reticle = new Reticle(DRAW_OPTION_X - 50, DRAW_OPTION_Y);
			Game.handler.addObject(reticle);
			setReticleY();
		}

		if (this.getId() == ID.MainMenu ) {
			importLogo();
		}
		try {
            stream = AudioSystem.getAudioInputStream(Menu.class.getResource("pageFlip.wav"));
            clip = AudioSystem.getClip();
           
            menuOpenSound = new Sounds((long) 0.0, clip, "?", stream, filePathSound);
            //System.out.println("sound stuff" + stream + clip + filePathSound + menuOpenSound);
            menuOpenSound.play();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
		}
		Game.gameState = Game.STATES.Menu;	
		System.out.println("Menu howdy");
	}
	
	
	private void setReticleY() {
		if (this.reticle != null) {
			reticle.setTargetY((OPTSTRY - ((numChoices - 1) - selectedChoice) * Y_GAP) - 25);	
		}
	}
	
	protected void drawReticle(Graphics g) {
		g.drawImage(arrow, RETICLEX, reticleY, null);
	}
	
	public void callFunction(String choiceID) {
		menuFunction.processChoice(choiceID);
		//callFunction calls this menu's personal function (menuFunction.) That function calls the functional interface's 
		//only function, processChoice, which is defined for each individual menu. 
	}
	
	/* Takes in player input. */
	public void keyPressed(KeyEvent e) {
		/* This if statement was specifically added to stop you from "skipping" the wake-up 
		 * menu after you sleep IF you press e while you're sleeping.
		 * But it probably also helps in other weird scenarios.
		 */
		if (Game.gameState == Game.STATES.Menu) {
			key = e.getKeyCode(); 
		}
	}
	
	public void tick() {

		
		if (reticle != null) {
			reticle.tick();
		}

		x += velX;
		
		if (this.moveable && !moveXComplete) {
			speedControlX(DRAW_MENU_X);
		} else { ticksSinceCruise++; }
		
		if (this.getId() != ID.InventoryMenu) {
			modifyChoice(key);
			getFinalChoice(key);

			loopSelection();
			setReticleY();
		} else { //this is the inventory
			invCloseHandler();

		}
		/* At the end of each tick, get rid of the input.
		 * This stops bad stuff from happening, like pressing W once sending a menu into
		 * an infinite reticle-movement loop.
		 */
		key = KeyEvent.VK_UNDEFINED;
	}
	
	/* Passes choiceID to this menu's processor, so the processor can decide what to do. */
	private void choiceProcessorMaster(String choiceID) {
		this.callFunction(choiceID);
		}
	
	/* Changes selected option when player presses W or S. */
	private void modifyChoice(int key) {
		if (key == KeyEvent.VK_W) {
			selectedChoice -= 1;
		} else if (key == KeyEvent.VK_S) {
			selectedChoice += 1;
		}
	}
	
	/* Changes how see-through drawn things are.
	 * Alpha = 1 -> fully visible.
	 * Alpha = 0 -> fully invisible.
	 */
	private AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		if (bookAntiqua25 == null) {
			createMenuFonts(g);
		}
		
			g.setFont(bookAntiqua25);
			decideTransparency(g2d);
			drawTitle(g);
			alpha = (float) .93;
			g2d.setComposite(makeTransparent(alpha));
			drawMenuBox(g);
			if (this.getId() != ID.InventoryMenu) {
				decideTransparency(g2d);
				drawPrompt(g);
				drawOptions(g);
			} else if (this.getId() == ID.InventoryMenu) { //this is the inventory 
				g.setFont(bookAntiqua25);
				decideTransparency(g2d);
				decideInventoryStuff(g);
				drawInventory(g);
			}
	
			if (this.reticle != null) {
				reticle.render(g);	
			}
		
		}

	private void getFinalChoice(int key) {
		if (key == KeyEvent.VK_E || key == KeyEvent.VK_ENTER) {
			finalChoice = choiceIdArray[selectedChoice];
			choiceProcessorMaster(finalChoice);
		}
	}
	
	private void loopSelection() {
		if (selectedChoice > numChoices - 1) {
			selectedChoice = 0;
		} else if (selectedChoice < 0) {
			selectedChoice = numChoices - 1;
		}
	}
	private void drawMenuBox(Graphics g) {
		if (this.moveable) {
			g.setColor(Color.black);
			g.fillRoundRect((int) x, DRAW_MENU_Y, MENU_SIZE_X, MENU_SIZE_Y, 50, 50); //the menu itself
			g.setColor(Color.white);
			g.drawRoundRect((int) x, DRAW_MENU_Y, MENU_SIZE_X, MENU_SIZE_Y, 50, 50); //outline
		} else if (this.getId() != ID.MainMenu){
			g.setColor(Color.black);
			g.fillRoundRect(DRAW_MENU_X, DRAW_MENU_Y, MENU_SIZE_X, MENU_SIZE_Y, 50, 50); //the menu itself
			g.setColor(Color.white);
			g.drawRoundRect(DRAW_MENU_X, DRAW_MENU_Y, MENU_SIZE_X, MENU_SIZE_Y, 50, 50); //outline
		} else {
			//g.setColor(Color.black);
			//g.drawRect(-50, -50, Game.WIDTH * 10, Game.HEIGHT * 10);
			//System.out.println("Menu renders main menu box");
		}
	}
	
	private void decideTransparency(Graphics2D g2d) {
		if (moveXComplete || (this.getId() != ID.MainMenu)) {
				double tempAlpha = ((double) ticksSinceCruise) / 60; //bigger denominator here -> slower fade in on text
				alpha = Game.clamp( (float) tempAlpha, (float) 0, (float) 1);
			} else {
				alpha = (float) 1;
			}
			g2d.setComposite(makeTransparent(alpha));
	}
	
	private void drawTitle(Graphics g) {
		if (this.title != null) {
			int titleBoxWidth = 16 * this.title.length();
			int titleBoxHeight = 40;
			g.setColor(Color.black);
			g.fillRoundRect(DRAW_MENU_X + (MENU_SIZE_X) - titleBoxWidth - titleBoxHeight/2,
					DRAW_MENU_Y - titleBoxHeight,
					titleBoxWidth, titleBoxHeight, 10, 10);
			g.setColor(Color.white);
			g.drawRoundRect(DRAW_MENU_X + MENU_SIZE_X - titleBoxWidth - titleBoxHeight/2,
					DRAW_MENU_Y - titleBoxHeight,
					titleBoxWidth, titleBoxHeight, 10, 10);
			g.drawString(this.title, DRAW_MENU_X + MENU_SIZE_X - titleBoxWidth - titleBoxHeight/2 + 5,
					DRAW_MENU_Y - titleBoxHeight/5);
		}
	}
	
	private void drawPrompt(Graphics g) {
		String[] promptWordsArray = prompt.split(" ");
		g.setColor(Color.white);
		//this code takes the string and breaks it up into single words so it can be displayed on multiple lines
		//and not run outside menu bounds
		if (this.getId() == ID.MainMenu) {
			g.drawImage(img, DRAW_MENU_X + 25, DRAW_MENU_Y + 10, null);
		}
		int wordsLengthPixels = 0;
		int printLine = 0;
		for (int j = 0; j < promptWordsArray.length; j++) {
			g.drawString(promptWordsArray[j], DRAW_MENU_X + 30 + wordsLengthPixels, DRAW_MENU_Y + 40 + printLine * 35); //the main story prompt
			wordsLengthPixels += promptWordsArray[j].length() * 18;
			if (promptWordsArray[j].length() <= 4) {
				wordsLengthPixels += 5;
			}
			if (promptWordsArray[j].length() > 7) {
				wordsLengthPixels -= (int) promptWordsArray[j].length() * 3;
			}
			if (wordsLengthPixels >= 530) {
				wordsLengthPixels = 0;
				printLine++;
			}
		}

		
	}
	
	private void createMenuFonts(Graphics g) {
		g.setFont(bookAntiqua);
		Map<TextAttribute, Object> attributes = new HashMap<>();
		bookAntiquaRaw = g.getFont();
		attributes.put(TextAttribute.FAMILY, bookAntiquaRaw.getFamily());
		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM);
		attributes.put(TextAttribute.SIZE, (int) (bookAntiquaRaw.getSize() * 25));
		bookAntiqua25 = Font.getFont(attributes);
		Map<TextAttribute, Object> attributes2 = new HashMap<>();
		attributes.put(TextAttribute.FAMILY, bookAntiquaRaw.getFamily());
		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM);
		attributes.put(TextAttribute.SIZE, (int) (bookAntiquaRaw.getSize() * 16));
		bookAntiqua16 = Font.getFont(attributes2);
	}
	private void decideInventoryStuff(Graphics g) {
		switch (Inventory.getBestClothes().cleanliness) { //check quality of CURRENT clothes, i.e. best
		case 2: 
			currentQualityColor = Color.green;
			clothesCurrentString = "Fresh.";
			break;
		case 1: 
			currentQualityColor = Color.yellow;
			clothesCurrentString = "Used.";
			break;
		case 0:
			currentQualityColor = Color.red;
			clothesCurrentString = "Disgusting.";
			break;
			
		}
	}
	private void decideClothesColor(Graphics g, Clothes c) { //decide the color of a single clothes instace
		switch (c.cleanliness) {
		case 2:
			g.setColor(Color.green);
			break;
		case 1:
			g.setColor(Color.yellow);
			break;
		case 0: 
			g.setColor(Color.red);
			break;
		}
	}
	private void drawInventory(Graphics g) {
		/* Clothes. */
		g.drawString("Clothes: " + Inventory.clothesList.size() + "/" + Inventory.clothesLimit, DRAW_MENU_X + 5, DRAW_MENU_Y + 50);
		g.drawString("Current Cleanliness:", DRAW_MENU_X + 5, DRAW_MENU_Y + 75);
		g.setColor(currentQualityColor);
		g.drawString(clothesCurrentString, DRAW_MENU_X + 240, DRAW_MENU_Y + 75);
		for (int i = 0; i < Inventory.clothesList.size(); i ++) {
			decideClothesColor(g, Inventory.clothesList.get(i));
			Clothes clth = Inventory.clothesList.get(i);
			g.drawString(clth.name, DRAW_MENU_X + 5, DRAW_MENU_Y + 115 + 20*i);
			if (clth.equals(Inventory.getBestClothes())) {
				g.drawString("<-- Current Outfit", DRAW_MENU_X + 200, DRAW_MENU_Y + 115 + 20*i);
			}
		}
		/* Bike. */
		if (Inventory.hasBike) {
			g.setColor(Color.white);
			g.drawString("You have a bike.", DRAW_MENU_X + 420, DRAW_MENU_Y + 300);
		}
		
		/* Finances */
		g.setColor(Color.white);
		long creditScore = Finance.getCreditScore();
		String interestString = String.format("%.2f", Finance.getInterestRate());
		g.drawString("Credit Score: " + creditScore, DRAW_MENU_X + MENU_SIZE_X - 250, DRAW_MENU_Y + 30);
		g.drawString("Credit Limit: $" + Finance.getCreditLimit(), DRAW_MENU_X + MENU_SIZE_X - 250, DRAW_MENU_Y + 60);
		g.drawString("Interest Rate: " + interestString + "%", DRAW_MENU_X + MENU_SIZE_X - 250, DRAW_MENU_Y + 90);
		
		/* Strikes */
        for (Job job : JobFunction.jobList) {
            if (job.strikes > 0) {
            	System.out.println("drawing jobs strikes in inv");
                int strikesNumber = job.strikes;
                g.drawString("# of Strikes: " + strikesNumber, DRAW_MENU_X + MENU_SIZE_X - 160,
                        DRAW_MENU_Y + MENU_SIZE_Y - 160);
            }
        }
	}
	
	private void importLogo() {
		try {
			img = ImageIO.read(getClass().getResource("titleLogo.png"));
		} catch (IOException e) {
		}
	}
	
	private void drawOptions(Graphics g) {
		for (int i = 0; i < numChoices; i++) {
			Color offWhite = new Color(190, 190, 190); // Color white
			g.setColor(offWhite);
			if (i == (numChoices - 1 - selectedChoice)) {
				g.setColor(Color.white);
			}
			g.drawString(choiceStrings[i], OPTSTRX, OPTSTRY + i*(-Y_GAP));  //draw each option's string
			drawReticle(g);
		}
	}
	
	private void invCloseHandler() {
		if (key == KeyEvent.VK_Q) {
			Game.gameState = Game.STATES.Playing;
			PlayerInput.invMenu = null;
			Game.thisGame.removeKeyListener(this);
			Game.handler.removeObject(this);
		}
	}
	
	/* Don't worry about these. */
	
	@Override
	public Rectangle getBounds() {
		return null;
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}


	@Override
	public void keyTyped(KeyEvent arg0) {
	} 

}
