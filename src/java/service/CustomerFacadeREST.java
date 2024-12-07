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
        List<Customer> customers = super.findAll();
        customers.forEach(customer -> {
            //customer.setPassword(null);
            if (customer.getArticles() != null && !customer.getArticles().isEmpty()) {
                Article lastArticle = customer.getArticles()
                        .stream()
                        .max((a1, a2) -> a1.getPublishedDate().compareTo(a2.getPublishedDate()))
                        .orElse(null);
                Link link = new Link();
                link.setLink("/api/v1/article/" + (lastArticle != null ? lastArticle.getId() : ""));
                link.setCustomer(customer);
                customer.getLinks().add(link);
            }
        });
        return Response.ok(customers).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getCustomer(@PathParam("id") Long id) {
        Customer customer = (Customer) find(id);
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(customer).build();
    }
    
    @PUT
    @Path("{id}")
    @Secured
    public Response updateUser(@PathParam("id") Long id, Customer customer, @Context HttpHeaders headers) {
        Customer existingCustomer = (Customer) super.find(id);
        if (existingCustomer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        String currentUser = getCurrentUser(headers);
        if (!existingCustomer.getUsername().equals(currentUser)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can only modify your own profile").build();
        }
        
        if (customer.getUsername() == null || isUserRegistered(customer.getUsername())) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Username already in use").build();
        }
        
        existingCustomer.setUsername(customer.getUsername());
        existingCustomer.setPassword(customer.getPassword());
        existingCustomer.setIsAuthor(customer.getIsAuthor());
        super.edit(existingCustomer);
        return Response.ok(existingCustomer).build();
    }
    
    private String getCurrentUser(HttpHeaders headers) {
        // Obtener el valor de la cabecera "Authorization" (usuario:contraseña)
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            // Decodifica la cabecera de autorización
            String encodedCredentials = authorizationHeader.substring("Basic ".length());
            String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials));

            // Los datos estarán en el formato "usuario:contraseña"
            String[] credentials = decodedCredentials.split(":");
            if (credentials.length > 0) {
                return credentials[0]; // Devuelve el nombre de usuario
            }
        }
        return null; // Si no se puede obtener el usuario
    }
    
    private boolean isUserRegistered(String username) {
        try {
            TypedQuery<Credentials> query = em.createNamedQuery("Credentials.findUser", Credentials.class);
            query.setParameter("username", username);
            Credentials credentials = query.getSingleResult();
            return credentials != null; // Si encuentra el usuario, está registrado
        } catch (NoResultException e) {
            return false; // Usuario no encontrado
        }
    }
}
