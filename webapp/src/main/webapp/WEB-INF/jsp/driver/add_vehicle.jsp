<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>

<body>
<h2><spring:message code="driver.add_vehicle.title" arguments="${username}"/></h2>
<c:url var="postUrl" value="/driver/${driverId}/vehicle/create"/>
<form:form action="${postUrl}" method="post" modelAttribute="vehicleForm">
  <div>
    <label>
      <spring:message code="driver.add_vehicle.plateNumber"/>
      <form:input path="plateNumber" type="text"/>
    </label>
    <form:errors path="plateNumber" element="p" cssStyle="color: red"/>
  </div>
  <div>
    <label>
      <spring:message code="driver.add_vehicle.volume"/>
      <form:input path="volume"/>
    </label>
    <form:errors path="volume" element="p" cssStyle="color: red"/>
  </div>
  <div>
    <label>
      <spring:message code="driver.add_vehicle.description"/>
      <form:input path="description"/>
    </label>
    <form:errors path="description" element="p" cssStyle="color: red"/>
  </div>
  <div>
    <input type="submit">
  </div>
</form:form>
</body>

</html>