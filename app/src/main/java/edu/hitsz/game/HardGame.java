package edu.hitsz.game;

import android.content.Context;
import android.os.Handler;

import edu.hitsz.ImageManager;
import edu.hitsz.aircraft.enemy.AbstractEnemyAircraft;
import edu.hitsz.aircraft.enemy.factory.EnemyFactory;
import edu.hitsz.aircraft.enemy.factory.MobEnemyFactory;
import edu.hitsz.aircraft.enemy.factory.SuperEnemyFactory;

public class HardGame extends BaseGame{
    public  static  int GameDifficulty=3;

    public HardGame(Context context, Handler handler) {
        super(context,handler);
        this.backGround = ImageManager.BACKGROUND3_IMAGE;

        // 困难模式下 boss敌机出现 每次分数阈值涨幅为 borderToBoss = 500
        borderAddForBoss=500;
        // 血量涨幅50
        bossHpAdd=50;
    }

    int enemyHpAdd = 0;
    int enemySpeedAdd = 0;

    @Override
    protected void controlDifficulty() {
        if (super.getTime() % 3000 == 0) {
            if (cycleDuration >= 200) {
                cycleDuration -= 10;
            }
            if (mobEnemyPro >= 0.1) {
                mobEnemyPro -= 0.02;
                superEnemyPro += 0.02;
            }
            if (enemySpeedAdd <= 30) {
                enemyHpAdd += 4;
                if (enemyHpAdd % 4==0) {
                    enemySpeedAdd += 1;
                }
                enemySpeedAdd += 1;
            }

            System.out.printf("时间:%d\t周期:%d\t最大敌机数量:%d\t普通敌机概率:%.2f\t精英敌机概率:%.2f\t敌机血量增幅:%d\t敌机速度增幅:%d\n",super.getTime(),cycleDuration,enemyMaxNumber,mobEnemyPro,superEnemyPro, enemyHpAdd, enemySpeedAdd);
        }else if (super.getTime() % 7000 == 0){
            enemyMaxNumber+=1;
        }
    }

    @Override
    protected AbstractEnemyAircraft getEnemyAircraft() {
        EnemyFactory factory;
        AbstractEnemyAircraft enemy;
        if (Math.random() <= superEnemyPro) {
            factory = new SuperEnemyFactory();
            enemy = factory.createEnemy(10 + enemySpeedAdd, 60 + enemyHpAdd);
        } else {
            factory = new MobEnemyFactory();
            enemy = factory.createEnemy(10 + enemySpeedAdd, 30 + enemyHpAdd);
        }
        return enemy;
    }
}
