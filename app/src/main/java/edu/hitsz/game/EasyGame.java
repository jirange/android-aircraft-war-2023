package edu.hitsz.game;

import android.content.Context;
import android.os.Handler;

import edu.hitsz.ImageManager;
import edu.hitsz.aircraft.enemy.AbstractEnemyAircraft;
import edu.hitsz.aircraft.enemy.factory.EnemyFactory;
import edu.hitsz.aircraft.enemy.factory.MobEnemyFactory;
import edu.hitsz.aircraft.enemy.factory.SuperEnemyFactory;

public class EasyGame extends BaseGame{
    public  static  int GameDifficulty=1;
    public static final String TAG = "EasyGame";


    public EasyGame(Context context, Handler handler) {
        super(context,handler);
        this.backGround = ImageManager.BACKGROUND1_IMAGE;
    }

    @Override
    protected void controlDifficulty() {
        //简单模式：不用控制难度变化
    }

    @Override
    public void creatBossEnemy(){
        //简单模式：不生成boss敌机
    }

    @Override
    protected AbstractEnemyAircraft getEnemyAircraft() {
        EnemyFactory factory;
        AbstractEnemyAircraft enemy;
        if (Math.random() <= superEnemyPro) {
            factory = new SuperEnemyFactory();
            enemy = factory.createEnemy(10,60);
        } else {
            factory = new MobEnemyFactory();
            enemy = factory.createEnemy(10,30);
        }
        return enemy;
    }

}
