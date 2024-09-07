<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="siteName"/></title>
    <%@include file="../lib/bootstrap_css.jsp" %>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
<%@include file="../components/header.jsp" %>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-4">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title text-center mb-4">
                        <spring:message code="public.login.greeting"/>
                    </h5>
                    <form action="/login" method="post" enctype="application/x-www-form-urlencoded">
                        <div class="mb-3">
                            <label for="username" class="form-label">
                                <spring:message code="generic.word.username"/>
                            </label>
                            <input id="username" name="j_username" type="text" class="form-control"/>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">
                                <spring:message code="generic.word.password"/>
                            </label>
                            <input id="password" name="j_password" type="password" class="form-control"/>
                        </div>
                        <div class="mb-3 form-check">
                            <input id="rememberMe" name="j_rememberme" type="checkbox" class="form-check-input"/>
                            <label for="rememberMe" class="form-check-label">
                                <spring:message code="public.login.rememberMe"/>
                            </label>
                        </div>
                        <div class="d-grid">
                            <spring:message code="public.login.login" var="login"/>
                            <input type="submit" class="btn btn-primary" value="${login}">
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
