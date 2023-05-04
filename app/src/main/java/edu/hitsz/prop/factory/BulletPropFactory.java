package edu.hitsz.prop.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BulletProp;

/**
 * @author leng
 */
public class BulletPropFactory implements PropFactory{
    @Override
    public BaseProp createProp(int locationX, int locationY) {
        return new BulletProp(locationX, locationY, 5);
    }
}
