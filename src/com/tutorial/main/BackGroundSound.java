package com.tutorial.main;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
//Sounds of this class will loop continuously until told to stop.
public class BackGroundSound {
    Long currentFrame;
    Clip clip;
    String status;
    AudioInputStream audioInputStream;
    File filePath;
    
    BackGroundSound(Long setFrame, Clip setClip, String setStatus, AudioInputStream setAudio, File setFilePath) throws LineUnavailableException, IOException {
    	this.currentFrame = setFrame;
    	this.clip = setClip;
    	this.status = setStatus;
    	this.audioInputStream = setAudio;
    	this.filePath = setFilePath;
    }
    
    public void play() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
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
    	//System.out.println("stop()");
        currentFrame = 0L;
        clip.stop();
        //clip.close();
    }
}
