<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Posts</title>
    <%@include file="../lib/bootstrap_css.jsp" %>
    <%@include file="../lib/bootstrap_js.jsp" %>
    <jsp:include page="../lib/tom_select.jsp"/>
    <c:url value="/js/availability.js" var="js"/>
    <c:url value="/css/availability_styles.css" var="css"/>
    <script src="${js}"></script>
    <link rel="stylesheet" href="${css}">
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>
    <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js'></script>
</head>
<body>
<comp:header/>
<div class="container mt-4">
    <c:url var="postUrl" value="/availability"/>
    <form:form action="${postUrl}" method="get" modelAttribute="availabilitySearchForm">

        <div class="row g-3">
            <div class="col-sm-7">
                <spring:message code="driver.add_availability.selectZones" var="selectZones"/>
                <spring:bind path="zoneId">
                    <form:select path="zoneId" id="select-zones" multiple="false"
                                 placeholder="${selectZones}..." autocomplete="off"
                                 cssClass="form-control ${status.error ? 'is-invalid' : ''}"
                    >
                        <form:options items="${zones}" itemValue="id"/>
                    </form:select>
                </spring:bind>
            </div>
            <div class="col-sm">
                <form:select path="size" id="select-zones" multiple="false"
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
            <div class="col-sm">
                <input type="submit" class="btn btn-primary" value="Buscar">
            </div>
        </div>

    </form:form>







        <div class="d-flex justify-content-center">
            <c:choose>
                <c:when test="${drivers.isEmpty()}">
                    <p><spring:message code="availability.posts.noPosts"/></p>
                </c:when>
                <c:otherwise>
                    <div class="container">
                        <div class="row row-cols-3">
                            <c:forEach var="driver" items="${drivers}" varStatus="status">
                            <div class="col mb-4">
                                <div class="card anchor-card h-100" style="width: 18rem;">
                                    <div class="card-body">
                                        <h5 class="card-title">${driver.username}</h5>
                                        <p class="card-text"></p>
                                        <a href="${pageContext.request.contextPath}/availability/${driver.id}" type="button" class="btn btn-primary" >
                                            Ver disponibilidad
                                        </a>

                                    </div>
                                </div>
                            </div>
                            </c:forEach>
                        </div>



                    </div>
                </div>


            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
    new TomSelect("#select-zones");
</script>
</body>
</html>