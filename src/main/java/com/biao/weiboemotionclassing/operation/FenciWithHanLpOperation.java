package com.biao.weiboemotionclassing.operation;

import com.biao.weiboemotionclassing.entities.Comment_fenci_storeString;
import com.biao.weiboemotionclassing.tools.TxtFileOperation;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;

import java.util.*;

/**
 * 二、情感分类第二步：利用HanLp进行分词相关操作，并计算出特征值
 */
public class FenciWithHanLpOperation {

    public static void main(String[] args) {
        String filepath_0 = "data_group/simple_data_set/0_1000_comments.txt";
        String filepath_1 = "data_group/simple_data_set/1_1000_comments.txt";
        String filepath_2 = "data_group/simple_data_set/2_1000_comments.txt";
        String filepath_3 = "data_group/simple_data_set/3_1000_comments.txt";

        //获取分词预处理后的对象集合：对象（id，category，comment_fencis）
        List<Comment_fenci_storeString> comment_fencis_0 = PreOperation(filepath_0, "0-happy");
        List<Comment_fenci_storeString> comment_fencis_1 = PreOperation(filepath_1, "1-angry");
        List<Comment_fenci_storeString> comment_fencis_2 = PreOperation(filepath_2, "2-hate");
        List<Comment_fenci_storeString> comment_fencis_3 = PreOperation(filepath_3, "3-downcast");

        Map<String, Double> tops = new HashMap<>();
        Map<String, Double> tops1 = new HashMap<>();
        Map<String, Double> tops2 = new HashMap<>();
        Map<String, Double> tops3 = new HashMap<>();

        // 以 0 类为例，进行CHI计算：
        tops = jisuanCHIOf0(comment_fencis_0, comment_fencis_1, comment_fencis_2, comment_fencis_3, false);
        tops1 = jisuanCHIOf1(comment_fencis_0, comment_fencis_1, comment_fencis_2, comment_fencis_3, false);
        tops2 = jisuanCHIOf2(comment_fencis_0, comment_fencis_1, comment_fencis_2, comment_fencis_3, false);
        tops3 = jisuanCHIOf3(comment_fencis_0, comment_fencis_1, comment_fencis_2, comment_fencis_3, false);

        System.out.println("---------------------------------------------------------");
        System.out.println("tops.size() = " + tops.size());
        System.out.println("tops1.size() = " + tops1.size());
        System.out.println("tops2.size() = " + tops2.size());
        System.out.println("tops3.size() = " + tops3.size());

        //将所有特征词存入文件中：用于后面使用贝叶斯算法进行概率计算
        TxtFileOperation.saveAsFileWithMaps(tops, "data_group/feature_word_set_all/0_happy_all.txt");
        TxtFileOperation.saveAsFileWithMaps(tops1, "data_group/feature_word_set_all/1_angry_all.txt");
        TxtFileOperation.saveAsFileWithMaps(tops2, "data_group/feature_word_set_all/2_hate_all.txt");
        TxtFileOperation.saveAsFileWithMaps(tops3, "data_group/feature_word_set_all/3_downcast_all.txt");

        // 对Map排序，藉此选择词频为 top100 的词
        List<String> top_0_100;
        List<String> top_1_100;
        List<String> top_2_100;
        List<String> top_3_100;
        top_0_100 = getTopN(tops, 100);
        top_1_100 = getTopN(tops1, 100);
        top_2_100 = getTopN(tops2, 100);
        top_3_100 = getTopN(tops3, 100);

        System.out.println("---------------------------------------------------------");

        //将特征词存成特征文件
        String savePath_tezhengci_0 = "data_group/feature_word_set/0_happy.txt";
        String savePath_tezhengci_1 = "data_group/feature_word_set/1_angry.txt";
        String savePath_tezhengci_2 = "data_group/feature_word_set/2_hate.txt";
        String savePath_tezhengci_3 = "data_group/feature_word_set/3_downcast.txt";
        TxtFileOperation.saveAsFileWithContent(top_0_100, savePath_tezhengci_0);
        TxtFileOperation.saveAsFileWithContent(top_1_100, savePath_tezhengci_2);
        TxtFileOperation.saveAsFileWithContent(top_2_100, savePath_tezhengci_1);
        TxtFileOperation.saveAsFileWithContent(top_3_100, savePath_tezhengci_3);

        // 然后存入数据库

    }

    /**
     * 读取评论文件 -> 分词 -> 返回分词后的对象集合
     * @param filepath
     * @param category
     * @return
     */
    public static List<Comment_fenci_storeString> PreOperation(String filepath, String category) {
        //读取评论文件
        List<String> comments = TxtFileOperation.readAllLinesWithContent(filepath);
        //分词处理，得到分词后的评论形式
        List<List<Term>> commentsAfterQieFen;
        commentsAfterQieFen = qiefenAndDescTingyongciWithList(comments);
        // 将commentsAfterQieFen 拓展成对象（id，category，comment_fencis）
        List<Comment_fenci_storeString> comment_fencis = tuozhancheng_duixiang(commentsAfterQieFen, category);
        return comment_fencis;
    }

    /**
     * 切分并去除停用词--对一整个集合操作
     * @param comments ：评论集合
     * @return ：返回已经切分并且去除停用词的处理后的评论集合
     */
    public static List<List<Term>> qiefenAndDescTingyongciWithList(List<String> comments){
        List<List<Term>> contentAfterChooses = new ArrayList<>();
        List<Term> termList;

        for (String comment : comments){
            // 将一些富含繁体字的评论转化成简体字版的评论
            HanLP.convertToSimplifiedChinese(comment);
            //分词
            termList = HanLP.segment(comment);

            //去除停用词
            CoreStopWordDictionary.apply(termList);
            System.out.println(termList);

            contentAfterChooses.add(termList);
        }

        return contentAfterChooses;
    }

    /**
     * 进队单个字符串进行分词操作
     * @param comment
     * @return ：返回分词结果
     */
    public static List<Term> qiefenAndDescTingyongci(String comment){
        List<Term> termList = new ArrayList<>();

        //将一些富含繁体字的评论转化成简体字版的评论
        HanLP.convertToSimplifiedChinese(comment);
        termList = HanLP.segment(comment);

        //去除停用词
        CoreStopWordDictionary.apply(termList);
        System.out.println(termList);
        return termList;
    }

    /**
     * 将commentsAfterQieFen 拓展成对象（id，category，comments_fenci(string)）并返回
     * @param commentsAfterQieFen ：切分后的评论集合
     * @param category ：情感倾向种类
     * @return ：返回 Comment_fenci_storeString 集合
     */
    public static List<Comment_fenci_storeString> tuozhancheng_duixiang(List<List<Term>> commentsAfterQieFen,
                                                                        String category){
        List<Comment_fenci_storeString> comment_fenci_storeStrings = new ArrayList<>();
        Comment_fenci_storeString comment_fenci_storeString;
        int count = 0;
        for (int i = 0; i < commentsAfterQieFen.size(); i++){
            List<String> comments_string = new ArrayList<>();
            // 将List<Term>转化为List<String>存储起来
            for (int j = 0; j < commentsAfterQieFen.get(i).size(); j++){
                comments_string.add(commentsAfterQieFen.get(i).get(j).word);
            }
            comment_fenci_storeString = new Comment_fenci_storeString(count, category, comments_string);
            count++;
            comment_fenci_storeStrings.add(comment_fenci_storeString);
        }
        System.out.println(category + " comment_fencis.size():" + comment_fenci_storeStrings.size());
        return comment_fenci_storeStrings;
    }


    // 进行CHI计算---------------------------------------------------------------------------------------

    /**
     * 对 0-喜悦类 计算 CHI
     * @param comment_fenci_0
     * @param comment_fenci_1
     * @param comment_fenci_2
     * @param comment_fenci_3
     * @param isopt : 是否开启词频因素优化（即是否考虑词频因素对 CHI 计算结果的影响）
     * @return ：返回所有特征词以及该词对应的权值
     */
    public static Map<String , Double> jisuanCHIOf0(List<Comment_fenci_storeString> comment_fenci_0,
                                                    List<Comment_fenci_storeString> comment_fenci_1,
                                                    List<Comment_fenci_storeString> comment_fenci_2,
                                                    List<Comment_fenci_storeString> comment_fenci_3,
                                                    boolean isopt) {
        Map<String, Double> tops = new HashMap<>();

        for (int i = 1; i < comment_fenci_0.size(); i++){
            for (int j = 0; j < comment_fenci_0.get(i).getComments_fenci().size(); j++){
                String term = comment_fenci_0.get(i).getComments_fenci().get(j);
                System.out.println(term);

                //计算该词的double_x
                //只有当前词没被加入到Map中，即该词还没有计算CHI，才对该词进行计算，以避免重复计算
                if (!tops.containsValue(term)){
                    int a = 0;  //a表示0类中含该词的评论数
                    int b = 0;  //b表示其余类中含该词的评论数
                    int c = 0;  //c表示0类中不含该词的评论数
                    int d = 0;  //d表示其余类中不含该词的评论数

                    //计算a、b、c、d
                    for (int m0 = 0; m0 < comment_fenci_0.size(); m0++) {
                        if (comment_fenci_0.get(m0).getComments_fenci().contains(term)) {
                            a++;
                        } else {
                            c++;
                        }
                    }
                    for (int m1 = 0; m1 < comment_fenci_1.size(); m1++) {
                        if (comment_fenci_1.get(m1).getComments_fenci().contains(term)) {
                            b++;
                        } else {
                            d++;
                        }
                    }
                    for (int m2 = 0; m2 < comment_fenci_2.size(); m2++) {
                        if (comment_fenci_2.get(m2).getComments_fenci().contains(term)) {
                            b++;
                        } else {
                            d++;
                        }
                    }
                    for (int m3 = 0; m3 < comment_fenci_3.size(); m3++) {
                        if (comment_fenci_3.get(m3).getComments_fenci().contains(term)) {
                            b++;
                        } else {
                            d++;
                        }
                    }

                    //计算
                    double dx;
                    dx = (Math.pow((a * d - b * c), 2)) / ((a + b) * (c + d));
                    //是否要求加入词频因素优化计算结果
                    if (isopt){
                        //为了提升精确度，进一步加入词频影响因素进行计算
                        int arf = a+b;
                        //可能会有整除0异常--------->>> 注意 <<<-----------
                        double berta = ((double)a / ((double)a + (double)b));
                        dx = dx * arf * berta;
                    }
                    System.out.println("-----> a:" + a);
                    System.out.println("-----> b:" + b);
                    System.out.println("-----> c:" + c);
                    System.out.println("-----> d:" + d);
                    System.out.println("-----> dx:" + dx);
                    tops.put(term, dx);
                }
            }
        }
        return tops;
    }

    /**
     * 对 1-愤怒类 计算 CHI
     * @param comment_fencis_0
     * @param comment_fencis_1
     * @param comment_fencis_2
     * @param comment_fencis_3
     * @param isopt ： 是否开启词频优化
     * @return
     */
    public static Map<String, Double> jisuanCHIOf1(List<Comment_fenci_storeString> comment_fencis_0,
                                                   List<Comment_fenci_storeString> comment_fencis_1,
                                                   List<Comment_fenci_storeString> comment_fencis_2,
                                                   List<Comment_fenci_storeString> comment_fencis_3,
                                                   boolean isopt) {

        Map<String, Double> tops = new HashMap<>();

        for (int i = 1; i < comment_fencis_1.size(); i++) {
            for (int j = 0; j < comment_fencis_1.get(i).getComments_fenci().size(); j++) {
                String term = comment_fencis_1.get(i).getComments_fenci().get(j);
                System.out.println(term);

                // 计算该词的double_x
                if (!tops.containsValue(term)) {
                    int a = 0;
                    int b = 0;
                    int c = 0;
                    int d = 0;

                    // 计算abcd：
                    for (int m0 = 0; m0 < comment_fencis_1.size(); m0++) {
                        if (comment_fencis_1.get(m0).getComments_fenci().contains(term)) {
                            a++;
                        } else {
                            c++;
                        }
                    }
                    for (int m1 = 0; m1 < comment_fencis_0.size(); m1++) {
                        if (comment_fencis_0.get(m1).getComments_fenci().contains(term)) {
                            b++;
                        } else {
                            d++;
                        }
                    }
                    for (int m2 = 0; m2 < comment_fencis_2.size(); m2++) {
                        if (comment_fencis_2.get(m2).getComments_fenci().contains(term)) {
                            b++;
                        } else {
                            d++;
                        }
                    }
                    for (int m3 = 0; m3 < comment_fencis_3.size(); m3++) {
                        if (comment_fencis_3.get(m3).getComments_fenci().contains(term)) {
                            b++;
                        } else {
                            d++;
                        }
                    }

                    double dx;
                    dx = (Math.pow((a * d - b * c), 2)) / ((a + b) * (c + d));
                    //是否要求加入词频因素优化计算结果
                    if (isopt){
                        //为了提升精确度，进一步加入词频影响因素进行计算
                        int arf = a+b;
                        //可能会有整除0异常--------->>> 注意 <<<-----------
                        double berta = ((double)a / ((double)a + (double)b));
                        dx = dx * arf * berta;
                    }
                    System.out.println("-----> a:" + a);
                    System.out.println("-----> b:" + b);
                    System.out.println("-----> c:" + c);
                    System.out.println("-----> d:" + d);
                    System.out.println("-----> dx:" + dx);
                    tops.put(term, dx);
                }
            }
        }
        return tops;
    }

    /**
     * 对 2-厌恶类 计算 CHI
     * @param comment_fencis_0
     * @param comment_fencis_1
     * @param comment_fencis_2
     * @param comment_fencis_3
     * @param isopt ：是否开启词频优化
     * @return
     */
    public static Map<String, Double> jisuanCHIOf2(List<Comment_fenci_storeString> comment_fencis_0,
                                                   List<Comment_fenci_storeString> comment_fencis_1,
                                                   List<Comment_fenci_storeString> comment_fencis_2,
                                                   List<Comment_fenci_storeString> comment_fencis_3,
                                                   boolean isopt) {

        Map<String, Double> tops = new HashMap<>();

        for (int i = 1; i < comment_fencis_2.size(); i++) {
            for (int j = 0; j < comment_fencis_2.get(i).getComments_fenci().size(); j++) {
                String term = comment_fencis_2.get(i).getComments_fenci().get(j);
                System.out.println(term);

                // 计算该词的double_x
                if (!tops.containsValue(term)) {
                    int a = 0;
                    int b = 0;
                    int c = 0;
                    int d = 0;

                    // 计算abcd：
                    for (int m0 = 0; m0 < comment_fencis_2.size(); m0++) {
                        if (comment_fencis_2.get(m0).getComments_fenci().contains(term)) {
                            a++;
                        } else {
                            c++;
                        }
                    }
                    for (int m1 = 0; m1 < comment_fencis_1.size(); m1++) {
                        if (comment_fencis_1.get(m1).getComments_fenci().contains(term)) {
                            b++;
                        } else {
                            d++;
                        }
                    }
                    for (int m2 = 0; m2 < comment_fencis_0.size(); m2++) {
                        if (comment_fencis_0.get(m2).getComments_fenci().contains(term)) {
                            b++;
                        } else {
                            d++;
                        }
                    }
                    for (int m3 = 0; m3 < comment_fencis_3.size(); m3++) {
                        if (comment_fencis_3.get(m3).getComments_fenci().contains(term)) {
                            b++;
                        } else {
                            d++;
                        }
                    }

                    //计算
                    double dx;
                    dx = (Math.pow((a * d - b * c), 2)) / ((a + b) * (c + d));
                    //是否要求加入词频因素优化计算结果
                    if (isopt){
                        //为了提升精确度，进一步加入词频影响因素进行计算
                        int arf = a+b;
                        //可能会有整除0异常--------->>> 注意 <<<-----------
                        double berta = ((double)a / ((double)a + (double)b));
                        dx = dx * arf * berta;
                    }
                    System.out.println("-----> a:" + a);
                    System.out.println("-----> b:" + b);
                    System.out.println("-----> c:" + c);
                    System.out.println("-----> d:" + d);
                    System.out.println("-----> dx:" + dx);
                    tops.put(term, dx);
                }
            }
        }

        return tops;
    }

    /**
     * 对 3-低落类 计算 CHI
     * @param comment_fencis_0
     * @param comment_fencis_1
     * @param comment_fencis_2
     * @param comment_fencis_3
     * @param isopt ：是否开启词频优化
     * @return
     */
    public static Map<String, Double> jisuanCHIOf3(List<Comment_fenci_storeString> comment_fencis_0,
                                                   List<Comment_fenci_storeString> comment_fencis_1,
                                                   List<Comment_fenci_storeString> comment_fencis_2,
                                                   List<Comment_fenci_storeString> comment_fencis_3,
                                                   boolean isopt) {

        Map<String, Double> tops = new HashMap<>();

        for (int i = 1; i < comment_fencis_3.size(); i++) {
            for (int j = 0; j < comment_fencis_3.get(i).getComments_fenci().size(); j++) {
                String term = comment_fencis_3.get(i).getComments_fenci().get(j);
                System.out.println(term);

                // 计算该词的double_x
                if (!tops.containsValue(term)) {
                    int a = 0;
                    int b = 0;
                    int c = 0;
                    int d = 0;

                    // 计算abcd：
                    for (int m0 = 0; m0 < comment_fencis_3.size(); m0++) {
                        if (comment_fencis_3.get(m0).getComments_fenci().contains(term)) {
                            a++;
                        } else {
                            c++;
                        }
                    }
                    for (int m1 = 0; m1 < comment_fencis_1.size(); m1++) {
                        if (comment_fencis_1.get(m1).getComments_fenci().contains(term)) {
                            b++;
                        } else {
                            d++;
                        }
                    }
                    for (int m2 = 0; m2 < comment_fencis_2.size(); m2++) {
                        if (comment_fencis_2.get(m2).getComments_fenci().contains(term)) {
                            b++;
                        } else {
                            d++;
                        }
                    }
                    for (int m3 = 0; m3 < comment_fencis_0.size(); m3++) {
                        if (comment_fencis_0.get(m3).getComments_fenci().contains(term)) {
                            b++;
                        } else {
                            d++;
                        }
                    }

                    //计算
                    double dx;
                    dx = (Math.pow((a * d - b * c), 2)) / ((a + b) * (c + d));
                    //是否要求加入词频因素优化计算结果
                    if (isopt){
                        //为了提升精确度，进一步加入词频影响因素进行计算
                        int arf = a+b;
                        //可能会有整除0异常--------->>> 注意 <<<-----------
                        double berta = ((double)a / ((double)a + (double)b));
                        dx = dx * arf * berta;
                    }
                    System.out.println("-----> a:" + a);
                    System.out.println("-----> b:" + b);
                    System.out.println("-----> c:" + c);
                    System.out.println("-----> d:" + d);
                    System.out.println("-----> dx:" + dx);
                    tops.put(term, dx);
                }
            }
        }

        return tops;
    }

    /**
     * 对 Map 排序，借此选择前 N 个词为特征词：topN
     * @param tops
     * @param N
     * @return
     */
    public static List<String> getTopN(Map<String, Double> tops, Integer N) {
        List<Map.Entry<String, Double>> topsList = new ArrayList<>(tops.entrySet());
        Collections.sort(topsList, new Comparator<Map.Entry<String, Double>>() {
            //降序排序，因为开方值越大，越相关
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        List<String> topN = new ArrayList<>();
        int topNd = 0;
        for (Map.Entry<String, Double> mapping : topsList) {
            System.out.println(mapping.getKey() + ":" + mapping.getValue());
            //获取降序排序的前100个高频词
            topN.add(mapping.getKey());
            topNd++;
            if (topNd == N) {
                break;
            }
        }
        return topN;
    }


}
