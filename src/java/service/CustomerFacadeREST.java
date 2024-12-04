/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import model.entities.Article;
import model.entities.Customer;

import java.util.List;

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
            customer.setPassword("0");
            if (customer.getArticles() != null && !customer.getArticles().isEmpty()) {
                Article lastArticle = customer.getArticles()
                        .stream()
                        .max((a1, a2) -> a1.getPublishedDate().compareTo(a2.getPublishedDate()))
                        .orElse(null);
                customer.getLinks().add("/api/v1/article/" + (lastArticle != null ? lastArticle.getId() : ""));
            }
            customer.setPassword(null);
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
        customer.setPassword("0"); // No devolver contraseñas
        return Response.ok(customer).build();
    }
    
    @PUT
    @Path("{id}")
    public Response updateUser(@PathParam("id") Long id, Customer customer) {
        Customer existingCustomer = (Customer) super.find(id);
        if (existingCustomer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        existingCustomer.setUsername(customer.getUsername());
        existingCustomer.setIsAuthor(customer.getIsAuthor());
        existingCustomer.setLinks(customer.getLinks());
        existingCustomer.setArticles(customer.getArticles());
        super.edit(existingCustomer);
        return Response.ok(existingCustomer).build();
    }
}
