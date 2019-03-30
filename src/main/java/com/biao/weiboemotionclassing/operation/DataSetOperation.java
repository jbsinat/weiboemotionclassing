package com.biao.weiboemotionclassing.operation;

import com.biao.weiboemotionclassing.entities.Comment;
import com.biao.weiboemotionclassing.tools.TxtFileOperation;

import java.util.List;

/**
 * 一、情感分类第一步：操作并整理数据集
 * 对数据集进行操作的类
 * 注：
     * 0：喜悦
     * 1：愤怒
     * 2：厌恶
     * 3：低落
     * 对应不同类别的感情
 */
public class DataSetOperation {

    public static void main(String[] args) {
        DataSetOperation dataSetOperation = new DataSetOperation();

        //读取原始数据集，每一类取出1000条存入新建的文件中
        String initialDataFilename = "data_group/initial_data_set/0_simplifyweibo_huanyuanhou.txt";
        String simpleDataFilename = "data_group/simple_data_set/0_1000_comments.txt";
        dataSetOperation.createSimpleDataSet(initialDataFilename, simpleDataFilename, 1000);

        initialDataFilename = "data_group/initial_data_set/1_simplifyweibo_huanyuanhou.txt";
        simpleDataFilename = "data_group/simple_data_set/1_1000_comments.txt";
        dataSetOperation.createSimpleDataSet(initialDataFilename, simpleDataFilename, 1000);

        initialDataFilename = "data_group/initial_data_set/2_simplifyweibo_huanyuanhou.txt";
        simpleDataFilename = "data_group/simple_data_set/2_1000_comments.txt";
        dataSetOperation.createSimpleDataSet(initialDataFilename, simpleDataFilename, 1000);

        initialDataFilename = "data_group/initial_data_set/3_simplifyweibo_huanyuanhou.txt";
        simpleDataFilename = "data_group/simple_data_set/3_1000_comments.txt";
        dataSetOperation.createSimpleDataSet(initialDataFilename, simpleDataFilename, 1000);

    }

    /**
     * 从原始数据集中提取出 部分（1000-5000条）数据
     * @param initialDataFilename ：原始文件路径
     * @param simpleDataFilename ：抽取出的文件路径
     * @param lines ：要抽取的行数
     */
    public void createSimpleDataSet(String initialDataFilename, String simpleDataFilename, Integer lines){

        List<Comment> comments = TxtFileOperation.readFileByLines(initialDataFilename, lines);
        TxtFileOperation.saveAsFileByLines(comments, simpleDataFilename);

    }

}
