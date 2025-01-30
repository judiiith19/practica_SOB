/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package deim.urv.cat.homework2.controller;

import deim.urv.cat.homework2.model.ArticleSimpleDTO;
import deim.urv.cat.homework2.model.Customer;
import deim.urv.cat.homework2.model.Topic;
import deim.urv.cat.homework2.service.ArticleService;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author JUDITH
 */
@Controller
@Path("MainPage")
public class MainPageController {
    @Inject
    private ArticleService articleService;
    
    @Inject
    private ArticleSimpleForm articleForm;
    
    @Inject
    private HttpSession session;
    
    @GET
    public String showSimpleArticles(@Context HttpServletRequest req,
            @QueryParam("topic") List<String> topics, @QueryParam("author") String author) {
        Customer customer = (Customer) this.session.getAttribute("user");
        String returnUrl = (customer != null) ? "main-page.jsp" : "main-page.jsp";
        String username = (customer != null) ? customer.getCredentials().getUsername() : "";
        String password = (customer != null) ? customer.getCredentials().getPassword() : "";
        
        List<ArticleSimpleDTO> articles = articleService.findArticles(topics, author);
        
        if (articles == null) {
            req.setAttribute("error", "No se han podido obtener los articulos.");
            return "errorPage.jsp";
        }
        
        List<String> topicsInDB = articles.stream()
                .flatMap((ArticleSimpleDTO a) -> a.getTopics().stream().map((Topic t) -> t.getName()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        List<String> authorsInDB = articles.stream()
                .map((ArticleSimpleDTO a) -> a.getAuthor())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        articleForm.setArticles(articles);
        // Pasar las listas únicas de temas y autores al contexto de la aplicación
        ServletContext servletContext = req.getServletContext();
        servletContext.setAttribute("articleSimpleForm", this.articleForm);
        servletContext.setAttribute("topicsInDB", topicsInDB);
        servletContext.setAttribute("authorsInDB", authorsInDB);
        
        return returnUrl;
    }
    
}
