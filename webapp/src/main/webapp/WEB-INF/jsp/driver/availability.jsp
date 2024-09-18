<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <jsp:include page="../lib/bootstrap_css.jsp"/>
</head>

<body>
<comp:header inAvailability="true"/>
<div class="container">

    <c:forEach var="v" items="${vehicles}">
        <div>
            <h6 class="card-subtitle mb-2 mt-2">
                <c:out value="${v.plateNumber}"/> - <c:out value="${v.volume}"/> m³
            </h6>
            <ul class="list-group">
                <c:forEach var="av" items="${v.weeklyAvailability}">
                    <c:url var="vUrl" value="/driver/availability/edit"/>
                    <div class="list-group-item list-group-item-action vehicle-item">
                        <c:out value="${av.weekDayString}"/> |
                        <c:out value="${av.timeStart}"/> to
                        <c:out value="${av.timeEnd}"/>
                    </div>
                    <%--                    <a href="${vUrl}?availabilityId=TODO"--%>
                    <%--                       class="list-group-item list-group-item-action vehicle-item"--%>
                    <%--                    >--%>
                    <%--                        <c:out value="${av.weekDayString}"/> |--%>
                    <%--                        <c:out value="${av.timeStart}"/> to--%>
                    <%--                        <c:out value="${av.timeEnd}"/>--%>
                    <%--                    </a>--%>
                </c:forEach>
            </ul>
        </div>
    </c:forEach>

    <div class="mt-4 navbar sticky-bottom">
        <a class="btn btn-primary mb-4"
           href="${pageContext.request.contextPath}/driver/availability/add"
           role="button"
        >
            <spring:message code="generic.phrase.add_availability"/>
        </a>
    </div>
</div>
</body>

</html>