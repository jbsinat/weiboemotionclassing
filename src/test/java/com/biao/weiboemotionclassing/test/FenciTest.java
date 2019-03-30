package com.biao.weiboemotionclassing.test;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import org.junit.Test;

import java.util.List;

public class FenciTest {

    @Test
    public void testFenci(){
        String comment = "昨晚上经过中润，看到的一个立柱，价格应该不低，可是看了半天也没看明白诉求点是什么";
        List<Term> termList;
        // 将一些富含繁体字的评论转化成简体字版的评论
        HanLP.convertToSimplifiedChinese(comment);
        //分词
        termList = HanLP.segment(comment);
        System.out.println(termList);

        //去除停用词
        CoreStopWordDictionary.apply(termList);
        System.out.println(termList);
    }

}
