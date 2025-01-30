/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deim.urv.cat.homework2.controller;

import deim.urv.cat.homework2.model.ArticleDetailedDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JUDITH
 */
public class ArticleDetailedForm implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<ArticleDetailedDTO> articles = new ArrayList<>();
    
    public ArticleDetailedForm() {}
    
    public ArticleDetailedForm(List<ArticleDetailedDTO> articles) {
        this.articles = articles;
    }

    public List<ArticleDetailedDTO> getArticles() { return articles; }
    public void setArticles(List<ArticleDetailedDTO> articles) { this.articles = articles; }
    
    public void addArticle(ArticleDetailedDTO article){
        articles.add(article);
    }
}
