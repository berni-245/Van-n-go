<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<comp:Head titleCode="components.header.profile" bsIcons="true">
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
                           accept="image/png, image/jpeg, image/webp, image/heic"
                           onchange="document.getElementById('uploadProfilePicForm').submit();">
                    <label for="profilePicInput" style="cursor: pointer;">
                        <c:choose>
                            <c:when test="${loggedUser.pfp ne null}">
                                <img src="<c:url value='/user/pfp?userPfp=${loggedUser.pfp}'/>" alt="Profile Picture"
                                     class="rounded-circle border object-fit: cover" width="150" height="150">
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/images/defaultUserPfp.png"
                                     alt="No Profile Picture object-fit: cover" class="rounded-circle border mb-2" width="150"
                                     height="150">
                            </c:otherwise>
                        </c:choose>
                    </label>
                </form>
            </div>

            <div class="user-info text-center">
                <p class="mb-1"><strong><spring:message code="generic.word.email"/>: </strong> <c:out
                        value="${loggedUser.mail}"/></p>
                <c:choose>
                    <c:when test="${loggedUser.isDriver}">
                        <p class="mb-1"><strong><spring:message code="generic.word.description"/>: </strong>
                            <c:choose>
                                <c:when test="${loggedUser.description ne null}">
                                    <c:out value="${loggedUser.description}"/>
                                </c:when>
                                <c:otherwise>
                                    <spring:message code="public.profile.noDescription"/>
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <p class="mb-1"><strong><spring:message code="generic.word.cbu"/>: </strong>
                            <c:choose>
                            <c:when test="${loggedUser.cbu ne null}">
                                <c:out value="${loggedUser.cbu}"/>
                            </c:when>
                            <c:otherwise>
                                <spring:message code="public.profile.noCbu"/>
                            </c:otherwise>
                            </c:choose>
                        </p>

                        <div>

                            <c:choose>
                                <c:when test="${loggedUser.rating ne null}">
                                    <span class="fw-bold text-warning">
                                       <fmt:formatNumber value="${loggedUser.rating}" type="number"
                                                         maxFractionDigits="2"/>
                                    </span>


                                    <div class="ms-2">
                                        <c:set var="fullStars" value="${loggedUser.rating.intValue()}"/>
                                        <c:set var="halfStar"
                                               value="${(loggedUser.rating - loggedUser.rating.intValue() >= 0.5) ? true : false}"/>
                                        <c:set var="emptyStars"
                                               value="${5 - fullStars - (halfStar ? 1 : 0)}"/>


                                        <c:forEach var="i" begin="1" end="${fullStars}">
                                            <i class="bi bi-star-fill text-warning"></i>
                                        </c:forEach>


                                        <c:if test="${halfStar}">
                                            <i class="bi bi-star-half text-warning"></i>
                                        </c:if>


                                        <c:forEach var="i" begin="1" end="${emptyStars}">
                                            <i class="bi bi-star text-secondary"></i>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <span><spring:message code="client.availability.no_rating"/></span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                    </c:when>
                    <c:otherwise>
                        <c:if test="${loggedUser.zone ne null}">
                            <p class="mb-1"><strong><spring:message code="generic.word.zone"/>: </strong>
                                <c:out value="${clientZone.toString()}"/>
                            </p>
                        </c:if>
                    </c:otherwise>
                </c:choose>
                <div class="d-flex justify-content-end mt-4">
                    <a href="<c:url value='/${loggedUser.type}/profile/edit'/>" class="btn btn-primary"><spring:message
                            code="generic.word.edit"/></a>
                </div>
                <div class="d-flex justify-content-end mt-4">
                    <a href="<c:url value='/${loggedUser.type}/change/password'/>"
                       class="btn btn-primary"><spring:message
                            code="user.editUser.changePassword"/></a>
                </div>
            </div>
        </div>
    </div>
</div>

<comp:ToastManager toasts="${toasts}"/>

</body>
</html>
