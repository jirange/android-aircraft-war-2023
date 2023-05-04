package edu.hitsz.prop.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BombProp;

/**
 * @author leng
 */
public class BombPropFactory implements PropFactory{
    @Override
    public BaseProp createProp(int locationX, int locationY) {
        return new BombProp(locationX, locationY, 5);
    }
}
