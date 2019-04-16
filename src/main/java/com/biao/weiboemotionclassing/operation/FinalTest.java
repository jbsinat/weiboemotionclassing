package com.biao.weiboemotionclassing.operation;

import com.biao.weiboemotionclassing.tools.TxtFileOperation;
import com.hankcs.hanlp.seg.common.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 四、情感分类第四步：测试集正确率测试
 */
public class FinalTest {

    public static void main(String[] args) {

        //利用评论语料集来测试
//        init_test_with_comments();

        //利用卡方验证计算得到的特征词来测试
        init_test_with_tezhengci();

    }

    /**
     * 直接利用评论语料集来进行测试计算
     */
    public static void init_test_with_comments() {
        /**
         * TP：样本为正，预测结果为正；
         * FP：样本为负，预测结果为正；
         * TN：样本为负，预测结果为负；
         * FN：样本为正，预测结果为负。
         */
        double TP=0,FP=0,FN=0,TN=0;

        List<String> testList_0 = TxtFileOperation.readAllLinesWithContent("data_group/test_data_set/0_1000_happy_test.txt");
        List<String> testList_1 = TxtFileOperation.readAllLinesWithContent("data_group/test_data_set/1_1000_angry_test.txt");

        //对正面类测试
        for (int i=0;i<testList_0.size();i++) {
            if (zhengquelvjisuan_withComments(testList_0.get(i)) == 0) {
                //样本为正，预测结果为正
                TP++;
            } else {
                //样本为正，预测结果为负
                FN++;
            }
        }
        //对负面类测试
        for (int i=0;i<testList_1.size();i++) {
            if (zhengquelvjisuan_withComments(testList_1.get(i)) == 0) {
                //样本为负，预测结果为正
                FP++;
            } else {
                //样本为负，预测结果为负
                TN++;
            }
        }

        System.out.println("TP = " +TP);
        System.out.println("FP = " +FP);
        System.out.println("TN = " +TN);
        System.out.println("FN = " +FN);

        double accuracy,precision,recall,F_mearsure;
        accuracy = (TP + TN) / (TP + FP + TN + FN);
        precision = TP / (TP + FP);
        recall = TP / (TP + FN);
        F_mearsure = 2 / (1/precision + 1/recall);

        System.out.println("accuracy = " + accuracy);
        System.out.println("precision = " + precision);
        System.out.println("recall = " + recall);
        System.out.println("F-mearsure = " + F_mearsure);

    }

    /**
     * 利用CHI来进行测试计算-依据特征词+权重
     */
    public static void init_test_with_tezhengci() {
        /**
         * TP：样本为正，预测结果为正；
         * FP：样本为负，预测结果为正；
         * TN：样本为负，预测结果为负；
         * FN：样本为正，预测结果为负。
         */
        double TP=0,FP=0,FN=0,TN=0;

        List<String> testList_0 = TxtFileOperation.readAllLinesWithContent("data_group/test_data_set/0_1000_happy_test.txt");
        List<String> testList_1 = TxtFileOperation.readAllLinesWithContent("data_group/test_data_set/1_1000_angry_test.txt");

        //对正面类测试
        for (int i=0;i<testList_0.size();i++) {
            if (zhengquelvjisuan_withTezhengci(testList_0.get(i)) == 0) {
                //样本为正，预测结果为正
                TP++;
            } else {
                //样本为正，预测结果为负
                FN++;
            }
        }
        //对负面类测试
        for (int i=0;i<testList_1.size();i++) {
            if (zhengquelvjisuan_withTezhengci(testList_1.get(i)) == 0) {
                //样本为负，预测结果为正
                FP++;
            } else {
                //样本为负，预测结果为负
                TN++;
            }
        }

        System.out.println("TP = " +TP);
        System.out.println("FP = " +FP);
        System.out.println("TN = " +TN);
        System.out.println("FN = " +FN);

        double accuracy,precision,recall,F_mearsure;
        accuracy = (TP + TN) / (TP + FP + TN + FN);
        precision = TP / (TP + FP);
        recall = TP / (TP + FN);
        F_mearsure = 2 / (1/precision + 1/recall);

        System.out.println("accuracy = " + accuracy);
        System.out.println("precision = " + precision);
        System.out.println("recall = " + recall);
        System.out.println("F-mearsure = " + F_mearsure);
    }

    /**
     * 传入一条评论，计算该评论的类别--直接依据评论
     * @param comment
     * @return
     */
    public static Integer zhengquelvjisuan_withComments(String comment){

        //处理评论：分词、去除停用词;然后获取 所有 原词语 的集合
        List<Term> termList = FenciWithHanLpOperation.qiefenAndDescTingyongci(comment);
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < termList.size(); i++) {
            stringList.add(termList.get(i).word);
        }
        System.out.println(stringList);

        //先验概率:因为每种评论都有1000条，所以各类占比相同
        double py0 = 0.5;
        double py1 = 0.5;

        //从训练过程生成的文件中获取词语，这里给出路径
        String allWordsPath0 = "data_group/simple_data_set/0_1000_comments.txt";
        String allWordsPath1 = "data_group/simple_data_set/1_1000_comments.txt";

        //1.对y0类计算
        Double p0 = JudgeClass.getPtc_pls(stringList, py0, allWordsPath0);
        //2.对y1类计算
        Double p1 = JudgeClass.getPtc_pls(stringList, py1, allWordsPath1);
        System.out.println("p1:" + p1);

        //5.比较p0、p1、p2、p3,选择最大者则当前评论属于该类
        int class_ = JudgeClass.outLeibie(p0,p1);

        return class_;

    }

    /**
     * 传入一条评论，计算该评论的类别--依据特征词+权重
     * @param comment
     * @return
     */
    public static Integer zhengquelvjisuan_withTezhengci(String comment){

        //处理评论：分词、去除停用词;然后获取 所有 原词语 的集合
        List<Term> termList = FenciWithHanLpOperation.qiefenAndDescTingyongci(comment);
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < termList.size(); i++) {
            stringList.add(termList.get(i).word);
        }
        System.out.println(stringList);

        //先验概率:因为每种评论都有1000条，所以各类占比相同
        double py0 = 0.5;
        double py1 = 0.5;

        //从训练过程生成的文件中获取词语，这里给出路径
        String allWordsPath0 = "data_group/feature_word_set_all/0_happy_all.txt";
        String allWordsPath1 = "data_group/feature_word_set_all/1_angry_all.txt";
        String tezhengWordsPath0 = "data_group/feature_word_set/0_happy.txt";
        String tezhengWordsPath1 = "data_group/feature_word_set/1_angry.txt";

        //全部特征词
        Map<String, Double> featurelist0_all = TxtFileOperation.readFeatureSetFile(allWordsPath0);
        Map<String, Double> featurelist1_all = TxtFileOperation.readFeatureSetFile(allWordsPath1);
        //topN 特征词
        Map<String, Double> featurelist0_part = TxtFileOperation.readFeatureSetFile(tezhengWordsPath0);
        Map<String, Double> featurelist1_part = TxtFileOperation.readFeatureSetFile(tezhengWordsPath1);

        //对类0进行计算
        //首先计算出∑ weight
        Double p0_wei = 1.0;
        for (int i=0;i<stringList.size();i++){
            if(featurelist0_part.get(stringList.get(i)) != null){
                //因为权值大多大于1000，这里将权值取对数，缩小权值，使得后面计算概率时更精确
                p0_wei += Math.log10(featurelist0_part.get(stringList.get(i)));
            }
        }
        System.out.println("p0_wei = " + p0_wei);

        double pp0 = 1.0;
        for (int i=0;i<stringList.size();i++) {
            if (featurelist0_part.get(stringList.get(i)) != null) {
                pp0 *= Math.pow(featurelist0_part.get(stringList.get(i)) / p0_wei, Math.log10(featurelist0_part.get(stringList.get(i))));
            }
        }
        //若pp0还为1，则说明待分类评论中的所有词都没有在topN 个特征词中找到，姑且令属于0类的概率为0
        if (pp0 == 1.0) {
            pp0 = 0.0;
        }
        pp0 *= py0;
        System.out.println("pp0 = " + pp0);


        //对类1进行计算
        //首先计算出∑ weight
        Double p1_wei = 1.0;
        for (int i=0;i<stringList.size();i++){
            if(featurelist1_part.get(stringList.get(i)) != null){
                p1_wei += Math.log10(featurelist1_part.get(stringList.get(i)));
            }
        }
        System.out.println("p1_wei = " + p1_wei);

        double pp1 = 1.0;
        for (int i=0;i<stringList.size();i++) {
            if (featurelist1_part.get(stringList.get(i)) != null) {
                pp1 *= Math.pow(featurelist1_part.get(stringList.get(i)) / p1_wei, Math.log10(featurelist1_part.get(stringList.get(i))));
            }
        }
        //同上
        if (pp1 == 1.0) {
            pp1 = 0.0;
        }
        pp1 *= py1;
        System.out.println("pp1 = " + pp1);

        if (pp0 > pp1){
            System.out.println("为 0 类");
            return 0;
        } else {
            System.out.println("为 1 类");
            return 1;
        }

    }


}
