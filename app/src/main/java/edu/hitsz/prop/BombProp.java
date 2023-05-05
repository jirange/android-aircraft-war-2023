package edu.hitsz.prop;

import android.util.Log;

import edu.hitsz.activity.GameActivity;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.music.MySoundPool;
import edu.hitsz.observer.Subscriber;
import edu.hitsz.music.MusicThread;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leng
 */
public class BombProp extends BaseProp {

    public static double drop_probability =0.3;

    public BombProp(int locationX, int locationY, int propSpeed) {
        super(locationX, locationY, propSpeed);
    }

    public void setSubscribers(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }

    /**
     * 观察者列表
     */
    public List<Subscriber> subscribers=new ArrayList<>();


    /**
     * 增加观察者
     */
    public void addSubscribers (Subscriber subscriber){
        subscribers.add(subscriber);
    }

    /**
     * 删除观察者
     */
    public void removeSubscriber (Subscriber subscriber){
        subscribers.remove(subscriber);
    }

    /**
     * 通知所有观察者
     */
    public void notifyAllSubscribers(){
        for (Subscriber subscriber : subscribers) {
            subscriber.update();
        }
    }

    /**
     * 炸弹道具生效
     * mainBusinessLogic
     * @param heroAircraft 碰到炸弹的英雄减血
     */
    @Override
    public void activeProp(HeroAircraft heroAircraft) {
        Log.i("BombProp","active!");
        //todo 炸弹爆炸音效
        MySoundPool.playMusic("bomb_explosion");
        notifyAllSubscribers();

    }
}
