<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <title><spring:message code="siteName"/></title>
    <jsp:include page="../lib/bootstrap_css.jsp"/>
    <jsp:include page="../lib/bootstrap_js.jsp"/>
    <jsp:include page="../lib/popper.jsp"/>
    <%--    Why doesn't this work?--%>
    <%--    <c:url value="/css/styles.css" var="css"/>--%>
    <%--    <link rel="stylesheet" href="${css}">--%>
    <style>
        <%@ include file="/css/styles.css" %>
    </style>
</head>

<body>
<comp:header/>
<div class="container mt-2 p-3 border border-primary rounded">
    <h2 class="mb-4"><spring:message code="driver.add_availability.title"/></h2>
    <c:url var="postUrl" value="/driver/${driverId}/availability/add"/>
    <form:form action="${postUrl}" method="post" modelAttribute="availabilityForm">

        <div class="mb-3">
            <comp:WeekDaySelector path="weekDays"/>
            <form:errors path="weekDays" element="p" cssStyle="color: red"/>
        </div>

        <div class="mb-3">
            <label class="form-label"><spring:message code="driver.add_availability.timeRange"/></label>
            <div class="row">
                <div class="col">
                    <form:input path="timeStart" cssClass="form-control" type="time"/>
                    <form:errors path="timeStart" element="p" cssStyle="color: red"/>
                </div>
                <div class="col">
                    <form:input path="timeEnd" cssClass="form-control" type="time"/>
                    <form:errors path="timeEnd" element="p" cssStyle="color: red"/>
                </div>
                <form:errors element="div" cssClass="alert alert-danger"/>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label"><spring:message code="driver.add_availability.selectVehicles"/></label>
            <ul class="list-group mh-25 overflow-auto">
                <c:choose>
                    <c:when test="${vehicles.isEmpty()}">
                        <a class="btn btn-primary"
                           href="${pageContext.request.contextPath}/driver/${driverId}/vehicle/add"
                           role="button"><spring:message code="driver.add_availability.noVehicles"/></a>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="zone" items="${vehicles}">
                            <li class="list-group-item">
                                <form:checkbox path="vehicleIds" class="form-check-input me-1" value="${zone.id}"
                                               id="vehicle_${zone.id}"/>
                                <label class="form-check-label" for="vehicle_${zone.id}">${zone.plateNumber}</label>
                            </li>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </ul>
            <form:errors path="vehicleIds" element="p" cssStyle="color: red"/>
        </div>

        <div class="mb-3">
            <label class="form-label"><spring:message code="driver.add_availability.selectZones"/></label>
            <ul class="list-group mh-25 overflow-auto">
                <c:forEach var="zone" items="${zones}">
                    <li class="list-group-item">
                        <form:checkbox path="zoneIds" class="form-check-input me-1" value="${zone.id}"
                                       id="zone_${zone.id}"/>
                        <label class="form-check-label" for="zone_${zone.id}">
                                ${zone.provinceName} - ${zone.neighborhoodName}
                        </label>
                    </li>
                </c:forEach>
            </ul>
            <form:errors path="vehicleIds" element="p" cssStyle="color: red"/>
        </div>

        <div>
            <input type="submit">
        </div>

    </form:form>
</div>
</body>

</html>
