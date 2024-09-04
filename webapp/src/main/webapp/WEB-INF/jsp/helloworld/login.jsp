<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    <%@include file="../components/bootstrap.jsp" %>
    <title>Penguin Express</title>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
<c:url value="/login" var="loginUrl"/>
<header class="d-flex flex-wrap justify-content-center py-3 mb-4 border-bottom bg-white">
    <a href="${pageContext.request.contextPath}/"
       class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-body-emphasis text-decoration-none">
        <span class="fs-4">Logo</span>
    </a>
    <ul class="nav nav-pills">
        <li class="nav-item"><a href="${pageContext.request.contextPath}/home" class="nav-link"><spring:message code="hwc.create.returnHome"/></a></li>
        <li class="nav-item"><a href="${pageContext.request.contextPath}/create" class="nav-link"><spring:message code="Register"/></a></li>
    </ul>
</header>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-4">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title text-center mb-4">Login</h5>
                    <form action="${loginUrl}" method="post" enctype="application/x-www-form-urlencoded">
                        <div class="mb-3">
                            <label for="username" class="form-label">Username:</label>
                            <input id="username" name="j_username" type="text" class="form-control"/>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Password:</label>
                            <input id="password" name="j_password" type="password" class="form-control"/>
                        </div>
                        <div class="mb-3 form-check">
                            <input id="rememberMe" name="j_rememberme" type="checkbox" class="form-check-input"/>
                            <label for="rememberMe" class="form-check-label">
                                <spring:message code="remember_me"/>
                            </label>
                        </div>
                        <div class="d-grid">
                            <input type="submit" value="Log in" class="btn btn-primary"/>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
