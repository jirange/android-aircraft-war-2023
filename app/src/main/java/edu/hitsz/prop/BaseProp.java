package edu.hitsz.prop;

import edu.hitsz.activity.MainActivity;
import edu.hitsz.aircraft.HeroAircraft;
//import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.prop.factory.BloodPropFactory;
import edu.hitsz.prop.factory.BombPropFactory;
import edu.hitsz.prop.factory.BulletPropFactory;
import edu.hitsz.prop.factory.PropFactory;

import java.util.Random;

/**实例数量 不限 工厂模式
 种类 3 种：火力道具、炸弹道具、加血道具
 出现方式 敌机坠毁后，以一定概率随机掉落某种道具
 使用方式 英雄机碰撞后自动触发
 移动方式 以一定速度向屏幕下方移动
 效果
 火力道具：英雄机由直射切换为散射弹道并持续一段时间，结束后恢复原状态
 炸弹道具：清除界面上除 boss 机外的所有敌机和敌机子弹     观察者模式
 加血道具：可使英雄机恢复一定血量，但不能超过英雄机初始的最大血量
 消灭 与英雄机碰撞或移动至界面底部则消失
 名称：子弹 预期应用设计模式*/
public abstract class BaseProp extends AbstractFlyingObject {

    private int prop_speed = 10;
    public static double drop_probability = 0.9;

    public BaseProp() {
    }


    public BaseProp(int locationX, int locationY, int prop_speed) {

        super(locationX, locationY, 0, prop_speed);
        this.prop_speed = prop_speed;
    }


    @Override
    public void forward() {
        super.forward();

        // 判定 x 轴出界
        if (locationX <= 0 || locationX >= MainActivity.screenWidth) {
            vanish();
        }

        // 判定 y 轴出界
        if (speedY > 0 && locationY >= MainActivity.screenHeight) {
            // 向下飞行出界
            vanish();
        } else if (locationY <= 0) {
            // 向上飞行出界
            vanish();
        }

    }


    public String toString() {
        return "BaseProp{prop_speed = " + prop_speed + "}";
    }


    public abstract void activeProp(HeroAircraft heroAircraft);

    public  static BaseProp getProp(double dropPR, int locationX, int locationY) {

        PropFactory propFactory = null;
        BaseProp prop = null;
        Random random = new Random();

        double bulletPropDropPR = dropPR / (BulletProp.drop_probability * 10);
        double bloodPropDropPR = dropPR / (BloodProp.drop_probability * 10);
        double bombPropDropPR = dropPR / (BombProp.drop_probability * 10);

        double whoRandom = random.nextDouble();
        double bulletPro = bulletPropDropPR;
        double bombPro = bulletPro + bombPropDropPR;
        double bloodPro = bombPro + bloodPropDropPR;

        if (whoRandom < bulletPro) {
            //掉落火力道具
            propFactory = new BulletPropFactory();
            prop = propFactory.createProp(locationX, locationY);
        } else if (bulletPro <= whoRandom && whoRandom < bombPro) {
            //掉落炸弹道具
            propFactory = new BombPropFactory();
            prop = propFactory.createProp(locationX, locationY);
        } else if (bombPro <= whoRandom && whoRandom < bloodPro) {
            //掉落加血道具
            propFactory = new BloodPropFactory();
            prop = propFactory.createProp(locationX, locationY);
        }
        return prop;
    }

    /**
     *
     */
    @Override
    public void update() {
        return;
    }
}
