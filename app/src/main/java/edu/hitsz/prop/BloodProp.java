package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;

/**
 * @author leng
 */
public class BloodProp extends BaseProp{
    /**
     * 一次加血的血量：60
     */
    public static int increase_blood = 60;

    public static double drop_probability =0.3;

    public BloodProp(int locationX, int locationY,int propSpeed) {
        super(locationX,locationY,propSpeed);
    }

    public BloodProp() {
    }

    @Override
    public void activeProp(HeroAircraft heroAircraft) {

        heroAircraft.increaseHp(BloodProp.increase_blood);
//        System.out.println("BloodSupply active!");

    }

}
