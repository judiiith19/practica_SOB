/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deim.urv.cat.homework2.controller;

import deim.urv.cat.homework2.model.ArticleSimpleDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JUDITH
 */
public class ArticleSimpleForm implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<ArticleSimpleDTO> articles = new ArrayList<>();
    
    public ArticleSimpleForm() {}
    
    public ArticleSimpleForm(List<ArticleSimpleDTO> articles) {
        this.articles = articles;
    }

    public List<ArticleSimpleDTO> getArticles() { return articles; }
    public void setArticles(List<ArticleSimpleDTO> articles) { this.articles = articles; }
    
    public void addArticle(ArticleSimpleDTO article){ articles.add(article); }
}
