<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Blog - Artículos</title>
    <link href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet" >
    <link href="<c:url value="/resources/css/style-main.css" />" rel="stylesheet">
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
                    <button type="submit" class="button">Filtrar</button>     
                </form>
            </div>
            <div class="log-btn-cont">
                <button class="button" onClick="redirectToSignUp()">L o g  I n</button>
            </div>
        </header>
        <!-- Lista de artículos -->
        <h2 class="article-title">Lista de Artículos</h2>
        <div class="article-list">
            <c:forEach var="article" items="${articleSimpleForm.articles}">
                <article class="article-card" onClick="redirectToArticle(${article.id}, ${article.isPublic})">
                    <div>
                        <!--<img src="{article.imageUrl}" class="article-img" alt="Imagen del artículo"> -->
                    </div>
                    <div class="article-content">
                        <p class="card-title">${article.title}</p>
                        <p class="card-text">${article.summary}</p>
                        <div class="article-meta">
                            <span class="card-text">${article.publishedDate} | ${article.views}</span>
                            <span class="article-user">
                                <!--<img src="user-placeholder.jpg" alt="Usuario" class="user-avatar">-->
                                <span class="card-text">${article.author}</span>
                            </span>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
    </div>
    <script>
        function redirectToSignUp() {
            // Redirigir a otro archivo HTML
            window.location.href = "${mvc.uri('sign-up')}";
        }
    </script>
    <script>
        function redirectToArticle(articleId, isPublic) {
            const isRegistered = <c:out value="${not empty sessionScope.user}" />; // Verifica si el usuario está loggeado

            if (isPublic) {
                if (isRegistered) {
                    // Redirige al artículo detallado si es privado y el usuario está logueado
                    var url = "${mvc.uri('showDetailedArticle')}" + "?param=" + encodeURIComponent(articleId);
                    window.location.href = url;
                } else {
                    // Redirige a la página de login si no está logueado
                    window.location.href = 'SignUp';
                }
            } else {
                // Redirige al artículo detallado si no es privado
                var url = "${mvc.uri('showDetailedArticle')}" + "?param=" + encodeURIComponent(articleId);
                window.location.href = url;
            }
        }
    </script>
</body>
</html>