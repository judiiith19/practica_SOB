/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deim.urv.cat.homework2.model;

import java.io.Serializable;

/**
 *
 * @author JUDITH
 */
public class CustomerDTO implements Serializable {
    private String username;
    private Boolean isAuthor;
    private String lastArticleLink; // Enlace al último artículo publicado.
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public Boolean getIsAuthor() { return isAuthor; }
    public void setIsAuthor(Boolean isAuthor) { this.isAuthor = isAuthor; }
    
    public String getLastArticleLink() { return lastArticleLink; }
    public void setLastArticleLink(String link) { this.lastArticleLink = link; }
}
