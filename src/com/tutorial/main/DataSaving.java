package com.tutorial.main;

import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class DataSaving {
	
	static HUD playerData = new HUD(HUD.money, HUD.health, HUD.hunger, HUD.energy);
	static Inventory playerData1 = new Inventory(Inventory.clothesFull, Inventory.hasBike);
	public void keyPressed (KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ESCAPE) {
			try {
		         FileOutputStream fileOut =
		         new FileOutputStream("/tmp/game.ser");
		         ObjectOutputStream out = new ObjectOutputStream(fileOut);
		         out.writeObject(playerData);
		         out.writeObject(playerData1);
		         out.close();
		         fileOut.close();
		         System.out.printf("Serialized data is saved in /tmp/game.ser");
		      } catch (IOException i) {
		         i.printStackTrace();
		      }
		}
	}
}
