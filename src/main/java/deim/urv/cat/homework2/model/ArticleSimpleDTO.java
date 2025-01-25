/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deim.urv.cat.homework2.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author JUDITH
 */
public class ArticleSimpleDTO implements Serializable {
    private String title;
    private String author;
    private String summary;
    private Date publishedDate;
    private Integer views;
    private String imageUrl;
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Date getPublishedDate() { return publishedDate; }
    public void setPublishedDate(Date publishedDate) { this.publishedDate = publishedDate; }

    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
