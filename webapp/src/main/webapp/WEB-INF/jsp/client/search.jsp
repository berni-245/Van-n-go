<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<comp:Head titleCode="components.header.availability" tomselect="true" bsIcons="true">
  <c:url value="/css/availability.css" var="css"/>
  <link rel="stylesheet" href="${css}">
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
        <form:select path="size" id="select-size" multiple="false"
                     placeholder="${selectZones}..." autocomplete="off"
                     cssClass="form-control"
        >
          <spring:message var="small" code="generic.word.small"/>
          <form:option value="SMALL" label="${small}"/>
          <spring:message var="medium" code="generic.word.medium"/>
          <form:option value="MEDIUM" label="${medium}"/>
          <spring:message var="large" code="generic.word.large"/>
          <form:option value="LARGE" label="${large}"/>
        </form:select>
        <form:errors path="zoneId" element="p" cssClass="invalid-feedback"/>
      </div>
      <div class="col-sm d-flex align-items-center">
        <button type="submit" class="btn btn-primary">
          <spring:message code="components.availability.Search"/>
        </button>
      </div>
    </div>
  </form:form>

  <div class="d-flex justify-content-center">

  </div>
</div>

<script>
  new TomSelect("#select-zones");
</script>
</body>
</html>