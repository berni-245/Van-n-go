<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Posts</title>
    <%@include file="../components/bootstrap.jsp" %>
</head>
<body>

<%@include file="../components/myHeader.jsp" %>

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
                                        <h5 class="card-title">
                                            <c:out value="${driver.username}"/>
                                        </h5>
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
                                            <c:forEach var="av" items="${availability}">
                                                <p class="card-text text-body-secondary">
                                                    Asdf:
                                                    <c:out value="${av.weekDayString}"/> |
                                                    <c:out value="${av.timeStart}"/> to <c:out value="${av.timeEnd}"/> |
                                                    <c:out value="${vehicles[av.vehicleId].plateNumber}"/> |
                                                    <c:out value="${zones[av.zoneId].neighborhoodName}"/>
                                                </p>
                                            </c:forEach>
                                        </div>

                                        <a href="#" class="card-link">
                                            ¡Contactar!
                                        </a>
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
