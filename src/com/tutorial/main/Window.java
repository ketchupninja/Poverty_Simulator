package com.tutorial.main;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;
//handles the creation of the game window
public class Window extends Canvas{

	private static final long serialVersionUID = -240840600533728354L; // idk what this means, but it's important
	
	public Window(int width, int height, String title, Game game) {
		JFrame frameName = new JFrame(title);
		frameName.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //make it so game closes when user clicks "X"
		//defining stuff for the game window
		frameName.add(game);
		frameName.setPreferredSize(new Dimension(width, height));
		frameName.pack(); //make border offsets constant across OS
		frameName.setLocationRelativeTo(null); //center window on user screen
		frameName.setVisible(true);
		frameName.setResizable(false); //don't let user resize window, that messes stuff up
		game.start(); //function that will be in game class

	}

}
