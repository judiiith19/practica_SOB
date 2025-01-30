package deim.urv.cat.homework2.service;

import deim.urv.cat.homework2.controller.UserForm;
import deim.urv.cat.homework2.model.Customer;
import deim.urv.cat.homework2.model.CustomerDTO;
import java.util.List;

public interface UserService {
    
    public List<CustomerDTO> findAllCustomers();
    public CustomerDTO findCustomerById(Long id);
    public Customer findCustomerByCredentials(String username, String password);
    public boolean updateCustomer(Long id, CustomerDTO updatedCustomer);
    public boolean addUser(UserForm user);
}
