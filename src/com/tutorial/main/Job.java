package com.tutorial.main;
 
public class Job{
    //should display a menu thats not attached to a building
    //maybe instead just have a boolean set for when shift starts and ends, or
    //if player missed shift
    private String name;
    private boolean hasJob = false;
    private boolean fired = false;
   
    private ImportantBuilding jobBuilding;
    private double wage;
    private MenuDataArchive jobMenu;
    private MenuDataArchive offShiftMenu;

   
    public int strikes;
    public int strikeTracker = 0;
   
   
    public Job(String jobName, double pay, ImportantBuilding building, MenuDataArchive onMenu, MenuDataArchive offMenu) {
        name = jobName;
        wage = pay;
        jobBuilding = building;
        jobMenu = onMenu;
        offShiftMenu = offMenu;
    }
    public void setHasJob(boolean bool) {
        hasJob = bool;
    }
    public boolean getHasJob() {
        return hasJob;
    }
   
   
    /**
     * If player should be on the job, as in its during shift hours, return true
     * Reminder: shifts are fixed weekdays 9-5
     **/
    public boolean onJob() {
        //checks if player has job
        if (hasJob) {
            double dayOfTheWeek = HUD.day % 7;
            //if statement check if weekday. 0 and 6 are weekend
            if (dayOfTheWeek == 1 || dayOfTheWeek == 2 || dayOfTheWeek == 3
                    || dayOfTheWeek == 4|| dayOfTheWeek == 5) {
                //if statement check if 9-5
                if (HUD.hoursSinceMidnight >= 9 && HUD.hoursSinceMidnight <= 17) {
                    return true;
                }
            }
        }
        return false;
    }
   
   
    public double getWage() {
        return wage;
    }
    public ImportantBuilding getBuilding() {
        return jobBuilding;
    }
    public MenuDataArchive getOnMenu() {
        return jobMenu;
    }
   
    public MenuDataArchive getOffMenu() {
        return offShiftMenu;
    }
   
    public void increaseStrikes() {
        //write in menu somewhere that 3 strikes and you're fired
        strikes++;
    }
   
    public void fired() {
        fired = true;
        hasJob = false;
    }
}