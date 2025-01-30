/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deim.urv.cat.homework2.controller;

import deim.urv.cat.homework2.model.ArticleDetailedDTO;
import deim.urv.cat.homework2.model.Customer;
import deim.urv.cat.homework2.service.ArticleService;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;


/**
 *
 * @author JUDITH
 */
@Controller
@Path("ArticlePage")
public class DetailedArticlePageController {
    @Inject
    private Models models;
    
    @Inject
    private ArticleService articleService;
    
    @Inject
    private ArticleDetailedForm articleForm;
    
    @Inject
    private HttpSession session;
    
    @GET
    public String showDetailedArticle(@QueryParam("id") Long id){
        Customer customer = (Customer) this.session.getAttribute("user");
        String username = (customer != null) ? customer.getCredentials().getUsername() : "";
        String password = (customer != null) ? customer.getCredentials().getPassword() : "";
       
        ArticleDetailedDTO article = articleService.findArticleById(id, username, password);
        
        if (article == null) {
            models.put("error", "No se han podido obtener el articulo.");
            return "errorPage.jsp";
        }
        
        articleForm.addArticle(article);
        models.put("oneArticleForm", articleForm);
        
        return "article-page.jsp";
    }
}
