/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package deim.urv.cat.homework2.service;

import deim.urv.cat.homework2.model.ArticleSimpleDTO;
import deim.urv.cat.homework2.model.ArticleDetailedDTO;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.List;

/**
 *
 * @author JUDITH
 */
public interface ArticleService {
    public List<ArticleSimpleDTO> findArticles (List<String> topics, String author);
    public ArticleDetailedDTO findArticleById (Long id, HttpHeaders headers);
}
