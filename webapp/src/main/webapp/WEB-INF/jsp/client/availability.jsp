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
    <script src="${pageContext.request.contextPath}/js/availability.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/availability.css">
    <%--    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AnchorCard.css">--%>
</head>
<body>
<comp:header/>
<main>
    <div class="container">
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

        <%--        <comp:AvailabilityGrid drivers="${drivers}"/>--%>

        <div class="d-flex justify-content-center">
            <c:choose>
                <c:when test="${drivers.isEmpty()}">
                    <p>No hay postulaciones actualmente, intente más tarde</p>
                </c:when>
                <c:otherwise>
                    <div class="nav flex-column nav-pills me-3" id="v-pills-tab" role="tablist"
                         aria-orientation="vertical">
                        <c:forEach var="driver" items="${drivers}">
                            <button class="nav-link text-truncate" style="max-width: 200px;" id="${driver.id}-tab"
                                    data-bs-toggle="pill" data-bs-target="#${driver.id}"
                                    type="button" role="tab" aria-controls="${driver.id}" aria-selected="false">
                                <c:out value="${driver.username} (id: ${driver.id})"/>
                            </button>
                        </c:forEach>
                    </div>
                    <div class="tab-content flex-grow-1 px-2" id="v-pills-tabContent">
                        <c:forEach var="driver" items="${drivers}" varStatus="status">
                            <div class="tab-pane fade show" id="${driver.id}" role="tabpanel"
                                 aria-labelledby="v-pills-home-tab"
                                 tabindex="${driver.id}">
                                <div class="card">
                                    <div class="card-body">
                                        <h4 class="card-title d-flex align-items-center justify-content-between">
                                            <c:out value="${driver.username}"/>
                                        </h4>
                                        <h6 class="card-subtitle mb-2 text-body-secondary">
                                            Cantidad de vehículos: <c:out value="${driver.vehicles.size()}"/>
                                        </h6>
                                        <p class="card-text">
                                            Descripción: <c:out value="${driver.extra1}"/>
                                        </p>
                                        <div>
                                            <h6 class="card-title mb-2">Horarios</h6>
                                            <c:forEach var="v" items="${driver.vehicles}">
                                                <div>
                                                    <h6 class="card-subtitle mb-2 mt-2">
                                                        <c:out value="${v.plateNumber} - ${v.volume}m³"/>
                                                    </h6>
                                                    <ul class="list-group">
                                                        <c:forEach var="av" items="${v.weeklyAvailability}">
                                                            <li class="list-group-item card-text text-body-secondary">
                                                                <c:out value="${av.weekDayString}"/> |
                                                                <c:out value="${av.timeStart}"/> to <c:out
                                                                    value="${av.timeEnd}"/>
                                                            </li>
                                                        </c:forEach>
                                                    </ul>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <div>
                                            <button id="contactButton${driver.id}" class="btn btn-secondary mt-2"
                                                    onclick="showMailForm(${driver.id})">
                                                Contactar
                                            </button>
                                            <div class="form-control mt-5" id="contactForm${driver.id}" style="display: none;">
                                                <form action="${pageContext.request.contextPath}/availability/contact"
                                                      method="post">
                                                    <label for="clientName">Your name:</label>
                                                    <input type="text" id="clientName" name="clientName" required>
                                                    <label for="clientMail">Your email:</label>
                                                    <input type="email" id="clientMail" name="clientMail" required>
                                                    <label for="jobDescription">Description:</label>
                                                    <textarea id="jobDescription" name="jobDescription" rows="4"
                                                              cols="50" required></textarea>
                                                    <input type="hidden" name="driverMail" value="${driver.mail}"/>
                                                    <input type="hidden" name="driverName" value="${driver.username}"/>
                                                    <button type="submit" class="btn btn-primary mt-2">Submit</button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <script>
        new TomSelect("#select-zones");
    </script>
</main>
</body>
</html>
