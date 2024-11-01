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
                    <p class="mt-5 display-4 font-weight-bold"><spring:message
                            code="call_to_action.driver_vehicles"/></p>
                </div>
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
                                             src="<c:url value='/vehicle/image?imgId=${v.imgId}' />"
                                             alt="Vehicle Image Preview" class="card-img-top"/>
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/images/defaultVehicle.png"
                                             alt="DefaultVehiclePicture">
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
            <c:if test="${totalPages > 1}">
                <nav aria-label="Page navigation">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage - 1}" tabindex="-1"
                               aria-disabled="${currentPage == 0}">&laquo; Previous</a>
                        </li>
                        <c:forEach begin="0" end="${totalPages - 1}" var="i">
                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                <a class="page-link" href="?page=${i}">${i + 1}</a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage + 1}"
                               aria-disabled="${currentPage == totalPages - 1}">Next &raquo;</a>
                        </li>
                    </ul>
                </nav>
            </c:if>
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