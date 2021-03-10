package com.tutorial.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ReadData {
	static HUD playerData = null;
	static Inventory gameState = null;
	public static void readData(Object input) throws FileNotFoundException {
	      
	      try {
	         FileInputStream fileIn = new FileInputStream("/tmp/game.ser");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         playerData = (HUD) in.readObject();
	         gameState = (Inventory) in.readObject();
	         in.close();
	         fileIn.close();
	      } catch (IOException i) {
	         i.printStackTrace();
	         return;
	      } catch (ClassNotFoundException c) {
	         System.out.println("Employee class not found");
	         c.printStackTrace();
	         return;
	      } 
	}
}
