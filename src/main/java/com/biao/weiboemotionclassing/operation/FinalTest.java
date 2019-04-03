package com.biao.weiboemotionclassing.operation;

import com.biao.weiboemotionclassing.tools.TxtFileOperation;
import com.hankcs.hanlp.seg.common.Term;

import java.util.ArrayList;
import java.util.List;

/**
 * 四、情感分类第四步：测试集正确率测试
 */
public class FinalTest {

    public static void main(String[] args) {

        List<String> testList_0 = TxtFileOperation.readAllLinesWithContent("data_group/test_data_set/0_1000_happy_test.txt");
//        List<String> testList_1 = TxtFileOperation.readAllLinesWithContent("data_group/test_data_set/1_1000_angry_test.txt");
//        List<String> testList_2 = TxtFileOperation.readAllLinesWithContent("data_group/test_data_set/2_1000_hate_test.txt");
//        List<String> testList_3 = TxtFileOperation.readAllLinesWithContent("data_group/test_data_set/3_1000_downcast_test.txt");

        int p0 = 0; //p0：是0类的次数
        int p_0 = 0; //p_0：不是0类的次数
        for (int i=0;i<testList_0.size();i++) {
            if (zhengquelvjisuan(testList_0.get(i)) == 0) {
                p0++;
            } else {
                p_0++;
            }
        }
        System.out.println("p0 = " + p0);
        System.out.println("p_0 = " + p_0);
        double p = (double)p0 / (double)(p0 + p_0);
        System.out.println("p = " + p);


    }


    /**
     * 传入一条评论，计算该评论的类别
     * @param comment
     * @return
     */
    public static Integer zhengquelvjisuan(String comment){

        //处理评论：分词、去除停用词;然后获取 所有 原词语 的集合
        List<Term> termList = FenciWithHanLpOperation.qiefenAndDescTingyongci(comment);
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < termList.size(); i++) {
            stringList.add(termList.get(i).word);
        }
        System.out.println(stringList);

        //先验概率:因为每种评论都有1000条，所以各类占比相同
        double py0 = 0.25;
        double py1 = 0.25;
        double py2 = 0.25;
        double py3 = 0.25;

        //从训练过程生成的文件中获取词语，这里给出路径
        String allWordsPath0 = "data_group/feature_word_set_all/0_happy_all.txt";
        String allWordsPath1 = "data_group/feature_word_set_all/1_angry_all.txt";
        String allWordsPath2 = "data_group/feature_word_set_all/2_hate_all.txt";
        String allWordsPath3 = "data_group/feature_word_set_all/3_downcast_all.txt";
        String tezhengWordsPath0 = "data_group/feature_word_set/0_happy.txt";
        String tezhengWordsPath1 = "data_group/feature_word_set/1_angry.txt";
        String tezhengWordsPath2 = "data_group/feature_word_set/2_hate.txt";
        String tezhengWordsPath3 = "data_group/feature_word_set/3_downcast.txt";

        /**
         * 利用贝叶斯公式进行计算
         * 注意点：
         * 1.为了避免出现 0 概率，采用拉普拉斯平滑，将所有词出现的次数+1;
         *
         * 2.特征权值  是指特征词在文本中的权重，也可称为词在文本中的向量，是分类器分类的重要依据。
         * 		主要使用的有词频(tf):tf(wi)= freq(wi,dj) ;  freq(wi,dj)是词 wi在文本 dj中出现的次数
         *
         * 3.后验概率  是指特征词 wi出现在类别 cj中的概率，可以从训练语料中通过计算进行估计。
         * 		普遍采用词 wi在属于类别 cj的文本中的权值之和除以类别 cj的文本中所有词的权值之和。
         *
         * 4.最小单位计算公式：P(x|y)=Ncx+1/N+V :
         * 		其中 Ncx 为类别 c 下特征词 x 出现的次数，N 为类别 c 的总词数，V 为特征词数
         */
        List<Double> p_a0s = new ArrayList<>();
        //1.对y0类计算
        Double p0 = JudgeClass.getPtc(stringList, py0, allWordsPath0, tezhengWordsPath0);
        //2.对y1类计算
        Double p1 = JudgeClass.getPtc(stringList, py1, allWordsPath1, tezhengWordsPath1);
        System.out.println("p1:" + p1);

        //3.对y2类计算
        Double p2 = JudgeClass.getPtc(stringList, py2, allWordsPath2, tezhengWordsPath2);
        System.out.println("p2:" + p2);

        //4.对y3类计算
        Double p3 = JudgeClass.getPtc(stringList, py3, allWordsPath3, tezhengWordsPath3);
        System.out.println("p3:" + p3);

        //5.比较p0、p1、p2、p3,选择最大者则当前评论属于该类
        int class_ = JudgeClass.outLeibie(p0,p1,p2,p3);

        return class_;
    }

}
