package edu.hitsz.aircraft.enemy.factory;

import edu.hitsz.ImageManager;
import edu.hitsz.activity.MainActivity;
import edu.hitsz.aircraft.enemy.AbstractEnemyAircraft;
import edu.hitsz.aircraft.enemy.SuperEnemy;

/**
 * @author leng
 */
public class SuperEnemyFactory implements EnemyFactory{
    @Override
    public AbstractEnemyAircraft createEnemy(int speed,int hp) {
        return new SuperEnemy((int) (Math.random() * (MainActivity.screenWidth - ImageManager.ELITE_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * MainActivity.screenHeight * 0.05),
                0,
                speed,
                hp);
    }
}
