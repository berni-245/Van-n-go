<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="ar.edu.itba.paw.models.BookingState" %>

<html>
<comp:Head titleCode="siteName"/>

<body class="d-flex flex-column min-vh-100">
<comp:Header inHome="true"/>
<main>
    <div class="container mt-4">

        <div class="row">
            <div class="col-12">
                <h1 class="text-left"><spring:message code="driver.home.yourBookings"/></h1>
            </div>
        </div>
        <c:choose>
            <c:when test="${empty bookings}">
                <div class="row">
                    <div class="col-12 text-center">
                        <p class="mt-5 display-4 font-weight-bold"><spring:message
                                code="call_to_action.driver_bookings"/></p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="row row-cols-4 row-cols-lg-3 row-cols-md-2 row-cols-sm-1 g-4">
                    <c:forEach var="booking" items="${bookings}">
                        <div class="col">
                            <div class="card mb-3 shadow h-100">
                                <c:choose>
                                    <c:when test="${booking.client.pfp==0}">
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
                                    <c:if test="${booking.state eq BookingState.PENDING}">
                                        <div class="d-flex justify-content-around mt-2">
                                            <c:url value="/driver/booking/${booking.id}/accept" var="bookingAcceptUrl"/>
                                            <form action="${bookingAcceptUrl}"
                                                  method="POST" class="mb-1">
                                                <button type="submit" class="btn btn-success">
                                                    <spring:message code="driver.home.booking.accept"/>
                                                </button>
                                            </form>
                                            <c:url value="/driver/booking/${booking.id}/reject" var="bookingRejectUrl"/>
                                            <form action="${bookingRejectUrl}"
                                                  method="POST" class="mb-1">
                                                <button type="submit" class="btn btn-danger">
                                                    <spring:message code="driver.home.booking.reject"/>
                                                </button>
                                            </form>
                                        </div>
                                    </c:if>
                                    <c:if test="${booking.state eq BookingState.ACCEPTED}">
                                        <div>
                                            <p><spring:message code="driver.home.booking.confirmed"/></p>
                                            <c:if test="${booking.pop eq null}">
                                                <spring:message code="driver.home.unpaid"/>
                                            </c:if>
                                            <c:if test="${booking.pop ne null}">
                                                <a href="<c:url value='/booking/pop?popId=${booking.pop}' />"
                                                   target="_blank">
                                                    <spring:message code="driver.home.paid"/>
                                                </a>
                                            </c:if>
                                            <c:if test="${booking.date.isBefore(currentDate)}">
                                                <div class="d-flex justify-content-around mt-2">
                                                    <c:url value="/driver/booking/${booking.id}/finish"
                                                           var="bookingFinishUrl"/>
                                                    <form action="${bookingFinishUrl}" method="POST" class="mb-1">
                                                        <button type="submit" class="btn btn-success">
                                                            <spring:message code="driver.home.booking.finish"/>
                                                        </button>
                                                    </form>
                                                </div>
                                            </c:if>
                                        </div>
                                    </c:if>
                                    <c:if test="${booking.state eq BookingState.REJECTED}">
                                        <div>
                                            <p><spring:message code="driver.home.booking.rejected"/></p>
                                        </div>
                                    </c:if>
                                </div>
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
