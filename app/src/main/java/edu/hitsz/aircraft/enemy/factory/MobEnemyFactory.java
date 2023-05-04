package edu.hitsz.aircraft.enemy.factory;

import edu.hitsz.ImageManager;
import edu.hitsz.activity.MainActivity;
import edu.hitsz.aircraft.enemy.AbstractEnemyAircraft;
import edu.hitsz.aircraft.enemy.MobEnemy;
//import edu.hitsz.application.ImageManager;
//import edu.hitsz.application.Main;

/**
 * @author leng
 */
public class MobEnemyFactory implements EnemyFactory {



    @Override
    public AbstractEnemyAircraft createEnemy(int speed, int hp) {
        return new MobEnemy((int) (Math.random() * (MainActivity.screenWidth - ImageManager.MOB_ENEMY_IMAGE.getWidth())),//ImageManager.MOB_ENEMY_IMAGE.getWidth()
                (int) (Math.random() * MainActivity.screenHeight * 0.05),
                0,
                speed,
                hp);
    }

}
