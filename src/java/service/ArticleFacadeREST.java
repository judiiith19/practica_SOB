/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import authn.Credentials;
import authn.Secured;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
        
        // Comprova que no s'inclouen més de 2 temes per consulta.
        if (topics != null && topics.size() > 2) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Maximum of two topics allowed").build();
        }
        //Constuccio consulta amb filtres opcionals per temes i autor.
        String query = "SELECT DISTINCT a FROM Article a LEFT JOIN a.topics t WHERE 1=1";
        if (topics != null && !topics.isEmpty()) {
            query += " AND t.name IN :topics";  // Filtra per temes.
        }
        if (author != null && !author.isEmpty()) {
            query += " AND a.author.username = :author";    // Filtra per autor.
        }
        query += " ORDER BY a.views DESC";  // Ordena per popularitat.

        TypedQuery<Article> q = em.createQuery(query, Article.class);
        
        // Establir parametres per temes i autor.
        if (topics != null && !topics.isEmpty()) {
            q.setParameter("topics", topics);
        }
        if (author != null && !author.isEmpty()) {
            q.setParameter("author", author);
        }
        
        // Retorna la llista resultant
        List<Article> articles = q.getResultList();
        return Response.ok(articles).build();   // 200 OK
    }
    
    @GET
    @Path("{id}")
    @Secured
    public Response findArticleById(@PathParam("id") Long id, @Context HttpHeaders headers) {
        // Busca l'article per ID.
        Article article = (Article) super.find(id);
        // Si no existeix...
        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Article not found").build(); // 404 NOT FOUND
        }
        // Si l'article es privat...
        if (Boolean.FALSE.equals(article.getIsPublic())) {
            String currentUser = getCurrentUser(headers);   // Obtenir l'username de l'usuari actual.
            // Si l'usuari actual no esta registrat...
            if (currentUser == null || !isUserRegistered(currentUser)) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Need to register").build();   // 401 UNAUTHORIZED
            }
        }
        article.setViews(article.getViews() + 1);   // Incrementar visites.
        super.edit(article);    // Actualitzar l'entitat.
        return Response.ok(article).build();    // Retorna l'article resultant i 200 OK
    }
    
    @POST
    @Secured
    public Response createArticle(Article article) {
        // Comprova si existeixen els temes a la BD
        for (Topic topic : article.getTopics()) {
            // Si no es troba el tema...
            if (em.find(Topic.class, topic.getId()) == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid topic: " + topic.getName()).build(); // 400 BAD REQUEST
            }
        }
        // Si no es troba l'autor...
        if (em.find(Customer.class, article.getAuthor().getId()) == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid author").build();  // 400 BAD REQUEST
        }
        // Si no era autor...
        if (!article.getAuthor().getIsAuthor()) {
            article.getAuthor().setIsAuthor(Boolean.TRUE);  //Pasa a ser autor.
        }
        article.getAuthor().getArticles().add(article);     //S'afegeix a la llista d'articles.
        article.setPublishedDate(new java.util.Date()); // Establir data de publicacio
        super.create(article);  // Guardar l'entitat
        return Response.status(Response.Status.CREATED)
                .entity("Article created with ID: " + article.getId()).build(); // 201 CREATED
    }
    
    @DELETE
    @Path("{id}")
    @Secured
    public Response deleteArticle(@PathParam("id") Long id, @Context HttpHeaders headers) {
        // Busca l'article per ID.
        Article article = (Article) super.find(id);
        // Si no existeix...
        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).build();  // 404 NOT FOUND
        }
        
        String currentUser = getCurrentUser(headers);   // Obtenir l'username de l'usuari actual.
        // Si no es l'autor de l'article...
        if (!article.getAuthor().getUsername().equals(currentUser)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can only delete your own articles").build();   // 403 FORBIDDEN
        }
        
        super.remove(article);  // Elimina l'entitat.
        return Response.ok().build();
    }
    
    private String getCurrentUser(HttpHeaders headers) {
        // Obtener el valor de la cabecera "Authorization" (usuario:contraseña)
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        // Si l'autentificacio es amb HTTP Basic...
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            // Decodifica la capçalera d'autorizacio
            String encodedCredentials = authorizationHeader.substring("Basic ".length());   //Agafem el contingut despres de "Basic"
            String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials)); //Decodifiquem Base64

            // Les dades estan en format "usuari:contrasenya"
            String[] credentials = decodedCredentials.split(":");
            if (credentials.length > 0) {
                return credentials[0]; // Retorna l'username.
            }
        }
        return null; // Retorna null si no ha pogut obtindre les credencials.
    }
    
    private boolean isUserRegistered(String username) {
        try {
            // Busca les credencials segons l'username.
            TypedQuery<Credentials> query = em.createNamedQuery("Credentials.findUser", Credentials.class);
            query.setParameter("username", username);   //Establir parametre per l'username.
            Credentials credentials = query.getSingleResult();   // Resultat
            return credentials != null; // Si es troba l'username, está registrat.
        } catch (NoResultException e) {
            return false; // Usuari no trobat.
        }
    }

}
