/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import authn.Credentials;
import authn.Secured;
import dto.CustomerDTO;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Base64;
import model.entities.Article;
import model.entities.Customer;

import java.util.List;
import model.entities.Link;

/**
 *
 * @author JUDITH
 */
@Stateless
@Path("api/v1/customer")
@Produces({"application/json", "application/xml"})
@Consumes({"application/json", "application/xml"})
public class CustomerFacadeREST extends AbstractFacade{
    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    public CustomerFacadeREST() {
        super(Customer.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    @GET
    public Response findAllCustomers() {
        // Busca tots els usuaris.
        List<Customer> customers = super.findAll();
        List<CustomerDTO> customerDTOs = new ArrayList<>(); // Usuaris DTO.
        // Per cada usuari s'afegeix l'ellaç HATEOAS.
        customers.forEach(customer -> {
            // Afegir dades al DTO.
            CustomerDTO dto = new CustomerDTO();
            dto.setUsername(customer.getUsername());
            dto.setIsAuthor(customer.getIsAuthor());
            
            // Si l'usuari te articles...
            if (customer.getIsAuthor() && customer.getArticles() != null && !customer.getArticles().isEmpty()) {
                // Busca l'ultim article de l'autor.
                Article lastArticle = customer.getArticles()
                        .stream()
                        .max((a1, a2) -> a1.getPublishedDate().compareTo(a2.getPublishedDate()))
                        .orElse(null);
                // Si existeix...
                if (lastArticle != null) {
                    String linkRef = "/api/v1/article/" + lastArticle.getId();  // Enllaç HATEOAS.
                    //Si no te enllaç o l'enllaç es diferent al nou...
                    if (customer.getLink() == null || !customer.getLink().getLink().equals(linkRef)) {
                        Link link = customer.getLink() == null ? new Link() : customer.getLink();   // Crea o obte l'enllaç.
                        link.setLink(linkRef);  // Actualitza l'enllaç.
                        link.setCustomer(customer); // Actualitza l'usuari propietari.
                        //Si s'ha creat un nou Link...
                        if (link.getId() == null) {
                            em.persist(link);   // Guarda l'entitat.
                        } else {
                            em.merge(link); // Actualitza l'entitat.
                        }
                        customer.setLink(link); // Actualitza l'enllaç asociat al usuari.
                    }              
                    dto.setLastArticleLink(linkRef);    // Afegir + dades al DTO
                }
            }
            customerDTOs.add(dto);  // Afegir dto a la llista de resultats.
        });
        return Response.ok(customerDTOs).build(); // Retorna la llista resultant i 200 OK
    }
    
    @GET
    @Path("/{id}")
    public Response getCustomer(@PathParam("id") Long id) {
        // Busca l'usuari per ID.
        Customer customer = (Customer) super.find(id);
        // Si no existeix...
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404 NOT FOUND 
        }
        // Afegir dades al DTO.
        CustomerDTO dto = new CustomerDTO();
        dto.setUsername(customer.getUsername());
        dto.setIsAuthor(customer.getIsAuthor());
        
        // Si l'usuari te articles...
        if (customer.getIsAuthor() && customer.getArticles() != null && !customer.getArticles().isEmpty()) {
                // Busca l'ultim article de l'autor.
                Article lastArticle = customer.getArticles()
                        .stream()
                        .max((a1, a2) -> a1.getPublishedDate().compareTo(a2.getPublishedDate()))
                        .orElse(null);
                // Si existeix...
                if (lastArticle != null) {
                    String linkRef = "/api/v1/article/" + lastArticle.getId();  // Enllaç HATEOAS.
                    //Si no te enllaç o l'enllaç es diferent al nou...
                    if (customer.getLink() == null || !customer.getLink().getLink().equals(linkRef)) {
                        Link link = customer.getLink() == null ? new Link() : customer.getLink();   // Crea o obte l'enllaç.
                        link.setLink(linkRef);  // Actualitza l'enllaç.
                        link.setCustomer(customer); // Actualitza l'usuari propietari.
                        //Si s'ha creat un nou Link...
                        if (link.getId() == null) {
                            em.persist(link);   // Guarda l'entitat.
                        } else {
                            em.merge(link); // Actualitza l'entitat.
                        }
                        customer.setLink(link); // Actualitza l'enllaç asociat al usuari.
                    }              
                    dto.setLastArticleLink(linkRef);    // Afegir + dades al DTO
                }
            }
        return Response.ok(dto).build(); // Retorna l'usuari resultant i 200 OK
    }
    
    @PUT
    @Path("{id}")
    @Secured
    public Response updateUser(@PathParam("id") Long id, Customer customer, @Context HttpHeaders headers) {
        // Busca l'usuari per ID.
        Customer existingCustomer = (Customer) super.find(id);
        // Si no existeix...
        if (existingCustomer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();  // 404 NOT FOUND
        }
        
        String currentUser = getCurrentUsername(headers);   // Obtenir l'username de l'usuari actual.
        // Si no es l'usuari a modificar no es l'actual...
        if (!existingCustomer.getUsername().equals(currentUser)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can only modify your own profile").build();    //403 FORBIDDEN
        }
        // Si l'username ja esta en us...
        if (customer.getUsername() == null || isUserRegistered(customer.getUsername())) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Username already in use").build();     // 400 BAD_REQUEST
        }
        
        // Actualitzar les dades.
        existingCustomer.setUsername(customer.getUsername());
        existingCustomer.setPassword(customer.getPassword());
        super.edit(existingCustomer);   //Actualitzar l'entitat.
        
        // Afegir dades al DTO.
        CustomerDTO dto = new CustomerDTO();
        dto.setUsername(existingCustomer.getUsername());
        dto.setIsAuthor(existingCustomer.getIsAuthor());
        return Response.ok(existingCustomer).build();   // Retorna les dades actualitzades i 200 OK
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
}
