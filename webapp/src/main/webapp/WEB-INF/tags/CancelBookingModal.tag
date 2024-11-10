<%@ attribute name="booking" required="true" type="ar.edu.itba.paw.models.Booking" %>
<%@ attribute name="userPath" required="true" type="java.lang.String" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="modal fade" id="cancelBooking${booking.id}" aria-hidden="true"
     aria-labelledby="cancelBooking${booking.id}" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">

            <div class="modal-header">
                <h5 class="modal-title" id="cancelBooking${booking.id}"><spring:message code="components.bookingModal.cancelBooking"/></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p><spring:message code="components.bookingModal.${userPath}.cancelBookingWarning"/></p>
            </div>
            <div class="modal-footer">
                <c:url value="/${userPath}/booking/${booking.id}/cancel" var="bookingCancelUrl"/>
                <form action="${bookingCancelUrl}" method="POST" class="mb-1">
                    <button type="submit" class="btn btn-danger"><spring:message
                            code="components.bookingModal.cancelBooking"/>
                    </button>
                </form>
                <button class="btn btn-primary"  data-bs-dismiss="modal">
                    <spring:message code="generic.phrase.goBack"/>
                </button>
            </div>
        </div>
    </div>
</div>