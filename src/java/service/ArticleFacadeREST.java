/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import authn.Credentials;
import authn.Secured;
import dto.ArticleSimpleDTO;
import dto.ArticleDetailedDTO;
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
import model.entities.Link;

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
            query += " AND a.author.credentials.username = :author";    // Filtra per autor.
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
        
        // Transformar resultados para incluir solo los campos requeridos.
        List<ArticleSimpleDTO> articleDTOs = articles.stream().map(article -> {
            ArticleSimpleDTO dto = new ArticleSimpleDTO();
            dto.setTitle(article.getTitle());
            dto.setAuthor(article.getAuthor().getCredentials().getUsername());
            dto.setSummary(article.getSummary());
            dto.setPublishedDate(article.getPublishedDate());
            dto.setViews(article.getViews());
            dto.setImageUrl(article.getImageUrl());
            return dto;
        }).toList();
        
        return Response.ok(articleDTOs).build();   // 200 OK
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
        
        String currentUser = getCurrentUsername(headers);   // Obtenir l'username de l'usuari actual.
        
        // Si l'article es privat...
        if (Boolean.FALSE.equals(article.getIsPublic())) {
            // Si l'usuari actual no esta registrat...
            if (!article.getAuthor().getCredentials().getUsername().equals(currentUser)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("This article is private").build(); // 403 FORBIDDEN
        }
        } else {
            if (currentUser == null || !isUserRegistered(currentUser)) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Need to register").build();   // 401 UNAUTHORIZED
            }
            article.setViews(article.getViews() + 1);   // Incrementar visites.
            super.edit(article);    // Actualitzar l'entitat.
        }
        
        
        ArticleDetailedDTO dto = new ArticleDetailedDTO();
        dto.setTitle(article.getTitle());
        dto.setAuthor(article.getAuthor().getCredentials().getUsername());
        dto.setPublishedDate(article.getPublishedDate());
        dto.setViews(article.getViews());
        dto.setTopics(article.getTopics().stream().map(Topic::getName).toList());
        dto.setContent(article.getContent());
        dto.setImageUrl(article.getImageUrl());
        
        return Response.ok(dto).build();    // Retorna l'article resultant i 200 OK
    }
    
    @POST
    @Secured
    public Response createArticle(Article article, @Context HttpHeaders headers) {
        String currentUser = getCurrentUsername(headers);
        
        // Valida que l'usuari actual esta registrat i obte les seves dades.
        Customer author = getCurrentCustomer(currentUser);
        
        // Comprova si existeixen els temes a la BD
        for (Topic topic : article.getTopics()) {
            // Si no es troba el tema...
            if (em.find(Topic.class, topic.getId()) == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid topic: " + topic.getName()).build(); // 400 BAD REQUEST
            }
        }
        
        // Si no era autor...
        if (!author.getIsAuthor()) {
            author.setIsAuthor(true);  //Pasa a ser autor.
        }
        
        author.getArticles().add(article);  //S'afegeix a la llista d'articles.
        article.setAuthor(author);  //Establir autor de l'article.   
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
        
        String currentUser = getCurrentUsername(headers);   // Obtenir l'username de l'usuari actual.
        // Si no es l'autor de l'article...
        if (!article.getAuthor().getCredentials().getUsername().equals(currentUser)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can only delete your own articles").build();   // 403 FORBIDDEN
        }
        Customer author = article.getAuthor();
        // Si l'article esta en la llista...
        if (author.getArticles() != null && author.getArticles().contains(article)) {
            author.getArticles().remove(article); // Quitar el artículo de la lista.
            super.edit(author); // Actualizar la relación en la base de datos.
        }
        
        super.remove(article);  // Elimina l'entitat.
        
        // Actualitza l'enllaç al ultim article de l'autor.
        // Si l'usuari te articles...
        if (author.getArticles() != null && !author.getArticles().isEmpty()) {
            // Busca l'ultim article restant de l'autor.
            Article lastArticle = author.getArticles()
                    .stream()
                    .max((a1, a2) -> a1.getPublishedDate().compareTo(a2.getPublishedDate()))
                    .orElse(null);

            // Si hi ha mes articles...
            if (lastArticle != null) {
                String linkRef = "/api/v1/article/" + lastArticle.getId();  // Nou enllaç HATEOAS.
                Link link = author.getLink() == null ? new Link() : author.getLink();   // Obté o crea l'enllaç.
                link.setLink(linkRef);  // Actualitza l'enllaç.
                link.setCustomer(author);  // Actualitza l'usuari propietari.

                // Si és un nou enllaç...
                if (link.getId() == null) {
                    em.persist(link);   // Guarda el nou enllaç.
                } else {
                    em.merge(link); // Actualitza l'enllaç existent.
                }
                author.setLink(link);  // Actualitza l'enllaç asociat a l'usuari.
            } else {
                // Si no hi ha més articles, elimina l'enllaç.
                if (author.getLink() != null) {
                    em.remove(author.getLink());  // Elimina el link de la BD.
                    author.setLink(null); // Actualitza l'enllaç asociat a l'usuari.
                }
            }
        }
        
        return Response.ok().entity("Article deleted with ID: " + id).build();    //200 OK
    }
    
    private String getCurrentUsername(HttpHeaders headers) {
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
    
    private Customer getCurrentCustomer(String username) {
        try {
            TypedQuery<Customer> query = em.createNamedQuery("Customer.findByUsername", Customer.class);
            query.setParameter("username", username);
            return query.getSingleResult(); // Retorna l'usuari actual
        } catch (NoResultException e) {
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity("User not found in the database").build()
            );
        }
    }

}
