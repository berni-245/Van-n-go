<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="content" required="true" type="java.lang.String" %>
<%@ attribute name="value" required="true" type="java.lang.String" %>
<%@ attribute name="tooltip" required="false" type="java.lang.String" %>
<%@ attribute name="id" required="false" type="java.lang.String" %>
<%@ attribute name="labelClass" required="false" type="java.lang.String" %>
<%@ attribute name="radio" required="false" type="java.lang.Boolean" %>
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

<div class="square-toggle-item">
    <c:choose>
        <c:when test="${radio}">
            <form:radiobutton path="${path}"
                              value="${value}"
                              cssClass="btn-check"
                              id="${finalId}"
                              element="div"
            />
        </c:when>
        <c:otherwise>
            <form:checkbox path="${path}"
                           value="${value}"
                           cssClass="btn-check"
                           id="${finalId}"
                           element="div"
            />
        </c:otherwise>
    </c:choose>
    <label class="btn btn-primary square-toggle-label ${empty labelClass ? "" : labelClass}" for="${finalId}"
            <c:if test="${not empty tooltip}">
                data-bs-toggle="tooltip" data-bs-title="${tooltip}"
                data-bs-placement="bottom"
            </c:if>
    >
        <c:out value="${content}"/>
    </label>
</div>