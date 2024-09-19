<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <jsp:include page="../lib/bootstrap_css.jsp"/>
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
</head>

<body>
<comp:header inVehicles="true"/>
<div class="container mt-4">
    <h3 class="mb-3"><spring:message code="generic.phrase.your_vehicles"/></h3>
    <ul class="list-group">
        <c:forEach items="${vehicles}" var="v">
            <c:url var="vUrl" value="/driver/vehicle/edit"/>
            <a href="${vUrl}?plateNumber=${v.plateNumber}" class="list-group-item list-group-item-action vehicle-item">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <strong><c:out value="${v.plateNumber}"/></strong> - <c:out value="${v.volume}"/>m&sup3
                        <span class="vehicle-description">
                            <c:out value="${v.description}"/>
                        </span>
                    </div>
                </div>
            </a>
        </c:forEach>
    </ul>
    <div class="mt-4">
        <a class="btn btn-primary"
           href="${pageContext.request.contextPath}/driver/vehicle/add"
           role="button"
        >
            <spring:message code="generic.phrase.add_vehicle"/>
        </a>
    </div>
</div>
</body>

</html>