<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title><spring:message code="siteName"/></title>
    <%@include file="../lib/bootstrap_css.jsp" %>
    <jsp:include page="../lib/bootstrap_js.jsp"/>
    <jsp:include page="../lib/popper.jsp"/>
    <c:url value="/css/styles.css" var="css"/>
    <link rel="stylesheet" href="${css}">
</head>

<body class="d-flex flex-column min-vh-100">
<comp:Header inHome="true"/>
<main>
    <body>
    <div class="container mt-4">

        <div class="row">
            <div class="col-12">
                <h1 class="text-left"><spring:message code="driver.home.yourBookings"/></h1>
            </div>
        </div>

        <div class="row row-cols-3">
            <c:forEach var="booking" items="${bookings}">
            <div class="col mb-4">
                <div class="card mb-3">
                    <div class="card-body">
                        <h5 class="card-title"><spring:message code="driver.home.date"/>: <c:out value="${booking.date}" /></h5>
                        <p class="card-text"><spring:message code="driver.home.client"/>: <c:out value="${booking.client.username}" /></p>
                        <p class="card-text"><spring:message code="driver.home.clientMail"/>: <c:out value="${booking.client.mail}" /></p>
                        <c:if test="${booking.confirmed}">
                            <p><spring:message code="driver.home.bookingConfirmed"/></p>
                        </c:if>
                        <c:if test="${!booking.confirmed}">
                            <div class="d-flex justify-content-between">
                            <form action="${pageContext.request.contextPath}/driver/acceptBooking" method="POST">
                                <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                <button type="submit" class="btn btn-success"><spring:message code="driver.home.accept"/></button>
                            </form>
                            <form action="${pageContext.request.contextPath}/driver/rejectBooking" method="POST">
                                <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                <button type="submit" class="btn btn-danger"><spring:message code="driver.home.reject"/></button>
                            </form>
                            </div>
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
