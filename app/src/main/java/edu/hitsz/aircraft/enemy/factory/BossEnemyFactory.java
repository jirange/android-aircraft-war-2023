package edu.hitsz.aircraft.enemy.factory;

import edu.hitsz.ImageManager;
import edu.hitsz.activity.MainActivity;
import edu.hitsz.aircraft.enemy.AbstractEnemyAircraft;
import edu.hitsz.aircraft.enemy.BossEnemy;

/**
 * @author leng
 */
public class BossEnemyFactory implements EnemyFactory {

    public static int bossHP = 150;

    @Override
    public AbstractEnemyAircraft createEnemy(int speed, int hp) {
        return new BossEnemy((int) (Math.random() * (MainActivity.screenWidth - ImageManager.BOSS_ENEMY_IMAGE.getWidth())), (int) (Math.random() * MainActivity.screenHeight * 0.05),
                speed, 0, hp);
    }
}
