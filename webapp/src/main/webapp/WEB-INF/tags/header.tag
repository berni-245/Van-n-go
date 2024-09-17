<%@ tag body-content="empty" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<header class="d-flex flex-wrap justify-content-center py-3 mb-4 border-bottom">
    <a href="${pageContext.request.contextPath}/"
       class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-body-emphasis text-decoration-none">
        <span class="fs-4 ms-3"><spring:message code="siteName"/></span>
    </a>
    <ul class="nav nav-pills">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/login" class="nav-link" aria-current="page">
                <spring:message code="components.header.login"/>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/register" class="nav-link">
                <spring:message code="components.header.register"/>
            </a>
        </li>
    </ul>
</header>