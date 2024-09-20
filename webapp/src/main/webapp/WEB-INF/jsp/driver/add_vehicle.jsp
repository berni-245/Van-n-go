<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <jsp:include page="../lib/bootstrap_css.jsp"/>
</head>

<body>
<comp:Header/>
<div class="container mt-4">
    <h2><spring:message code="driver.add_vehicle.title" arguments="${loggedUser.username}"/></h2>
    <comp:VehicleForm action="/driver/vehicle/add" modelAttribute="vehicleForm"/>
</div>
</body>

</html>