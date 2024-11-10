<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<comp:Head titleCode="siteName"/>
<body class="d-flex flex-column min-vh-100">
<comp:Header inHistory="true"/>
<main>
    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <h1 class="text-left"><spring:message code="driver.home.yourBookings"/></h1>
            </div>
        </div>
        <c:choose>
            <c:when test="${empty history}">
                <div class="row">
                    <div class="col-12 text-center">
                        <p class="mt-5 display-4 font-weight-bold"><spring:message
                                code="call_to_action.driver_history"/></p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="row row-cols-3">
                    <c:forEach var="booking" items="${history}">
                        <div class="col mb-4">
                            <div class="card mb-3 shadow h-100">
                                <c:choose>
                                    <c:when test="${booking.client.pfp eq null}">
                                        <img src="${pageContext.request.contextPath}/images/defaultUserPfp.png"
                                             alt="Client Profile Picture" style="width: 60px; height: 60px;"
                                             class="rounded-circle position-absolute top-0 end-0 mt-2 me-2"
                                        />
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/user/pfp?userPfp=${booking.client.pfp}"
                                             alt="ClientPfp" style="width: 60px; height: 60px;"
                                             class="rounded-circle position-absolute top-0 end-0 mt-2 me-2"
                                        />
                                    </c:otherwise>
                                </c:choose>
                                <div class="card-body d-flex flex-column justify-content-between h-100">
                                    <div>
                                        <h5 class="card-title"><c:out value="${booking.date}"/></h5>
                                        <p class="card-text"><c:out value="${booking.client.username}"/></p>
                                        <p class="card-text"><c:out value="${booking.client.mail}"/></p>
                                    </div>
                                </div>
                                <c:choose>
                                    <c:when test="${booking.rating == null}">
                                        <p><spring:message code="driver.history.noRating"/></p>
                                    </c:when>
                                    <c:otherwise>
                                        <p><spring:message code="driver.history.rating"/></p>
                                        <span class="fw-bold text-warning">
                                            <fmt:formatNumber value="${booking.rating}" type="number" maxFractionDigits="2"/>
                                        </span>


                                        <div class="ms-2">
                                            <c:set var="fullStars" value="${booking.rating}"/>
                                            <c:set var="emptyStars"
                                                   value="${5 - fullStars}"/>

                                            <c:forEach var="i" begin="1" end="${fullStars}">
                                                <i class="bi bi-star-fill text-warning"></i>
                                            </c:forEach>

                                            <c:forEach var="i" begin="1" end="${emptyStars}">
                                                <i class="bi bi-star text-secondary"></i>
                                            </c:forEach>
                                        </div>
                                        <p><spring:message code="driver.history.review"/></p>
                                        <p><c:out value="${booking.review}"/> </p>
                                    </c:otherwise>
                                </c:choose>


                            </div>
                        </div>
                    </c:forEach>
                </div>
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${currentPage - 1}" tabindex="-1"
                                   aria-disabled="${currentPage == 0}">&laquo; Previous</a>
                            </li>
                            <c:forEach begin="0" end="${totalPages - 1}" var="i">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="?page=${i}">${i + 1}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${currentPage + 1}"
                                   aria-disabled="${currentPage == totalPages - 1}">Next &raquo;</a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>


</main>

<footer class="mt-auto">
    <div class="container">
        <p class="float-end mb-1">
            <a href="#"><spring:message code="public.home.backToTop"/></a>
        </p>
        <p class="mb-1">&copy; PAW 2024B G1</p>
    </div>
</footer>

</body>
</html>
