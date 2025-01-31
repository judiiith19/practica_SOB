/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deim.urv.cat.homework2.controller;

import deim.urv.cat.homework2.model.CustomerDTO;
import deim.urv.cat.homework2.service.UserService;
import jakarta.inject.Inject;
import jakarta.mvc.Models;
import jakarta.mvc.UriRef;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;

/**
 *
 * @author JUDITH
 */
public class UserProfilePageController {
    @Inject UserService service;
    @Inject Models models;
    
    @GET
    @UriRef("showUser")
    public String showForm(@QueryParam("param") Long idCustomer) {
       
       UserForm userForm = new UserForm();
       
       CustomerDTO customer = service.findCustomerById(idCustomer);
       userForm.addCustomer(customer);
       models.put("oneUserForm", userForm);
       models.put("userId", userForm.getId());
       return "UserProfile.jsp"; 
    }
}
