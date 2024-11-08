<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<comp:Head titleCode="siteName">
</comp:Head>
<body class="d-flex flex-column min-vh-100">
<comp:Header/>

<div class="container mt-5">
    <div class="card">
        <div class="card-header text-center">
            <h1><c:out value="${loggedUser.username}"/></h1>
        </div>
        <div class="card-body">
            <div class="text-center mb-4">
                <form id="uploadProfilePicForm" method="post" action="<c:url value='/upload/pfp'/>"
                      enctype="multipart/form-data">
                    <input type="file" id="profilePicInput" name="profilePicture" class="d-none"
                           accept="image/png, image/jpeg"
                           onchange="document.getElementById('uploadProfilePicForm').submit();">
                    <label for="profilePicInput" style="cursor: pointer;">
                        <c:choose>
                            <c:when test="${loggedUser.pfp ne null}">
                                <img src="<c:url value='/profile/picture'/>" alt="Profile Picture"
                                     class="rounded-circle border" width="150" height="150">
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/images/defaultUserPfp.png"
                                     alt="No Profile Picture" class="rounded-circle border mb-2" width="150"
                                     height="150">
                            </c:otherwise>
                        </c:choose>
                    </label>
                </form>
            </div>

            <div class="user-info text-center">
                <p class="mb-1"><strong><spring:message code="generic.word.email"/>: </strong> <c:out
                        value="${loggedUser.mail}"/></p>
                <c:set var="userPath" value="${loggedUser.isDriver ? 'driver' : 'client'}"/>
                <c:choose>
                    <c:when test="${loggedUser.isDriver}">
                            <p class="mb-1"><strong><spring:message code="generic.word.description"/>: </strong>
                                <c:if test="${loggedUser.description ne null}">
                                    <c:out value="${loggedUser.description}"/>
                                </c:if>
                                <c:if test="${loggedUser.description eq null}">
                                    <spring:message code="public.profile.noDescription"/>
                                </c:if>
                            </p>
                        <p class="mb-1"><strong><spring:message code="generic.word.cbu"/>: </strong>
                            <c:if test="${loggedUser.cbu ne null}">
                                <c:out value="${loggedUser.cbu}"/>
                            </c:if>
                            <c:if test="${loggedUser.cbu eq null}">
                                <spring:message code="public.profile.noCbu"/>
                            </c:if>
                        </p>
                    </c:when>
                    <c:otherwise>
                        <p class="mb-1"><strong><spring:message code="client.profile.zone"/>: </strong>
                            <c:if test="${loggedUser.zone ne null}">
                                This is where I'd put the client zone if I had it
                            </c:if>
                        </p>
                    </c:otherwise>
                </c:choose>
                <div class="d-flex justify-content-end mt-4">
                    <a href="<c:url value='/${userPath}/profile/edit'/>" class="btn btn-primary"><spring:message
                            code="generic.word.edit"/></a>
                </div>
                <div class="d-flex justify-content-end mt-4">
                    <a href="<c:url value='/${userPath}/change/password'/>" class="btn btn-primary"><spring:message
                            code="user.editUser.changePassword"/></a>
                </div>
            </div>
        </div>
    </div>
</div>

<comp:ToastManager toasts="${toasts}"/>

</body>
</html>
