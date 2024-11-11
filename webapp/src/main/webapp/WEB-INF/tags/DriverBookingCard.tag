<%@ attribute name="booking" required="true" type="ar.edu.itba.paw.models.Booking" %>
<%@ attribute name="currentDate" required="true" type="java.time.LocalDate" %>
<%@ attribute name="loggedUser" required="true" type="ar.edu.itba.paw.models.User" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<%@ tag import="ar.edu.itba.paw.models.BookingState" %>

<div class="col">
    <div class="card mb-3 shadow h-100">
        <c:choose>
            <c:when test="${booking.client.pfp eq null}">
                <img src="${pageContext.request.contextPath}/images/defaultUserPfp.png"
                     alt="Client Profile Picture" style="width: 60px; height: 60px;"
                     class="rounded-circle position-absolute top-0 end-0 mt-2 me-2"
                />
            </c:when>
            <c:otherwise>
                <img src="${pageContext.request.contextPath}/images/query?imgId=${booking.client.pfp}"
                     alt="ClientPfp" style="width: 60px; height: 60px;"
                     class="rounded-circle position-absolute top-0 end-0 mt-2 me-2"
                />
            </c:otherwise>
        </c:choose>
        <div class="card-body d-flex flex-column justify-content-between h-100">
            <div>
                <h5 class="card-title w-75">
                    <spring:message arguments="${booking.date}"
                                    code="components.bookingCard.dateTime"/>
                    <spring:message code="generic.word.${booking.shiftPeriod.lowerCaseText}"/>
                </h5>
                <p class="card-text"><c:out value="${booking.client.username}"/></p>
                <p class="card-text"><comp:ZoneFromTo booking="${booking}"/></p>
            </div>
            <c:choose>
                <c:when test="${booking.state eq BookingState.PENDING}">
                    <div class="d-flex justify-content-around mt-2">
                        <div class="d-flex flex-lg-row flex-md-column gap-1">
                            <c:url value="/driver/booking/${booking.id}/accept" var="bookingAcceptUrl"/>
                            <form action="${bookingAcceptUrl}" method="POST" class="mb-1">
                                <button type="submit" class="btn btn-success w-100">
                                    <spring:message code="driver.home.booking.accept"/>
                                </button>
                            </form>
                            <c:url value="/driver/booking/${booking.id}/reject" var="bookingRejectUrl"/>
                            <form action="${bookingRejectUrl}" method="POST" class="mb-1">
                                <button type="submit" class="btn btn-danger w-100">
                                    <spring:message code="driver.home.booking.reject"/>
                                </button>
                            </form>
                        </div>
                        <div class="d-flex flex-lg-row flex-md-column gap-1">
                            <comp:SeeDetailsBtutton targetId="bookingModal${booking.id}"
                                                    tooltipCode="generic.phrase.seeDetails"/>
                            <comp:ChatButton
                                    path="/driver/chat?bookingId=${booking.id}&recipientId=${booking.client.id}"
                                    tooltipCode="generic.word.chat"/>
                        </div>
                    </div>
                </c:when>
                <c:when test="${booking.state eq BookingState.ACCEPTED}">
                    <div>
                        <c:if test="${booking.pop eq null}">
                            <spring:message code="driver.home.unpaid"/>
                        </c:if>
                        <c:if test="${booking.pop ne null}">
                            <c:url value='/images/query?imgId=${booking.pop}' var="popUrl"/>
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
                </c:when>
            </c:choose>
            <c:if test="${booking.state ne BookingState.PENDING}">
                <div class="d-flex justify-content-between">
                    <comp:SeeDetailsBtutton targetId="bookingModal${booking.id}"
                                            tooltipCode="generic.phrase.seeDetails"/>
                    <comp:ChatButton path="/driver/chat?bookingId=${booking.id}&recipientId=${booking.client.id}"
                                     tooltipCode="generic.word.chat"/>
                </div>
            </c:if>
        </div>
    </div>
</div>

<comp:BookingModal booking="${booking}" currentDate="${currentDate}" loggedUser="${loggedUser}"/>