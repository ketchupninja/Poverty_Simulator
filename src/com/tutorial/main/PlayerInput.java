package com.tutorial.main;
 
import java.awt.FontFormatException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.jar.JarException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.simple.JSONObject;

public class PlayerInput extends KeyAdapter {
   
	/* Booleans used to keep track of what direction player is going. */
    private static boolean moveUp = false; 
    private static boolean moveDown = false; 
    private static boolean moveLeft = false;
    private static boolean moveRight = false;
    
	Menu pauseMenu;
	
   
    /* Stuff to set up walking sound. */
    private File filePathSound = new File("").getAbsoluteFile();
    private AudioInputStream stream;
    private Clip clip;
    private Sounds walkSound;
    
    /* Variables for player speed */
    private static int baseSpeed = 3; //Baseline player speed.
    private static int debugSpeed = 20; //Player speed when debug enabled. 
    
    private static int speedY; //The speed the player will move up and down with, if they move up and down.
    private static int speedX; //The speed the player will move left and right right, if they're moving left or right.
    
    public static boolean caffeinated;
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
    
    public PlayerInput(Handler handler)
    		throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        
    	stream = AudioSystem.getAudioInputStream(PlayerInput.class.getResource("FOOTSTEPS.wav"));
        clip = AudioSystem.getClip();
        walkSound = new Sounds((long) 0.0, clip, "?", stream, filePathSound);
        
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
				walkSound.loop();
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
    	if (caffeinated) {
    		baseSpeed = 4;
    	} else {
    		baseSpeed = 3;
    	}
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
    		biking = !biking;}
        
    	/* Pressing O enables debug mode - no collision, fast movements, display some useful info.
        Comment this line out on release. */
    	//else if(key == KeyEvent.VK_O) {Game.debug = !Game.debug;} 
        
    	else if (key == KeyEvent.VK_M) {
    		Game.mute = !Game.mute;
    	} else if (key == KeyEvent.VK_U && Game.debug) {
    		ImportantBuilding burgerBuilding = (ImportantBuilding) Game.burgerBuilding;
    		burgerBuilding.setMenu(Game.library.hospitalArchive);
    	} else if (key == KeyEvent.VK_C && Game.debug) {
    		Inventory.dirtyClothes();
    	} else if (key == KeyEvent.VK_B && Game.debug) {
    		Inventory.addClothes();
    	} else if (key == KeyEvent.VK_K && Game.debug) {
    		HUD.energy = 10;
    	} else if (key == KeyEvent.VK_L && Game.debug) {
    		HUD.energy = 100;
    	} else if (key == KeyEvent.VK_EQUALS && Game.debug) {
    		HUD.money += 1000;
    	} else if (key == KeyEvent.VK_NUMPAD7 && Game.debug) {
    		HUD.health = 0;
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
        }
        key = KeyEvent.VK_UNDEFINED;
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
        if (key == KeyEvent.VK_ESCAPE && Game.gameState == Game.STATES.Playing) {
			try { pauseMenu = new Menu(Game.library.pauseArchive);
			} catch (FontFormatException | IOException e1) {}
        	Game.handler.addObject(pauseMenu);
        	Game.thisGame.addKeyListener(pauseMenu);
        }
    }
}

