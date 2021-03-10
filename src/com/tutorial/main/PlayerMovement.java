package com.tutorial.main;
 
import java.awt.FontFormatException;

import java.awt.event.KeyAdapter;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import javax.lang.model.element.Element;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class PlayerMovement extends KeyAdapter implements java.io.Serializable{
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* Booleans used to keep track of what direction player is going. */
    private static boolean moveUp = false; 
    private static boolean moveDown = false; 
    private static boolean moveLeft = false;
    private static boolean moveRight = false;
   
    /* Stuff to set up walking sound. */
    private File filePathSound = new File("").getAbsoluteFile();
    private AudioInputStream stream;
    private Clip clip;
    private BackGroundSound walkSound;
    
    /* Variables for player speed */
    private static int baseSpeed = 2; //Baseline player speed.
    private static int debugSpeed = 20; //Player speed when debug enabled. 
    
    private static int speedY; //The speed the player will move up and down with, if they move up and down.
    private static int speedX; //The speed the player will move left and right right, if they're moving left or right.
    
    /* Bike's current direction axis. */
    private static dir bikeDir;
    /* Whether or not the player is on the bike */
    public static boolean biking;
    
    /* This class shares the "invMenu". Whenever player presses Q it makes this a new menu and fills it out.
     * When inventory is closed it refers it to null again.
     */
    public static Menu invMenu;
    
    /* Stores user input. */
    private int key;
    
    /* The player object. 
     * I could also have just referred to the static player object in the Game class, but I think this is cleaner?
     */
    private static GameObject thePlayer;
    static HUD playerData = new HUD(HUD.money, HUD.health, HUD.hunger, HUD.energy);
	static Inventory playerData1 = new Inventory(Inventory.clothesFull, Inventory.hasBike);
    
    public PlayerMovement(Handler handler)
    		throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        
    	stream = AudioSystem.getAudioInputStream(PlayerMovement.class.getResource("FOOTSTEPS.wav"));
        clip = AudioSystem.getClip();
        walkSound = new BackGroundSound((long) 0.0, clip, "?", stream, filePathSound);
        
        speedX = baseSpeed;
        speedY = baseSpeed;
    
        thePlayer = Game.thePlayer;

    }
   
    /* The bike's movement works uniquely. When the player mounts the bike and starts moving, their bike is 
     * pointed in that direction. They'll go faster that way, but will move more slowly to their left and right.
     * Basically, it's slow to steer. 
     * 
     * IMPORTANT NOTE: This is technically bike "axis", since it doesn't distinguish between left and right or up and down.
     * This means you can turn 180 degrees instantly at full speed. This is sort of weird, but I tried other
     * implementations and they were also sort of weird as well as being hard to control. So this is what I went with.
     * 
     * UNIMPORTANT NOTE: I got the idea for this when I was using debug mode, and noticed that because of how movement code worked 
     * at the time, turning off debug mode while still moving would make steering hard, and I found that fun to
     * mess around with, so I decided to make the bike control like that. 
     */
    private enum dir {
    	horiz,
    	vert,
    	none;
    }

    /* Returns true if the player is moving (or trying to). */
    public static boolean moving() {
    	if (moveUp || moveDown || moveRight || moveLeft) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public void tick() {
        decideSpeed();        
        decideMovement();
    	playWalkSound(); 
    	decideBikeDir();
    }

    /* Decide what the bike's current direction is. */
    private void decideBikeDir() {
    	/* If you're not moving, bike has no direction. */
    	if (!moving()) {
    		bikeDir = dir.none;
    	}
    	
    	/* This code makes turning work. If your bike is pointed in one direction, but you
    	 * stop moving in that direction and start moving the other way, your bike will turn.
    	 * 
    	 * Example: You're biking up. If you start moving right while still biking up,
    	 * your direction stays up and you move right slowly.
    	 * But if you stop moving up and only move right, your bike's direction will change to point to the right..
    	 */
    	if (thePlayer.getVelY() == 0 && bikeDir == dir.vert && biking && thePlayer.getVelX() != 0) {
    		bikeDir = dir.horiz;
    		//if your bikeDir is vertical but you're not moving vertical and you're moving horizontal
    	} else if (thePlayer.getVelX() == 0 && bikeDir == dir.horiz && biking && thePlayer.getVelY() != 0) {
    		bikeDir = dir.vert;
    	}
    }
    
    /* Plays the walk sound only if the player is moving, and the game isn't in a menu or something. */
    private void playWalkSound() {
    	if (moving() && Game.gameState == Game.STATES.Playing) {
            try {
				walkSound.play();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				e1.printStackTrace();
			}
    	} else {
    		 try {
					walkSound.stop();
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
					e1.printStackTrace();
				}
    	}
    }
    /* Decides how fast the player should be able to move in each direction, based on
     * if debug mode is on, if they have a bike, and what direction they're going on the bike.
     */
    private void decideSpeed() {
    	if (Game.debug) {
        	speedY = debugSpeed;
        	speedX = debugSpeed;
        } else if (biking && bikeDir != dir.none) {
        	if (bikeDir == dir.vert) {
        		speedY = baseSpeed * 4;
        		speedX = baseSpeed;
        	}
        	if (bikeDir == dir.horiz) {
        		speedX = baseSpeed * 4;
        		speedY = baseSpeed; 
        	}
        } else {
        	speedY = baseSpeed;
        	speedX = baseSpeed;
        }
    }
    
    /* This code is run on button press. */
    public void keyPressed(KeyEvent e) {
        key = e.getKeyCode();
        /* Open Inventory Code */
        if (key == KeyEvent.VK_Q && Game.gameState == Game.STATES.Playing) {
    		try {
    			/* Open up the inventory, if the game is in the Playing state and you press Q. */
				invMenu = new Menu(Game.library.invArchive);
	    		Game.handler.addObject(invMenu);
	    		Game.thisGame.addKeyListener(invMenu);
			} catch (FontFormatException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	
    	} else if (key == KeyEvent.VK_SHIFT && Inventory.hasBike && !(moving()) && Game.gameState == Game.STATES.Playing) {
    		biking = !biking;
    	/* Pressing O enables debug mode - no collision, fast movements, display some useful info. */
    	} else if(key == KeyEvent.VK_O) {
    		Game.debug = !Game.debug;
    	} else if (key == KeyEvent.VK_C && Game.debug) {
    		Inventory.dirtyClothes();
    	} else if (key == KeyEvent.VK_B && Game.debug) {
    		Inventory.addClothes();
    	} else if (key == KeyEvent.VK_K && Game.debug) {
    		HUD.energy = 10;
    	} else if (key == KeyEvent.VK_L && Game.debug) {
    		HUD.energy = 100;
    	} else if (key == KeyEvent.VK_EQUALS && Game.debug) {
    		HUD.rawTimeElapsed += (1440 * HUD.gameSlowFactor);
    	} else if (key == KeyEvent.VK_MINUS && Game.debug) {
    		HUD.rawTimeElapsed += (60 * HUD.gameSlowFactor);
    	} else if(key == KeyEvent.VK_W) { //if W pressed
    		if (bikeDir == dir.none) {
            	bikeDir = dir.vert;
            }
            moveUp = true; //set moveUp to true

        } else if (key == KeyEvent.VK_S) {
        	if (bikeDir == dir.none) {
            	bikeDir = dir.vert;
            }
            moveDown = true;
            
        } else if (key == KeyEvent.VK_A) {
        	if (bikeDir == dir.none) {
            	bikeDir = dir.horiz;
            }
            moveLeft = true;
            
        } else if (key == KeyEvent.VK_D) {
        	if (bikeDir == dir.none) {
            	bikeDir = dir.horiz;
            }
            moveRight = true;
        } else if (key == KeyEvent.VK_ESCAPE) {
        	JSONObject gamedata = new JSONObject();
        	try {
				gamedata.put("health", HUD.health);
			} catch (JSONException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
        	try {
				gamedata.put("money", HUD.money);
			} catch (JSONException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
        	try {
				gamedata.put("hunger", HUD.hunger);
			} catch (JSONException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
        	try {
				gamedata.put("energy", HUD.energy);
			} catch (JSONException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
        	try {
				gamedata.put("bike", Inventory.hasBike);
			} catch (JSONException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
        	try {
				gamedata.put("clothes", Inventory.clothesFull);
			} catch (JSONException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
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
            /*
        	Document dom;
            Element e1 = null;
            // get an instance of factory
            String money = Double.toString(HUD.money);
            String health = Double.toString(HUD.health);
            String hunger = Double.toString(HUD.hunger);
            String energy = Double.toString(HUD.energy);
            String hasBike = String.valueOf(Inventory.hasBike);
            String clothes = String.valueOf(Inventory.clothesFull);
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            FileOutputStream file = null;
			try {
				file = new FileOutputStream("Game.xml");
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
            // using a factory get an instance of document builder
            DocumentBuilder db = null;
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
            // create an instance of DOM
            dom = (Document) db.newDocument();
            
            // create the root element
            Element rootElement = (Element) ((org.w3c.dom.Document) dom).createElement("myparameters");
            // create data elements and place them under root
            e1 = (Element) ((org.w3c.dom.Document) dom).createElement("HUD");
            ((Node) e1).appendChild(((org.w3c.dom.Document) dom).createTextNode(money));
            ((Node) e1).appendChild(((org.w3c.dom.Document) dom).createTextNode(health));
            ((Node) e1).appendChild(((org.w3c.dom.Document) dom).createTextNode(hunger));
            ((Node) e1).appendChild(((org.w3c.dom.Document) dom).createTextNode(energy));
            ((Node) e1).appendChild(((org.w3c.dom.Document) dom).createTextNode(hasBike));
            ((Node) e1).appendChild(((org.w3c.dom.Document) dom).createTextNode(clothes));
            ((Node) rootElement).appendChild((Node) e1);
            try {
            	Transformer tr = TransformerFactory.newInstance().newTransformer();
            	tr.transform(new DOMSource((Node) dom), 
                        new StreamResult(file));
            } catch (Exception a) {
            	System.out.println("there are some mistakes");
            }
        }
        */
        key = KeyEvent.VK_UNDEFINED;
        }
        
    }
    
    /* Each tick, looks at the movement booleans and sets the Player object's velocity accordingly. */
    private void decideMovement() {
    	if (moveUp) {
    		thePlayer.setVelY(-speedY);
    	} else if (moveDown) {
    		thePlayer.setVelY(speedY);
    	} else {
    		thePlayer.setVelY(0);
    	}
    	
    	if (moveRight) {
    		thePlayer.setVelX(speedX);
    	} else if (moveLeft) {
    		thePlayer.setVelX(-speedX);
    	} else {
    		thePlayer.setVelX(0);
    	}
    }
    
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        /**
         * NOTE: As Y increases, things move down the screen. So Y = 0 is the top of the screen.
         * This code will set the relevant move boolean to false when you release a given key.
         */
        if (key == KeyEvent.VK_W) { //when W key released
            moveUp = false; //set moveUp to false                
        } else if (key == KeyEvent.VK_S) { //S key released
            moveDown = false;    
        } else if (key == KeyEvent.VK_A) {
            moveLeft = false;    
        } else if (key == KeyEvent.VK_D) {
            moveRight = false;            
        }
        
        /* Pressing escape will close the game. TODO: Add confirmation menu. */
        if (key == KeyEvent.VK_ESCAPE) {
        	System.exit(1); 
        }
    }
}

