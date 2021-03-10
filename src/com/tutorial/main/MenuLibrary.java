package com.tutorial.main;

import java.awt.FontFormatException;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//One instance of this class will be made. That class will have as variables...every piece of menu data, for easy,
//object oriented retrieval. Yes.
public class MenuLibrary {
	public Menu resultMenu;
	
	AudioInputStream stream;
    Clip clip;
    Sounds cityNoise;
    File filePathSound = new File("").getAbsoluteFile();
    
	AudioInputStream stream2;
    Clip clip2;
    Sounds menuOpenSound;
    File filePathSound2 = new File("").getAbsoluteFile();
    
    public MoneyEffect moneyEffectML;
    private Random r;
    
    private String invTitle = "Inventory";
    private String invPrompt = "";
    private String[][] invOptions = {};
    private ChoiceProcessorInterface invProcessor;
    //private ID inventoryMenuType = ID.InventoryMenu;
    public MenuDataArchive invArchive;
    
    private String pausePrompt = "Paused.";
    private String[][] pauseOptions = { {"Resume", "leave"}, {"Save Game", "saveGame"}, {"Quit without Saving", "quitNoSave"}, {"Save and Quit", "saveAndQuit"} };
    private ChoiceProcessorInterface pauseProcessor;
    public MenuDataArchive pauseArchive;
    
	private String mainMenuPrompt = ""; //this is replaced by the logo image
	private String[][] mainMenuOptions = {
			{"Begin", "mainMenuBegin"},
			{"Resume", "mainMenuResume"},
			{"Quit", "mainMenuQuit"} };	
	private ChoiceProcessorInterface mainMenuProcessor;
	public MenuDataArchive mainMenuArchive;
	
	private String creditDeniedPrompt = "You swipe your credit card and the register flashes \"DENIED\". " + 
	" Looks like you won't be buying anything today.";
	private String[][] creditDeniedOptions = { {"Okay", "leave"} };
	private ChoiceProcessorInterface creditDeniedProcessor;
	public MenuDataArchive creditDeniedArchive;
	
	 private String burgerTitle = "McSpanky's Burgers";
    private String burgerPrompt = "It smells like fast food and grease in here. But it's cheap, and they're hiring.";
    private String[][] burgerOptions = { {"Buy a Meal ($4.99)", "buyBurgerFood"},
            {"Apply for a Job", "applyBurgerJob"},
            {"Leave", "leaveBurger"} };
    private ChoiceProcessorInterface burgerProcessor;
    public MenuDataArchive burgerArchive;
   
    private String burger0Prompt = "The fast food is tasty, but not great for your health.";
    private String[][] burger0Options = { {"Okay", "burger0Leave"} };
    private ChoiceProcessorInterface burger0Processor;
    public MenuDataArchive burger0Archive;
   
    private String burger1Prompt =  "After an interview with the manager, you're handed your schedule for the first week."
            + " Not exactly convenient, but you suppose you can make time. (9 - 5 Every Day)";
    private String[][] burger1Options = { {"Happy to McServe you, sir.", "startBurgerJob"} };
    private ChoiceProcessorInterface burger1Processor;
    public MenuDataArchive burger1Archive;
   
    private String burger2Title = "Welcome McWorker!";
    private String burger2Prompt = "Make sure you sign in at your assigned shift time, 9-5 every day";
    private String[][] burger2Options = { {"Sign in to shift", "signIn"}, {"Buy a Meal ($3.50 with employee discount)", "buyBurgerFood"},
            {"Quit Job", "quit"}, {"Exit Menu", "burger2Leave"}};
    private ChoiceProcessorInterface burger2Processor;
    public MenuDataArchive burger2Archive;
   
    private String burgerSignOut =  "Good job finishing your shift! Your wallet is happy but your energy levels certainly aren't";
    private String[][] burgerSOOptions = { {"Exit", "exitBurger"} };
    private ChoiceProcessorInterface burgerSOProcessor;
    public MenuDataArchive burgerSOArchive;
   
    private String burgerOffTitle = "Off-shift menu";
    private String burgerOffPrompt =  "You're off your shift! Use your employee discount to buy a burger";
    private String[][] burgerOffOptions = { {"Buy a Meal ($3.50)", "buyBurgerFood"}, {"Quit Job", "quitBurgerJob"}, {"Okay", "burgerOffLeave"}};
    private ChoiceProcessorInterface burgerOffProcessor;
    public MenuDataArchive burgerOffArchive;
   
    private String burgerFiredTitle = "McSpanky's Burgers";
    private String burgerFPrompt = "It smells like fast food and grease in here. But it's cheap. You cannot work for a place that you've been fired from";
    private String[][] burgerFOptions = { {"Buy a Meal ($4.99)", "buyBurgerFood"},
            {"Leave", "leaveBurger"} };
    private ChoiceProcessorInterface burgerFProcessor;
    public MenuDataArchive burgerFArchive;

	private String apartmentPrompt = "It's your apartment.";
	private String[][] apartmentOptions = { {"Sleep", "sleepApartment"}, {"Leave", "leave"} };
	private ChoiceProcessorInterface apartmentProcessor;
	public MenuDataArchive apartmentTopArchive;
	
	public static SleepEffect sleepEffect;
	
	private String apartmentTitle = "Your Apartment";
	private String apartment0Prompt = "You wake up, feeling not quite as rested as you'd like.";
	private String[][] apartment0Options = { {"Leave apartment", "leave"} };
	private ChoiceProcessorInterface apartment0Processor;
	public MenuDataArchive apartment0Archive;
	//apartment logic variables
	private int nextMorning;
	private int timeSlept;
	
	public MenuDataArchive apartmentArchive;
	private String collegeTitle = "S.U. Community College";
	private String collegePrompt = "The local University's admissions office. They have pamphlets that detail the costs of "
			+ "attendance. It will not be cheap.";
	private String[][] collegeOptions = { {"Pay Tuition($3,000)", "payTuition"}, {"Leave", "leave"} };
	private ChoiceProcessorInterface collegeProcessor;
	public MenuDataArchive collegeArchive;
	
	private String college0Prompt = "As you sign the paperwork, you feel hope for the first time in a long time. You believe"
			+ " things are going to get better.";
	private String[][] college0Options = { {"Thanks for Playing!", "leave"} };
	private ChoiceProcessorInterface college0Processor;
	public MenuDataArchive college0Archive;
	
	private String walmartTitle = "Wolmart";
	private String walmartPrompt = "The hum of fluorescent light bulbs and off white paint tone are the first things you"
			+ " notice as you walk in, but they fade into the background as you browse the aisles.";
	private String[][] walmartOptions = {
			{"Buy 1 day of groceries ($10)", "buy1Grocery"},
			{"Buy 7 days of groceries ($40)", "buy7Grocery"}, 
			{"Buy a Bike", "buyBike"},
			{"Leave", "leave"}
	};
	
	private String[][] walmartBikeOptions = {
			{"Buy 1 day of groceries ($10)", "buy1Grocery"},
			{"Buy 7 days of groceries ($40)", "buy7Grocery"}, 
			{"Leave", "leave"}
	};
	
	private ChoiceProcessorInterface walmartBikeProcessor;
	public MenuDataArchive walmartBikeArchive;
	
	private String bikePrompt = "Stand still and press SHIFT to get on/off the bike. The bike lets you move quickly in one direction.";
	private String bikeOptions[][] = { {"Okay", "leave"} };
	private ChoiceProcessorInterface bikeProcessor;
	public MenuDataArchive bikeArchive;
			
	
	private ChoiceProcessorInterface walmartProcessor;
	public MenuDataArchive walmartArchive;
	
	private String hospitalTitle = "M.D. Anderson Clinic";
	private String hospitalPrompt = "It's the hospital reception room.";
    private String[][] hospitalOptions = { {"Check Up ($500/2 Hours)", "checkUp"}, {"Leave", "leave"} };
    private ChoiceProcessorInterface hospitalProcessor;
    public MenuDataArchive hospitalArchive;
   
    private String hospital0Prompt = "You feel much better after your checkup.";
    private String[][] hospital0Options = { {"Leave", "leave"} };
    private ChoiceProcessorInterface hospital0Processor;
    public MenuDataArchive hospital0Archive;
	   
    private String eventFindMoneyPrompt = "You find a few coins on the ground.";
    private String[][] eventFindMoneyOptions = { {"How Lucky!", "leave"} };
    private ChoiceProcessorInterface eventFindMoneyProcessor;
    public MenuDataArchive eventFindMoneyArchive;
    
    private String eventCollapsePrompt = "You feel really tired. Really tired. You're going to lay down for a second. Just a quick second.";
    private String[][] eventCollapseOptions = { {"A quick rest...", "collapse"} };
    private ChoiceProcessorInterface eventCollapseProcessor;
    public MenuDataArchive eventCollapseArchive;
    
    private String eventCollapse0Prompt = "Your whole body is stiff. You fell asleep outside. And didn't you have more money on you yesterday?";
    private String[][] eventCollapse0Options = { {"Try to get more sleep next time.", "leave"} };
    private ChoiceProcessorInterface eventCollapse0Processor;
    public MenuDataArchive eventCollapse0Archive;
    
    private String eventCollapse0NoMoneyPrompt = "Your whole body is stiff. You fell asleep outside.";
    private String[][] eventCollapse0NoMoneyOptions = { {"Try to get more sleep next time.", "leave"} };
    private ChoiceProcessorInterface eventCollapse0NoMoneyProcessor;
    public MenuDataArchive eventCollapse0NoMoneyArchive;
    
    private String kickedOutPrompt = "As soon as you walk in, you're practically chased out by an employee. They make a rude comment about the state"
    		+ " of your clothes before shutting the door in your face."; 
    private String[][] kickedOutOptions = { {"Leave", "leave"} };
    private ChoiceProcessorInterface kickedOutProcessor;
    public MenuDataArchive kickedOutArchive;
    
    public String deathPrompt = "You passed away.     Poverty is more than a game for millions worldwide. Select "
    		+ " \"Learn More\" to see how you can help.";
    private String[][] deathOptions = { {"Try Again", "tryAgain"}, {"Learn More", "learnMore"}, {"Close Game", "closeGame"} };
    private ChoiceProcessorInterface deathProcessor;
    public MenuDataArchive deathArchive; 
    
    // gives you tutorial information
    public String torenPrompt = "Hey, I'm Toren. Are you new here? I can give you some helpful advice.";
    private String[][] torenOptions = { {"Finance", "financeTut"}, {"Clothing", "clothingTut"},{"What should I do?", "goalTut"}, {"Leave", "leave"} };
    private ChoiceProcessorInterface torenProcessor;
    public MenuDataArchive torenArchive;
    
    private String toren0Prompt = "Your credit score determines how much money you can borrow, and how much interest you are charged each day. Your credit score goes down when you're in debt, and increases when you're out of debt. You can check it in your inventory (press Q!) ";
    private String[][] toren0Options = { {"I feel financially literate", "leave"} };
    private ChoiceProcessorInterface toren0Processor;
    public MenuDataArchive toren0Archive;
    
    public String toren1Prompt = "You always wear the best set of clothes you have. After each day, the clothes you wore will get dirtier. If your clothes are too dirty, some places won't let you in. You can get them cleaned at a laundromat. The more clothes you have, the less often you need to get them all cleaned.";
    private String[][] toren1Options = { {"I guess appearances matter to some", "leave"} };
    private ChoiceProcessorInterface toren1Processor;
    public MenuDataArchive toren1Archive;
    
    private String toren2Prompt = "You should probably get a job. There are some restaurants and gas stations around here where you could get hired pretty easily. Make sure you don't miss too many shifts, or you'll get fired. If you don't want to do that for the rest of your life, you'll probably want to save up money to go to college.";
    private String[][] toren2Options = { {"Time to enter the workforce", "leave" } };
    private ChoiceProcessorInterface toren2Processor;
    public MenuDataArchive toren2Archive;
    
    private String laundromatPrompt = "You can get your clothes cleaned here.";
    private String[][] laundromatOptions = {
            {"Do laundry ($.25)", "doLaundry"},
            {"Leave", "leave"} };
    private ChoiceProcessorInterface laundromatProcessor;
   
    public MenuDataArchive laundromatArchive;
   
    private String laundromat0Prompt = "Your clothes look and smell respectable now.";
    private String[][] laundromat0Options = { {"Leave", "leave"} };
    private ChoiceProcessorInterface laundromat0Processor;
   
    public MenuDataArchive laundromat0Archive;
    
    private String gasStationPrompt = "It's a gas station on the edge of town.";
    private String [][] gasStationOptions = {
            {"Buy some snacks ($5)", "buySnacks"},
            {"Leave", "leave"}
    };
    private ChoiceProcessorInterface gasStationProcessor;
   
    public MenuDataArchive gasStationArchive;
   
    private String gasStation0Prompt = "The snacks are tasty, but not very filling.";
    private String[][] gasStation0Options = { {"Leave", "leave"} };
    private ChoiceProcessorInterface gasStation0Processor;
   
    public MenuDataArchive gasStation0Archive;
   
    private String clothingStorePrompt = "A nice clothing store. It's got a soft carpet.";
    private String[][] clothingStoreOptions = { {"Buy Clothes ($30)", "buyClothes"}, {"Leave", "leave"} };
    private ChoiceProcessorInterface clothingStoreProcessor;
    
    public MenuDataArchive clothingStoreArchive;
    
    private String clothingStore0Prompt = "You purchase a nice set of clothes.";
    private String[][] clothingStore0Options = { {"Nice", "leave"} };
    private ChoiceProcessorInterface clothingStore0Processor;
    //use toren2Processor
    
    public MenuDataArchive clothingStore0Archive;
    
    private String coffeeShopPrompt = "A coffee shop. It's got plants in the windows and a clean floor. What more can you ask for?";
    private String[][] coffeeShopOptions = {
            {"Buy cup of coffee ($3.00)	", "buyCoffee"},
            {"Leave", "leave"}
    };
    private ChoiceProcessorInterface coffeeShopProcessor;
   
    public MenuDataArchive coffeeShopArchive;
   
    private String coffeeShop0Prompt = "The coffee smells great. You feel full of energy, and your heart rate is through the roof.";
    private String[][] coffeeShop0Options = { {"Leave", "leave"} };
    private ChoiceProcessorInterface coffeeShop0Processor;
   
    public MenuDataArchive coffeeShop0Archive;
    
    private String eventBikeStolenPrompt = "Your bike is missing from where you parked it last night. The chain you used to lock it is broken.";
    private String[][] eventBikeStolenOptions = { {"A hundred bucks down the drain.", "leave"} };
    private ChoiceProcessorInterface eventBikeStolenProcessor;
    
    public MenuDataArchive eventBikeStolenArchive;
    
    private String eventGetSickPrompt = "You start coughing. Your throat burns and you have a terrible headache. It seem like you're sick. You'd better try to see a doctor soon.";
    private String[][] eventGetSickOptions = { {"Okay", "leave"} };
    private ChoiceProcessorInterface eventGetSickProcessor;
    public MenuDataArchive eventGetSickArchive;
    
    private String eventStrikesPrompt =  "Oops! You're late to work (2 hourrs past shift start). You recieved a strike!"
            + "You get 3 strikes at a job before you will be fired! Be careful!";
    private String[][] eventStrikesOptions = { {"Understood", "exitStrikes"} };
    private ChoiceProcessorInterface eventStrikesProcessor;
    public MenuDataArchive eventStrikesArchive;
    
    private URI oxfam;
    
    MenuLibrary() {
    	
    	this.pauseProcessor = (String choiceIDPr) -> {
    		switch (choiceIDPr) {
    		case "leave":
    			closePauseMenu();
    			break;
    		case "saveGame":
    			saveGame();
    			try {
    	            stream2 = AudioSystem.getAudioInputStream(Menu.class.getResource("pageFlip.wav"));
    	            clip2 = AudioSystem.getClip();
    	           
    	            menuOpenSound = new Sounds((long) 0.0, clip2, "?", stream2, filePathSound2);
    	            //System.out.println("sound stuff" + stream + clip + filePathSound + menuOpenSound);
    	            menuOpenSound.play();
    			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
    	        // TODO Auto-generated catch block
    	        e1.printStackTrace();
    			}
    			break;
    		case "quitNoSave":
    			System.exit(1);
    			break;
    		case "saveAndQuit":
    			saveGame();
    			try {
    	            stream2 = AudioSystem.getAudioInputStream(Menu.class.getResource("pageFlip.wav"));
    	            clip2 = AudioSystem.getClip();
    	           
    	            menuOpenSound = new Sounds((long) 0.0, clip2, "?", stream2, filePathSound2);
    	            //System.out.println("sound stuff" + stream + clip + filePathSound + menuOpenSound);
    	            menuOpenSound.play();
    			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
    	        // TODO Auto-generated catch block
    	        e1.printStackTrace();
    			}
    			System.exit(1);
    			break;
    		}	
    	};
    	
    	pauseArchive = new MenuDataArchive(pausePrompt, pauseOptions, pauseProcessor, ID.PauseMenu, null);
    	this.bikeProcessor = (String choiceIDPr) -> {
    		switch (choiceIDPr) {
    		case "leave":
    			closeResultMenu();
    			break;
    		}
    	};
    	bikeArchive = new MenuDataArchive(bikePrompt, bikeOptions, bikeProcessor, ID.ResultMenu, null);
    	this.eventBikeStolenProcessor = (String choiceIDPr) -> {
    		switch (choiceIDPr) {
    		case "leave":
    			Inventory.hasBike = false;
    			Game.walmartBuilding.setMenu(walmartArchive);
    			closeEvent();
    			break;
    		}
    	};
    	
    	this.eventGetSickProcessor = (String choiceIDPr) -> {
    		switch(choiceIDPr) {
    			case "leave":
    				HUD.isSick = true;
    				closeEvent();
    				break;
    		}
    	};
    	
  
    	eventGetSickArchive = new MenuDataArchive(eventGetSickPrompt, eventGetSickOptions, eventGetSickProcessor, ID.ResultMenu, null);
    	eventBikeStolenArchive = new MenuDataArchive(eventBikeStolenPrompt, eventBikeStolenOptions, eventBikeStolenProcessor, ID.ResultMenu, null);
    	this.clothingStoreProcessor = (choiceIDPr) -> {
    		switch (choiceIDPr) {
    		case "buyClothes":
    			if (Inventory.clothesList.size() < 14 && Finance.tryBuy(30)) {
    				Inventory.addClothes();
    				try {
						resultMenu = new Menu(clothingStore0Archive);
					} catch (FontFormatException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				makeResultMenu();
    				closeOldMenu();
    			} else {
    				System.out.println("cant buy clothes");
    			}
    		break;
    		case "leave":
    			closeOldMenu();
    			break;
    		}
    	};
    	
    	clothingStore0Processor = (String choiceIDPr) -> {
    		switch (choiceIDPr) {
    		case "leave":
    			closeResultMenu();
    			break;
    		}
    	};
    	
    	clothingStoreArchive = new MenuDataArchive(clothingStorePrompt, clothingStoreOptions, clothingStoreProcessor, ID.ChoiceMenu, "Fabulous Fabrics");
    	clothingStore0Archive = new MenuDataArchive(clothingStore0Prompt, clothingStore0Options, clothingStore0Processor, ID.ResultMenu, null);
    	
    	r = new Random();
    	this.torenProcessor= (String choiceIDPr)-> {
    		switch(choiceIDPr) {
    		case "financeTut":
    			try {
					resultMenu = new Menu(toren0Archive);
				} catch (FontFormatException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			makeResultMenu();
    			closeOldMenu();
    			//open financetut menu
    			break;
    		case "clothingTut":
    			try {
					resultMenu = new Menu(toren1Archive);
				} catch (FontFormatException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			makeResultMenu();
    			closeOldMenu();
    			//open clothingTutmenu
    			break;
    		case "goalTut":
    			try {
    				resultMenu = new Menu(toren2Archive);
    			} catch (Exception e) {
    				System.out.println(e + "torenProcessor");
    			}
    			makeResultMenu();
    			closeOldMenu();
    			break;
    		case "leave":
    			//leave
    			closeOldMenu();
    			break;
    		}
    	};
    	this.torenArchive = new MenuDataArchive(torenPrompt, torenOptions, torenProcessor, ID.ChoiceMenu, "Toren the Helpful");
    	
    	this.toren0Processor = (String choiceIDPr) -> {
    		switch (choiceIDPr) {
    		case "leave":
    			closeResultMenu();
    			break;
    		}
    	};
    	this.toren0Archive = new MenuDataArchive(toren0Prompt, toren0Options, toren0Processor, ID.ResultMenu, null);
    	
    	this.toren1Processor = (String choiceIDPr) -> {
    		switch (choiceIDPr) {
    		case "leave":
    			closeResultMenu();
    			break;
    		}
    	};
    	
    	this.toren1Archive = new MenuDataArchive(toren1Prompt, toren1Options, toren1Processor, ID.ResultMenu, null);
    	
    	this.toren2Processor = (choiceIDPr) -> {
    		switch (choiceIDPr) {
    		case "leave":
    			closeResultMenu();
    		}
    	};
    	
    	this.toren2Archive = new MenuDataArchive(toren2Prompt, toren2Options, toren2Processor, ID.ResultMenu, null); 
    			
    	this.invArchive = new MenuDataArchive(invPrompt, invOptions, invProcessor, ID.InventoryMenu, invTitle);
    	
		this.mainMenuProcessor = (String choiceIDPr) -> {
			System.out.println("choice ID = " + choiceIDPr);
			switch (choiceIDPr) {
			case "mainMenuBegin":
				try {
					Game.thisGame.gameMapSetup();
					//HUD.rawtTime = fromFileTime
					//Hud.Money = fromFileMoney
					//...
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					Game.mainMenuOpen = false;
					Game.gameState = Game.STATES.Playing;
					Game.handler.removeObject(Game.mainMenu);
					Game.thisGame.removeKeyListener(Game.mainMenu);
					HUD.hungerFullnessDelayedX = 200;
					
					try {
                         stream = AudioSystem.getAudioInputStream(PlayerInput.class.getResource("cityAmbience.wav"));
                         clip = AudioSystem.getClip();
                        
                         cityNoise = new Sounds((long) 0.0, clip, "?", stream, filePathSound);

                     cityNoise.loop();
                 } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                 }
            
					break;
				case "mainMenuResume":
					try {
	                    Object obj = new JSONParser().parse(new FileReader("gamedata.json"));
	                    JSONObject jo = (JSONObject) obj;
	                    HUD.health = (double) jo.get("health");
	                    HUD.energy = (double) jo.get("energy");
	                    HUD.hunger = (double) jo.get("hunger");
	                    HUD.money = (double) jo.get("money");
	                    HUD.rawTimeElapsed = (long) jo.get("time");
	                    Inventory.hasBike = (boolean) jo.get("bike");
	                    HUD.isSick = (boolean) jo.get("sick");
	                    long toSetCreditScore = (long) jo.get("creditScore");
	                    Game.finance.setCreditScore(toSetCreditScore);
	                    
	                    long clothesToAdd = (long) jo.get("clothes") - 1;
	                    for (int i = 0; i < clothesToAdd; i++) {
	                    	Inventory.addClothes();
	                    }
	                    //Inventory.clothesList = (ArrayList<Clothes>) jo.get("clothesList");
	                    //Inventory.clothesFull = (boolean) jo.get("clothes");
	                    Game.mainMenuOpen = false;
	                    Game.gameState = Game.STATES.Playing;
	                    Game.handler.removeObject(Game.mainMenu);
	                    Game.thisGame.removeKeyListener(Game.mainMenu);
	                    Game.thisGame.gameMapSetup();
	                } catch (Exception e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
					break;
				case "mainMenuQuit":
					System.out.println("GAME QUIT DEBUG");
					System.exit(1);
					break;
			}
		};
		
		mainMenuArchive = new MenuDataArchive(mainMenuPrompt, mainMenuOptions, mainMenuProcessor, ID.MainMenu, null);
		
		this.creditDeniedProcessor = (String choiceIDPr) -> {
			switch (choiceIDPr) {
			case "leave":
				closeResultMenu();
				break;
			}
		};
		
		creditDeniedArchive = new MenuDataArchive(creditDeniedPrompt, creditDeniedOptions, creditDeniedProcessor, ID.ResultMenu, null);
		//=====================================================================================================================
		
		this.burgerProcessor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "buyBurgerFood" :
                if (Finance.tryBuy(4.99)) {
                    try {
                        resultMenu = new Menu(burger0Archive);
                    } catch (FontFormatException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    makeResultMenu();
                    closeOldMenu();
                    HUD.hunger += 10;
                    HUD.health -= 1;
                } else {
                    //play sound
                    System.out.println("No money.");
                }
               
                break;
            case "applyBurgerJob" :
                try {
                    resultMenu = new Menu(burger1Archive);
                   
                } catch (FontFormatException | IOException e) {
                    e.printStackTrace();
                }
                makeResultMenu();
                closeOldMenu();
                break;
            case "leaveBurger" :
                closeOldMenu();
                closeResultMenu();
                break;
            }
        };
       
        this.burger0Processor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "burger0Leave" :
                closeResultMenu();
                break;
            }
        };
       
        this.burger1Processor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "startBurgerJob" :
                System.out.println("starting burger job");
                closeResultMenu();
                JobFunction.jobList.get(0).setHasJob(true);
                break;
            }
        };
       
        this.burger2Processor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "signIn" :
                System.out.println("signing into job");
               
                if (Game.jobFunction.jobList.get(0).onJob()) {
                    int fivePM = (int) ((17 * 60 * HUD.gameSlowFactor) + ((HUD.day - 1) * 1440 * HUD.gameSlowFactor));
                    double timeWorked = fivePM - HUD.rawTimeElapsed;
                    double pay = (timeWorked * 8.5) / (60 * HUD.gameSlowFactor);
                    HUD.rawTimeElapsed = fivePM; //timelapses to 5pm
                    System.out.println("Signed out of job");
                    try {
                        resultMenu = new Menu(burgerSOArchive);
                    } catch (FontFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Game.moneyChangeCause = "Worked at Job";
                    HUD.money += pay;//pay should be dependent on how many hours worked
                    HUD.hunger -= (HUD.METABOLISM / HUD.gameSlowFactor) * timeWorked;
                    HUD.energy -= (HUD.ENERGY_LOSS / HUD.gameSlowFactor) * timeWorked;
                    makeResultMenu(); //shows a menu after timelapse
                    closeOldMenu();
                   
                }
                break;
            case "buyBurgerFood" :
                if (Finance.tryBuy(3.50)) {
                    try {
                        resultMenu = new Menu(burger0Archive);
                    } catch (FontFormatException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    makeResultMenu();
                    closeOldMenu();
                    HUD.hunger += 10;
                } else {
                    //play sound
                    System.out.println("No money.");
                }
               
                break;
            case "quit" :
                System.out.println("quit job");
                JobFunction.jobList.get(0).fired();
                JobFunction.jobList.get(0).getBuilding().setMenu(Game.library.burgerFArchive);
                closeOldMenu();
                closeResultMenu();
                 //for now, future: lead back to old menu & give quit message
                break;
            case "burger2Leave" :
                closeOldMenu();
                closeResultMenu();
                break;
            }
        };
       
        this.burgerSOProcessor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "exitBurger" :
                closeResultMenu();
                break;
            }
        };
       
        this.burgerOffProcessor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "buyBurgerFood" :
                if (Finance.tryBuy(3.50)) {
                    try {
                        resultMenu = new Menu(burger0Archive);
                    } catch (FontFormatException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    makeResultMenu();
                    closeOldMenu();
                    HUD.hunger += 10;
                } else {
                    //play sound
                    System.out.println("No money.");
                }
                break;
            case "burgerOffLeave" :
                closeOldMenu();
                closeResultMenu();
                break;
            case "quitBurgerJob" :
                closeOldMenu();
                closeResultMenu();
                break; //temporarily until quitting system is ok.
            }
           
        };
       
        this.burgerFProcessor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "buyBurgerFood" :
                if (Finance.tryBuy(4.99)) {
                    try {
                        resultMenu = new Menu(burger0Archive);
                    } catch (FontFormatException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    makeResultMenu();
                    closeOldMenu();
                    HUD.hunger += 10;
                } else {
                    //play sound
                    System.out.println("No money.");
                }
               
                break;
           
            case "leaveBurger" :
                closeOldMenu();
                closeResultMenu();
                break;
            }
        };
       
        burger0Archive = new MenuDataArchive(burger0Prompt, burger0Options, burger0Processor, ID.ResultMenu, null);
        burger1Archive = new MenuDataArchive(burger1Prompt, burger1Options, burger1Processor, ID.ResultMenu, null);
        burgerSOArchive = new MenuDataArchive(burgerSignOut, burgerSOOptions, burgerSOProcessor, ID.ResultMenu, null);
        burger2Archive = new MenuDataArchive(burger2Prompt, burger2Options, burger2Processor, ID.ChoiceMenu, burger2Title);
        burgerOffArchive = new MenuDataArchive(burgerOffPrompt, burgerOffOptions, burgerOffProcessor, ID.ChoiceMenu, burgerOffTitle);
        burgerArchive = new MenuDataArchive(burgerPrompt, burgerOptions, burgerProcessor, ID.ChoiceMenu, burgerTitle);
        burgerFArchive = new MenuDataArchive(burgerFPrompt, burgerFOptions, burgerFProcessor, ID.ChoiceMenu, burgerFiredTitle);
		this.apartmentProcessor = (String choiceIDPr) -> {
			switch (choiceIDPr) {
			case "sleepApartment" :
				try {
					cityNoise.stop();
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
				}
				try {
					resultMenu = new Menu(apartment0Archive);
				} catch (FontFormatException | IOException e) {
					e.printStackTrace();
				}
				sleepEffect = new SleepEffect(0, 0, ID.SleepEffect);
				PlayerInput.caffeinated = false;
				Game.handler.addObject(sleepEffect);
				Game.gameState = Game.STATES.Sleeping;
				if (HUD.hoursSinceMidnight > 6) {
            		nextMorning = (int) ((8 * 60 * HUD.gameSlowFactor) + (HUD.day * 1440 * HUD.gameSlowFactor) - 1);
        		} else {
        			nextMorning = (int) ((8 * 60 * HUD.gameSlowFactor) + ((HUD.day - 1) * 1440 * HUD.gameSlowFactor) - 1);
        		}
				//player wakes up at 7:59 AM so they have time to make their 8 AM rent payment
				timeSlept = (int) (nextMorning - HUD.rawTimeElapsed);
        		HUD.energy += SleepEffect.getEnergyRestored();
				HUD.rawTimeElapsed = nextMorning; //set gameTime to the next morning
				makeResultMenu();
				closeOldMenu();
				break;
			case "leave" :
				closeOldMenu();
				break;
			}
		};
	

		
		this.apartment0Processor = (String choiceIDPr) -> {
			switch (choiceIDPr) {
			case "leave" :
				HUD.hungerLoss = (float) (timeSlept * (HUD.METABOLISM / HUD.gameSlowFactor) / 2);
				HUD.health -= (HUD.hungerPain * timeSlept) / 6;
				HUD.hunger -= (double) HUD.hungerLoss;
				EventMaker.justWokeUp = true;
				Inventory.dirtyClothes();
				closeResultMenu();
				try {
					cityNoise.loop();
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		};
		
		apartmentArchive = new MenuDataArchive(apartmentPrompt, apartmentOptions, apartmentProcessor, ID.ChoiceMenu, apartmentTitle);
		apartment0Archive = new MenuDataArchive(apartment0Prompt, apartment0Options, apartment0Processor, ID.ResultMenu, null);
		
		this.collegeProcessor = (String choiceIDPr) -> {
			switch (choiceIDPr) {
			case "payTuition" :
				if (Finance.tryBuy(3000)) {
					try {
						resultMenu = new Menu(college0Archive);
					} catch (FontFormatException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					makeResultMenu();
					closeOldMenu();
				}				
				break;
			case "leave":
				closeOldMenu();
				break;
			}
		};
		
		collegeArchive = new MenuDataArchive(collegePrompt, collegeOptions, collegeProcessor, ID.ChoiceMenu, collegeTitle);
		
		this.college0Processor = (String choiceIDPr) -> {
			switch (choiceIDPr) {
			case "leave" :
				closeResultMenu();
				System.exit(1);
				break;
			}
		};
		
		college0Archive = new MenuDataArchive(college0Prompt, college0Options, college0Processor, ID.ResultMenu, null);
		 this.coffeeShopProcessor = (String choiceIDPr) -> {
             switch (choiceIDPr) {
             case "buyCoffee" :
            	 if (Finance.tryBuy(3)) {
            		 PlayerInput.caffeinated = true;
            		 PlayerInput.biking = false;
            		 HUD.energy += 5;
                     try {
                         resultMenu = new Menu(coffeeShop0Archive);
                     } catch (FontFormatException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                     } catch (IOException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                     }
                     makeResultMenu();
                     closeOldMenu();
            	 }

                 break;
             case "leave" :
                 closeOldMenu();
                 break;
             }
         };
        
         coffeeShopArchive = new MenuDataArchive(coffeeShopPrompt, coffeeShopOptions, coffeeShopProcessor, ID.ChoiceMenu, "Made With Java");
        
         this.coffeeShop0Processor = (String choiceIDPr) -> {
             switch (choiceIDPr) {
             case "leave" :
                 closeResultMenu();
                 break;
             }
         };
        
        coffeeShop0Archive = new MenuDataArchive(coffeeShop0Prompt, coffeeShop0Options, coffeeShop0Processor, ID.ResultMenu, null);
        
		this.walmartProcessor = (String choiceIDPr) -> {
			switch(choiceIDPr) {
			case "buy1Grocery":
				if (Finance.tryBuy(10.00)) {
					HUD.hunger += 25; //placeholder
					closeOldMenu();
				} else {
					//play sound
				}
				break;
			case "buy7Grocery":
				if (Finance.tryBuy(40.00)) {
					HUD.hunger += 100;
					closeOldMenu();	
				} else {
					//play sound
				}

				break;
			case "buyBike":
				if (Finance.tryBuy(100.00)) {
					Inventory.hasBike = true;
					//HUD.bike = true;
					try {
						resultMenu = new Menu(bikeArchive);
					} catch (FontFormatException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					makeResultMenu();
					closeOldMenu();
					Game.walmartBuilding.setMenu(walmartBikeArchive);
				} else {
					//play sound
				}

				break;
			case "leave":
				closeOldMenu();
				break;
			}
		};
		
		walmartArchive = new MenuDataArchive(walmartPrompt, walmartOptions, walmartProcessor, ID.ChoiceMenu, walmartTitle);
		
		this.walmartBikeProcessor = (String choiceIDPr) -> {
			switch(choiceIDPr) {
			case "buy1Grocery":
				if (Finance.tryBuy(10.00)) {
					HUD.hunger += 25; //placeholder
					closeOldMenu();
				} else {
					//play sound
				}
				break;
			case "buy7Grocery":
				if (Finance.tryBuy(40.00)) {
					HUD.hunger += 100;
					closeOldMenu();	
				} else {
					//play sound
				}

				break;
			case "leave":
				closeOldMenu();
				break;
			}
		};
		
		walmartBikeArchive = new MenuDataArchive(walmartPrompt, walmartBikeOptions, walmartBikeProcessor, ID.ChoiceMenu, "Wolmart");
		
		this.laundromatProcessor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "doLaundry" :
            	if (Finance.tryBuy(.25)) {
            		Inventory.cleanAllClothes();
            	
	                try {
	                    resultMenu = new Menu(laundromat0Archive);
	                } catch (FontFormatException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                } catch (IOException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	                makeResultMenu();
	                closeOldMenu();
            	}
	             break;
            case "leave" :
                closeOldMenu();
                break;
            }
        };
       
        laundromatArchive = new MenuDataArchive(laundromatPrompt, laundromatOptions, laundromatProcessor, ID.ChoiceMenu, "Spin Cycles' Laundromat");
       
        this.laundromat0Processor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "leave" :
                closeResultMenu();
                break;
            }
        };
       
        laundromat0Archive = new MenuDataArchive(laundromat0Prompt, laundromat0Options, laundromat0Processor, ID.ResultMenu, null);
        
		this.hospitalProcessor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "checkUp":
            	if (Finance.tryBuy(500.00)) {
                	HUD.health = 100;
                	HUD.isSick = false;
                	HUD.rawTimeElapsed += 2 * 60 * HUD.gameSlowFactor; //takes one hour to complete
                    try {
    					resultMenu = new Menu(hospital0Archive);
    				} catch (FontFormatException | IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
                    makeResultMenu();
                    closeOldMenu();
            	} else {
            		//play sound
            	}

                break;
            case "leave":
                closeOldMenu();
                break;
            }
        };
        this.gasStationProcessor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "buySnacks" :
            	if (Finance.tryBuy(5)) {
            		HUD.hunger += 5;
                    try {
                        resultMenu = new Menu(gasStation0Archive);
                    } catch (FontFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    makeResultMenu();
                    closeOldMenu();
            	}
                break;
            case "leave":
                closeOldMenu();
                break;
            }
        };
       
        gasStationArchive = new MenuDataArchive(gasStationPrompt, gasStationOptions, gasStationProcessor, ID.ChoiceMenu, "Phillips' Fill-Ups");
       
        this.gasStation0Processor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "leave" :
                closeResultMenu();
                break;
            }
        };
       
        gasStation0Archive = new MenuDataArchive(gasStation0Prompt, gasStation0Options, gasStation0Processor, ID.ResultMenu, null);

        hospitalArchive = new MenuDataArchive(hospitalPrompt, hospitalOptions, hospitalProcessor, ID.ChoiceMenu, hospitalTitle);
       
        this.hospital0Processor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "leave" :
                closeResultMenu();
                break;
            }
        };
       
        hospital0Archive = new MenuDataArchive(hospital0Prompt, hospital0Options, hospital0Processor, ID.ResultMenu, null);
        
        eventFindMoneyProcessor = (String choiceIDPr) -> {
        	switch (choiceIDPr) {
        	case "leave":
        		double rng = r.nextInt(100);
        		double foundMoney = rng / 100;
        		if (foundMoney == 0) {
        			foundMoney = .01;
        		}
        		HUD.money += foundMoney;
        		closeEvent();
        		break;
        	}
        };
        
        eventFindMoneyArchive = new MenuDataArchive(eventFindMoneyPrompt, eventFindMoneyOptions,
        		eventFindMoneyProcessor, ID.ResultMenu, null);
        
        eventCollapseProcessor = (String choiceIDPr) -> {
        	switch (choiceIDPr) {
        	case "collapse":
        		try {
					cityNoise.stop();
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
				}
				if ((HUD.money > 0)) { //if you have any money, make the result menu where you lose money
					try {
						resultMenu = new Menu(eventCollapse0Archive);
					} catch (FontFormatException | IOException e) {
						e.printStackTrace();
					}
				} else {
					try { //else make the result menu where you lose moeny
						resultMenu = new Menu(eventCollapse0NoMoneyArchive);
					} catch (FontFormatException | IOException e) {
						e.printStackTrace();
					}
				}
         		sleepEffect = new SleepEffect(0, 0, ID.SleepEffect);
         		PlayerInput.caffeinated = false;
         		PlayerInput.biking = false;
         		HUD.health -= 10;
        		Game.handler.addObject(sleepEffect);
        		Game.gameState = Game.STATES.Sleeping;
        		if (HUD.hoursSinceMidnight > 6) {
            		nextMorning = (int) ((8 * 60 * HUD.gameSlowFactor) + (HUD.day * 1440 * HUD.gameSlowFactor) - 1);
        		} else {
        			nextMorning = (int) ((8 * 60 * HUD.gameSlowFactor) + ((HUD.day - 1) * 1440 * HUD.gameSlowFactor) - 1);
        		}

        		//player wakes up at 7:59 AM so they have time to make their 8 AM rent payment
        		timeSlept = (int) (nextMorning - HUD.rawTimeElapsed);
        		HUD.energy += SleepEffect.getEnergyRestored();
        		HUD.rawTimeElapsed = nextMorning; //set gameTime to the next morning
				makeResultMenu();
				closeEvent();
				break;
        	}
        };
        
        eventCollapseArchive = new MenuDataArchive(eventCollapsePrompt, eventCollapseOptions,
        		eventCollapseProcessor, ID.ResultMenu, null);
        
        eventCollapse0Processor = (String choiceIDPr) -> {
        	switch (choiceIDPr) {
        	case "leave":	
        		HUD.hungerLoss = (float) (timeSlept * (HUD.METABOLISM / HUD.gameSlowFactor) / 2);
        		HUD.health -= (HUD.hungerPain * timeSlept) / 6;
        		HUD.hunger -= (double) HUD.hungerLoss;
        		HUD.money -= HUD.money / 2; //lose half your money
        		Game.moneyChangeCause = "Stolen";
        		//moneyEffectML = new MoneyEffect(10, 85, ID.MoneyEffect, -HUD.money / 2);
        		System.out.println(moneyEffectML + " = moneyEffectML");
        		Inventory.dirtyClothes();
        		System.out.println("Wake up from Collapse Menu");
        		try {
                    stream = AudioSystem.getAudioInputStream(PlayerInput.class.getResource("cityAmbience.wav"));
                    clip = AudioSystem.getClip();
                   
                    cityNoise = new Sounds((long) 0.0, clip, "?", stream, filePathSound);

                cityNoise.loop();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        		closeResultMenu();
        		System.out.println(moneyEffectML + " = moneyEffectML");
        		break;
        	}
        };
        
        eventCollapse0Archive = new MenuDataArchive(eventCollapse0Prompt, eventCollapse0Options,
        		eventCollapse0Processor, ID.ResultMenu, null);
        
        eventCollapse0NoMoneyProcessor = (String choiceIDPr) -> {
        	switch (choiceIDPr) {
        	case "leave":	
        		HUD.hungerLoss = (float) (timeSlept * (HUD.METABOLISM / HUD.gameSlowFactor) / 2);
        		HUD.health -= (HUD.hungerPain * timeSlept) / 6;
        		HUD.hunger -= (double) HUD.hungerLoss;
        		Inventory.dirtyClothes();
        		System.out.println("Wake up from Collapse Menu");
        		try {
                    stream = AudioSystem.getAudioInputStream(PlayerInput.class.getResource("cityAmbience.wav"));
                    clip = AudioSystem.getClip();
                   
                    cityNoise = new Sounds((long) 0.0, clip, "?", stream, filePathSound);

                cityNoise.loop();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        		closeResultMenu();
        		break;
        	}
        };
        
        eventCollapse0NoMoneyArchive = new MenuDataArchive(eventCollapse0NoMoneyPrompt, eventCollapse0NoMoneyOptions,
        		eventCollapse0NoMoneyProcessor, ID.ResultMenu, null);
        
        kickedOutProcessor = (choiceIDPr) -> {
        	switch (choiceIDPr) {
        	case "leave":
        		closeOldMenu();
        		break;
        	}
        	
        };
        
        kickedOutArchive = new MenuDataArchive(kickedOutPrompt, kickedOutOptions, kickedOutProcessor, ID.ResultMenu, null);
        
        deathProcessor = (String choiceIDPr) -> {
        	switch(choiceIDPr) {
        	case "tryAgain":
        		HUD.health = 100;
        		HUD.hunger = 100;
        		HUD.money = HUD.STARTING_MONEY;
        		HUD.energy = 100;
        		HUD.rawTimeElapsed = 10 * 60 * HUD.gameSlowFactor;
        		Player.setPlayerX(Player.startX);
        		Player.setPlayerY(Player.startY);
        		closeDeathMenu();
        		break;
        	case "learnMore":
        		try {
        			oxfam = new URI("https://www.oxfamAmerica.org");
					java.awt.Desktop.getDesktop().browse(oxfam);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		break;
        	case "closeGame":
        		System.exit(1);
        		break;
        	}
        };
        
        deathArchive = new MenuDataArchive(deathPrompt, deathOptions, deathProcessor, ID.ChoiceMenu, null);
        
        this.eventStrikesProcessor = (String choiceIDPr) -> {
            switch (choiceIDPr) {
            case "exitStrikes" :
                closeEvent();
                break;
            }
        };
       
        eventStrikesArchive = new MenuDataArchive(eventStrikesPrompt, eventStrikesOptions,
        eventStrikesProcessor, ID.ResultMenu, null);
        
	} // END OF LIBARRY CONSTRUCTOR
		
	public void closeResultMenu() {
		Game.thisGame.removeKeyListener(resultMenu);
		Game.handler.removeObject(resultMenu);
		resultMenu = null;
		Game.gameState = Game.STATES.Playing;

	}
	public void closeOldMenu() {
		try {
			Game.handler.removeObject(Game.activeListener.getBuildingMenu());
			Game.thisGame.removeKeyListener(Game.activeListener.getBuildingMenu());
		} catch (Exception e) {
			System.out.println("closeOldMenu error, " + e);
		}

		if (resultMenu == null) {
			Game.gameState = Game.STATES.Playing;
		}
	}
	public void makeResultMenu() {
		Game.handler.addObject(resultMenu);
		Game.thisGame.addKeyListener(resultMenu);
	}

	public void closeEvent() {
		Game.handler.removeObject(Game.eventMaker.eventMenu);
		Game.thisGame.removeKeyListener(Game.eventMaker.eventMenu);
		if (resultMenu == null) {
			Game.gameState = Game.STATES.Playing;
		}
		Game.eventMaker.eventMenu = null;
	}
	
	public void closeDeathMenu() {
		Game.handler.removeObject(Game.hud.deathMenu);
		Game.thisGame.removeKeyListener(Game.hud.deathMenu);
		Game.gameState = Game.STATES.Playing;
		Game.hud.deathMenu = null;
	}
	
	private void saveGame() {
		JSONObject gamedata = new JSONObject();
        try {
            gamedata.put("health", HUD.health);
            gamedata.put("money", HUD.money);
            gamedata.put("hunger", HUD.hunger);
            gamedata.put("energy", HUD.energy);
            gamedata.put("bike", Inventory.hasBike);
            //wrong, that's...a boolean, not the list
            int clothesCount = Inventory.clothesList.size();
            gamedata.put("clothes", clothesCount);
            gamedata.put("time", HUD.rawTimeElapsed);
            gamedata.put("sick", HUD.isSick);
            gamedata.put("creditScore", Game.finance.getCreditScore());
            //gamedata.put("clothesList", Inventory.clothesList);
        } catch (Exception er) {
        	System.out.print(er);
        	er.printStackTrace();
        }
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
	}
	
	private void closePauseMenu() {
		Game.handler.removeObject(Game.playerMovementThing.pauseMenu);
		Game.thisGame.removeKeyListener(Game.playerMovementThing.pauseMenu);
		Game.gameState = Game.STATES.Playing;
		Game.playerMovementThing.pauseMenu = null;
	}
}
		

		
