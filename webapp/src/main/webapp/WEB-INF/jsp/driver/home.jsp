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
                <ul class="nav nav-tabs" id="statusTabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="pending-tab" data-bs-toggle="tab"
                                data-bs-target="#pending" type="button" role="tab" aria-controls="pending"
                                aria-selected="true">
                            Pending
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="accepted-tab" data-bs-toggle="tab" data-bs-target="#accepted"
                                type="button" role="tab" aria-controls="accepted" aria-selected="false">
                            Accepted
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="rejected-tab" data-bs-toggle="tab" data-bs-target="#rejected"
                                type="button" role="tab" aria-controls="rejected" aria-selected="false">
                            Rejected
                        </button>
                    </li>
                </ul>

                <div class="tab-content mt-3" id="statusTabsContent">
                    <div class="tab-pane fade show active" id="pending" role="tabpanel" aria-labelledby="pending-tab">
                        <div class="row row-cols-3">
                            <c:forEach var="booking" items="${bookings}">
                                <comp:BookingCard booking="${booking}"/>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="tab-pane fade show active" id="accepted" role="tabpanel" aria-labelledby="accepted-tab">
                        <div class="row row-cols-3">
                            <c:forEach var="booking" items="${bookings}">
                            </c:forEach>
                        </div>
                    </div>

                    <div class="tab-pane fade show active" id="rejected" role="tabpanel" aria-labelledby="rejected-tab">
                        <div class="row row-cols-3">
                            <c:forEach var="booking" items="${bookings}">
                            </c:forEach>
                        </div>
                    </div>
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
