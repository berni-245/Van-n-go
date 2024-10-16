<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
                <p class="mt-5 display-4 font-weight-bold"><spring:message code="call_to_action.driver_bookings"/></p>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row row-cols-3">
            <c:forEach var="booking" items="${bookings}">
                <div class="col mb-4">
                    <div class="card mb-3">
                        <div class="card-body d-flex justify-content-between">
                            <div>
                            <h5 class="card-title"><c:out
                                    value="${booking.date}"/></h5>
                            <p class="card-text"><c:out
                                    value="${booking.client.username}"/></p>
                            <p class="card-text"><c:out
                                    value="${booking.client.mail}"/></p>
                            <c:if test="${booking.confirmed}">
                                <p><spring:message code="driver.home.bookingConfirmed"/></p>
                                <c:if test="${empty booking.pop or booking.pop == 0}">
                                    <spring:message code="driver.home.unpaid"/>
                                </c:if>
                                <c:if test="${not empty booking.pop and booking.pop != 0}">
                                    <a href="<c:url value='/booking/pop?bookingId=${booking.bookingId}' />" target="_blank">
                                        <spring:message code="driver.home.paid"/>
                                    </a>
                                </c:if>
                            </c:if>
                            <c:if test="${!booking.confirmed}">
                                <div class="d-flex justify-content-between">
                                    <form action="${pageContext.request.contextPath}/driver/acceptBooking"
                                          method="POST">
                                        <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                        <button type="submit" class="btn btn-success"><spring:message
                                                code="driver.home.accept"/></button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/driver/rejectBooking"
                                          method="POST">
                                        <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                        <button type="submit" class="btn btn-danger"><spring:message
                                                code="driver.home.reject"/></button>
                                    </form>
                                </div>
                            </c:if>
                            </div>
                            <c:choose>
                                <c:when test="${booking.client.pfp==0}">
                                    <img src="${pageContext.request.contextPath}/images/defaultUserPfp.png" alt="Client Profile Picture" class="rounded-circle" style="width: 60px; height: 60px;"/>
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/user/pfp?userId=${booking.client.id}" alt="ClientPfp" class="rounded-circle" style="width: 60px; height: 60px;"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
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
