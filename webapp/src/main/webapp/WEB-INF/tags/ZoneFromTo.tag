<%@ attribute name="booking" required="true" type="ar.edu.itba.paw.models.Booking" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:out value="${booking.originZone.neighborhoodName}"/>
<c:if test="${booking.destinationZone ne null}">
    <spring:message
            code="components.bookingCard.zone.to"
            arguments="${booking.destinationZone.neighborhoodName}"
    />
</c:if>
