<%@ attribute name="type" required="true" type="java.lang.String" %>
<%@ attribute name="titleCode" required="false" type="java.lang.String" %>
<%@ attribute name="descriptionCode" required="true" type="java.lang.String" %>
<%@ attribute name="delay" required="true" type="java.lang.Integer" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="toast text-bg-${type} align-items-center fade" role="alert"
     aria-live="assertive" aria-atomic="true" data-bs-delay="${delay}"
>
    <div class="d-flex">
        <div class="toast-body">
            <c:if test="${titleCode}">
                <h4 class="alert-heading">
                    <spring:message code="${titleCode}"/>
                </h4>
            </c:if>
            <p class="mb-0">
                <spring:message code="${descriptionCode}"/>
            </p>
        </div>
        <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
</div>