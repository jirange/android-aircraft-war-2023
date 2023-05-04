package edu.hitsz.aircraft.enemy;


import java.util.LinkedList;
import java.util.List;

import edu.hitsz.activity.MainActivity;
import edu.hitsz.aircraft.enemy.AbstractEnemyAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.prop.BaseProp;


/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends AbstractEnemyAircraft {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.crashScore=10;
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= MainActivity.screenHeight) {
            vanish();
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        return new LinkedList<>();
    }


    @Override
    public List<BaseProp> dropProp() {
        return new LinkedList<>();
    }

    public int getCrashScore() {
        return crashScore;
    }

    /**
     * 炸弹爆炸时 普通敌机 全部消失
     */
    @Override
    public void update() {
        this.vanish();
    }
}
