/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deim.urv.cat.homework2.service;

import deim.urv.cat.homework2.model.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JUDITH
 */
public class ArticleServiceImpl implements ArticleService{
    private static final Logger LOGGER = Logger.getLogger(ArticleServiceImpl.class.getName());
    private final WebTarget webTarget;
    private final Client client;
    private static final String baseURI = "http://localhost:8080/Homework1/webresources/api/v1/";

    public ArticleServiceImpl() {
        client = ClientBuilder.newClient();
        webTarget = client.target(baseURI).path("article"); // Sin path adicional
    }
    
    /**
     * Obtiene todos los artículos disponibles o filtrados por autor y/o tópicos.
     *
     * @param author Nombre del autor (opcional).
     * @param topics Lista de tópicos (opcional, máximo 2).
     * @return Lista de artículos o null en caso de error.
     */
    @Override
    public List<ArticleSimpleDTO> findArticles (List<String> topics, String author) {
        WebTarget queryTarget = webTarget;

        if (topics != null && !topics.isEmpty()) {
            if (topics.size() > 2) {
                LOGGER.log(Level.WARNING, "Máximo de dos tópicos permitidos.");
                return null;
            }
            for (String topic : topics) {
                queryTarget = queryTarget.queryParam("topic", topic);
            }
        }

        if (author != null && !author.isEmpty()) {
            queryTarget = queryTarget.queryParam("author", author);
        }

        try (Response response = queryTarget.request(MediaType.APPLICATION_JSON).get()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(new GenericType<List<ArticleSimpleDTO>>() {});
            } else {
                LOGGER.log(Level.SEVERE, "Error al obtener artículos. Código de estado: {0}", response.getStatus());
                return null;
            }
        }
    }

    /**
     * Obtiene un artículo por su ID.
     * 
     * @param id ID del artículo.
     * @param headers Cabeceras HTTP.
     * @return El artículo encontrado o null si no existe.
     */
    @Override
    public ArticleDetailedDTO findArticleById(Long id, HttpHeaders headers){
        try (Response response = webTarget.path("/"+String.valueOf(id))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, headers)
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
     * Libera recursos del cliente.
     */
    public void close() {
        client.close();
    }
}