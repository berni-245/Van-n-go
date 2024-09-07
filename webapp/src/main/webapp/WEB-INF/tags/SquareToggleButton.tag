<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="content" required="true" type="java.lang.String" %>
<%@ attribute name="value" required="true" type="java.lang.String" %>
<%@ attribute name="tooltip" required="true" type="java.lang.String" %>
<%@ attribute name="id" required="false" type="java.lang.String" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:choose>
    <c:when test="${not empty id}">
        <c:set var="finalId" value="${id}"/>
    </c:when>
    <c:otherwise>
        <c:set var="finalId" value="btn-check-${value}"/>
    </c:otherwise>
</c:choose>

<div class="weekday-toggle-item">
    <form:checkbox path="${path}"
                   value="${value}"
                   cssClass="btn-check"
                   id="${finalId}"
                   element="div"
    />
    <label class="btn btn-primary weekday-toggle-label" for="${finalId}"
           data-bs-toggle="tooltip" data-bs-title="${tooltip}"
           data-bs-placement="bottom">
        <c:out value="${content}"/>
    </label>
</div>