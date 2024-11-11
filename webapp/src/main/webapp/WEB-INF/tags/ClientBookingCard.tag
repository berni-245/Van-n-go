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
    <div class="card mb-3 shadow h-100">
        <c:choose>
            <c:when test="${booking.driver.pfp eq null}">
                <img src="${pageContext.request.contextPath}/images/defaultUserPfp.png"
                     alt="Driver Profile Picture" style="width: 60px; height: 60px;"
                     class="rounded-circle position-absolute top-0 end-0 mt-2 me-2"
                />
            </c:when>
            <c:otherwise>
                <img src="${pageContext.request.contextPath}/user/pfp?userPfp=${booking.driver.pfp}"
                     alt="DriverPfp" style="width: 60px; height: 60px;"
                     class="rounded-circle position-absolute top-0 end-0 mt-2 me-2"
                />
            </c:otherwise>
        </c:choose>
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
                                       class="d-none" accept="application/pdf, image/png, image/jpeg, image/webp, image/heic"
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
                <c:if test="${booking.state eq BookingState.PENDING}">
                    <p><spring:message code="client.bookings.bookingUnconfirmed"/></p>
                </c:if>
            </div>
            <div class="d-flex justify-content-between">
                <button type="button" class="btn btn-primary" data-bs-toggle="modal"
                        data-bs-target="#bookingModal${booking.id}">
                    <spring:message code="generic.phrase.seeDetails"/>
                </button>
                <a href="${pageContext.request.contextPath}/client/chat?bookingId=${booking.id}&recipientId=${booking.driver.id}" type="button" class="btn btn-secondary">
                    <spring:message code="generic.word.chat"/>
                </a>
            </div>
        </div>
    </div>
</div>


<comp:BookingModal booking="${booking}" currentDate="${currentDate}" loggedUser="${loggedUser}"/>