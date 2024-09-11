<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Posts</title>
    <%@include file="../lib/bootstrap_css.jsp" %>
    <%@include file="../lib/bootstrap_js.jsp" %>
    <script src="${pageContext.request.contextPath}/js/availability.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/availability.css">
</head>
<body>
<comp:header/>
<main>
    <div class="container">
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
                            <c:set var="vehicles" value="${vehicleLists[status.index]}"/>
                            <c:set var="availability" value="${availabilityLists[status.index]}"/>
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
                                            Cantidad de vehículos: <c:out value="${vehicles.size()}"/>
                                        </p>
                                            <%--                                        <p class="card-text text-body-secondary">--%>
                                            <%--                                            Peso máximo disponible: <c:out value="${vehicles}"/>--%>
                                            <%--                                        </p>--%>

                                        <div>
                                            <p>Horarios:</p>
                                            <c:forEach var="av" items="${availability}">
                                                <p class="card-text text-body-secondary">

                                                    <c:out value="${av.weekDayString}"/> |
                                                    <c:out value="${av.timeStart}"/> to <c:out value="${av.timeEnd}"/> |
                                                    <c:out value="${vehicles[av.vehicleId].plateNumber} - volumen: ${vehicles[av.vehicleId].volume}m^3"/>
                                                    |
                                                    <c:out value="${zones[av.zoneId].neighborhoodName}"/>
                                                </p>
                                            </c:forEach>
                                        </div>

                                        <div>
                                            <button id="contactButton${driver.id}" onclick="showMailForm(${driver.id})">Contactar</button>
                                            <div id="contactForm${driver.id}" style="display: none;">
                                                <form action="${pageContext.request.contextPath}/availability/contact" method="post">
                                                    <label for="clientName">Your name:</label>
                                                    <input type="text" id="clientName" name="clientName" required>
                                                    <label for="clientMail">Your email:</label>
                                                    <input type="email" id="clientMail" name="clientMail" required>
                                                    <label for="jobDescription">Description:</label>
                                                    <textarea id="jobDescription" name="jobDescription" rows="4" cols="50" required></textarea>
                                                    <input type="hidden" name="driverMail" value="${driver.mail}" />
                                                    <input type="hidden" name="driverName" value="${driver.username}" />
                                                    <button type="submit">Submit</button>
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
</main>
</body>
</html>
