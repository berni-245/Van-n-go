<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="booking" required="true" type="ar.edu.itba.paw.models.Booking" %>
<%@ attribute name="loggedUser" required="true" type="ar.edu.itba.paw.models.User" %>
<%@ tag import="ar.edu.itba.paw.models.BookingState" %>


<c:choose>
    <c:when test="${loggedUser.isDriver}">
        <c:if test="${booking.pop eq null}">
            <li class="list-group-item"><spring:message code="driver.home.unpaid"/></li>
        </c:if>
        <c:if test="${booking.pop ne null}">
            <c:url value='/booking/pop?popId=${booking.pop}' var="popUrl"/>
            <li class="list-group-item"><a href="${popUrl}" target="_blank">
                <spring:message code="driver.home.paid"/>
            </a></li>
        </c:if>

    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${empty booking.pop or booking.pop == 0}">
                <c:if test="${booking.driver.cbu == null}">
                    <li class="list-group-item"><p><spring:message code="public.profile.noCbu"/></p></li>
                </c:if>
                <c:if test="${booking.driver.cbu != null}">
                    <li class="list-group-item"><spring:message code="client.bookings.transfer" arguments="${booking.driver.cbu}"/></li>
                </c:if>
                <li class="list-group-item">
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
                </li>
            </c:when>
            <c:otherwise>
                <li class="list-group-item"><a
                        href="<c:url value='/booking/pop?popId=${booking.pop}' />" target="_blank">
                    <spring:message code="client.bookings.popProvided"/>
                </a></li>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>