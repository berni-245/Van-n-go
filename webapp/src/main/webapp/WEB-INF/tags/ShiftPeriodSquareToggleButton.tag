<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="content" required="true" type="java.lang.String" %>
<%@ attribute name="value" required="true" type="java.lang.String" %>
<%@ attribute name="id" required="false" type="java.lang.String" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<style>
    .square-toggle-item {
        display: inline-block;
        text-align: center;
        margin: 5px;
    }

    .square-toggle-label {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 80px;
        height: 80px;
        background-color: #6c757d;
        color: white;
        border-radius: 5px;
        font-size: 12px;
        padding: 10px;
        cursor: pointer;
    }
</style>

<c:choose>
    <c:when test="${not empty id}">
        <c:set var="finalId" value="${id}"/>
    </c:when>
    <c:otherwise>
        <c:set var="finalId" value="btn-check-${value}"/>
    </c:otherwise>
</c:choose>

<div class="square-toggle-item">
    <form:radiobutton path="${path}"
                      value="${value}"
                      id="${finalId}"
                      cssClass="btn-check"
                      element="div"
                      disabled="${disabled}" />
    <label class="btn btn-primary square-toggle-label" for="${finalId}">
        <c:out value="${content}"/>
    </label>
</div>