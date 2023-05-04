package edu.hitsz.game;

import android.content.Context;
import android.os.Handler;

import edu.hitsz.ImageManager;
import edu.hitsz.aircraft.enemy.AbstractEnemyAircraft;
import edu.hitsz.aircraft.enemy.factory.EnemyFactory;
import edu.hitsz.aircraft.enemy.factory.MobEnemyFactory;
import edu.hitsz.aircraft.enemy.factory.SuperEnemyFactory;

public class MediumGame extends BaseGame{
    public  static  int GameDifficulty=2;

    public MediumGame(Context context, Handler handler) {
        super(context,handler);
        this.backGround = ImageManager.BACKGROUND2_IMAGE;
        //普通模式下 boss敌机出现 每次分数阈值涨幅为 borderToBoss = 800
        this.borderAddForBoss=800;
    }


    private int enemyHpAdd = 0;
    private int enemySpeedAdd = 0;

    @Override
    protected void controlDifficulty() {
        if (super.getTime() % 4000 == 0) {
            if (cycleDuration >= 300) {
                cycleDuration -= 10;
            }
            if (mobEnemyPro >= 0.3) {
                mobEnemyPro -= 0.01;
                superEnemyPro += 0.01;
            }
            if (enemySpeedAdd <= 30) {
                enemyHpAdd += 2;
                if (enemyHpAdd % 4==0) {
                    enemySpeedAdd += 1;
                }
            }
            System.out.printf("时间:%d\t周期:%d\t最大敌机数量:%d\t普通敌机概率:%.2f\t精英敌机概率:%.2f\t敌机血量增幅:%d\t敌机速度增幅:%d\n",super.getTime(),cycleDuration,enemyMaxNumber,mobEnemyPro,superEnemyPro, enemyHpAdd, enemySpeedAdd);
        }else if (super.getTime()  % 9000 == 0){
            enemyMaxNumber++;
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
