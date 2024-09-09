<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <title><spring:message code="siteName"/></title>
    <%@include file="../lib/bootstrap_css.jsp" %>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
<comp:header/>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title text-center mb-4">
                        <spring:message code="public.register.registerUser"/>
                    </h5>
                    <c:url var="postUrl" value="/driver/register"/>
                    <form:form action="${postUrl}" method="post" modelAttribute="userForm">
                        <div class="mb-3">
                            <label class="form-label">
                                <spring:message code="generic.word.username"/>
                            </label>
                            <form:input path="username" type="text" class="form-control"/>
                            <form:errors path="username" element="p" cssClass="text-danger"/>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">
                                <spring:message code="public.register.mail"/>
                            </label>
                            <form:input path="mail" type="email" class="form-control"/>
                            <form:errors path="mail" element="p" cssClass="text-danger"/>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">
                                <spring:message code="generic.word.password"/>
                            </label>
                            <form:input type="password" path="password" class="form-control"/>
                            <form:errors path="password" element="p" cssClass="text-danger"/>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">
                                <spring:message code="public.register.confirmPassword"/>
                            </label>
                            <form:input type="password" path="confirmPassword" class="form-control"/>
                            <form:errors path="confirmPassword" element="p" cssClass="text-danger"/>
                            <form:errors element="div" cssClass="alert alert-danger"/>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">
                                <spring:message code="generic.word.description"/>
                            </label>
                            <form:input type="text" path="extra1" class="form-control"/>
                            <form:errors path="extra1" element="p" cssClass="text-danger"/>
                            <form:errors element="div" cssClass="alert alert-danger"/>
                        </div>
                        <div class="d-grid">
                            <spring:message code="generic.word.confirm" var="confirm"/>
                            <input type="submit" class="btn btn-primary" value="${confirm}">
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>