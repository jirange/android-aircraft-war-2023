package edu.hitsz.strategy.shoot;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 散射弹道
 * @author leng
 */
public class ScatteringShoot implements ShootStrategy{

    @Override
    public  List<BaseBullet> doShoot(int locationX,int locationY,int speedX,int speedY,int direction,int shootNum) {
        List<BaseBullet> res = new LinkedList<>();
        Random random = new Random();
        int x = locationX;


        int bulletSpeedY = speedY + direction * 7;
        BaseBullet bullet1;
        BaseBullet bullet2;
        BaseBullet bullet3;

        // 子弹发射位置相对飞机位置向前偏移
        // 多个子弹横向分散
        if (direction==-1){
            int bulletSpeedX = 5;
            int y = locationY+ direction*42;
            bullet1 = new HeroBullet(x, y, bulletSpeedX, bulletSpeedY);
            bullet2 = new HeroBullet(x, y, 0, bulletSpeedY);
            bullet3 = new HeroBullet(x, y, -bulletSpeedX, bulletSpeedY);
        }else {
            int bulletSpeedX = random.nextInt(10);
            int y = locationY+ direction*100;
            bullet1 = new EnemyBullet(x, y, bulletSpeedX, bulletSpeedY);
            bullet2 = new EnemyBullet(x, y, 0, bulletSpeedY);
            bullet3 = new EnemyBullet(x, y, -bulletSpeedX, bulletSpeedY);
        }

        Collections.addAll(res,bullet1,bullet2,bullet3);

        return res;
    }
}
