package com.biao.weiboemotionclassing.operation;

import com.biao.weiboemotionclassing.tools.TxtFileOperation;
import com.hankcs.hanlp.seg.common.Term;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 三、情感分类第三步：利用朴素贝叶斯算法进行类别判断
 */
public class JudgeClass {

    public static void main(String[] args) {

        //给定一条评论：
//        String comment = "我去！一看特警俩字儿我晕~单兵装备，一个字“帅”；";   //正面，预测结果为负面
//        String comment = "永远的偶像哦你...真的是...全世界...最帅的男人！比十全十美还完美！帅惨了!!!我到家了!哈哈哈哈!";   //正面，预测结果为正面
//        String comment = "世界上还是有些不知廉耻的人《非诚勿扰》马诺唱《拨浪鼓》，边唱边浪~我受到了精吓！";    //负面，预测结果为负面
//        String comment = "滚。你的那句你就是铁公鸡。。。我会来拔得！";   //负面，依据特征词+权重预测结果为正面，直接依据评论统计判断预测结果为负面
        String comment = "傻婆啊。不如你改下个死人名啦！好难你！";   //负面，方式一预测结果为正面，方式二预测结果为负面

        init_with_tezhengci_final(comment);

        init_with_comments(comment);

    }


    /**
     * 直接利用评论来基于词频统计结果进行分类概率计算
     * @param comment：评论语料
     * @return 返回预测分类
     */
    public static int init_with_tezhengci_final(String comment){

        //处理评论：分词、去除停用词;然后获取 所有 原词语 的集合
        List<Term> termList = FenciWithHanLpOperation.qiefenAndDescTingyongci(comment);
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < termList.size(); i++) {
            stringList.add(termList.get(i).word);
        }
        System.out.println(stringList);

        //先验概率: 因为每种评论都有1000条，所以各类占比相同
        double py0 = 0.5;
        double py1 = 0.5;

        //从训练过程得到的文件中获取特征词+bool权值集合，这里给出路径
        String allWordsPath0 = "data_group/feature_word_set/0_pos_all_with_boolQZ.txt";
        String allWordsPath1 = "data_group/feature_word_set/1_neg_all_with_boolQZ.txt";
        Map<String, Integer> tezhengci_weight_0 = TxtFileOperation.readFeatureSetFile_Str_Int(allWordsPath0);
        Map<String, Integer> tezhengci_weight_1 = TxtFileOperation.readFeatureSetFile_Str_Int(allWordsPath1);

        //首先计算出∑ weight，即两类中词wi的权重之和
        double heofwordi = 0.0;
        Map<String, Double> word_towclassquanzhongzhihe = new HashMap<>();
        for (int i=0; i<stringList.size(); i++) {
            heofwordi = 0.0;
            if (tezhengci_weight_0.get(stringList.get(i)) != null) {
                heofwordi += tezhengci_weight_0.get(stringList.get(i));
            }
            if (tezhengci_weight_1.get(stringList.get(i)) != null) {
                heofwordi += tezhengci_weight_1.get(stringList.get(i));
            }
            word_towclassquanzhongzhihe.put(stringList.get(i), heofwordi);
        }

        //1.对y0类计算
        double p0 = 1.0;
        //待分类评论的所有词在0类中的权值之和
        double he0 = Math.log10(tezhengci_weight_0.size());
        for (int i=0;i<stringList.size();i++) {
            if (tezhengci_weight_0.get(stringList.get(i)) == null) {
                p0 *= 1 / (word_towclassquanzhongzhihe.get(stringList.get(i)) + 2 + he0);
            } else {
                p0 *= (tezhengci_weight_0.get(stringList.get(i)) + 1) / (word_towclassquanzhongzhihe.get(stringList.get(i)) + 2 + he0);
            }
        }
        //再乘上先验概率
        p0 *= py0;
        System.out.println("p0 = " + p0);

        //2.对类1进行计算
        double p1 = 1.0;
        //待分类评论的所有词在1类中的权值之和
        double he1 = Math.log10(tezhengci_weight_1.size());
        for (int i=0;i<stringList.size();i++) {
            if (tezhengci_weight_1.get(stringList.get(i)) == null) {
                p1 *= 1 / (word_towclassquanzhongzhihe.get(stringList.get(i)) + 2 + he1);
            }
            else {
                p1 *= (tezhengci_weight_1.get(stringList.get(i)) + 1) / (word_towclassquanzhongzhihe.get(stringList.get(i)) + 2 + he1);
            }
        }
        p1 *= py1;
        System.out.println("p1 = " + p1);

        if (p0 >= p1) {
            System.out.println("是 0-正面类");
            return 0;
        } else {
            System.out.println("是 1-负面类");
            return 1;
        }

    }

    /**
     * 直接利用评论来基于词频统计结果进行分类概率计算
     * @param comment：评论语料
     * @return 返回预测分类
     */
    public static int init_with_comments(String comment){

        //处理评论：分词、去除停用词;然后获取 所有 原词语 的集合
        List<Term> termList = FenciWithHanLpOperation.qiefenAndDescTingyongci(comment);
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < termList.size(); i++) {
            stringList.add(termList.get(i).word);
        }
        System.out.println(stringList);

        //先验概率: 因为每种评论都有1000条，所以各类占比相同
        double py0 = 0.5;
        double py1 = 0.5;

        //从训练过程的文件中获取评论语料集，这里给出路径
        String allWordsPath0 = "data_group/simple_data_set/0_1000_comments.txt";
        String allWordsPath1 = "data_group/simple_data_set/1_1000_comments.txt";

        /**
         * 利用贝叶斯公式进行计算
         * 注意点：
         * 1.为了避免出现 0 概率，采用拉普拉斯平滑，将所有词出现的次数+1;
         *
         * 2.对每一类计算：
         *      1）对词ci：属于类C的概率 = 含有词ci的评论数 / 类C的总评论数
         *      2)对评论c：属于类C的概率 = (∑ p(ci属于类C)) * 先验概率(即为0.5)
         */
        //1.对y0类计算
        Double p0 = getPtc_pls(stringList, py0, allWordsPath0);
        //2.对y1类计算
        Double p1 = getPtc_pls(stringList, py1, allWordsPath1);

        return outLeibie(p0, p1);
    }

    /**
     * 比较概率大小，得出所属类别
     * @param p0
     * @param p1
     */
    public static Integer outLeibie(double p0, double p1){
        if(p0 > p1){
            System.out.println("当前评论类别为： " + "0-正面");
            return 0;
        } else {
            System.out.println("当前评论类别为： " + "1-负面");
            return 1;
        }
    }

    /**
     * 计算贝叶斯概率--依据评论来计算
     * @param stringList
     * @param xianYanGaiLv
     * @param path
     * @return
     */
    public static Double getPtc_pls(List<String> stringList, Double xianYanGaiLv, String path) {
        List<Double> p_as = new ArrayList<>();
        //每个词的出现后验概率计算
        double p = 1.0;
        for (int k1 = 0; k1 < stringList.size(); k1++) {
            String ci = stringList.get(k1); //获取当前评论的一个词
            //返回含有词的评论数
            Double Ycx = getNcx_pls(ci, path).doubleValue();
            System.out.println("Ycx = " + Ycx);
            //不含该词的评论数
            Double NYcx = 1000 - Ycx;
            //使用拉普拉斯平滑，将所有词频加1，避免0概率的出现
            p = ((Ycx + 1) / 1000);
            System.out.println("p " + k1 + " = " + p);
            p_as.add(p);
        }
        //整条评论的后验概率计算。p(y_i)，当前类的概率，初始化为1
        Double p_i = 1.0;
        for (int k = 0; k < p_as.size(); k++){
            p_i = p_i * p_as.get(k);
        }
        p_i = p_i * xianYanGaiLv;    //可以看成是属于 0 类的概率
        return p_i;
    }

    /**
     * 含有词ci的评论数
     * @param ci
     * @param path
     * @return
     */
    public static Integer getNcx_pls(String ci, String path){
        List<String> comments = TxtFileOperation.readAllLinesWithContent(path);
        int count = 0;
        for (String comment : comments){
            if(comment.contains(ci)){
                count++;
            }
        }
        return count;
    }

    /**
     * 返回文件行数，因为每一行都是一个单位（或为评论，或为特征词），所以便可藉此得到所有单位数
     * @param path
     * @return
     */
    public static Integer getN(String path) {
        List<String> words = TxtFileOperation.readAllLinesWithContent(path);
        return words.size();
    }

}