package com.tutorial.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
//Sounds of this class will loop continuously until told to stop.
public class Sounds {
    Long currentFrame;
    Clip clip;
    String status;
    AudioInputStream audioInputStream;
    File filePath;
    public static ArrayList<Sounds> allSounds = new ArrayList<Sounds>();
    
    Sounds(Long setFrame, Clip setClip, String setStatus, AudioInputStream setAudio, File setFilePath) throws LineUnavailableException, IOException {
    	this.currentFrame = setFrame;
    	this.clip = setClip;
    	this.status = setStatus;
    	this.audioInputStream = setAudio;
    	this.filePath = setFilePath;
    }
    
    public void play() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
    	allSounds.add(this);
    	if (!Game.mute) {
    		//mute need to mute continuous sounds as well
    		//so we need to have a list of all game sounds that are playing
    		//but we also need to only begin them again when un-muted if conditions are fulfilled
    		//also: add rest of menu sounds
	        clip.start();
	        status = "play";
	        if (!clip.isOpen()) {
	        	clip.open(audioInputStream);
	        }
	    	//clip.open(audioInputStream);
	        clip.loop(0);
    	}

    }
    
    public void loop() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
    	//System.out.println("play()");
        clip.start();
        status = "play";
        if (!clip.isOpen()) {
        	clip.open(audioInputStream);
        }
    	//clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY); 
    }
    
    public void stop() throws UnsupportedAudioFileException,
    IOException, LineUnavailableException {
    	allSounds.remove(this);
    	//System.out.println("stop()");
        currentFrame = 0L;
        clip.stop();
        //clip.close();
    }
    
    public void mute() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
    	for (Sounds aSound : allSounds) {
    		aSound.stop();
    	}
    }
    
    public void unmute() {
    	/* Start all background/running sounds that need to be present, given player circumstances. */
    	if (Game.gameState != Game.STATES.Sleeping) {
    		
    	}
    }
}