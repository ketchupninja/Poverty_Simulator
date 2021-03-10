package com.tutorial.main;

//import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Rectangle;
//import java.io.IOException;

/* Handles stuff involving the player's finances: debt, bankruptcy, interest rates, etc. */
public class Finance extends GameObject {
	
	/* Amount you can go negative in money without going bankrupt. */
	private static int creditLimit;
	public static int getCreditLimit() {
		return creditLimit;
	}
	private static final double LIMIT_PER_SCORE = (double) 300.0 / (double) 1000;
	/* How much interest you pay at the end of a period when you're on credit. (percent) */
	private static double interestRate;
	public static double getInterestRate() {
		return interestRate;
	}
	private static final double INTEREST_PER_SCORE = 25.0 / 1000;
	/* Determines your credit limit and interest rate. High credit score -> high credit limit, low interest rate.
	 * Range is 0 - 1000.
	 * Increased each period when you're not in debt. Decreases if you're in debt and decreases a lot if you go bankrupt.*/
	private static long creditScore;
	public static long getCreditScore() {
		return creditScore;
	}
	/* A finance check that occurs on game start means player's actual starting credit is 300. */
	private final static int INIT_CREDIT_SCORE = 190;
	/* Length of period (in days) between each 'credit check', when your credit score changes, 
	 * you pay interest on debt, and/or go bankrupt. */
	public final static int PERIOD_LENGTH = 1;
	/* If you are over credit limit when it's checked (probably weekly or bi-weekly) you will
	 * become BANKRUPT. You can't buy anything until you earn enough money to get back within credit limit.*/
	private static boolean bankrupt;
	public static boolean getBankrupt() { //Getter.
		return bankrupt;
	}

	Finance() {
		super(0, 0, ID.Abstract);
		creditScore = INIT_CREDIT_SCORE;
		financeUpdate();
	}
	
	public void setCreditScore(long setCredit) {
		creditScore = setCredit;
	}
	public void resetFinance() {
		creditScore = INIT_CREDIT_SCORE;
	}
	
	@Override
	public void tick() {
		if (HUD.money >= 0) {
			bankrupt = false;
		} else if (HUD.money >= -creditLimit) { //You're in debt but still within credit limit.
			bankrupt = false;
		} else {
			System.out.println(bankrupt + " = bankrupt");
			bankrupt = true; //You're over your credit limit, bankrupt time.
		}
	}
	
	
	public static boolean tryBuy(double price) {
		if (canAfford(price)) {
			HUD.money -= price;
			return true; //purchase successful
		}
		
		return false;
	}
	/* Call from menu to see if you can buy something without going over credit limit. */
	private static boolean canAfford(double price) {
		if (bankrupt) {
			/* You can't afford anything when you're bankrupt. */
			return false;
		}
		if (HUD.money - price < -creditLimit) {
			return false;
		}
		
		return true;
	}
	
	/* Should be called at 8 AM, every financial period. */
	public static void financeUpdate() {
		double debtPayment;
		/* Update credit score/bankrupt status. */
		if (HUD.money >= 0) {
			creditScore += 10;
		} else if (HUD.money >= -creditLimit) { //You're in debt but still within credit limit.
			debtPayment = -1 * HUD.money * (interestRate / 100);
			Game.moneyChangeCause = "Interest on debt";
			HUD.money -= debtPayment;
			creditScore -= 20;
		} else {
			debtPayment = -1 * HUD.money * (interestRate / 100);
			Game.moneyChangeCause = "Interest on debt";
			HUD.money -= debtPayment;
			creditScore = 0; //bottoms out
		}
		
		creditScore = (long) Game.clamp( (int) creditScore, 0, 1000);
		creditLimit = (int) (LIMIT_PER_SCORE * ( (double) creditScore));
		interestRate = 5 + (INTEREST_PER_SCORE * ( (double) creditScore));
	}


	/* Unused functions due to extension of GameObject class. Should not be called. */
	@Override
	public void render(Graphics g) {}
	@Override
	public Rectangle getBounds() {return null;}
}
