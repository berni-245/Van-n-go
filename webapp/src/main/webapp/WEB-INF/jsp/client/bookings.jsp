<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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

        <div class="row row-cols-3">
            <c:forEach var="booking" items="${bookings}">
                <div class="col mb-4">
                    <div class="card shadow-sm mb-3">
                        <div class="card-body">
                            <h5 class="card-title"><c:out value="${booking.date}"/></h5>
                            <p class="card-text"><c:out value="${booking.driver.username}"/></p>
                            <p class="card-text"><c:out value="${booking.driver.mail}"/></p>
                            <c:if test="${booking.confirmed}">
                                <p><spring:message code="driver.home.bookingConfirmed"/></p>
                                <c:if test="${empty booking.pop or booking.pop == 0}">
                                    <form id="uploadProofOfPaymentForm_${booking.bookingId}" method="post" action="<c:url value='/upload/pop'/>" enctype="multipart/form-data">
                                        <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                        <input type="hidden" name="driverId" value="${booking.driver.id}">
                                        <input type="file" id="proofInput_${booking.bookingId}" name="proofOfPayment" class="d-none" accept="application/pdf" onchange="document.getElementById('uploadProofOfPaymentForm_${booking.bookingId}').submit();">
                                        <label for="proofInput_${booking.bookingId}" style="cursor: pointer; text-decoration: underline;">
                                            <spring:message code="client.bookings.clickHereToPop"/>
                                        </label>
                                    </form>
                                </c:if>
                                <c:if test="${not empty booking.pop}">
                                    <spring:message code="client.bookings.popProvided"/>
                                </c:if>
                            </c:if>
                            <c:if test="${!booking.confirmed}">
                                <p><spring:message code="client.bookings.bookingUnconfirmed"/></p>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>


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
