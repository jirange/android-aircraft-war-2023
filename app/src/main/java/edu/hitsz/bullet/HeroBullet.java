package edu.hitsz.bullet;

/**
 * @Author hitsz
 */
public class HeroBullet extends BaseBullet {

    public HeroBullet(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        this.power=30;
    }

    /**
     * 炸弹爆炸时 英雄机子弹无动作
     */
    @Override
    public void update() {}
}
