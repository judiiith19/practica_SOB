/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deim.urv.cat.homework2.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author JUDITH
 */
@Entity
@XmlRootElement
public class Article implements Serializable {
    @Id
    @SequenceGenerator(name = "Article_Gen", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Article_Gen")
    private Long id;

    @Column(name = "TITLE", nullable = false, length = 200, columnDefinition = "VARCHAR(200)")
    private String title;   //Titol de l'article.

    @Column(name = "CONTENT", nullable = false, length = 5000, columnDefinition = "VARCHAR(5000)")
    private String content; // Contingut de l'article (max. 500 paraules).

    @Column(name = "SUMMARY", nullable = false, length = 20, columnDefinition = "VARCHAR(255)")
    private String summary; // Resum del contingut de l'article (max. 20 paraules).

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PUBLISHED_DATE", columnDefinition = "TIMESTAMP")
    private Date publishedDate; // Data de publicacio.
    
    @Column(name = "VIEWS", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer views = 0;  // Num. visites, default 0.
    
    @Column(name ="IMAGE_URL", nullable = false, columnDefinition = "VARCHAR(255)")
    @Pattern(regexp = "^(http|https)://.*$", message = "Not valid URL")
    private String imageUrl;    // URL de la imatge de l'article.

    @Column(name = "IS_PUBLIC", columnDefinition = "BOOLEAN")
    private Boolean isPublic;   // Indica si l'article es public o no.

    @ManyToMany
    // Taula que guarda les relacions Article - Topic.
    @JoinTable(
        name = "ARTICLE_TOPIC",
        joinColumns = @JoinColumn(name = "ARTICLE_ID"),
        inverseJoinColumns = @JoinColumn(name = "TOPIC_ID")
    )
    private List<Topic> topics = new ArrayList<>(); // Llista de temes asociats a l'article

    @ManyToOne(optional = false)
    @JoinColumn(name = "AUTHOR_ID", nullable = false)
    private Customer author;    // L'autor del article.
    
    //Getters i Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Date getPublishedDate() { return publishedDate; }
    public void setPublishedDate(Date publishedDate) { this.publishedDate = publishedDate; }

    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public List<Topic> getTopics() { return topics; }
    public void setTopics(List<Topic> topics) { this.topics = topics; }

    public Customer getAuthor() { return author; }
    public void setAuthor(Customer author) { this.author = author; }
    
    //equals, hashCode i toString

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Article)) {
            return false;
        }
        Article other = (Article) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "model.entities.Article[ id=" + id + " ]";
    }
    
}