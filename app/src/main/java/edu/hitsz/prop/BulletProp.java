package edu.hitsz.prop;

import android.util.Log;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.shoot.DirectShoot;
import edu.hitsz.strategy.shoot.ScatteringShoot;


/**
 * @author leng
 */
public class BulletProp extends BaseProp {

    public static double drop_probability = 0.3;

    public BulletProp(int locationX, int locationY, int propSpeed) {
        super(locationX, locationY, propSpeed);
    }

    @Override
    public void activeProp(HeroAircraft heroAircraft) {
        Log.i("BulletProp","FireSupply active!");
//让直射线程暂停五秒 散射线程持续五秒后关闭
        Runnable scatteringRun = () -> {
            synchronized (BulletProp.class) {
                heroAircraft.setShootStrategy(new ScatteringShoot());
                try {
                    BulletProp.class.wait(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                heroAircraft.setShootStrategy(new DirectShoot());
            }
        };
        //开启散射线程
        new Thread(scatteringRun).start();
        Log.i("BulletProp","FireSupply end!");

    }

}
