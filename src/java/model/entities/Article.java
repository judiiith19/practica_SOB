/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author JUDITH
 */
@Entity
@XmlRootElement
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Article_Gen")
    @SequenceGenerator(name = "Article_Gen", sequenceName = "ARTICLE_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private String summary;

    @Temporal(TemporalType.TIMESTAMP)
    private Date publishedDate;
    
    @Column(nullable = false)
    private Integer views;
    
    @Column(nullable = false)
    @Pattern(regexp = "^(http|https)://.*$", message = "Not valid URL")
    private String imageUrl;

    private Boolean isPublic;

    @ManyToMany
    @JoinTable(
        name = "ARTICLE_TOPIC",
        joinColumns = @JoinColumn(name = "ARTICLE_ID"),
        inverseJoinColumns = @JoinColumn(name = "TOPIC_ID")
    )
    private List<Topic> topics = new ArrayList<>();

    @ManyToOne(optional = false)
    private Customer author;
    
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
