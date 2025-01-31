<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Sign Up</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" />
<script src="<c:url value="/resources/js/jquery-1.11.1.min.js" />"></script>
<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>

<!-- FontAwesome -->
    <link href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"
          rel="stylesheet"
          integrity="sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN"
          crossorigin="anonymous"/>
</head>
<body>
	<div class="container">
		<div class="col-md-offset-2 col-md-7">
			<h2 class="text-center">Blog - Artículos</h2>
			<div class="panel panel-info">
				<div class="panel-heading">
					<div class="panel-title">Sign Up</div>
				</div>
				<div class="panel-body">
					<form action="${mvc.uri('sign-up')}" class="form-horizontal" method="POST">
                                                <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
						<div class="form-group">
							<label for="username" class="col-md-3 control-label">Username</label>
							<div class="col-md-9">
                                                            <input type="text" name="username" value="${user.username}" class="form-control"
                                                                   minlenght="2" maxlength="25" required/>
							</div>
						</div>
						<div class="form-group">
							<label for="password" class="col-md-3 control-label">Password</label>
							<div class="col-md-9">
                                                            <input type="password" name="password" value="${user.password}" class="form-control" 
                                                                   minlenght="6" maxlength="10" required/>
							</div>
						</div>
                                                <div class="form-group">
                                                    <label for="rememberMe" class="col-md-3 control-label">Remember Me</label>
							<div class="col-md-9">
                                                            <input type="checkbox" name="rememberMe" class="form-check-input" />
							</div>
                                                </div>
						<div class="form-group">
							<!-- Button -->
							<div class="col-md-offset-3 col-md-9">
								<input type="submit" value="Log In" />
							</div>
						</div>
					</form>
                                        <c:if test="${not empty message}">
                                            <div class="alert alert-danger" role="alert">
                                                ${message}        
                                            </div>
                                        </c:if>
                                        <jsp:include page="/WEB-INF/views/layout/alert.jsp" />
                                </div>
			</div>
		</div>
	</div>
        <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>