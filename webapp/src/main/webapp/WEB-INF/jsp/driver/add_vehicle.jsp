<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <jsp:include page="../components/bootsrap.jsp"/>
</head>

<body>
<div class="container">
    <h2><spring:message code="driver.add_vehicle.title" arguments="${username}"/></h2>
    <c:url var="postUrl" value="/driver/${driverId}/vehicle/add"/>
    <form:form action="${postUrl}" method="post" modelAttribute="vehicleForm">
        <div class="mb-3">
            <label class="form-label">
                <spring:message code="driver.add_vehicle.plateNumber"/>
                <form:input path="plateNumber" cssClass="form-control"/>
            </label>
            <form:errors path="plateNumber" element="p" cssStyle="color: red"/>
        </div>
        <div>
            <label class="form-label">
                <spring:message code="driver.add_vehicle.volume"/>
                <form:input path="volume" cssClass="form-control"/>
            </label>
            <form:errors path="volume" element="p" cssStyle="color: red"/>
        </div>
        <div>
            <label class="form-label">
                <spring:message code="driver.add_vehicle.description"/>
                <form:input path="description" cssClass="form-control"/>
            </label>
            <form:errors path="description" element="p" cssStyle="color: red"/>
        </div>
        <div>
            <input type="submit">
        </div>
    </form:form>
</div>
</body>

</html>