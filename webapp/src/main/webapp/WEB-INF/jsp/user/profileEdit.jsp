<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<comp:Head titleCode="components.header.editUser"/>
<body class="d-flex flex-column min-vh-100">

<comp:Header/>
<div class="d-flex justify-content-between align-items-start mb-5">
    <c:choose>
        <c:when test="${loggedUser.isDriver}">
            <c:url value="/driver/profile" var="profilePath"/>
        </c:when>
        <c:otherwise>
            <c:url value="/client/profile" var="profilePath"/>
        </c:otherwise>
    </c:choose>
<a href="${profilePath}"
   class="btn me-auto">
    <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="bi bi-arrow-left"
         viewBox="0 0 16 16">
        <path fill-rule="evenodd"
              d="M5.854 4.146a.5.5 0 0 1 0 .708L3.707 7H13.5a.5.5 0 0 1 0 1H3.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 0 1 .708 0z"></path>
    </svg>
</a>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title text-center mb-4">
                        <spring:message code="user.editUser.edit"/>
                    </h5>
                    <c:url var="postUrl" value="/${loggedUser.type}/profile/edit"/>
                    <form:form action="${postUrl}" method="post" modelAttribute="changeUserInfoForm"
                               id="changeUserInfoForm">
                    <div class="mb-3">
                        <label class="form-label">
                            <spring:message code="generic.word.username"/>
                        </label>
                        <form:input path="username" type="text" class="form-control" id="username"/>
                        <form:errors path="username" element="p" cssClass="text-danger"/>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">
                            <spring:message code="public.register.mail"/>
                        </label>
                        <form:input path="mail" type="email" class="form-control"  id="mail"/>
                        <form:errors path="mail" element="p" cssClass="text-danger"/>
                    </div>
                    <div class="mb-3">
                        <label for="select-language">
                            <spring:message code="client.profile.languageSelect"/>
                        </label>
                        <spring:bind path="language">
                            <form:select path="language" id="select-language" multiple="false" autocomplete="off"
                                         cssClass="form-select ${status.error ? 'is-invalid' : ''}">
                                <c:forEach var="lang" items="${languages}">
                                    <option value="${lang.name()}">
                                        <spring:message code="generic.word.language.${lang.name().toLowerCase()}"/>
                                    </option>
                                </c:forEach>
                            </form:select>
                        </spring:bind>
                    </div>
                    <c:choose>
                     <c:when test="${loggedUser.isDriver}">
                         <div class="form-group mb-3"><label for="cbu">
                             <spring:message code="generic.word.cbu"/></label>
                             <form:input path="cbu" id="cbu" cssClass="form-control" />
                             <form:errors path="cbu" cssClass="text-danger" />
                        </div>
                        <div class="form-group mb-3">
                            <label for="description"><spring:message code="generic.word.description"/></label>
                            <form:input path="description" id="description" cssClass="form-control" />
                            <form:errors path="description" cssClass="text-danger" />
                         </div>
                      </c:when>
                    <c:otherwise>
                        <div class="mb-3">
                            <label for="zoneId">
                                <spring:message code="client.profile.zoneSelect"/>
                            </label>
                            <spring:bind path="zoneId">
                                <form:select path="zoneId" id="select-zones" multiple="false" autocomplete="off"
                                             cssClass="form-select ${status.error ? 'is-invalid' : ''}">
                                    <form:options items="${zones}" itemValue="id"/>
                                </form:select>
                            </spring:bind>
                        </div>
                    </c:otherwise>
                    </c:choose>
                    <form:errors element="div" cssClass="alert alert-danger"/>
                    <spring:message code="generic.word.confirm" var="confirm"/>
                    <input type="submit" class="btn btn-success" value="${confirm}">
                </div>
                </form:form>
            </div>

        </div>
    </div>
</div>
</div>
</body>
</html>