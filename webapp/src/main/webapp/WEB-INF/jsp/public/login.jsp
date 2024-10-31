<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<comp:Head titleCode="public.login.login"/>
<body class="d-flex flex-column min-vh-100">
<c:url value="/login" var="loginUrl"/>
<comp:Header/>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-4">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title text-center mb-4">
                        <spring:message code="public.login.greeting"/>
                    </h5>
                    <form action="${pageContext.request.contextPath}/login" method="post"
                          enctype="application/x-www-form-urlencoded">
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
                            <div class="input-group">
                                <input id="password" name="j_password" type="password" class="form-control"/>
                                <button type="button" class="btn btn-outline-secondary" onclick="togglePasswordVisibility()">
                                    <spring:message code="public.login.showPassword"/>
                                </button>
                            </div>
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
                    <c:if test="${not empty param.error}">
                        <div class="alert alert-danger" role="alert">
                            <c:choose>
                                <c:when test="${param.error == 'user'}">
                                    <spring:message code="public.login.error.userNotExist"/>
                                </c:when>
                                <c:when test="${param.error == 'password'}">
                                    <spring:message code="public.login.error.incorrectPassword"/>
                                </c:when>
                                <c:otherwise>
                                    <spring:message code="public.login.error.unknown"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    function togglePasswordVisibility() {
        const passwordField = document.getElementById('password');
        if (passwordField.type === 'password') {
            passwordField.type = 'text';
        } else {
            passwordField.type = 'password';
        }
    }
</script>
</body>
</html>
