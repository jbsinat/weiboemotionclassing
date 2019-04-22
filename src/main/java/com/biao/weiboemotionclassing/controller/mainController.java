package com.biao.weiboemotionclassing.controller;

import com.biao.weiboemotionclassing.operation.FenciWithHanLpOperation;
import com.biao.weiboemotionclassing.operation.JudgeClass;
import com.biao.weiboemotionclassing.tools.Msg;
import com.biao.weiboemotionclassing.tools.TxtFileOperation;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 唯一的接口类，接受前端传回的评论，并对其进行分词等处理，然后计算判断属于哪一分类
 */
@RestController
@RequestMapping(value = "/emotionClassing")
public class mainController {

    /**
     * 返回分词结果
     * 示例：
         * "word": "今天",
         * "nature": "t",       //词性，t：时间词
         * "offset": 0,         //单词在文本中的偏移量
         * "frequency": 867     //
     * @param comment
     * @return
     */
    @GetMapping(value = "/fenci")
    public Msg fenci(@RequestParam("comment") String comment) {
        //分词
        List<Term> comment_fenci = FenciWithHanLpOperation.qiefenAndDescTingyongci(comment);
        //权重
        Map<String, Integer> quanzhong0 = new HashMap<>();
        Map<String, Integer> quanzhong1 = new HashMap<>();
        //全部特征词文件存储路径
        String allWordsPath0 = "data_group/feature_word_set/0_pos_all_with_boolQZ.txt";
        String allWordsPath1 = "data_group/feature_word_set/1_neg_all_with_boolQZ.txt";
        Map<String, Integer> featurelist0_all = TxtFileOperation.readFeatureSetFile_Str_Int(allWordsPath0);
        Map<String, Integer> featurelist1_all = TxtFileOperation.readFeatureSetFile_Str_Int(allWordsPath1);
        //0类下的各词权值
        for (int i = 0; i < comment_fenci.size(); i++) {
            quanzhong0.put(comment_fenci.get(i).word, featurelist0_all.get(comment_fenci.get(i).word));
        }
        //1类下的各词权值
        for (int i = 0; i < comment_fenci.size(); i++) {
            quanzhong1.put(comment_fenci.get(i).word, featurelist1_all.get(comment_fenci.get(i).word));
        }
        //分类
        //1.利用 特征词及对应的权重 并利用贝叶斯算法计算（此种方式处理速度更快，因为利用了前面特征提取与权值计算过程的处理结果）
        int class_ = JudgeClass.init_with_tezhengci_final(comment);
        //2.利用 评论数据直接统计 并利用贝叶斯算法计算
//        int class_ = JudgeClass.init_with_comments(comment);
        String cl = (class_ == 0)?"0":"1";
        return Msg.success().add("comment_seg", comment_fenci).add("featureWeight_0", quanzhong0).add("featureWeight_1", quanzhong1).add("class", cl);
    }

}
