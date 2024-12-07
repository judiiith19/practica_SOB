/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

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
@XmlRootElement
public class Customer implements Serializable{
    @Id
    @SequenceGenerator(name = "Customer_Gen", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Customer_Gen")   
    private Long id;

    @Column(name = "USERNAME", unique = true, nullable = false, columnDefinition = "VARCHAR(255)")
    private String username;    // Nom de l'usuari.

    @Column(name = "PASSWORD", nullable = false, length = 12, columnDefinition = "VARCHAR(12)")
    private String password;    // Contrasenya de l'usuari.
    
    @Column(name = "IS_AUTHOR", columnDefinition = "BOOLEAN")
    private Boolean isAuthor;   // Indica si l'usuari es autor d'algun article.
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Link> links = new ArrayList<>();   // Llista d'enllaços HATEOAS asociats a l'usuari.

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient // No serialitzat per evitar cicles.
    private List<Article> articles = new ArrayList<>(); // Llista d'articles creats per l'usuari.
    
    //Getters i Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getIsAuthor() { return isAuthor; }
    public void setIsAuthor(Boolean isAuthor) { this.isAuthor = isAuthor; }

    public List<Link> getLinks() { return links; }
    public void setLinks(List<Link> links) { this.links = links; }
       
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
        return "model.entities.User[ id=" + id + " ]";
    }
    
}
