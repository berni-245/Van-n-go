<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <%@include file="../components/bootstrap.jsp" %>
    <title>
        Penguin Express
    </title>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
<c:url var="postUrl" value="/driver/register"/>
<%@include file="../components/myHeader.jsp" %>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title text-center mb-4"><spring:message code="hwc.create.userRegistry"/></h5>
                    <form:form action="${postUrl}" method="post" modelAttribute="driverForm">
                        <div class="mb-3">
                            <label class="form-label">
                                <spring:message code="hwc.create.username"/>
                            </label>
                            <form:input path="username" type="text" class="form-control"/>
                            <form:errors path="username" element="p" cssClass="text-danger"/>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">
                                <spring:message code="hwc.create.mail"/>
                            </label>
                            <form:input path="mail" type="email" class="form-control"/>
                            <form:errors path="mail" element="p" cssClass="text-danger"/>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">
                                <spring:message code="hwc.create.password"/>
                            </label>
                            <form:input type="password" path="password" class="form-control"/>
                            <form:errors path="password" element="p" cssClass="text-danger"/>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">
                                <spring:message code="hwc.create.confirmPassword"/>
                            </label>
                            <form:input type="password" path="confirmPassword" class="form-control"/>
                            <form:errors path="confirmPassword" element="p" cssClass="text-danger"/>
                            <form:errors element="div" cssClass="alert alert-danger"/>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">
                                <spring:message code="hwc.create.description"/>
                            </label>
                            <form:input type="text" path="extra1" class="form-control"/>
                            <form:errors path="extra1" element="p" cssClass="text-danger"/>
                            <form:errors element="div" cssClass="alert alert-danger"/>
                        </div>
                        <div class="d-grid">
                            <input type="submit" class="btn btn-primary" value=<spring:message code="confirm"/>>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>