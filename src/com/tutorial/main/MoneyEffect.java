package com.tutorial.main;

import java.awt.AlphaComposite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
//This is the thing that display "+$x" or "-$x" when player gains or loses money.

public class MoneyEffect extends GameObject {

	private double moneyChange;
	private String moneyChangeStr;
	private Color myColor;
	private String sign;
	private float alpha = 1;
	
	public MoneyEffect(float x, float y, ID id, double setMoneyChange) {
		super(x, y, id);
		posNegHandle(setMoneyChange);
		this.moneyChange = Math.abs(setMoneyChange);
		this.velY = (float) .50;
		this.moneyChangeStr = sign + "$" + String.format("%.2f", moneyChange);
		if (Game.moneyChangeCause != null) {
			this.moneyChangeStr = moneyChangeStr + "  " + Game.moneyChangeCause;
		}
		Game.handler.effectQueue.add(Game.handler.effectQueue.size(), this);
		
	}

	@Override
	public void tick() {
		//queue handling
		y += velY;
		alpha -= .007;
		alpha = Game.clamp(alpha, (float) 0 , (float) 1);
		if (this.y > 175) {
			Game.handler.removeObject(this);
		}
	}
	
	private void posNegHandle(double setMoneyChange) {
		if (setMoneyChange < 0) {
			this.sign = "-";
			this.myColor = Color.red;
		} else {
			this.sign = "+";
			this.myColor = Color.green;
		}
	}
	
	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(makeTransparent(alpha));

		g.setColor(myColor);
		Font effectFont = new Font("arial", 1, 16);
		g.setFont(effectFont);
		g.drawString(moneyChangeStr, (int) x, (int) y); 
		
		g2d.setComposite(makeTransparent(1));		
	}

	private AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}
	
	@Override
	public Rectangle getBounds() {
		return null;
	}

}
