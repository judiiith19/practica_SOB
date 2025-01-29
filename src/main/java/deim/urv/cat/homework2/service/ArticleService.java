package deim.urv.cat.homework2.service;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import deim.urv.cat.homework2.model.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JUDITH
 */
public class ArticleService {
    private static final Logger LOGGER = Logger.getLogger(ArticleService.class.getName());
    private final WebTarget webTarget;
    private final Client client;
    private static final String baseURI = "http://localhost:8080/Homework1/webresources/api/v1/";

    public ArticleService() {
        client = ClientBuilder.newClient();
        webTarget = client.target(baseURI); // Sin path adicional
    }
    
    /**
     * Obtiene todos los artículos disponibles.
     * 
     * @return Lista de artículos o null en caso de error.
     */
    public List<ArticleSimpleDTO> findArticles () {
        try (Response response = webTarget.request(MediaType.APPLICATION_JSON).get()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(new GenericType<List<ArticleSimpleDTO>>() {});
            } else {
                LOGGER.log(Level.SEVERE, "Error al obtener los artículos. Código de estado: {0}", response.getStatus());
                return null;
            }
        }
    }
    
    /**
     * Obtiene un artículo por su ID.
     * 
     * @param id ID del artículo.
     * @return El artículo encontrado o null si no existe.
     */
    public ArticleDetailedDTO findArticleById(long id){
        /*List<Article> articles = findArticles();
        Article article = articles.stream()
                .filter((Article a) -> a.getId() == id)
                .findFirst()
                .orElse(null);
        return article;*/
        
        try (Response response = webTarget.path(String.valueOf(id))
                                          .request(MediaType.APPLICATION_JSON)
                                          .get()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(ArticleDetailedDTO.class);
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                LOGGER.log(Level.WARNING, "Artículo con ID {0} no encontrado.", id);
            } else {
                LOGGER.log(Level.SEVERE, "Error al obtener el artículo. Código de estado: {0}", response.getStatus());
            }
            return null;
        }
    }
    
    /**
     * Obtiene artículos filtrados por autor.
     * 
     * @param author Nombre del autor.
     * @return Lista de artículos filtrados o null en caso de error.
     */
    public List<ArticleSimpleDTO> findArticlesByAuthor(String author){
        /*List<Article> articles = findArticles();
        List<Article> filteredArticles = articles.stream()
                .filter((Article a) -> a.getAuthor().getCredentials().getUsername().equals(author))
                .toList();
        return filteredArticles;*/
        
        try (Response response = webTarget.queryParam("author", author)
                                          .request(MediaType.APPLICATION_JSON)
                                          .get()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(new GenericType<List<ArticleSimpleDTO>>() {});
            } else {
                LOGGER.log(Level.SEVERE, "Error al obtener artículos por autor. Código de estado: {0}", response.getStatus());
                return null;
            }
        }
    }
    
    /**
     * Obtiene artículos filtrados por tópico.
     *
     * @param topic Nombre del tópico.
     * @return Lista de artículos filtrados o null en caso de error.
     */
    public List<ArticleSimpleDTO> findArticlesByTopic(String topic) {
        try (Response response = webTarget.queryParam("topic", topic)
                                          .request(MediaType.APPLICATION_JSON)
                                          .get()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(new GenericType<List<ArticleSimpleDTO>>() {});
            } else {
                LOGGER.log(Level.SEVERE, "Error al obtener artículos por tópico. Código de estado: {0}", response.getStatus());
                return null;
            }
        }
    }
    
    /**
     * Libera recursos del cliente.
     */
    public void close() {
        client.close();
    }
}