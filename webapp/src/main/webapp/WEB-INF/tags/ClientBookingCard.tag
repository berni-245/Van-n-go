<%@ attribute name="booking" required="true" type="ar.edu.itba.paw.models.Booking" %>
<%@ attribute name="currentDate" required="true" type="java.time.LocalDate" %>
<%@ attribute name="loggedUser" required="true" type="ar.edu.itba.paw.models.User" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ tag body-content="empty" %>

<%@ tag import="ar.edu.itba.paw.models.BookingState" %>

<div class="col">
    <div class="card mb-3 anchor-card shadow h-100">
        <div class="card-body d-flex flex-column justify-content-between h-100">

            <div>
                <h5 class="card-title"><c:out value="${booking.date}"/></h5>
                <p class="card-text"><c:out value="${booking.driver.username}"/></p>
                <p class="card-text"><c:out value="${booking.driver.mail}"/></p>
                <c:if test="${booking.state eq BookingState.ACCEPTED}">
                    <p><spring:message code="driver.home.booking.confirmed"/></p>
                    <c:choose>
                        <c:when test="${empty booking.pop or booking.pop == 0}">
                            <spring:message code="client.bookings.transfer"/>
                            <c:out value="${booking.driver.cbu}"/>
                            <form id="uploadProofOfPaymentForm_${booking.id}" method="post"
                                  action="<c:url value='/client/bookings/upload/pop'/>"
                                  enctype="multipart/form-data">
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
                            <a href="<c:url value='/booking/pop?popId=${booking.pop}' />"
                               target="_blank">
                                <spring:message code="client.bookings.popProvided"/>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:if>
                <c:if test="${booking.state eq BookingState.PENDING or booking.state eq BookingState.REJECTED}">
                    <p><spring:message code="client.bookings.bookingUnconfirmed"/></p>
                </c:if>
                <c:if test="${booking.state eq BookingState.FINISHED}">
                    <c:if test="${booking.rating eq null}">
                        <button type="button" class="btn btn-primary" data-bs-toggle="modal"
                                data-bs-target="#reviewModal${booking.id}">
                            <spring:message code="client.review"/>
                        </button>
                    </c:if>
                </c:if>
            </div>
            <c:choose>
                <c:when test="${booking.driver.pfp eq null}">
                    <img src="${pageContext.request.contextPath}/images/defaultUserPfp.png"
                         alt="Driver Profile Picture" class="rounded-circle"
                         style="width: 60px; height: 60px;"/>
                </c:when>
                <c:otherwise>
                    <img src="${pageContext.request.contextPath}/user/pfp?userPfp=${booking.driver.pfp}"
                         alt="DriverPfp" class="rounded-circle" style="width: 60px; height: 60px;"/>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</div>

<c:if test="${booking.state eq BookingState.FINISHED and booking.rating eq null}">
    <div class="modal fade" id="reviewModal${booking.id}" tabindex="-1"
         aria-labelledby="reviewModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="reviewModalLabel"><c:out
                            value="${booking.date}"/></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                            aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form:form modelAttribute="bookingReviewForm" method="post"
                               action="/client/history/send/review">
                        <form:input type="hidden" path="bookingID" value="${booking.id}"/>
                        <div class="mb-3">
                            <div class="star-rating d-flex justify-content-evenly" id="starRating${booking.id}">
                                <span class="star" data-value="1">&#9733;</span>
                                <span class="star" data-value="2">&#9733;</span>
                                <span class="star" data-value="3">&#9733;</span>
                                <span class="star" data-value="4">&#9733;</span>
                                <span class="star" data-value="5">&#9733;</span>
                                <input type="number" name="rating" id="rating${booking.id}" required>
                            </div>
                        </div>
                        <div class="mb-3">
                            <form:label path="review" cssClass="form-label">
                                <spring:message code="client.make.review" arguments="${booking.driver.username}"/>
                            </form:label>
                            <form:input path="review" type="text" required="true" minlength="6" maxlength="255"/>
                            <form:errors path="review" cssClass="text-danger" element="p"/>
                            <form:errors element="div" cssClass="alert alert-danger"/>
                        </div>
                        <button type="submit" class="btn btn-primary mt-3">
                            <spring:message code="generic.word.confirm"/>
                        </button>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</c:if>
<comp:BookingModal booking="${booking}" currentDate="${currentDate}" loggedUser="${loggedUser}"/>