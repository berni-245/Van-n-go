<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<comp:Head titleCode="public.login.login" passInput="true"/>
<body class="d-flex flex-column min-vh-100">
<c:url value="/login" var="loginUrl"/>
<comp:Header/>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="w-auto">
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
                            <comp:ShowHidePassInput isFormInput="false" labelCode="generic.word.password" name="j_password"/>
                        </div>
                        <div class="form-check m-1">
                            <input id="rememberMe" name="j_rememberme" type="checkbox" class="form-check-input"/>
                            <label for="rememberMe" class="form-check-label">
                                <spring:message code="public.login.rememberMe"/>
                            </label>
                        </div>
                        <div class="mb-3">
                            <a href="${pageContext.request.contextPath}/register">
                                <spring:message code="public.login.noAccount"/>
                            </a>
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
</body>
</html>