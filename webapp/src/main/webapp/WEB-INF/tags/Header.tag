<%@ attribute name="inHome" required="false" type="java.lang.Boolean" %>
<%@ attribute name="inVehicles" required="false" type="java.lang.Boolean" %>
<%@ attribute name="inAvailability" required="false" type="java.lang.Boolean" %>
<%@ attribute name="inHistory" required="false" type="java.lang.Boolean" %>
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
                                    <spring:message code="components.header.bookings"/>
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
                                <a href="${pageContext.request.contextPath}/client/bookings"
                                   class="nav-link ${inHome ? 'active' : ''}">
                                    <spring:message code="components.header.bookings"/>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/availability"
                                   class="nav-link ${inAvailability ? 'active' : ''}">
                                    <spring:message code="components.header.availability"/>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/client/history"
                                   class="nav-link ${inHistory ? 'active' : ''}">
                                    <spring:message code="generic.word.history"/>
                                </a>
                            </li>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </c:if>

            <ul class="nav nav-pills ms-auto">
                <c:choose>
                    <c:when test="${loggedIn}">
                        <div class="dropdown me-2 user-select-none">
                            <a class="text-body-emphasis d-flex align-items-center text-decoration-none dropdown-toggle"
                               data-bs-toggle="dropdown" aria-expanded="false" role="button">
                                <c:choose>
                                <c:when test="${loggedUser.pfp != 0}">
                                    <c:url value='/profile/picture' var="pfpUrl"/>
                                </c:when>
                                <c:otherwise>
                                    <c:url value='/images/defaultUserPfp.png' var="pfpUrl"/>
                                </c:otherwise>
                                </c:choose>
                                <img src="${pfpUrl}" alt="Profile Picture"
                                     class="rounded-circle me-2" width="50" height="50">
                                <span class="d-sm-inline mx-1">${loggedUser.username}</span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end text-small shadow">
                                <li class="dropdown-item">
                                    <c:url value='/profile' var="profileUrl"/>
                                    <a href="${profileUrl}"
                                       class="nav-link icon-link text-body-emphasis"
                                       aria-current="page">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                             fill="currentColor" class="bi bi-person-lines-fill"
                                             viewBox="0 0 16 16">
                                            <path d="M6 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6m-5 6s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zM11 3.5a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 0 1h-4a.5.5 0 0 1-.5-.5m.5 2.5a.5.5 0 0 0 0 1h4a.5.5 0 0 0 0-1zm2 3a.5.5 0 0 0 0 1h2a.5.5 0 0 0 0-1zm0 3a.5.5 0 0 0 0 1h2a.5.5 0 0 0 0-1z"></path>
                                        </svg>
                                        <spring:message code="generic.word.profile"/>
                                    </a>
                                </li>
                                <li>
                                    <hr class="dropdown-divider">
                                </li>
                                <li class="dropdown-item">
                                    <c:url value='/account/edit' var="accountEditUrl"/>
                                    <a href="${accountEditUrl}"
                                       class="nav-link icon-link text-body-emphasis"
                                       aria-current="page">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                             fill="currentColor" class="bi bi-gear-fill dropdown-icon"
                                             viewBox="0 0 16 16">
                                            <path d="M8 0a2 2 0 0 0-2 2v.41c-.741.124-1.45.357-2.102.693L3.5 2.5a2 2 0 1 0-2 3.464l.41.237a6.992 6.992 0 0 0 0 3.598l-.41.237a2 2 0 1 0 2 3.464l.398-.603a6.992 6.992 0 0 0 2.102.693V14a2 2 0 1 0 4 0v-.41a6.992 6.992 0 0 0 2.102-.693l.398.603a2 2 0 1 0 2-3.464l-.41-.237a6.992 6.992 0 0 0 0-3.598l.41-.237a2 2 0 1 0-2-3.464l-.398.603a6.992 6.992 0 0 0-2.102-.693V2a2 2 0 0 0-2-2zm0 4a4 4 0 1 1 0 8 4 4 0 0 1 0-8z"></path>
                                        </svg>
                                        <spring:message code="user.editUser.edit"/>
                                    </a>
                                </li>
                                <li>
                                    <hr class="dropdown-divider">
                                </li>
                                <li class="dropdown-item">
                                    <c:url value='/logout' var="logoutUrl"/>
                                    <a href="${logoutUrl}"
                                       class="nav-link icon-link icon-link-hover text-body-emphasis"
                                       aria-current="page">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40"
                                             viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                             stroke-width="1.5" stroke-linecap="round"
                                             stroke-linejoin="round" class="bi" style="fill: none">
                                            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                                            <polyline points="16 17 21 12 16 7"></polyline>
                                            <line x1="21" y1="12" x2="9" y2="12"></line>
                                        </svg>
                                        <spring:message code="components.header.logout"/>
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/login"
                               class="nav-link icon-link icon-link-hover" aria-current="page">
                                <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24"
                                     fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"
                                     stroke-linejoin="round" class="bi" style="fill: none">
                                    <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"></path>
                                    <polyline points="10 17 15 12 10 7"></polyline>
                                    <line x1="15" y1="12" x2="3" y2="12"></line>
                                </svg>
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