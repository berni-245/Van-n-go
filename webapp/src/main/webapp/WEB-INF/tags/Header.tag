<%@ attribute name="inHome" required="false" type="java.lang.Boolean" %>
<%@ attribute name="inVehicles" required="false" type="java.lang.Boolean" %>
<%@ attribute name="inAvailability" required="false" type="java.lang.Boolean" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar navbar-expand-md border-bottom border-body mb-2">
    <div class="container-fluid">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarToggler"
                aria-controls="navbarToggler" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarToggler">
            <a href="${pageContext.request.contextPath}/" class="navbar-brand">
                <spring:message code="siteName"/>
            </a>
            <c:if test="${loggedIn}">
                <c:choose>
                    <c:when test="${loggedUser.isDriver}">
                        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/"
                                   class="nav-link ${inHome ? 'active' : ''}">
                                    <spring:message code="components.header.home"/>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/driver/vehicles"
                                   class="nav-link ${inVehicles ? 'active' : ''}">
                                    <spring:message code="components.header.vehicles"/>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/driver/availability"
                                   class="nav-link ${inAvailability ? 'active' : ''}">
                                    <spring:message code="components.header.availability"/>
                                </a>
                            </li>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/"
                                   class="nav-link ${inHome ? 'active' : ''}">
                                    <spring:message code="components.header.home"/>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/availability"
                                   class="nav-link ${inAvailability ? 'active' : ''}">
                                    <spring:message code="components.header.availability"/>
                                </a>
                            </li>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </c:if>

            <ul class="nav nav-pills ms-auto">
                <c:choose>
                    <c:when test="${loggedIn}">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/logout" class="nav-link" aria-current="page">
                                <spring:message code="components.header.logout"/>
                            </a>
                        </li>
                    </c:when>
                    <c:otherwise>
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
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>