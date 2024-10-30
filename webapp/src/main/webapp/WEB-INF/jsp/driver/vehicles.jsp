<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<comp:Head titleCode="components.header.vehicles">
    <c:url value="/css/availability.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <style>
        .vehicle-description {
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
            max-height: 3rem;
        }
    </style>
</comp:Head>

<body>
<comp:Header inVehicles="true"/>
<div class="container mt-4">
    <h3 class="mb-3"><spring:message code="generic.phrase.your_vehicles"/></h3>
    <c:choose>
    <c:when test="${empty vehicles}">
        <div class="row">
            <div class="col-12 text-center">
                <p class="mt-5 display-4 font-weight-bold"><spring:message code="call_to_action.driver_vehicles"/></p>
            </div>
        </div>
    </c:when>
    <c:otherwise>
    <div class="row row-cols-4 row-cols-lg-3 row-cols-md-2 row-cols-sm-1 g-4">
        <c:url var="vUrl" value="/driver/vehicle/edit"/>
        <c:forEach items="${vehicles}" var="v">
            <div class="col mb-4">
                <a href="${vUrl}?plateNumber=${v.plateNumber}" class="text-decoration-none">
                    <div class="card anchor-card h-100">
                        <c:choose>
                            <c:when test="${v.imgId ne null}">
                        <img id="vehicleImagePreview" src="<c:url value='/vehicle/image?vehicleId=${v.id}' />"
                             alt="Vehicle Image Preview" class="card-img-top"/>
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/images/defaultVehicle.png" alt="DefaultVehiclePicture">
                            </c:otherwise>
                        </c:choose>
                        <div class="card-body">
                            <h5 class="card-title">
                                <strong><c:out value="${v.plateNumber}"/></strong> -
                                <c:out value="${v.volume}"/>m&sup3
                            </h5>
                            <p class="card-text">
                                <span class="vehicle-description">
                                    <c:out value="${v.description}"/>
                                </span>
                            </p>
                            <span class="stretched-link"></span>
                        </div>
                    </div>
                </a>
            </div>

        </c:forEach>
    </div>
    </c:otherwise>
    </c:choose>
    <div class="mt-4">
        <a class="btn btn-primary"
           href="${pageContext.request.contextPath}/driver/vehicle/add"
           role="button"
        >
            <spring:message code="generic.phrase.add_vehicle"/>
        </a>
    </div>
</div>

<comp:ToastManager toasts="${toasts}"/>

</body>

</html>