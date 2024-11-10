<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<comp:Head titleCode="siteName" goBack="true"/>
<body class="d-flex flex-column min-vh-100">
<comp:Header/>

<comp:GoBackButton/>

<c:choose>
    <c:when test="${loggedUser.pfp ne null}">
        <c:url var="driverPfpUrl" value="/user/pfp?userPfp=${loggedUser.pfp}"/>
    </c:when>
    <c:otherwise>
        <c:url var="driverPfpUrl" value="/images/defaultUserPfp.png"/>
    </c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${recipient.pfp ne null}">
        <c:url var="clientPfpUrl" value="/user/pfp?userPfp=${recipient.pfp}"/>
    </c:when>
    <c:otherwise>
        <c:url var="clientPfpUrl" value="/images/defaultUserPfp.png"/>
    </c:otherwise>
</c:choose>

<div class="container my-4">
    <div class="row">
        <div class="col-lg-8">
            <div class="chat-box border rounded p-3 mb-3 d-flex flex-column-reverse" style="height: 60vh; overflow-y: auto;">
                <c:forEach var="message" items="${messages}">
                    <div class="d-flex <c:choose><c:when test="${!message.sentByDriver}">justify-content-end</c:when><c:otherwise>justify-content-start</c:otherwise></c:choose> mb-2 align-items-end">
                        <c:choose>
                            <c:when test="${!message.sentByDriver}">
                                <div class="message p-2 rounded text-dark"
                                     style="background-color: #d1e7dd; color: #212529; word-break: break-word; max-width: 70%; font-size: 1.1em;">
                                    <p class="mb-1"><c:out value="${message.content}"/></p>
                                    <small class="text-dark" style="color: #212529;">
                                            ${message.timeSent.toLocalDate().toString().substring(8,10)}/${message.timeSent.toLocalDate().toString().substring(5,7)} ${message.timeSent.toLocalTime().toString().substring(0,5)}
                                    </small>
                                </div>

                                <div class="text-end ms-2">
                                    <small class="d-block text-muted">${recipient.username}</small>
                                    <img src="${clientPfpUrl}" class="rounded-circle" alt="Profile Picture" style="width: 64px; height: 64px;">
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-start me-2">
                                    <small class="d-block text-muted">${loggedUser.username}</small>
                                    <img src="${driverPfpUrl}" class="rounded-circle" alt="Profile Picture" style="width: 64px; height: 64px;">
                                </div>

                                <div class="message p-2 rounded text-dark"
                                     style="background-color: #f8d7da; color: #212529; word-break: break-word; max-width: 70%; font-size: 1.1em;">
                                    <p class="mb-1"><c:out value="${message.content}"/></p>
                                    <small class="text-dark" style="color: #212529;">
                                            ${message.timeSent.toLocalDate().toString().substring(8,10)}/${message.timeSent.toLocalDate().toString().substring(5,7)} ${message.timeSent.toLocalTime().toString().substring(0,5)}
                                    </small>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </div>

            <form action="<c:url value='/driver/send'/>" method="POST" class="d-flex">
                <input type="hidden" name="recipientId" value="${recipient.id}"/>
                <input type="hidden" name="bookingId" value="${booking.id}" />
                <input type="text" name="content" class="form-control me-2" placeholder="<spring:message code="generic.type.message"/>" required maxlength="255"/>
                <button type="submit" class="btn btn-primary"><spring:message code="generic.word.send"/></button>
            </form>
        </div>

        <div class="col-lg-4">
            <div class="card">
                <div class="card-header">
                    <c:choose>
                        <c:when test="${recipient.pfp ne null}">
                            <c:url value='/user/pfp?userPfp=${recipient.pfp}' var="recipientPfpUrl"/>
                        </c:when>
                        <c:otherwise>
                            <c:url value='/images/defaultUserPfp.png' var="recipientPfpUrl"/>
                        </c:otherwise>
                    </c:choose>
                    <img src="${recipientPfpUrl}" alt="Profile Picture"
                         class="rounded-circle me-2; object-fit: cover;" width="120" height="120">
                </div>
                <div class="card-body">
                    <p>
                        <strong><spring:message code="generic.word.username"/>:</strong>
                        <c:out value="${recipient.username}"/>
                    </p>
                    <p>
                        <strong><spring:message code="generic.word.email"/>:</strong>
                        <c:out value="${recipient.mail}"/>
                    </p>
                    <p>
                        <strong><spring:message code="generic.word.zone"/>:</strong>
                        <c:out value="${clientZone}"/>
                    </p>
                    <p>
                        <strong><spring:message code="generic.word.created"/>:</strong>
                        <c:out value="${recipient.creationTime.toLocalDate()}"/>
                    </p>
                    <p>
                        <strong><spring:message code="generic.booking.date"/>:</strong>
                        <c:out value="${booking.date}"/>
                    </p>
                    <p>
                        <strong><spring:message code="generic.booking.shift.period"/>:</strong>
                        <spring:message code="generic.word.${booking.shiftPeriod.capitalizedText}"/>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<comp:ToastManager toasts="${toasts}"/>
</body>
</html>