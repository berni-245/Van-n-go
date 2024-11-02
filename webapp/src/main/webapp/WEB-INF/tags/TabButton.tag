<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="targetId" required="true" type="java.lang.String" %>
<%@ attribute name="code" required="true" type="java.lang.String" %>
<%@ attribute name="active" required="true" type="java.lang.Boolean" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<li class="nav-item" role="presentation">
    <button class="nav-link ${active ? 'active' : ''}" id="${id}" data-bs-toggle="tab"
            data-bs-target="#${targetId}" type="button" role="tab" aria-controls="${targetId}"
            aria-selected="${active ? 'true' : 'false'}">
        <spring:message code="${code}"/>
    </button>
</li>
