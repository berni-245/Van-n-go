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
    <jsp:include page="../lib/tom_select.jsp"/>
    <c:url value="/css/weekdaySelector.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <style>
        .ts-wrapper .option .title {
            display: block;
        }

        .ts-wrapper .option .description {
            font-size: 14px;
            display: block;
            color: #a0a0a0;
        }
    </style>
</head>

<body>
<comp:header/>
<div class="container mt-2 p-3 border border-primary rounded">
    <h2 class="mb-4"><spring:message code="driver.add_availability.title"/></h2>
    <c:url var="postUrl" value="/driver/${driverId}/availability/add"/>
    <form:form action="${postUrl}" method="post" modelAttribute="availabilityForm">

        <div class="mb-3">
            <comp:WeekdaySelector path="weekDays"/>
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
            <c:choose>
                <c:when test="${vehicles.isEmpty()}">
                    <a class="btn btn-primary"
                       href="${pageContext.request.contextPath}/driver/vehicle/add"
                       role="button"><spring:message code="driver.add_availability.noVehicles"/></a>
                </c:when>
                <c:otherwise>
                    <spring:message code="driver.add_availability.selectVehicles" var="selectVehicles"/>
                    <form:select path="vehicleIds" id="select-vehicles" multiple="true"
                                 placeholder="${selectVehicles}..." autocomplete="off"
                    >
                        <form:options items="${vehicles}" itemValue="id"/>
                    </form:select>
                </c:otherwise>
            </c:choose>
            <form:errors path="vehicleIds" element="p" cssStyle="color: red"/>
        </div>


        <div class="mb-3">
            <spring:message code="driver.add_availability.selectZones" var="selectZones"/>
            <form:select path="zoneIds" id="select-zones" multiple="true"
                         placeholder="${selectZones}..." autocomplete="off"
            >
                <form:options items="${zones}" itemValue="id"/>
            </form:select>
            <form:errors path="zoneIds" element="p" cssStyle="color: red"/>
        </div>

        <div>
            <input type="submit">
        </div>

    </form:form>
</div>
<script>
    new TomSelect("#select-zones");
    new TomSelect("#select-vehicles", {
        render: {
            option: function (data, escape) {
                const [plateNumber, description, volume] = data.text.split('||');
                return '<div>' +
                    '<span class="title">' +
                    escape(plateNumber) + ' - ' + escape(volume) + ' m³' +
                    '</span>' +
                    '<span class="description">' + escape(description) + '</span>' +
                    '</div>';
            },
            item: function (data, escape) {
                const [plateNumber] = data.text.split('||');
                return '<div>' + escape(plateNumber) + '</div>';
            }
        }
    });
</script>
</body>

</html>
