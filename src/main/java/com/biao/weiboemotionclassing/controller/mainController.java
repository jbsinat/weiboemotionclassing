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
//    @CrossOrigin(origins = "http://localhost:8080")
    public Msg fenci(/*@RequestParam("comment") String comment*/) {
        String comment = "让我们荡起双桨！";
        //分词
        List<Term> comment_fenci = FenciWithHanLpOperation.qiefenAndDescTingyongci(comment);
        //权重
        Map<String, Double> quanzhong0 = new HashMap<>();
        Map<String, Double> quanzhong1 = new HashMap<>();
        //全部特征词文件存储路径
        String allWordsPath0 = "data_group/feature_word_set_all/0_happy_all.txt";
        String allWordsPath1 = "data_group/feature_word_set_all/1_angry_all.txt";
        Map<String, Double> featurelist0_all = TxtFileOperation.readFeatureSetFile(allWordsPath0);
        Map<String, Double> featurelist1_all = TxtFileOperation.readFeatureSetFile(allWordsPath1);
        //0类下的各词权值
        for (int i = 0; i < comment_fenci.size(); i++) {
            quanzhong0.put(comment_fenci.get(i).word, featurelist0_all.get(comment_fenci.get(i).word));
        }
        //1类下的各词权值
        for (int i = 0; i < comment_fenci.size(); i++) {
            quanzhong1.put(comment_fenci.get(i).word, featurelist1_all.get(comment_fenci.get(i).word));
        }
        //分类
        int class_ = JudgeClass.init_with_tezhengci(comment);
        String cl = (class_ == 0)?"0-正面类":"1-负面类";
        return Msg.success().add("comment_fenci", comment_fenci).add("quanzhong_0", quanzhong0).add("quanzhong_1", quanzhong1).add("class", cl);
    }

}
