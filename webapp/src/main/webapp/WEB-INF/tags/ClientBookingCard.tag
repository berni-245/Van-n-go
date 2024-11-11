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
                <img src="${pageContext.request.contextPath}/images/query?imgId=${booking.driver.pfp}"
                     alt="DriverPfp" style="width: 60px; height: 60px;"
                     class="rounded-circle position-absolute top-0 end-0 mt-2 me-2"
                />
            </c:otherwise>
        </c:choose>
        <div class="card-body d-flex flex-column justify-content-between h-100">
            <div>
                <h5 class="card-title w-75"><c:out value="${booking.clientFormattedDate}"/></h5>
                <p class="card-text"><c:out value="${booking.driver.username}"/></p>
                <p class="card-text"><c:out value="${booking.driver.mail}"/></p>
                <c:if test="${booking.state eq BookingState.ACCEPTED}">
                    <p><spring:message code="driver.home.booking.confirmed"/></p>
                    <c:choose>
                        <c:when test="${empty booking.pop or booking.pop == 0}">
                            <spring:message code="client.bookings.transfer" arguments="${booking.driver.cbu}" var="cbu"/>
                            <c:out value="${cbu}"/>
                            <form id="uploadProofOfPaymentForm_${booking.id}" method="post"
                                  action="<c:url value='/client/bookings/upload/pop'/>"
                                  enctype="multipart/form-data">
                                <input type="hidden" name="bookingId" value="${booking.id}">
                                <input type="hidden" name="driverId" value="${booking.driver.id}">
                                <input type="file" id="proofInput_${booking.id}" name="proofOfPayment"
                                       class="d-none"
                                       accept="application/pdf, image/png, image/jpeg, image/webp, image/heic"
                                       onchange="document.getElementById('uploadProofOfPaymentForm_${booking.id}').submit();">
                                <label for="proofInput_${booking.id}"
                                       style="cursor: pointer; text-decoration: underline;">
                                    <spring:message code="client.bookings.clickHereToPop"/>
                                </label>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:url value='/images/query?imgId=${booking.pop}' />"
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
                <comp:SeeDetailsBtutton targetId="bookingModal${booking.id}"
                                        tooltipCode="generic.phrase.seeDetails"/>
                <comp:ChatButton path="/client/chat?bookingId=${booking.id}&recipientId=${booking.driver.id}"
                                 tooltipCode="generic.word.chat"/>
            </div>
        </div>
    </div>
</div>


<comp:BookingModal booking="${booking}" currentDate="${currentDate}" loggedUser="${loggedUser}"/>