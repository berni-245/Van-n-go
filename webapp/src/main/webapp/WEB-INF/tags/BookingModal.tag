<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="booking" required="true" type="ar.edu.itba.paw.models.Booking" %>
<%@ attribute name="currentDate" required="true" type="java.time.LocalDate" %>
<%@ attribute name="loggedUser" required="true" type="ar.edu.itba.paw.models.User" %>
<%@ tag import="ar.edu.itba.paw.models.BookingState" %>

<div class="modal fade" id="bookingModal${booking.id}" tabindex="-1"
     aria-labelledby="bookingModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title me-2" id="bookingModalLabel">
                    <spring:message arguments="${booking.date}" code="components.bookingCard.dateTime"/>
                    <spring:message code="generic.word.${booking.shiftPeriod.lowerCaseText}"/>
                </h5>
                <c:choose>
                    <c:when test="${booking.state eq BookingState.PENDING}">
                        <span class="badge bg-primary">
                            <spring:message code="generic.word.pending.booking"/>
                        </span>
                    </c:when>
                    <c:when test="${booking.state eq BookingState.ACCEPTED}">
                        <span class="badge bg-success">
                            <spring:message code="generic.word.accepted.booking"/>
                        </span>
                    </c:when>
                    <c:when test="${booking.state eq BookingState.REJECTED}">
                        <span class="badge bg-warning text-dark">
                            <spring:message code="generic.word.rejected.booking"/>
                        </span>
                    </c:when>
                    <c:when test="${booking.state eq BookingState.FINISHED}">
                        <span class="badge bg-light text-dark">
                            <spring:message code="generic.word.finished.booking"/>
                        </span>
                    </c:when>
                    <c:when test="${booking.state eq BookingState.CANCELED}">
                        <span class="badge bg-danger">
                            <spring:message code="generic.word.canceled.booking"/>
                        </span>
                    </c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <ul class="modal-body list-group list-group-flush">
                <c:choose>
                    <c:when test="${loggedUser.isDriver}">
                        <li class="list-group-item"><c:out value="${booking.client.username}"/></li>
                        <li class="list-group-item"><c:out value="${booking.client.mail}"/></li>
                    </c:when>
                    <c:otherwise>
                        <li class="list-group-item"><c:out value="${booking.driver.username}"/></li>
                        <li class="list-group-item"><c:out value="${booking.driver.mail}"/></li>
                    </c:otherwise>
                </c:choose>

                <li class="list-group-item"><comp:ZoneFromTo booking="${booking}"/></li>
                <li class="list-group-item">
                    <spring:message code="generic.phrase.job.description"/>:
                    <c:out value="${booking.jobDescription}"/>
                </li>
                <li class="list-group-item">
                    <spring:message arguments="${booking.vehicle.plateNumber}"
                                    code="components.bookingModal.vehicle"/>
                </li>
                <li class="list-group-item">
                    <spring:message arguments="${booking.vehicle.hourlyRate}"
                                    code="components.bookingModal.hourlyRate"/>
                </li>

                <c:choose>
                    <c:when test="${booking.state eq BookingState.PENDING}">
                        <c:if test="${loggedUser.isDriver}">
                            <div class="d-flex justify-content-around mt-2">
                                <c:url value="/driver/booking/${booking.id}/accept" var="bookingAcceptUrl"/>
                                <form action="${bookingAcceptUrl}" method="POST" class="mb-1">
                                    <button type="submit" class="btn btn-success">
                                        <spring:message code="driver.home.booking.accept"/>
                                    </button>
                                </form>
                                <c:url value="/driver/booking/${booking.id}/reject" var="bookingRejectUrl"/>
                                <form action="${bookingRejectUrl}" method="POST" class="mb-1">
                                    <button type="submit" class="btn btn-danger">
                                        <spring:message code="driver.home.booking.reject"/>
                                    </button>
                                </form>
                            </div>
                        </c:if>
                    </c:when>
                    <c:when test="${booking.state eq BookingState.ACCEPTED}">
                        <comp:PopBookingList booking="${booking}" loggedUser="${loggedUser}"/>
                        <c:if test="${loggedUser.isDriver}">
                            <div>
                                <c:if test="${booking.date.isBefore(currentDate)}">
                                    <div class="d-flex justify-content-around mt-2">
                                        <c:url value="/driver/booking/${booking.id}/finish" var="bookingFinishUrl"/>
                                        <form action="${bookingFinishUrl}" method="POST" class="mb-1">
                                            <button type="submit" class="btn btn-success">
                                                <spring:message code="driver.home.booking.finish"/>
                                            </button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>
                        </c:if>
                        <c:choose>
                            <c:when test="${currentDate.isBefore(booking.date.minusDays(2))}">
                                <button class="btn btn-danger" data-bs-target="#cancelBooking${booking.id}"
                                        data-bs-toggle="modal" data-bs-dismiss="modal">
                                    <spring:message code="components.bookingModal.cancelBooking"/>
                                </button>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${!booking.date.isBefore(currentDate)}">
                                    <li class="list-group-item"><spring:message
                                            code="components.bookingModal.cantCancel"/></li>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:when test="${booking.state eq BookingState.FINISHED}">
                        <comp:PopBookingList booking="${booking}" loggedUser="${loggedUser}"/>
                        <c:choose>
                            <c:when test="${booking.rating ne null}">
                                <li class="list-group-item">
                                    <p class="fw-bold">
                                        <spring:message code="driver.history.review"/>
                                    </p>
                                    <div class="d-flex mb-2">
                                        <span class="fw-bold text-warning">
                                            <c:out value="${booking.rating}"/>
                                        </span>
                                        <div class="ms-2">
                                            <c:set var="fullStars" value="${booking.rating}"/>
                                            <c:set var="emptyStars" value="${5 - fullStars}"/>
                                            <c:forEach var="i" begin="1" end="${fullStars}">
                                                <i class="bi bi-star-fill text-warning"></i>
                                            </c:forEach>
                                            <c:forEach var="i" begin="1" end="${emptyStars}">
                                                <i class="bi bi-star text-secondary"></i>
                                            </c:forEach>
                                        </div>
                                    </div>
                                    <p><c:out value="${booking.review}"/></p>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${loggedUser.isDriver}">
                                    <p><spring:message code="driver.history.noRating"/></p>
                                </c:if>
                                <c:if test="${!loggedUser.isDriver}">
                                    <p><spring:message code="client.history.noRating"/></p>
                                    <c:url var="ratingUrl"
                                           value="/client/booking/${booking.id}/review"/>
                                    <a href="${ratingUrl}" class="btn btn-primary">
                                        <spring:message code="client.review"/>
                                    </a>
                                </c:if>
                            </c:otherwise>
                        </c:choose>

                    </c:when>
                </c:choose>
            </ul>
        </div>
    </div>
</div>

<c:if test="${booking.state eq BookingState.ACCEPTED && currentDate.isBefore(booking.date.minusDays(2))}">
    <comp:CancelBookingModal booking="${booking}" userPath="${loggedUser.type}"/>
</c:if>