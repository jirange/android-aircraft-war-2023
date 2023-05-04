package edu.hitsz.strategy.shoot;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 直射
 * @author leng
 */
public class DirectShoot implements ShootStrategy{


    @Override
    public  List<BaseBullet> doShoot(int locationX,int locationY,int speedX,int speedY,int direction,int shootNum) {
        List<BaseBullet> res = new LinkedList<>();
        int y = locationY + direction*2;
        int bulletSpeedX = 0;
        int bulletSpeedY = speedY + direction*5;
        BaseBullet bullet;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            //子弹射击方向 (向上发射：1，向下发射：-1)
            if (direction==-1){
                bullet = new HeroBullet(locationX + (i*2 - shootNum + 1)*10, y, bulletSpeedX, bulletSpeedY);
            }else {
                bullet = new EnemyBullet(locationX + (i*2 - shootNum + 1)*10, y, bulletSpeedX, bulletSpeedY);
            }
            res.add(bullet);
        }
        return res;
    }
}
