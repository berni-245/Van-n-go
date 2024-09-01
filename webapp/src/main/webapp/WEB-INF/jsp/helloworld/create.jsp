<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>

<body>
<c:url var="postUrl" value="/create"/>
<form:form action="${postUrl}" method="post" modelAttribute="userForm">
    <div>
        <label>
            <spring:message code="hwc.create.username"/>
            <form:input path="username" type="text"/>
        </label>
        <form:errors path="username" element="p" cssStyle="color: red"/>
    </div>
    <div>
        <label>
            <spring:message code="hwc.create.mail"/>
            <form:input path="mail"/>
        </label>
        <form:errors path="mail" element="p" cssStyle="color: red"/>
    </div>
    <div>
        <input type="submit">
    </div>
</form:form>
</body>

</html>
