<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Blog - Artículos</title>
    <link href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet" >
    <link href="<c:url value="/resources/css/style.css" />" rel="stylesheet">
    <script src="scripts.js" defer></script>
</head>
<body>
    <div class="article_cont">
        <header class="top-div">
            <!-- Filtros -->
            <div class="filter-section">
                <form method="GET" action="MainPage">
                    <div class="filter-group">
                        <label for="topic">Filtrar por Tema:</label>
                        <select id="topic" name="topic" class="form-control">
                            <option value="">Todos</option>
                            <c:forEach var="topic" items="${topicsInDB}">
                                <option value="${topic} ${topic == param.topic ? 'selected' : ''}">${topic}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label for="author">Filtrar por Autor:</label>
                        <select id="author" name="author" class="form-control">
                            <option value="">Todos</option>
                            <c:forEach var="author" items="${authorsInDB}">
                                <option value="${author} ${author == param.author ? 'selected' : ''}">${author}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="filter-btn">Filtrar</button>     
                </form>
            </div>
            <button class="button">L o g  I n</button>
        </header>
        <!-- Lista de artículos -->
        <h2 class="article-title">Lista de Artículos</h2>
        <div class="article-list">
            <c:forEach var="article" items="${articleSimpleForm.articles}">
                <div class="article-card">
                    <div>
                        <!--<img src="{article.imageUrl}" class="article-img" alt="Imagen del artículo"> -->
                    </div>
                    <div class="article-content">
                        <p class="card-title">${article.title}</p>
                        <p class="card-text">${article.summary}</p>
                        <div class="article-meta">
                            <span>${article.publishedDate} | ${article.views}</span>
                        </div>
                    </div>
                    <div class="article-user">
                        <!--<img src="user-placeholder.jpg" alt="Usuario" class="user-avatar">-->
                        <span>${article.author}</span>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</body>
</html>