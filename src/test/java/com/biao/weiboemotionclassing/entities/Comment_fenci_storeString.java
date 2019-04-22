package com.biao.weiboemotionclassing.entities;

import java.util.List;

/**
 * 分词之后存储分词形式的类对象模型
 * 例：我是个帅哥！
 *    id = 1;
 *    category = 0;     //正面
 *    List<String> comments_fenci = [我是, 帅哥];
 */
public class Comment_fenci_storeString {

    private int id;
    private String category;
    private List<String> comments_fenci;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public List<String> getComments_fenci() {
        return comments_fenci;
    }
    public void setComments_fenci(List<String> comments_fenci) {
        this.comments_fenci = comments_fenci;
    }

    public Comment_fenci_storeString() {
    }

    public Comment_fenci_storeString(int id, String category, List<String> comments_fenci) {
        super();
        this.id = id;
        this.category = category;
        this.comments_fenci = comments_fenci;
    }

}
