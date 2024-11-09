<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="tabId" required="true" type="java.lang.String" %>
<%@ attribute name="callToActionCode" required="true" type="java.lang.String" %>
<%@ attribute name="active" required="true" type="java.lang.Boolean" %>
<%@ attribute name="bookings" required="true" type="java.util.List<ar.edu.itba.paw.models.Booking>" %>
<%@ attribute name="currentDate" required="true" type="java.time.LocalDate" %>
<%@ attribute name="paramName" required="true" type="java.lang.String" %>
<%@ attribute name="totalPages" required="true" type="java.lang.Integer" %>
<%@ attribute name="currentPage" required="true" type="java.lang.Integer" %>
<%@ attribute name="loggedUser" required="true" type="ar.edu.itba.paw.models.User" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<div class="tab-pane fade ${active ? 'show active' : ''}" id="${id}"
     role="tabpanel" aria-labelledby="${tabId}">
    <c:if test="${empty bookings}">
        <comp:CallToAction code="${callToActionCode}"/>
    </c:if>
    <div class="row row-cols-3 g-4">
        <c:choose>
            <c:when test="${loggedUser.isDriver}">
                <c:forEach var="booking" items="${bookings}">
                    <comp:DriverBookingCard booking="${booking}" currentDate="${currentDate}"
                                            loggedUser="${loggedUser}"/>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <c:forEach var="booking" items="${bookings}">
                    <comp:ClientBookingCard booking="${booking}" currentDate="${currentDate}"
                                            loggedUser="${loggedUser}"/>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    <comp:Pagination paramName="${paramName}"
                     totalPages="${totalPages}"
                     currentPage="${currentPage}"/>
</div>
