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
</head>
<body>
<comp:header/>
<main>
    <div class="container">
        <c:url var="postUrl" value="/availability"/>
        <form:form action="${postUrl}" method="get" modelAttribute="availabilitySearchForm">
            <spring:message code="driver.add_availability.selectZones" var="selectZones"/>
            <spring:bind path="zoneId">
                <form:select path="zoneId" id="select-zones" multiple="false"
                             placeholder="${selectZones}..." autocomplete="off"
                             cssClass="form-control ${status.error ? 'is-invalid' : ''}"
                >
                    <form:options items="${zones}" itemValue="id"/>
                </form:select>
            </spring:bind>
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
            <input type="submit" value="Buscar">
        </form:form>
        <div class="d-flex justify-content-center">
            <c:choose>
                <c:when test="${drivers.isEmpty()}">
                    <p>No hay postulaciones actualmente, intente más tarde</p>
                </c:when>
                <c:otherwise>
                    <div class="nav flex-column nav-pills me-3" id="v-pills-tab" role="tablist"
                         aria-orientation="vertical">
                        <c:forEach items="${drivers}" var="driver">
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
                                        <div class="card-title d-flex align-items-center justify-content-between">
                                            <c:out value="${driver.username}"/>
                                            <a class="btn btn-primary"
                                               href="${pageContext.request.contextPath}/driver/${driver.id}/vehicle/add"
                                               role="button">Agregar vehículo</a>
                                            <a class="btn btn-primary"
                                               href="${pageContext.request.contextPath}/driver/${driver.id}/availability/add"
                                               role="button">Agregar horario</a>
                                        </div>
                                        <p class="card-text">
                                            Descripción: <c:out value="${driver.extra1}"/>
                                        </p>
                                        <p class="card-text text-body-secondary">
                                            Cantidad de vehículos: <c:out value="${driver.vehicles.size()}"/>
                                        </p>
                                            <%--                                        <p class="card-text text-body-secondary">--%>
                                            <%--                                            Peso máximo disponible: <c:out value="${vehicles}"/>--%>
                                            <%--                                        </p>--%>

                                        <div>
                                            <p>Horarios:</p>
                                            <c:forEach var="v" items="${driver.vehicles}">
                                                <div>
                                                    <h3><c:out value="${v.plateNumber} - ${v.volume}m³"/></h3>
                                                    <c:forEach var="av" items="${v.weeklyAvailability}">
                                                        <p class="card-text text-body-secondary">
                                                            <c:out value="${av.weekDayString}"/> |
                                                            <c:out value="${av.timeStart}"/> to <c:out
                                                                value="${av.timeEnd}"/>
                                                        </p>
                                                    </c:forEach>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <p>
                                            Contacto: ${driver.mail}
                                        </p>
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
