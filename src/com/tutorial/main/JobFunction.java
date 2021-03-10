package com.tutorial.main;
 
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
 
public class JobFunction extends GameObject{
   
    public static ArrayList<Job> jobList = new ArrayList<Job>();
    public JobFunction() {
        super(0, 0, ID.Abstract);
        Game.handler.addObject(this);
    }
   
    public void setList() {
        Job burgerJob = new Job("burger job", 9.15, (ImportantBuilding) Game.burgerBuilding,
                Game.library.burger2Archive, Game.library.burgerOffArchive);
        jobList.add(0, burgerJob);
        System.out.println(Game.burgerBuilding + "burger building");
    }
 
    @Override
    public void tick() throws FontFormatException, IOException {
        // TODO if any job in the list is active (hasJob == true), activate diff menu
        for (int i = 0; i < jobList.size(); i++) {
            if (jobList.get(i).getHasJob() && jobList.get(i).onJob()) {
                System.out.println(jobList.get(i).getBuilding() + "get Building");
                System.out.println(jobList.get(i).getOnMenu() + "get Job Menu");
                jobList.get(i).getBuilding().setMenu(jobList.get(i).getOnMenu());
            } else if (jobList.get(i).getHasJob() && !jobList.get(i).onJob()) {
                jobList.get(i).getBuilding().setMenu(jobList.get(i).getOffMenu());
            }
           
            int elevenAM = (int) ((11 * 60 * HUD.gameSlowFactor) + ((HUD.day - 1) * 1440 * HUD.gameSlowFactor));
            if (jobList.get(i).onJob() && HUD.rawTimeElapsed == elevenAM) {
                jobList.get(i).increaseStrikes(); // strike if your are 2 hrs late
                /*RandomEventMaker.eventMenu = new Menu(Game.library.eventStrikesArchive);
                Game.handler.add(eventMenu)
                Game.handler.addKeyListener(eventMenu)
                */
                System.out.println(jobList.get(i).strikes);
                if (jobList.get(i).strikes >= 3) {
                    jobList.get(i).fired();
                    jobList.get(i).getBuilding().setMenu(Game.library.burgerFArchive);
                }
            }
        }
       
    }
 
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