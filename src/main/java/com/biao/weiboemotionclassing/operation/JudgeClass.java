package com.biao.weiboemotionclassing.operation;

import com.biao.weiboemotionclassing.tools.TxtFileOperation;
import com.hankcs.hanlp.seg.common.Term;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 三、情感分类第三步：利用朴素贝叶斯算法进行类别判断
 */
public class JudgeClass {

    public static void main(String[] args) {

        //给定一条评论：
        String comment = "本来腿就不长，还埋起来。你快有新名字了顶天立地舞起来...~~~中秋节快乐！！吃hign玩high闹hign....";

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
        Double p0 = getPtc(stringList, py0, allWordsPath0, tezhengWordsPath0);
        //2.对y1类计算
        Double p1 = getPtc(stringList, py1, allWordsPath1, tezhengWordsPath1);
        System.out.println("p1:" + p1);

        //3.对y2类计算
        Double p2 = getPtc(stringList, py2, allWordsPath2, tezhengWordsPath2);
        System.out.println("p2:" + p2);

        //4.对y3类计算
        Double p3 = getPtc(stringList, py3, allWordsPath3, tezhengWordsPath3);
        System.out.println("p3:" + p3);

        //5.比较p0、p1、p2、p3,选择最大者则当前评论属于该类
        outLeibie(p0,p1,p2,p3);

    }

    /**
     * 比较概率大小，得出所属类别
     * @param p0
     * @param p1
     * @param p2
     * @param p3
     */
    public static void outLeibie(Double p0, Double p1, Double p2, Double p3){
        if(p0>p1 && p0>p2 && p0>p3){
            System.out.println("当前评论类别为： " + "0-喜悦");
        }
        else if(p1>p0 && p1>p2 && p1>p3){
            System.out.println("当前评论类别为： " + "1-愤怒");
        }
        else if(p2>p0 && p2>p1 && p2>p3){
            System.out.println("当前评论类别为： " + "2-厌恶");
        }
        else if(p3>p0 && p3>p1 && p3>p2){
            System.out.println("当前评论类别为： " + "3-低落");
        }
    }

    /**
     * 运用贝叶斯公式计算各类的概率
     * @param stringList
     * @param xianYanGaiLv
     * @param allWordsPath
     * @param tezhengWordsPath
     * @return
     */
    private static Double getPtc(List<String> stringList, Double xianYanGaiLv, String allWordsPath, String tezhengWordsPath) {
        List<Double> p_a0s = new ArrayList<>();
        //
        Double p = 1.0;
        for (int k1 = 0; k1 < stringList.size(); k1++) {
            String ci = stringList.get(k1); //获取当前评论的一个词
            Double Ncx = getNcx(ci, allWordsPath).doubleValue();
            Double N = getN(allWordsPath).doubleValue();
            Double V = getV(tezhengWordsPath).doubleValue();

            p = ((Ncx + 1) / (N + V));
            p_a0s.add(p);
        }
        Double p0 = 1.0;
        for (int k2 = 0; k2 < p_a0s.size(); k2++){
            p0 = p0 * p_a0s.get(k2);
        }
        p0 = p0 * xianYanGaiLv;    //可以看成是属于 0 类的概率
        return p0;
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
            if (ci.equals(words.get(i).trim())) {
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