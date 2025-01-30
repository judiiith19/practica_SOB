package deim.urv.cat.homework2.controller;

import deim.urv.cat.homework2.model.Customer;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.mvc.binding.MvcBinding;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("userForm")
@RequestScoped
public class UserForm implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Customer> customers = new ArrayList<>();
        
    @FormParam("id")
    @MvcBinding
    private Long id;
        
    @NotBlank
    @FormParam("username")
    @MvcBinding
    @Size(min=2, max=30, message = "Username must be between 2 and 30 characters")
    private String username;
    
    @NotBlank
    @FormParam("password")
    @MvcBinding
    @Size(min=2, max=30, message = "Password must be between 2 and 30 characters")
    private String password;
    
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return fixNull(this.username); }
    public void setUserName(String username) { this.username = username; }

    public String getPassword() { return fixNull(this.password); }
    public void setPassword(String password) { this.password = password; }
    
    public List<Customer> getCustomers() { return customers; }
    public void setCustomers(List<Customer> customers) { this.customers = customers; }

    private String fixNull(String in) { return (in == null) ? "" : in; }
}
