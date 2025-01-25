/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deim.urv.cat.homework2.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import java.io.Serializable;

/**
 *
 * @author JUDITH
 */
@Entity
@Table(name = "LINK")
public class Link implements Serializable {
    @Id
    @SequenceGenerator(name="Link_Gen", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Link_Gen")
    private Long id;

    @OneToOne
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @JsonbTransient // No serialitzat per evitar cicles.
    private Customer customer;  // Usuari asociat a l'enllaç.

    private String link;    // URL.
    
    public Link () {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Verificamos si el objeto es una instancia de Link
        if (!(object instanceof Link)) {
            return false;
        }
        Link other = (Link) object;
        // Comprobamos si los id son iguales
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.Link[ id=" + id + ", link=" + link + " ]";
    }
}
