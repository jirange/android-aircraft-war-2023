package edu.hitsz.prop.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BloodProp;

/**
 * @author leng
 */
public class BloodPropFactory implements PropFactory{
    @Override
    public BaseProp createProp(int locationX, int locationY) {
        return new BloodProp(locationX, locationY, 5);
    }
}
