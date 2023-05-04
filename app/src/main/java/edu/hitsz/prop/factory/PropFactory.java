package edu.hitsz.prop.factory;


import edu.hitsz.prop.BaseProp;


/**
 * @author leng
 */
public interface PropFactory {
    /**
     * 创建道具对象
     * @param locationX 横坐标
     * @param locationY 纵坐标
     * @return BaseProp
     */
    BaseProp createProp(int locationX, int locationY);

}
