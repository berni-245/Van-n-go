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
            const currentEmail = document.getElementById("mail").value;
            const currentUsername = document.getElementById("username").value;
            const mailChanged = currentEmail !== initialEmail;
            const usernameChanged = currentUsername !== initialUsername;

            const mailChangedInput = document.createElement("input");
            mailChangedInput.type = "hidden";
            mailChangedInput.name = "mailChanged";
            mailChangedInput.value = mailChanged;

            const usernameChangedInput = document.createElement("input");
            usernameChangedInput.type = "hidden";
            usernameChangedInput.name = "usernameChanged";
            usernameChangedInput.value = usernameChanged;


            this.appendChild(mailChangedInput);
            this.appendChild(usernameChangedInput);
        });
    });


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
                    <c:set var="userPath" value="${loggedUser.isDriver ? 'driver' : 'client'}"/>
                    <c:url var="postUrl" value="/${userPath}/profile/edit"/>
                    <form:form action="${postUrl}" method="post" modelAttribute="changeUserInfoForm"
                               id="changeUserInfoForm">
                    <div class="mb-3">
                        <label class="form-label">
                            <spring:message code="generic.word.username"/>
                        </label>
                        <form:input path="username" type="text" class="form-control"
                                    id="username"/>
                        <form:errors path="username" element="p" cssClass="text-danger"/>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">
                            <spring:message code="public.register.mail"/>
                        </label>
                        <form:input path="mail" type="email" class="form-control"  id="mail"/>
                        <form:errors path="mail" element="p" cssClass="text-danger"/>
                    </div>

                    <c:if test="${loggedUser.isDriver}">
                        <div class="form-group">
                            <label for="cbu"><spring:message code="generic.word.cbu"/></label>
                            <form:input path="cbu" id="cbu" cssClass="form-control" />
                            <form:errors path="cbu" cssClass="text-danger" />
                        </div>
                        <div class="form-group">
                            <label for="extra1"><spring:message code="generic.word.description"/></label>
                            <form:input path="extra1" id="extra1" cssClass="form-control" />
                            <form:errors path="extra1" cssClass="text-danger" />
                        </div>
                    </c:if>
                    <form:input path="oldUsername" type="hidden" id="oldUsername"/>
                    <form:input path="oldMail" type="hidden" id="oldMail"/>
                    <form:errors element="div" cssClass="alert alert-danger"/>
                    <spring:message code="generic.word.confirm" var="confirm"/>
                    <input type="submit" class="btn btn-success" value="${confirm}">
                </div>
                </form:form>
            </div>

        </div>
    </div>
</div>
</body>
</html>