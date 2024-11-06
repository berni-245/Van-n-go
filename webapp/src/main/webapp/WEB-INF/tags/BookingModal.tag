<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="booking" required="true" type="ar.edu.itba.paw.models.Booking" %>
<%@ attribute name="currentDate" required="true" type="java.time.LocalDate" %>
<%@ attribute name="loggedUser" required="true" type="ar.edu.itba.paw.models.User" %>
<%@ tag import="ar.edu.itba.paw.models.BookingState" %>

<div class="modal fade" id="bookingModal${booking.id}" tabindex="-1"
     aria-labelledby="bookingModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title me-2" id="bookingModalLabel"><spring:message arguments="${booking.date}"
                                                                                    code="components.bookingCard.dateTime"/>
                    <spring:message
                            code="generic.word.${booking.shiftPeriod.name().toLowerCase()}"/></h5>
                <c:choose>
                    <c:when test="${booking.state eq BookingState.PENDING}">
                        <span class="badge bg-primary"><spring:message code="generic.word.pending.bookings"/></span>
                    </c:when>
                    <c:when test="${booking.state eq BookingState.ACCEPTED}">
                        <span class="badge bg-success"><spring:message code="generic.word.accepted.bookings"/></span>
                    </c:when>
                    <c:when test="${booking.state eq BookingState.REJECTED}">
                        <span class="badge bg-warning text-dark"><spring:message
                                code="generic.word.rejected.bookings"/></span>
                    </c:when>
                    <c:when test="${booking.state eq BookingState.FINISHED}">
                        <span class="badge bg-light text-dark"><spring:message
                                code="generic.word.finished.bookings"/></span>
                    </c:when>
                    <c:when test="${booking.state eq BookingState.CANCELED}">
                        <span class="badge bg-danger"><spring:message
                                code="generic.word.canceled.bookings"/></span>
                    </c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body ">

                <ul>
                    <c:set var="userPath" value="${loggedUser.isDriver ? 'driver' : 'client'}"/>
                    <c:choose>
                        <c:when test="${loggedUser.isDriver}">
                            <li><p class="card-text"><c:out value="${booking.client.username}"/></p></li>
                            <li><p class="card-text"><c:out value="${booking.client.mail}"/></p></li>
                        </c:when>
                        <c:otherwise>
                            <li><p class="card-text"><c:out value="${booking.driver.username}"/></p></li>
                            <li><p class="card-text"><c:out value="${booking.driver.mail}"/></p></li>
                            <c:if test="${booking.driver.cbu == null}">
                                <p><spring:message code="public.profile.noCbu"/></p>
                            </c:if>
                            <c:if test="${booking.driver.cbu != null}">
                                <li><p class="card-text"><spring:message code="generic.word.cbu"/>:<c:out
                                        value="${booking.driver.cbu}"/></p></li>
                            </c:if>
                            <c:choose>
                                <c:when test="${empty booking.pop or booking.pop == 0}">
                                    <spring:message code="client.bookings.transfer"/>
                                    <c:out value="${booking.driver.cbu}"/>
                                    <form id="uploadProofOfPaymentForm_${booking.id}" method="post"
                                          action="<c:url value='/client/bookings/upload/pop'/>" enctype="multipart/form-data">
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

                        </c:otherwise>
                    </c:choose>

                    <li><p class="card-text"><spring:message
                            code="components.bookingCard.zone"
                            arguments="${booking.originZone.neighborhoodName}, ${booking.destinationZone.neighborhoodName}"/></p>
                    </li>
                    <li><p class="card-text"><spring:message code="generic.word.description"/>: <c:out
                            value="${booking.jobDescription}"/></p></li>
                    <li><p class="card-text"><spring:message arguments="${booking.vehicle.plateNumber}"
                                                             code="components.bookingModal.vehicle"/></p></li>
                </ul>

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
                        <c:if test="${loggedUser.isDriver}">
                            <div>
                                <c:if test="${booking.pop eq null}">
                                    <spring:message code="driver.home.unpaid"/>
                                </c:if>
                                <c:if test="${booking.pop ne null}">
                                    <c:url value='/booking/pop?popId=${booking.pop}' var="popUrl"/>
                                    <a href="${popUrl}" target="_blank">
                                        <spring:message code="driver.home.paid"/>
                                    </a>
                                </c:if>
                                <c:if test="${booking.date.isBefore(currentDate)}">
                                    <div class="d-flex justify-content-around mt-2">
                                        <c:url value="/driver/booking/${booking.id}/finish" var="bookingFinishUrl"/>
                                        <form action="${bookingFinishUrl}" method="POST" class="mb-1">
                                            <button type="submit" class="btn btn-success">
                                                <spring:message
                                                        code="driver.home.booking.finish"/>
                                            </button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>
                        </c:if>
                        <c:choose>
                            <c:when test="${currentDate.isBefore(booking.date.minusDays(2))}">
                                <button class="btn btn-danger" data-bs-target="#cancelBooking${booking.id}"
                                        data-bs-toggle="modal" data-bs-dismiss="modal"><spring:message
                                        code="components.bookingModal.cancelBooking"/></button>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${!booking.date.isBefore(currentDate)}">
                                    <p><spring:message code="components.bookingModal.cantCancel"/></p>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:when test="${booking.state eq BookingState.FINISHED}">
                        <c:choose>
                            <c:when test="${booking.rating.isPresent()}">
                                <div class="d-flex justify-content-around mt-2">
                                    <p><spring:message code="driver.history.rating"/></p>
                                    <div class="d-flex align-items-center">
                                <span class="fw-bold text-warning">
                                    <c:out value="${booking.rating.get()}"/>
                                </span>
                                        <div class="ms-2">
                                            <c:set var="fullStars" value="${booking.rating.get().intValue()}"/>
                                            <c:set var="emptyStars" value="${5 - fullStars}"/>

                                            <c:forEach var="i" begin="1" end="${fullStars}">
                                                <i class="bi bi-star-fill text-warning"></i>
                                            </c:forEach>

                                            <c:forEach var="i" begin="1" end="${emptyStars}">
                                                <i class="bi bi-star text-secondary"></i>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>

                                <div class="d-flex">
                                    <p class="me-2"><spring:message code="driver.history.review"/></p>
                                    <p><c:out value="${booking.review.get()}"/></p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${loggedUser.isDriver}">
                                    <p><spring:message code="driver.history.noRating"/></p>
                                </c:if>
                                <c:if test="${!loggedUser.isDriver}">
                                    <p><spring:message code="client.history.noRating"/></p>
                                </c:if>
                            </c:otherwise>
                        </c:choose>

                    </c:when>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<c:if test="${booking.state eq BookingState.ACCEPTED && currentDate.isBefore(booking.date.minusDays(2))}">
    <comp:CancelBookingModal booking="${booking}" userPath="${userPath}"/>
</c:if>