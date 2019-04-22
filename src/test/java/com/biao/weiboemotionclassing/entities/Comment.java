package com.biao.weiboemotionclassing.entities;

//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;

//@Entity
public class Comment {

//    @Id
//    @GeneratedValue     //主键自增
    private int id;

    private String category;

    private String content;

    public Comment() {
    }

    public Comment(Integer id, String category, String content) {
        this.id = id;
        this.category = category;
        this.content = content;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
