package edu.hitsz.aircraft.enemy;

import edu.hitsz.activity.GameActivity;
import edu.hitsz.music.MySoundPool;
import edu.hitsz.prop.*;
import edu.hitsz.strategy.shoot.ScatteringShoot;

import java.util.LinkedList;
import java.util.List;

/**
 * @author leng
 */
public class BossEnemy extends AbstractEnemyAircraft {

    /**
     * 道具一次掉落数量
     */
    private int dropNum = 3;
    int bgm_boss_id;


    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootNum = 3;
        this.power = 30;
        this.direction = 1;
        //rate: 调节子弹移动速度
        this.rate = 1.5;
        this.crashScore = 80;
        this.setShootStrategy(new ScatteringShoot());
        // boss出现音效
        bgm_boss_id = MySoundPool.playMusic("bgm_boss", true);

    }

    @Override
    public void forward() {
        super.forward();
    }


    @Override
    public List<BaseProp> dropProp() {

        List<BaseProp> props = new LinkedList<>();
        double dropPR = 1;

        int dropLocationX = locationX;

        for (int i = 0; i < dropNum; i++) {
            BaseProp prop = BaseProp.getProp(dropPR, dropLocationX, locationY);
            if (prop != null) {
                props.add(prop);
            }
            dropLocationX = dropLocationX + 15;
        }
        return props;
    }

    public int getCrashScore() {
        return crashScore;
    }

    @Override
    public void vanish() {
        isValid = false;
        // 停止播放boss-bgm
        if (bgm_boss_id != -1) {
            MySoundPool.mysp.stop(bgm_boss_id);
        }
    }

    /**
     * 炸弹爆炸 boss敌机减少一定的血量
     */
    @Override
    public void update() {
        this.decreaseHp(60);
    }
}
