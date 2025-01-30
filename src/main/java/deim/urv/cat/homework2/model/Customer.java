/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deim.urv.cat.homework2.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JUDITH
 */
@Entity
@Table(name = "CUSTOMER")
@NamedQuery(
    name = "Customer.findByUsername",
    query = "SELECT c FROM Customer c WHERE c.credentials.username = :username"
)
@XmlRootElement
public class Customer implements Serializable{
    @Id
    @SequenceGenerator(name = "Customer_Gen", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Customer_Gen")   
    private Long id;
    
    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "CREDENTIALS_ID", nullable = false)
    private Credentials credentials; // credencials de l'usuari (username i password)
    
    @Column(name = "IS_AUTHOR", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean isAuthor;   // Indica si l'usuari es autor d'algun article.
    
    @OneToOne(mappedBy = "customer")
    private Link link;   // Enllaç HATEOAS asociat a l'usuari.

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient // No serialitzat per evitar cicles.
    private List<Article> articles = new ArrayList<>(); // Llista d'articles creats per l'usuari.
    
    //Getters i Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Credentials getCredentials() { return credentials; }
    public void setCredentials(Credentials credentials) { this.credentials = credentials; }

    public Boolean getIsAuthor() { return isAuthor; }
    public void setIsAuthor(Boolean isAuthor) { this.isAuthor = isAuthor; }

    public Link getLink() { return link; }
    public void setLink(Link link) { this.link = link; }
       
    public List<Article> getArticles() { return articles; }
    public void setArticles(List<Article> articles) { this.articles = articles; }
    
    //equals, hashCode, toString
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "model.entities.Customer[ id=" + id + " ]";
    }
    
}