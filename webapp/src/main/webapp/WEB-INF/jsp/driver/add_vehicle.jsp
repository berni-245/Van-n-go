<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<comp:Head titleCode="components.header.vehicles" tomselect="true"/>

<body>
<comp:Header/>
<div class="d-flex align-items-start mb-5">
    <div class="me-3">
<comp:GoBackButton path="/driver/vehicles"/>
    </div>
        <div class="col-md-7 offset-md-2">
            <div class="d-flex align-items-center mb-4">
                <h2 class="mb-0 mt-2 ms-3 text-center">
                    <spring:message code="driver.add_vehicle.title" arguments="${loggedUser.username}" var="username"/>
                    <c:out value="${username}"/>
                </h2>
            </div>
            <div class="card p-4">
                <comp:VehicleForm action="/driver/vehicle/add" modelAttribute="vehicleForm"/>
            </div>
        </div>
</div>
</body>

</html>