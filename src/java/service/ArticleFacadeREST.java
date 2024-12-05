/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import authn.Secured;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import java.util.Base64;
import model.entities.Article;
import model.entities.Topic;
import model.entities.Customer;

import java.util.List;

/**
 *
 * @author JUDITH
 */
@Stateless
@Path("api/v1/article")
@Produces({"application/json", "application/xml"})
@Consumes({"application/json", "application/xml"})
public class ArticleFacadeREST extends AbstractFacade{
    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    public ArticleFacadeREST() {
        super(Article.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    @GET
    public Response findArticles(
            @QueryParam("topic") List<String> topics,
            @QueryParam("author") String author) {
        if (topics != null && topics.size() > 2) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Maximum of two topics allowed").build();
        }
        String query = "SELECT a FROM Article a WHERE 1=1";
        if (topics != null && !topics.isEmpty()) {
            query += " AND a.topic.name IN :topics";
        }
        if (author != null && !author.isEmpty()) {
            query += " AND a.author.username = :author";
        }
        query += " ORDER BY a.views DESC";

        TypedQuery<Article> q = em.createQuery(query, Article.class);
        if (topics != null && !topics.isEmpty()) {
            q.setParameter("topics", topics);
        }
        if (author != null && !author.isEmpty()) {
            q.setParameter("author", author);
        }
        
        List<Article> articles = q.getResultList();
        return Response.ok(articles).build();
    }
    
    @GET
    @Path("{id}")
    public Response findArticleById(@PathParam("id") Long id) {
        Article article = (Article) super.find(id);
        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        article.setViews(article.getViews() + 1);
        super.edit(article);
        return Response.ok(article).build();
    }
    
    @POST
    @Secured
    public Response createArticle(Article article) {
        for (Topic topic : article.getTopics()) {
            if (em.find(Topic.class, topic.getId()) == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid topic: " + topic.getName()).build();
            }
        }
        if (em.find(Customer.class, article.getAuthor().getId()) == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid author").build();
        }
        article.setPublishedDate(new java.util.Date());
        super.create(article);
        return Response.status(Response.Status.CREATED)
                .entity("Article created with ID: " + article.getId()).build();
    }
    
    @DELETE
    @Path("{id}")
    @Secured
    public Response deleteArticle(@PathParam("id") Long id, @Context HttpHeaders headers) {
        Article article = (Article) super.find(id);
        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        super.remove(article);
        return Response.ok().build();
    }
}
