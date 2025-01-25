/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deim.urv.cat.homework2.model;

import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author JUDITH
 */
@Entity
@Table(name ="CREDENTIALS")
@NamedQuery(name="Credentials.findUser", 
            query="SELECT c FROM Credentials c WHERE c.username = :username")
@XmlRootElement
public class Credentials implements Serializable { 
    @Id
    @SequenceGenerator(name="Credentials_Gen", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Credentials_Gen") 
    private Long id;
    
    @Column(name = "USERNAME", unique=true, nullable = false)
    private String username;
    
    @Column(name = "PASSWORD", nullable = false, length = 12)
    private String password;
    
    @OneToOne(mappedBy = "credentials", optional = false)
    private Customer customer;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
    // equals, hashCode y toString
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Credentials)) {
            return false;
        }
        Credentials other = (Credentials) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "model.entities.Credentials[ id=" + id + ", username=" + username + " ]";
    }
}