<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<comp:Head titleCode="components.header.availability" tomselect="true" bsIcons="true">
    <c:url value="/css/styles.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <c:url value="/js/client/availability.js" var="js"/>
    <script src="${js}"></script>
</comp:Head>

<body>
<comp:Header inAvailability="true"/>
<div class="container mt-4">
    <c:url var="postUrl" value="/client/availability"/>
    <form:form action="${postUrl}" method="get" modelAttribute="availabilitySearchForm">
        <div class="row g-3 mb-4">
            <div class="col-sm-7">
                <spring:message code="driver.add_availability.selectZones" var="selectZones"/>
                <spring:bind path="zoneId">
                    <form:select path="zoneId" id="select-zones" multiple="false"
                                 placeholder="${selectZones}..." autocomplete="off"
                                 cssClass="form-select ${status.error ? 'is-invalid' : ''}"
                    >
                        <form:options items="${zones}" itemValue="id"/>
                    </form:select>
                </spring:bind>
            </div>
            <div class="col-sm">
                <spring:message code="generic.word.size" var="sizeLabel"/>
                <form:select path="size" id="select-size" cssClass="form-control">
                    <form:option value="" label="${sizeLabel}"/>
                    <spring:message var="small" code="generic.word.small"/>
                    <form:option value="SMALL" label="${small}"/>
                    <spring:message var="medium" code="generic.word.medium"/>
                    <form:option value="MEDIUM" label="${medium}"/>
                    <spring:message var="large" code="generic.word.large"/>
                    <form:option value="LARGE" label="${large}"/>
                </form:select>
            </div>
        </div>

        <div class="row g-3">
            <div class="col-sm-2">
                <input type="number" step="0.01" min="0" name="priceMax" placeholder="Max Price" class="form-control" value="${priceMax}"/>
            </div>

            <div class="col-sm-3">
                <spring:message code="client.search.selectWeekday" var="weekdayLabel"/>
                <form:select path="weekday" id="select-weekday" cssClass="form-select">
                    <form:option value="" label="${weekdayLabel}"/>
                    <form:option value="MONDAY" label="Monday"/>
                    <form:option value="TUESDAY" label="Tuesday"/>
                    <form:option value="WEDNESDAY" label="Wednesday"/>
                    <form:option value="THURSDAY" label="Thursday"/>
                    <form:option value="FRIDAY" label="Friday"/>
                    <form:option value="SATURDAY" label="Saturday"/>
                    <form:option value="SUNDAY" label="Sunday"/>
                </form:select>
            </div>

            <div class="col-sm-2">
                <spring:message code="client.search.selectRating" var="ratingLabel"/>
                <form:select path="rating" id="select-rating" cssClass="form-select">
                    <form:option value="" label="${ratingLabel}"/>
                    <form:option value="3" label="3"/>
                    <form:option value="4" label="4"/>
                    <form:option value="5" label="5"/>
                </form:select>
            </div>

            <div class="col-sm-3">
                <spring:message code="client.search.selectOrder" var="weekdayLabel"/>
                <form:select path="order" id="select-weekday" cssClass="form-select">
                    <form:option value="" label="${weekdayLabel}"/>
                    <form:option value="ALPHABETICAL" label="Alphetical"/>
                    <form:option value="RECENT" label="Most Recent"/>
                    <form:option value="PRICE" label="Lowest Price"/>
                    <form:option value="RATING" label="Highest Rating"/>
                </form:select>
            </div>

            <div class="col-sm-2 d-flex align-items-center">
                <button type="submit" class="btn btn-primary w-100">
                    <spring:message code="components.availability.Search"/>
                </button>
            </div>
        </div>
    </form:form>

    <div class="d-flex justify-content-center">
        <c:choose>
            <c:when test="${empty drivers}">
                <p class="text-center mt-4"><spring:message code="availability.posts.noPosts"/></p>
            </c:when>
            <c:otherwise>
                <div class="container">
                    <div class="row row-cols-1 row-cols-md-3 g-4 mt-0">
                        <c:forEach var="driver" items="${drivers}" varStatus="status">
                            <div class="col mb-4">
                                <div class="card h-100">
                                    <div class="card-body d-flex justify-content-between">
                                        <div>
                                        <h5 class="card-title"><c:out value="${driver.username}"/></h5>
                                        <p class="card-text"><c:out value="${driver.description}"/></p>
                                        <div class="d-flex align-items-center">

                                            <c:choose>
                                                <c:when test="${driver.rating != null}">

                                                    <span class="fw-bold text-warning">
                                                           <fmt:formatNumber value="${driver.rating}" type="number" maxFractionDigits="2" />
                                                    </span>


                                                    <div class="ms-2">
                                                        <c:set var="fullStars" value="${driver.rating.intValue()}"/>
                                                        <c:set var="halfStar"
                                                               value="${(driver.rating - driver.rating.intValue() >= 0.5) ? true : false}"/>
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
                                        <a href="${pageContext.request.contextPath}/client/availability/${driver.id}?zoneId=${zoneId}&size=${size}&priceMax=${priceMax}&weekday=${weekday}&rating=${rating}&order=${order}&page=${currentPage}"
                                           class="btn btn-primary">
                                            <spring:message code="components.availability.SeeAvailability"/>
                                        </a>
                                        </div>
                                        <c:choose>
                                            <c:when test="${driver.pfp eq null}">
                                                <img src="${pageContext.request.contextPath}/images/defaultUserPfp.png" alt="Driver Profile Picture" class="rounded-circle object-fit: cover" style="width: 60px; height: 60px;"/>
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/user/pfp?userPfp=${driver.pfp}" alt="DriverPfp" class="rounded-circle object-fit: cover" style="width: 60px; height: 60px;"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${currentPage - 1}&zoneId=${zoneId}&size=${size}&priceMax=${priceMax}&weekday=${weekday}&rating=${rating}&order=${order}" tabindex="-1" aria-disabled="${currentPage == 0}">&laquo; <spring:message code="generic.word.previous"/></a>
                                </li>
                                <c:forEach begin="0" end="${totalPages - 1}" var="i">
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <a class="page-link" href="?page=${i}&zoneId=${zoneId}&size=${size}&priceMax=${priceMax}&weekday=${weekday}&rating=${rating}&order=${order}">${i + 1}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${currentPage + 1}&zoneId=${zoneId}&size=${size}&priceMax=${priceMax}&weekday=${weekday}&rating=${rating}&order=${order}" aria-disabled="${currentPage == totalPages - 1}"><spring:message code="generic.word.next"/>&raquo;</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>