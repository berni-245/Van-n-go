<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<comp:Head titleCode="siteName"/>
<body class="d-flex flex-column min-vh-100">
<comp:Header/>

<div class="container my-4">
    <!-- Chat Header -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>${recipient.username}</h2>
        <a href="${pageContext.request.contextPath}/driver/bookings" class="btn btn-secondary">
            <spring:message code="generic.phrase.goBack"/>
        </a>
    </div>

    <div class="row">
        <!-- Chat Column -->
        <div class="col-lg-8">
            <!-- Chat Messages Area -->
            <div class="chat-box border rounded p-3 mb-3 d-flex flex-column-reverse" style="height: 60vh; overflow-y: auto;">
                <c:forEach var="message" items="${messages}">
                    <div class="d-flex <c:choose><c:when test="${message.sentByDriver}">justify-content-start</c:when><c:otherwise>justify-content-end</c:otherwise></c:choose> mb-2">
                        <div class="message p-2 rounded text-dark"
                             style="background-color: <c:choose><c:when test="${message.sentByDriver}">#d1e7dd</c:when><c:otherwise>#f8d7da</c:otherwise></c:choose>;
                                     color: #212529; word-break: break-word;">
                            <p class="mb-1">${message.content}</p>
                            <small class="text-dark" style="color: #212529;">
                                    ${message.timeSent.toLocalTime().toString().substring(0,5)}
                            </small>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Message Sending Form -->
            <form action="<c:url value='/driver/send'/>" method="POST" class="d-flex">
                <input type="hidden" name="recipientId" value="${recipient.id}"/>
                <input type="text" name="content" class="form-control me-2" placeholder="Type a message..." required maxlength="255"/>
                <button type="submit" class="btn btn-primary"><spring:message code="generic.word.send"/></button>
            </form>
        </div>

        <!-- Recipient Info Card Column -->
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
                         class="rounded-circle me-2" width="120" height="120">
                </div>
                <div class="card-body">
                    <p><strong><spring:message code="generic.word.username"/>:</strong> ${recipient.username}</p>
                    <p><strong><spring:message code="generic.word.email"/>:</strong> ${recipient.mail}</p>
                    <p><strong><spring:message code="generic.word.zone"/>:</strong> ${recipient.creationTime}</p>
                    <p><strong><spring:message code="generic.word.created"/>:</strong> ${recipient.creationTime}</p>
                    <!-- Add any other recipient information as needed -->
                </div>
            </div>
        </div>
    </div>
</div>

<comp:ToastManager toasts="${toasts}"/>
</body>
</html>