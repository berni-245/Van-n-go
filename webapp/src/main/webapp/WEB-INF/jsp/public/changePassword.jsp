<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<comp:Head titleCode="components.header.editPass" passInput="true"/>
<body class="d-flex flex-column min-vh-100">
<comp:Header/>
<comp:GoBackButton path="/${loggedUser.type}/profile"/>
<div class="container mt-5">
    <div class="card">
        <div class="card-header text-center">
            <h1><c:out value="${loggedUser.username}"/></h1>
        </div>
        <div class="card-body">
            <c:url var="postUrl" value="/${loggedUser.type}/change/password"/>
            <form:form action="${postUrl}" method="post" modelAttribute="changePasswordForm"
                       id="changePasswordForm">
                <div class="mb-3">
                    <comp:ShowHidePassInput isFormInput="true" labelCode="public.changePassword.oldPassword"
                                            path="oldPassword"/>
                </div>
                <div class="mb-3">
                    <comp:ShowHidePassInput isFormInput="true" labelCode="generic.word.password"
                                            path="password"/>
                </div>
                <div class="mb-3">
                    <comp:ShowHidePassInput isFormInput="true" labelCode="public.register.confirmPassword"
                                            path="confirmPassword"/>
                </div>

                <form:errors element="div" cssClass="alert alert-danger"/>
                <spring:message code="generic.word.confirm" var="confirm"/>
                <input type="submit" class="btn btn-success" value="${confirm}">
            </form:form>

        </div>
    </div>
</div>

<comp:Footer toasts="${toasts}"/>

</body>
</html>