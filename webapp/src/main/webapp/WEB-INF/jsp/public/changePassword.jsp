<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<comp:Head titleCode="components.header.editPass">
</comp:Head>
<body class="d-flex flex-column min-vh-100">
<comp:Header/>

<div class="container mt-5">
    <div class="card">
        <div class="card-header text-center">
            <h1><c:out value="${loggedUser.username}"/></h1>
        </div>
        <div class="card-body">
            <c:url var="postUrl" value="/${userTypePath}/change/password"/>
            <form:form action="${postUrl}" method="post" modelAttribute="changePasswordForm"
                       id="changePasswordForm">
                <div class="mb-3">
                    <form:input type="hidden" path="driver"  value="${loggedUser.isDriver}"/>
                    <label class="form-label">
                        <spring:message code="public.changePassword.oldPassword"/>
                    </label>
                    <form:input type="password" path="oldPassword" class="form-control" id="password" value=""/>
                    <form:errors path="oldPassword" element="p" cssClass="text-danger"/>
                </div>
                <div class="mb-3">
                    <label class="form-label">
                        <spring:message code="generic.word.password"/>
                    </label>
                    <form:input type="password" path="password" class="form-control" id="password" value=""/>
                    <form:errors path="password" element="p" cssClass="text-danger"/>
                </div>
                <div class="mb-3">
                    <label class="form-label">
                        <spring:message code="public.register.confirmPassword"/>
                    </label>
                    <form:input type="password" path="confirmPassword" class="form-control" value=""/>
                    <form:errors path="confirmPassword" element="p" cssClass="text-danger"/>
                </div>
                <form:input type="hidden" path="userId" class="form-control" value="${loggedUser.id}"/>

                <form:errors element="div" cssClass="alert alert-danger"/>
                <spring:message code="generic.word.confirm" var="confirm"/>
                <input type="submit" class="btn btn-success" value="${confirm}">
            </form:form>

        </div>
    </div>
</div>

<footer class="mt-auto">
    <div class="container">
        <p class="float-end mb-1">
            <a href="#"><spring:message code="public.home.backToTop"/></a>
        </p>
        <p class="mb-1">&copy; PAW 2024B G1</p>
    </div>
</footer>

</body>
</html>

