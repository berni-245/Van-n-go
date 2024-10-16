<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<comp:Head titleCode="siteName">
</comp:Head>
<body class="d-flex flex-column min-vh-100">
<comp:Header/>

<div class="container mt-5">
  <div class="card">
    <div class="card-header text-center">
      <h1><spring:message code="driver.profile.edit.head"/></h1>
    </div>
    <div class="card-body">
      <form:form modelAttribute="profileForm" action="${pageContext.request.contextPath}/profile/edit" method="post" class="needs-validation">
        <div class="form-group">
          <label for="cbu"><spring:message code="generic.word.cbu"/></label>
          <form:input path="cbu" id="cbu" cssClass="form-control" />
          <form:errors path="cbu" cssClass="text-danger" />
        </div>
        <div class="form-group">
          <label for="extra1"><spring:message code="generic.word.description"/></label>
          <form:input path="extra1" id="extra1" cssClass="form-control" />
          <form:errors path="extra1" cssClass="text-danger" />
        </div>

        <div class="text-right mt-4">
          <button type="submit" class="btn btn-primary">
            <spring:message code="generic.word.confirm"/>
          </button>
        </div>
      </form:form>
    </div>
  </div>
</div>
<footer class="mt-auto">
  <div class="container">
    <p class="float-end mb-1">
      <a href="#"><spring:message code="public.home.backToTop"/></a>
    </p>
    <p class="mb-1">&copy; PAW 2024B G1</p>
  </div>
</footer>
</html>
