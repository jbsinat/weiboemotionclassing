package com.biao.weiboemotionclassing.operation;

import com.biao.weiboemotionclassing.tools.TxtFileOperation;
import com.hankcs.hanlp.seg.common.Term;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 三、情感分类第三步：利用朴素贝叶斯算法进行类别判断
 */
public class JudgeClass {

    public static void main(String[] args) {

        //给定一条评论：
        String comment = "我去！一看特警俩字儿我晕~单兵装备，一个字“帅”；";

        init_with_tezhengci(comment);

//        init_with_comments(comment);

    }

    /**
     * 利用特征值+权重计算分类
     * @param comment
     */
    public static int init_with_tezhengci(String comment){
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
         * 4.最小单位计算公式：
         */

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

        //全部特征词集合
        Map<String, Double> featurelist0_all = TxtFileOperation.readFeatureSetFile(allWordsPath0);
        Map<String, Double> featurelist1_all = TxtFileOperation.readFeatureSetFile(allWordsPath1);
        //topN 特征词集合
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
        //若pp0还是1，则说明在选取的特征词中没有找到待分类的句子切分得到的所有词语
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
                //取对数，增加计算精度
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

    /**
     * 仅利用特征词来进行分类概率计算
     * ---------------该方法算得的正确率很低，现已抛弃该方法---------------
     */
    public static void init(String comment){

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
        Double p0 = getPtc(stringList, py0, allWordsPath0, tezhengWordsPath0);
        //2.对y1类计算
        Double p1 = getPtc(stringList, py1, allWordsPath1, tezhengWordsPath1);
        System.out.println("p1:" + p1);

        outLeibie(p0, p1);
    }


    /**
     * 直接利用评论来进行分类概率计算
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
    public static Integer outLeibie(Double p0, Double p1){
        if(p0 > p1){
            System.out.println("当前评论类别为： " + "0-正面");
            return 0;
        } else {
            System.out.println("当前评论类别为： " + "1-负面");
            return 1;
        }
    }

    /**
     * 运用贝叶斯公式计算各类的概率 -- 依据特征词来计算
     * @param stringList
     * @param xianYanGaiLv
     * @param allWordsPath
     * @param tezhengWordsPath
     * @return
     */
    public static Double getPtc(List<String> stringList, Double xianYanGaiLv, String allWordsPath, String tezhengWordsPath) {
        List<Double> p_a0s = new ArrayList<>();
        //
        Double p = 1.0;
        for (int k1 = 0; k1 < stringList.size(); k1++) {
            String ci = stringList.get(k1); //获取当前评论的一个词
            //返回给定词在类 i 中的出现次数
            Double Ncx = getNcx(ci, allWordsPath).doubleValue();
            System.out.println("Ncx = " + Ncx);
            //总词数
            Double N = getN(allWordsPath).doubleValue();
            //特征词数
            Double V = getV(tezhengWordsPath).doubleValue();

            p = ((Ncx + 1) / (N + V));
            System.out.println("p " + k1 + " = " + p);
            p_a0s.add(p);
        }
        //p(y_i)，当前类的概率，为1
        Double p0 = 1.0;
        for (int k2 = 0; k2 < p_a0s.size(); k2++){
            p0 = p0 * p_a0s.get(k2);
        }
        p0 = p0 * xianYanGaiLv;    //可以看成是属于 0 类的概率
        return p0;
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
        //
        Double p = 1.0;
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
        //p(y_i)，当前类的概率，为1
        Double p_i = 1.0;
        for (int k = 0; k < p_as.size(); k++){
            p_i = p_i * p_as.get(k);
        }
        p_i = p_i * xianYanGaiLv;    //可以看成是属于 0 类的概率
        return p_i;
    }

    /**
     * 返回词wi在类中的权重：Weight(wi,cj)是词 wi在属于类别 cj的文本中的权值之和。
     * @param ci
     * @param map
     * @return
     */
    public static Double Weight(String ci, Map<String, Double> map) {
        Double wei = 1.0;
        wei = map.get(ci);
        return wei;
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
     * 返回类 i 的特征词数
     * @param path
     * @return
     */
    public static Integer getV(String path) {
        Integer tezhengWords = getwordNumber(path);
        return tezhengWords;
    }

    /**
     * 返回类 i 的总词数
     * @param path
     * @return
     */
    public static Integer getN(String path) {
        Integer Words = getwordNumber(path);
        return Words;
    }

    /**
     * 返回给定词在类 i 中的出现次数
     * @param ci
     * @param path
     * @return
     */
    public static Integer getNcx(String ci, String path) {
        Integer Ncx = 0;
        List<String> words = TxtFileOperation.readAllLinesWithContent(path);
        for (int i=0;i<words.size();i++){
            if (ci.contains(words.get(i).trim())) {
                Ncx++;
            }
        }
        return Ncx;
    }

    /**
     * 通过获得文件行数从而得到词语个数
     * @param fileName
     * @return
     */
    public static Integer getwordNumber(String fileName) {
        Integer lines = 0;
        File file = new File(fileName);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = bufferedReader.readLine()) != null) {
                lines++;
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return lines;
    }


}