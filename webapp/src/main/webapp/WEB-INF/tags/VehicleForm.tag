<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ attribute name="modelAttribute" required="true" type="java.lang.String" %>
<%@ tag body-content="scriptless" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<c:url var="postUrl" value="${action}"/>
<form:form action="${postUrl}" method="post" modelAttribute="${modelAttribute}">
    <div class="mb-3">
        <label class="form-label">
            <spring:message code="driver.add_vehicle.plateNumber"/>
            <form:input path="plateNumber" cssClass="form-control"/>
        </label>
        <form:errors path="plateNumber" element="p" cssStyle="color: red"/>
    </div>
    <div>
        <label class="form-label">
            <spring:message code="driver.add_vehicle.volume"/>
            <form:input path="volume" cssClass="form-control" type="number" min="1" max="100" step="0.01"/>
        </label>
        <form:errors path="volume" element="p" cssStyle="color: red"/>
    </div>
    <div>
        <label class="form-label">
            <spring:message code="driver.add_vehicle.description"/>
            <form:input path="description" cssClass="form-control"/>
        </label>
        <form:errors path="description" element="p" cssStyle="color: red"/>
    </div>
    <jsp:doBody/>
    <div class="mt-4">
        <button type="submit" class="btn btn-primary mt-2">
            <spring:message code="generic.word.confirm"/>
        </button>
    </div>
</form:form>