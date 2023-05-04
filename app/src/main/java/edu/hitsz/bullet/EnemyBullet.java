package edu.hitsz.bullet;

/**
 * @Author hitsz
 */
public class EnemyBullet extends BaseBullet {

    public EnemyBullet(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        this.power=30;
    }

    /**
     * 炸弹爆炸时 敌机子弹全部消失
     */
    @Override
    public void update() {
        this.vanish();
    }
}
