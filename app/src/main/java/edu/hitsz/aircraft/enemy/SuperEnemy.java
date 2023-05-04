package edu.hitsz.aircraft.enemy;

import edu.hitsz.activity.MainActivity;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.prop.*;
import edu.hitsz.strategy.shoot.DirectShoot;
import edu.hitsz.strategy.shoot.ShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * @author leng
 */
public class SuperEnemy extends AbstractEnemyAircraft {

    /**
     * 飞机坠毁后加的分数值
     */
    private int crashScore=20;


//    /** 攻击方式
//     * shootNum: 子弹一次发射数量
//     * power:子弹伤害
//     * direction:子弹射击方向 (向上发射：1，向下发射：-1)
//     * rate: 调节子弹移动速度
//     * shootStrategy: 攻击策略     * shootStrategy: 攻击策略
//     */
//    protected int shootNum;
//    protected int power;
//    protected int direction;
//    protected double rate;



    public SuperEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootNum = 1;
        this.power = 30;
        this.direction = 1;
        //rate: 调节子弹移动速度
        this.rate = 1.5;
        this.crashScore=20;
        this.setShootStrategy(new DirectShoot());
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= MainActivity.screenHeight) {
            vanish();
        }
    }

//    /**
//     * 飞机射击方法，可射击对象必须实现
//     * @return
//     *  可射击对象需实现，返回子弹
//     *  非可射击对象空实现，返回null
//     *       * 通过射击产生子弹
//     *      * @return 射击出的子弹List
//     */
//    @Override
//    public List<BaseBullet> shoot() {
//    }


    @Override
    public  List<BaseProp>  dropProp(){
        List<BaseProp> props = new LinkedList<>();
        double dropPR = 0.9;
        BaseProp prop = BaseProp.getProp(dropPR,locationX,locationY);
        if (prop !=null) {
            props.add(prop);
        }
        return  props;
    }


    public int getCrashScore() {
        return crashScore;
    }

    /**
     *  炸弹爆炸 精英敌机全部消失
     */
    @Override
    public void update() {
        this.vanish();
    }
}
