<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<comp:Head titleCode="components.header.editUser"/>
<body class="d-flex flex-column min-vh-100">
<script>
    window.addEventListener('DOMContentLoaded', (event) => {
        document.getElementById("changeUserInfoForm").addEventListener("submit", function (event) {

            const initialEmail = "${loggedUser.mail}";
            const initialUsername = "${loggedUser.username}";
            const initialPassword = "";
            const currentEmail = document.getElementById("mail").value;
            const currentUsername = document.getElementById("username").value;
            const currentPassword = document.getElementById("password").value
            const mailChanged = currentEmail !== initialEmail;
            const usernameChanged = currentUsername !== initialUsername;
            const passwordChanged = initialPassword !== currentPassword;

            const mailChangedInput = document.createElement("input");
            mailChangedInput.type = "hidden";
            mailChangedInput.name = "mailChanged";
            mailChangedInput.value = mailChanged;

            const usernameChangedInput = document.createElement("input");
            usernameChangedInput.type = "hidden";
            usernameChangedInput.name = "usernameChanged";
            usernameChangedInput.value = usernameChanged;

            const passwordChangedInput = document.createElement("input");
            passwordChangedInput.type = "hidden";
            passwordChangedInput.name = "passwordChanged";
            passwordChangedInput.value = passwordChanged;

            this.appendChild(mailChangedInput);
            this.appendChild(usernameChangedInput);
            this.appendChild(passwordChangedInput);
        });
    });

    function changePassword() {
        var div = document.getElementById("passwordForm");
        div.style.display = "block";
        var elemento = document.getElementById("changePasswordButton");
        elemento.style.display = "none";

    }
</script>
<comp:Header/>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title text-center mb-4">
                        <spring:message code="user.editUser.edit"/>
                    </h5>
                    <c:url var="postUrl" value="/account/edit"/>
                    <form:form action="${postUrl}" method="post" modelAttribute="changeUserInfoForm"
                               id="changeUserInfoForm">
                    <div class="mb-3">
                        <label class="form-label">
                            <spring:message code="generic.word.username"/>
                        </label>
                        <form:input path="username" type="text" class="form-control" value="${loggedUser.username}"
                                    id="username"/>
                        <form:errors path="username" element="p" cssClass="text-danger"/>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">
                            <spring:message code="public.register.mail"/>
                        </label>
                        <form:input path="mail" type="email" class="form-control" value="${loggedUser.mail}" id="mail"/>
                        <form:errors path="mail" element="p" cssClass="text-danger"/>
                    </div>

                    <div id="passwordForm" style="display: none">
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
                            <form:errors element="div" cssClass="alert alert-danger"/>
                        </div>
                    </div>
                    <spring:message code="generic.word.confirm" var="confirm"/>
                    <input type="submit" class="btn btn-success" value="${confirm}">
                </div>
                </form:form>
                <button class="btn btn-secondary w-auto" id="changePasswordButton" onclick="changePassword()">
                    <spring:message code="user.editUser.changePassword"/></button>
            </div>

        </div>
    </div>
</div>
</body>
</html>