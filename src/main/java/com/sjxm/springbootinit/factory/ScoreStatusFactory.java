package com.sjxm.springbootinit.factory;

import java.math.BigDecimal;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/5
 * @Description:
 */
public class ScoreStatusFactory {

    public static String getStatus(BigDecimal score){
        BigDecimal zero = new BigDecimal(0);
        BigDecimal sixty = new BigDecimal(60);
        BigDecimal ninety = new BigDecimal(90);
        if(score.max(zero).equals(score)&&score.compareTo(sixty)<0){
            return "不及格";
        }
        else if(score.max(sixty).equals(score)&&score.compareTo(ninety)<0){
            return "及格";
        }
        else{
            return "优秀";
        }
    }
}
