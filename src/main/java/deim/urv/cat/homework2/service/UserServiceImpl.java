package deim.urv.cat.homework2.service;

import deim.urv.cat.homework2.controller.UserForm;
import deim.urv.cat.homework2.model.Customer;
import deim.urv.cat.homework2.model.CustomerDTO;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
        
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());
    private WebTarget webTarget;
    private final jakarta.ws.rs.client.Client client;
    private static final String BASE_URI = "http://localhost:8080/Homework1/webresources/api/v1/";
    
    public UserServiceImpl() {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("customer");
    }

    @Override
    public List<CustomerDTO> findAllCustomers() {
        try (Response response = webTarget.request(MediaType.APPLICATION_JSON).get()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(new GenericType<List<CustomerDTO>>() {});
            } else {
                LOGGER.log(Level.SEVERE, "Error al obtener usuarios. Código de estado: {0}", response.getStatus());
                return null;
            }
        }
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return Usuario encontrado o null si no existe.
     */
    @Override
    public CustomerDTO findCustomerById(Long id) {
        try (Response response = webTarget.path("/"+String.valueOf(id))
                                          .request(MediaType.APPLICATION_JSON)
                                          .get()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(CustomerDTO.class);
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                LOGGER.log(Level.WARNING, "Usuario con ID {0} no encontrado.", id);
            } else {
                LOGGER.log(Level.SEVERE, "Error al obtener el usuario. Código de estado: {0}", response.getStatus());
            }
            return null;
        }
    }

    @Override
    public Customer findCustomerByCredentials(String username, String password) {
        try (Response response = webTarget.request(MediaType.APPLICATION_JSON).get()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                List<CustomerDTO> customerDTOs = response.readEntity(new GenericType<List<CustomerDTO>>() {});

                // Buscar CustomerDTO con el username indicado
                CustomerDTO matchedDTO = customerDTOs.stream()
                    .filter(dto -> dto.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);

                if (matchedDTO != null) {
                    LOGGER.log(Level.INFO, "Usuario {0} encontrado. Autentificando...", username);

                    // Intentar autenticación con HTTP Basic Auth
                    return authenticateUser(username, password);
                }
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                LOGGER.log(Level.WARNING, "Usuario con username {0} no encontrado.", username);
            } else {
                LOGGER.log(Level.SEVERE, "Error al obtener el usuario. Código de estado: {0}", response.getStatus());
            }
            return null;
        }
    }
    
    @Override
    public boolean updateCustomer(Long id, CustomerDTO updatedCustomer) {
        try (Response response = webTarget.path("/"+String.valueOf(id))
                                          .request(MediaType.APPLICATION_JSON)
                                          .put(Entity.entity(updatedCustomer, MediaType.APPLICATION_JSON))) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return true;
            } else if (response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
                LOGGER.log(Level.WARNING, "No tienes permisos para modificar este usuario.");
            } else {
                LOGGER.log(Level.SEVERE, "Error al actualizar el usuario. Código de estado: {0}", response.getStatus());
            }
            return false;
        }
    }
    
    

    @Override
    public boolean addUser(UserForm user) {
       Response response = webTarget.request(MediaType.APPLICATION_JSON)
               .post(Entity.entity(user, MediaType.APPLICATION_JSON), 
                    Response.class);
     return response.getStatus() == 201;
    }

    /**
     * Método auxiliar para autenticar al usuario usando HTTP Basic Auth.
    */
    private Customer authenticateUser(String username, String password) {
        // Codificar credenciales en Base64 para HTTP Basic Auth
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        try (Response response = webTarget.request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .get()) {

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(Customer.class);
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
                    || response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()){
                LOGGER.log(Level.WARNING, "Authentication failed for user: {0}. Response code: {1}", 
                        new Object[]{username, response.getStatus()});
            } else {
                LOGGER.log(Level.SEVERE, "Error during authentication for user: " + username, response.getStatus());
            }
            return null;
        }
    }
}
