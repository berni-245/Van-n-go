<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<comp:Head titleCode="components.header.vehicles"/>

<body>
<comp:Header/>
<div class="container mt-4">
    <h2><spring:message code="driver.edit_vehicle.title" arguments="${loggedUser.username}"/></h2>
    <comp:VehicleForm action="/driver/vehicle/edit" modelAttribute="vehicleForm">
        <form:input type="hidden" path="id" cssClass="form-control"/>
        <input type="hidden" name="ogPlateNumber" value="${plateNumber}">
    </comp:VehicleForm>
</div>
</body>

</html>