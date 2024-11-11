<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<comp:Head titleCode="components.header.vehicles" tooltips="true">
    <c:url value="/css/driver/vehicles.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <c:url value="/js/PaginationTag.js" var="js"/>
    <script type="module" src="${js}"></script>
</comp:Head>

<body>
<comp:Header inVehicles="true"/>
<div class="container mt-4">
    <h3 class="mb-3"><spring:message code="generic.phrase.your_vehicles"/></h3>
    <c:choose>
        <c:when test="${empty vehicles}">
            <div class="d-flex flex-column">
                <comp:CallToAction code="call_to_action.driver_vehicles"/>
                <a class="btn btn-primary mt-5 fs-2"
                   href="${pageContext.request.contextPath}/driver/vehicle/add"
                   role="button">
                    <spring:message code="generic.phrase.add_vehicle"/>
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row row-cols-4 row-cols-lg-3 row-cols-md-2 row-cols-sm-1 g-4">
                <c:forEach items="${vehicles}" var="v">
                    <c:url var="vUrl" value="/driver/vehicle/${v.plateNumber}/edit"/>
                    <div class="col mb-4">
                        <a href="${vUrl}" class="text-decoration-none">
                            <div class="card anchor-card h-100">
                                <c:choose>
                                    <c:when test="${v.imgId ne null}">
                                        <img id="vehicleImagePreview"
                                             src="<c:url value='/images/query?imgId=${v.imgId}' />"
                                             alt="Vehicle Image Preview" class="object-fit: cover; card-img-top"/>
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/images/defaultVehicle.png"
                                             class="object-fit: cover" alt="DefaultVehiclePicture">
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

                                <c:if test="${empty v.availability}">
                                    <spring:message code="vehicle.no.availability" var="noAv"/>
                                    <div class="position-absolute top-0 start-100 translate-middle
                                badge border border-light rounded-circle bg-danger p-3 z-3"
                                         data-bs-toggle="tooltip" data-bs-title="${noAv}">
                                    </div>
                                </c:if>
                            </div>
                        </a>
                    </div>
                </c:forEach>
            </div>
            <comp:Pagination paramName="page" totalPages="${totalPages}" currentPage="${currentPage}"/>
            <a class="btn btn-primary fixed-bottom-left"
               href="${pageContext.request.contextPath}/driver/vehicle/add"
               role="button"
            >
                <spring:message code="generic.phrase.add_vehicle"/>
            </a>
        </c:otherwise>
    </c:choose>
</div>

<comp:ToastManager toasts="${toasts}"/>

</body>

</html>