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
<div class="container d-flex justify-content-center mt-5">
  <div class="col-md-6 col-lg-5 text-center">
    <!-- Page Title -->
    <h1 class="display-5 mb-5">
      <spring:message code="siteName"/>
    </h1>

    <c:url var="postUrl" value="/client/availability"/>
    <form:form action="${postUrl}" method="get" modelAttribute="availabilitySearchForm">
      <!-- Zone Selection -->
      <div class="mb-4">
        <spring:message code="driver.add_availability.selectZones" var="selectZones"/>
        <spring:bind path="zoneId">
          <label for="select-zones" class="form-label h4">${selectZones}</label>
          <form:select path="zoneId" id="select-zones" multiple="false"
                       cssClass="form-select form-select-lg ${status.error ? 'is-invalid' : ''}"
                       autocomplete="off">
            <form:options items="${zones}" itemValue="id"/>
          </form:select>
          <form:errors path="zoneId" element="p" cssClass="invalid-feedback"/>
        </spring:bind>
      </div>

      <!-- Size Selection -->
      <div class="mb-4">
        <label for="select-size" class="form-label h4">
          <spring:message code="generic.word.size"/>
        </label>
        <form:select path="size" id="select-size" multiple="false"
                     cssClass="form-select form-select-lg"
                     autocomplete="off">
          <form:option value="" label="Any"/>
          <spring:message var="small" code="generic.word.small"/>
          <form:option value="SMALL" label="${small}"/>
          <spring:message var="medium" code="generic.word.medium"/>
          <form:option value="MEDIUM" label="${medium}"/>
          <spring:message var="large" code="generic.word.large"/>
          <form:option value="LARGE" label="${large}"/>
        </form:select>
      </div>

      <!-- Day of the Week Selection -->
      <div class="mb-4">
        <label for="select-day" class="form-label h4">
          <spring:message code="client.search.selectWeekday"/>
        </label>
        <form:select path="weekday" id="select-day" cssClass="form-select form-select-lg" autocomplete="off">
          <form:option value="" label="Any"/>
          <form:option value="MONDAY" label="Monday"/>
          <form:option value="TUESDAY" label="Tuesday"/>
          <form:option value="WEDNESDAY" label="Wednesday"/>
          <form:option value="THURSDAY" label="Thursday"/>
          <form:option value="FRIDAY" label="Friday"/>
          <form:option value="SATURDAY" label="Saturday"/>
          <form:option value="SUNDAY" label="Sunday"/>
        </form:select>
      </div>

      <!-- Rating Selection -->
      <div class="mb-4">
        <label for="select-rating" class="form-label h4">
          <spring:message code="client.search.selectRating"/>
        </label>
        <form:select path="rating" id="select-rating" cssClass="form-select form-select-lg" autocomplete="off">
        <form:option value="" label="Any"/>
          <form:option value="5" label="5 stars"/>
          <form:option value="4" label="4 stars"/>
          <form:option value="3" label="3 stars"/>
        </form:select>
      </div>

      <!-- Price Range Selection -->
      <div class="mb-4">
        <label class="form-label h4">
          <spring:message code="client.search.selectPriceRange"/>
        </label>
        <div class="row g-2">
          <div class="col">
            <form:input path="priceMin" type="number" id="price-min" placeholder="Min" cssClass="form-control" />
          </div>
          <div class="col">
            <form:input path="priceMax" type="number" id="price-max" placeholder="Max" cssClass="form-control" />
          </div>
        </div>
      </div>

      <!-- Submit Button -->
      <button type="submit" class="btn btn-primary btn-lg w-100">
        <spring:message code="components.availability.Search"/>
      </button>
    </form:form>
  </div>
</div>

<script>
  new TomSelect("#select-zones");
</script>
</body>
</html>