package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
//import edu.hitsz.application.Main;
import edu.hitsz.strategy.shoot.DirectShoot;
import edu.hitsz.strategy.shoot.ScatteringShoot;
//import org.apache.commons.lang3.concurrent.BasicThreadFactory;


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
        System.out.println("FireSupply active!");
//让直射线程暂停五秒 散射线程持续五秒后关闭
//        Runnable scatteringRun = () -> {
//            synchronized (BulletProp.class) {
//                heroAircraft.setShootStrategy(new ScatteringShoot());
//                try {
//                    BulletProp.class.wait(5000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                heroAircraft.setShootStrategy(new DirectShoot());
//            }
//        };
//        //todo 开启散射线程
//        new Thread(scatteringRun).start();

//        ScheduledExecutorService executorService= new ScheduledThreadPoolExecutor(1,
//                new BasicThreadFactory.Builder().namingPattern("bullet-prop-action-%d").daemon(true).build());
//
//        executorService.schedule(scatteringRun,40, TimeUnit.MILLISECONDS);
//        System.out.println("FireSupply end!");

    }

}
