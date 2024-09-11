<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Posts</title>
    <%@include file="../lib/bootstrap_css.jsp" %>
    <%@include file="../lib/bootstrap_js.jsp" %>
    <c:url value="${pageContext.request.contextPath}/css/availability_styles.css" var="css"/>
    <link rel="stylesheet" href="${css}">

</head>
<body>
<comp:header/>
<main>
    <div class="container">
        <div class="d-flex justify-content-center">
            <c:choose>
                <c:when test="${drivers.isEmpty()}">
                    <p><spring:message code="availability.posts.noPosts" /></p>
                </c:when>
                <c:otherwise>
                    <div class="nav flex-column nav-pills me-3" id="v-pills-tab" role="tablist"
                         aria-orientation="vertical">
                        <c:forEach items="${drivers}" var="driver">
                            <button class="availability-button nav-link text-truncate border" style="max-width: 300px;" id="${driver.id}-tab"
                                    data-bs-toggle="pill" data-bs-target="#${driver.id}"
                                    type="button" role="tab" aria-controls="${driver.id}" aria-selected="false">
                                <c:out value="${driver.username}"/>
                            </button>
                        </c:forEach>
                    </div>
                    <div class="tab-content flex-grow-1 px-2" id="v-pills-tabContent">
                        <c:forEach var="driver" items="${drivers}" varStatus="status">
                            <c:set var="vehicles" value="${vehicleLists[status.index]}"/>
                            <c:set var="availability" value="${availabilityLists[status.index]}"/>
                            <div class="tab-pane fade show" id="${driver.id}" role="tabpanel"
                                 aria-labelledby="v-pills-home-tab"
                                 tabindex="${driver.id}">
                                <div class="card">
                                    <div class="card-body">
                                        <div class="card-title d-flex align-items-center justify-content-between">
                                            <div class="three-line-truncate">
                                                <c:out value="${driver.username}"/>
                                            </div>
                                            <a class="btn availability-button border"
                                               href="${pageContext.request.contextPath}/driver/${driver.id}/vehicle/add"
                                               role="button"><spring:message code="availability.add.vehicle"/></a>
                                            <a class="btn availability-button border"
                                               href="${pageContext.request.contextPath}/driver/${driver.id}/availability/add"
                                               role="button"><spring:message code="availability.add.availability"/></a>
                                        </div>
                                        <p class="card-text three-line-truncate">
                                            <spring:message code="generic.word.description"/>: <c:out value="${driver.extra1}"/>
                                        </p>

                                        <div>
                                            <p>Horarios:</p>
                                            <c:forEach var="av" items="${availability}">
                                                <p class="card-text text-body-secondary">
                                                    <c:out value="${av.weekDayString}"/> |
                                                    <c:out value="${av.timeStart}"/> to <c:out value="${av.timeEnd}"/> |
                                                    <c:out value="${vehicles[av.vehicleId].plateNumber} - volumen: ${vehicles[av.vehicleId].volume}m^3"/>
                                                    |
                                                    <c:out value="${zones[av.zoneId].neighborhoodName}"/>
                                                </p>
                                            </c:forEach>
                                        </div>

                                        <p>
                                            Contacto: ${driver.mail}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>
</body>
</html>
