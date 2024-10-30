<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

        <div class="row mb-4">
            <div class="col-12">
                <h1 class="text-left"><spring:message code="driver.home.yourBookings"/></h1>
            </div>
        </div>
        <c:choose>
            <c:when test="${empty bookings}">
                <div class="row">
                    <div class="col-12 text-center">
                        <p class="mt-5 display-4 font-weight-bold">
                            <spring:message code="call_to_action.client_bookings"/>
                        </p>
                    </div>
                </div>
            </c:when>
        <c:otherwise>
        <div class="row row-cols-3">
            <c:forEach var="booking" items="${bookings}">
                <div class="col mb-4">
                    <div class="card shadow-sm mb-3">
                        <div class="card-body d-flex justify-content-between">
                            <div>
                            <h5 class="card-title"><c:out value="${booking.date}"/></h5>
                            <p class="card-text"><c:out value="${booking.driver.username}"/></p>
                            <p class="card-text"><c:out value="${booking.driver.mail}"/></p>
                            <c:if test="${booking.state eq BookingState.ACCEPTED}">
                                <p><spring:message code="driver.home.booking.confirmed"/></p>
                                <c:choose>
                                <c:when test="${empty booking.pop or booking.pop == 0}">
                                <spring:message code="client.bookings.transfer"/>
                                <c:out value="${booking.driver.cbu}"/>
                                    <form id="uploadProofOfPaymentForm_${booking.id}" method="post"
                                          action="<c:url value='/upload/pop'/>" enctype="multipart/form-data">
                                        <input type="hidden" name="bookingId" value="${booking.id}">
                                        <input type="hidden" name="driverId" value="${booking.driver.id}">
                                        <input type="file" id="proofInput_${booking.id}" name="proofOfPayment"
                                               class="d-none" accept="application/pdf"
                                               onchange="document.getElementById('uploadProofOfPaymentForm_${booking.id}').submit();">
                                        <label for="proofInput_${booking.id}"
                                               style="cursor: pointer; text-decoration: underline;">
                                            <spring:message code="client.bookings.clickHereToPop"/>
                                        </label>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <a href="<c:url value='/booking/pop?popId=${booking.pop}' />" target="_blank">
                                        <spring:message code="client.bookings.popProvided"/>
                                    </a>
                                </c:otherwise>
                                </c:choose>
                            </c:if>
                            <c:if test="${booking.state eq BookingState.PENDING or booking.state eq BookingState.REJECTED}">
                                <p><spring:message code="client.bookings.bookingUnconfirmed"/></p>
                            </c:if>
                            </div>
                            <c:choose>
                                <c:when test="${booking.driver.pfp eq null}">
                                    <img src="${pageContext.request.contextPath}/images/defaultUserPfp.png" alt="Driver Profile Picture" class="rounded-circle" style="width: 60px; height: 60px;"/>
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/user/pfp?userPfp=${booking.driver.pfp}" alt="DriverPfp" class="rounded-circle" style="width: 60px; height: 60px;"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
            <c:if test="${totalPages > 1}">
                <nav aria-label="Page navigation">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage - 1}" tabindex="-1" aria-disabled="${currentPage == 0}">&laquo; Previous</a>
                        </li>
                        <c:forEach begin="0" end="${totalPages - 1}" var="i">
                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                <a class="page-link" href="?page=${i}">${i + 1}</a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage + 1}" aria-disabled="${currentPage == totalPages - 1}">Next &raquo;</a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </c:otherwise>
        </c:choose>
        <comp:ToastManager toasts="${toasts}"/>
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