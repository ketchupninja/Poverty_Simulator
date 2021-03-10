package com.tutorial.main;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

//This stores all the stuff needed for the players' inventory
public class Inventory extends GameObject {
	
	public static ArrayList<Clothes> clothesList = new ArrayList<Clothes>(); //list that holds all the clothes instances
	public static boolean clothesFull = false; //will be used to stop player from buying so many clothes it breaks the menu
	public static int clothesLimit = 14; //no judgement, only technical limitations.
	public static boolean hasBike;
	
	public static void addClothes() {
		if (!clothesFull) {
			clothesList.add(new Clothes());
			if (clothesList.size() >= clothesLimit) {
				clothesFull = true;
			} else {
				clothesFull = false;
			}
		}

	}
	
	public static void cleanAllClothes() {
		for (int i = 0; i < clothesList.size(); i++) {
			Clothes clth = clothesList.get(i);
			clth.cleanliness = 2;
		}
	}
	
	public static boolean respectable() { //checks to see if player is "respectable", basically if they're NOT wearing a disgusting clothes
		Clothes best = getBestClothes();
		if (best.cleanliness == 0) {
			return false;
		}
		return true;
	}
	
	private static int currentClothesCleanliness; //measure how dirty the clothes _you are currently wearing_ are
	//you are always assumed to be wearing the cleanest set of clothes available
	
	public static int getCurrentClothesCleanliness() {
		return currentClothesCleanliness;
	}
	
	public static Clothes getBestClothes() {
		Clothes prospective = clothesList.get(0);
		for (int i = 0; i < clothesList.size(); i++) {
			if (clothesList.get(i).cleanliness > prospective.cleanliness) {
				prospective = clothesList.get(i);
			}
		}
		return prospective;
	}
	
	public static void dirtyClothes() {
		Clothes toDirty = getBestClothes();
		if (toDirty.cleanliness > 0) {
			toDirty.cleanliness -= 1;
		}
	}
	Inventory() {
		super(0, 0, ID.Inventory);
		addClothes(); //you start with one set of clothes
		hasBike = false;
	}
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	public enum cleanLevel { //determines what cleanliness bracket your avgClothes/currentClothes fall into
		Fresh,
		Used,
		Disgusting,
	};
	
	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
