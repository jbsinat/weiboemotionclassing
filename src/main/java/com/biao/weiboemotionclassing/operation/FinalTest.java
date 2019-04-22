package com.biao.weiboemotionclassing.operation;

import com.biao.weiboemotionclassing.tools.TxtFileOperation;
import java.util.List;

/**
 * 四、情感分类第四步：测试集正确率测试
 */
public class FinalTest {

    public static void main(String[] args) {

        //利用评论语料集来测试
        init_test_with_comments();

        //利用卡方验证计算得到的特征词来测试
        init_test_with_tezhengci_final();

    }

    /**
     * 利用CHI来进行测试计算-依据特征词+权重
     */
    public static void init_test_with_tezhengci_final() {
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
            if (JudgeClass.init_with_tezhengci_final(testList_0.get(i)) == 0) {
                //样本为正，预测结果为正
                TP++;
            } else {
                //样本为正，预测结果为负
                FN++;
            }
        }
        //对负面类测试
        for (int i=0;i<testList_1.size();i++) {
            if (JudgeClass.init_with_tezhengci_final(testList_1.get(i)) == 0) {
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

        double accuracy,
                pp_precision,   //正面类的精确率
                pn_precision,   //负面类的精确率
                rp_recall,      //正面类的召回率
                rn_recall,      //负面类的召回率
                Fp_measure,    //正面类的F值
                Fn_measure;    //负面类的F值
        accuracy = (TP + TN) / (TP + FP + TN + FN);
        pp_precision = TP / (TP + FP);
        pn_precision = TN / (FN + TN);
        rp_recall = TP / (TP + FN);
        rn_recall = TN / (TN +FP);
        Fp_measure = 2 / (1/pp_precision + 1/rp_recall);
        Fn_measure = 2 / (1/pn_precision + 1/rn_recall);

        System.out.println("accuracy = " + accuracy);
        System.out.println("pp_precision = " + pp_precision);
        System.out.println("pn_precision = " + pn_precision);
        System.out.println("rp_recall = " + rp_recall);
        System.out.println("rn_recall = " + rn_recall);
        System.out.println("Fp_measure = " + Fp_measure);
        System.out.println("Fn_measure = " + Fn_measure);
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
            if (JudgeClass.init_with_comments(testList_0.get(i)) == 0) {
                //样本为正，预测结果为正
                TP++;
            } else {
                //样本为正，预测结果为负
                FN++;
            }
        }
        //对负面类测试
        for (int i=0;i<testList_1.size();i++) {
            if (JudgeClass.init_with_comments(testList_1.get(i)) == 0) {
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

        double accuracy,
                pp_precision,   //正面类的精确率
                pn_precision,   //负面类的精确率
                rp_recall,      //正面类的召回率
                rn_recall,      //负面类的召回率
                Fp_measure,    //正面类的F值
                Fn_measure;    //负面类的F值
        accuracy = (TP + TN) / (TP + FP + TN + FN);
        pp_precision = TP / (TP + FP);
        pn_precision = TN / (FN + TN);
        rp_recall = TP / (TP + FN);
        rn_recall = TN / (TN +FP);
        Fp_measure = 2 / (1/pp_precision + 1/rp_recall);
        Fn_measure = 2 / (1/pn_precision + 1/rn_recall);

        System.out.println("accuracy = " + accuracy);
        System.out.println("pp_precision = " + pp_precision);
        System.out.println("pn_precision = " + pn_precision);
        System.out.println("rp_recall = " + rp_recall);
        System.out.println("rn_recall = " + rn_recall);
        System.out.println("Fp_measure = " + Fp_measure);
        System.out.println("Fn_measure = " + Fn_measure);

    }

}
