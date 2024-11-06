<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<comp:Head titleCode="components.header.availability">
    <c:url value="/css/styles.css" var="css"/>
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
<comp:Header inAvailability="true"/>
<div class="container">
    <div class="row row-cols-4 row-cols-lg-3 row-cols-md-2 row-cols-sm-1 g-4">
        <c:choose>
        <c:when test="${empty vehicles}">
            <div class="row">
                <div class="col-12 text-center">
                    <p class="mt-5 display-4 font-weight-bold"><spring:message code="call_to_action.driver_availability"/></p>
                </div>
            </div>
        </c:when>
        <c:otherwise>
        <c:url var="vUrl" value="/driver/availability/edit"/>
        <c:forEach items="${vehicles}" var="v">
            <div class="col mb-4">
                <a href="${vUrl}?plateNumber=${v.plateNumber}" class="text-decoration-none">
                    <div class="card anchor-card h-100">                        <c:choose>
                        <c:when test="${v.imgId != 0}">
                            <img id="vehicleImagePreview" src="<c:url value='/vehicle/image?imgId=${v.imgId}' />"
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
        </c:otherwise>
        </c:choose>

    </div>

    <%--    <c:forEach var="v" items="${vehicles}">--%>
    <%--        <div>--%>
    <%--            <h6 class="card-subtitle mb-2 mt-2">--%>
    <%--                <c:out value="${v.plateNumber}"/> - <c:out value="${v.volume}"/> m&sup3--%>
    <%--            </h6>--%>
    <%--            <ul class="list-group">--%>
    <%--                <c:forEach var="av" items="${v.weeklyAvailability}">--%>
    <%--                    <c:url var="vUrl" value="/driver/availability/edit"/>--%>
    <%--                    <div class="list-group-item list-group-item-action vehicle-item">--%>
    <%--                        <c:out value="${av.weekDayString}"/> |--%>
    <%--                        <c:out value="${av.hourInterval.startHour}"/> |--%>
    <%--                        Zone Id: <c:out value="${av.zoneId}"/>--%>
    <%--                    </div>--%>
    <%--                </c:forEach>--%>
    <%--            </ul>--%>
    <%--        </div>--%>
    <%--    </c:forEach>--%>

    <div class="mt-4 navbar sticky-bottom">
        <a class="btn btn-primary mb-4"
           href="${pageContext.request.contextPath}/driver/availability/add"
           role="button"
        >
            <spring:message code="generic.phrase.add_availability"/>
        </a>
    </div>
</div>
<comp:ToastManager toasts="${toasts}"/>
</body>

</html>