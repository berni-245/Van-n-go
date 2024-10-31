<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<comp:Head titleCode="components.header.vehicles" tomselect="true">
    <style>
        .btn-check:not(:checked) ~ .toggle-label:hover {
            background-color: var(--bs-primary-bg-subtle);
            border-color: var(--bs-primary-bg-subtle);
        }

        .btn-check:checked ~ .toggle-label:not(:hover) {
            color: var(--bs-btn-active-color);
            background-color: var(--bs-btn-active-bg);
            border-color: var(--bs-btn-active-border-color);
        }
    </style>
</comp:Head>

<body>
<comp:Header/>
<div class="container mt-3">
    <div class="d-flex justify-content-between flex-sm-wrap">
        <div>
            <comp:VehicleForm action="/driver/vehicle/edit" modelAttribute="vehicleForm">
                <form:input type="hidden" path="id" cssClass="form-control"/>
                <input type="hidden" name="ogPlateNumber" value="${plateNumber}">
            </comp:VehicleForm>
            <form id="deleteForm" method="POST" action="${pageContext.request.contextPath}/driver/vehicle/delete">
                <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#confirmDeleteModal">
                    <spring:message code="generic.word.delete"/>
                </button>
                <input type="hidden" name="plateNumber" value="${plateNumber}" />
            </form>
        </div>

        <div>
            <c:url var="postUrl" value="/driver/vehicle/${vehicle.plateNumber}/edit/availability"/>
            <form:form action="${postUrl}" method="post" modelAttribute="availabilityForm">
                <c:forEach var="day" items="${days}">
                    <div class="d-flex flex-column mb-3">
                        <label class="form-label text-capitalize">${day}</label>
                        <div class="d-flex justify-content-start" aria-label="${day} Availability">
                            <c:forEach var="period" items="${shiftPeriods}">
                                <div class="me-2">
                                    <form:checkbox
                                            path="${day}ShiftPeriods"
                                            class="btn-check"
                                            name="${day}" value="${period}"
                                            id="${day}-${period}"
                                    />
                                    <label class="btn btn-outline-primary toggle-label"
                                           for="${day}-${period}">${period}</label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:forEach>
                <div class="mt-4">
                    <button type="submit" class="btn btn-primary mt-2">
                        <spring:message code="generic.word.confirm"/>
                    </button>
                </div>
            </form:form>
        </div>
</div>
    <div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-labelledby="confirmDeleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-body">
                    <spring:message code="vehicle.delete.confirmation"/>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <spring:message code="generic.word.cancel"/>
                    </button>
                    <button type="button" class="btn btn-danger" onclick="confirmDelete()">
                        <spring:message code="generic.word.delete"/>
                    </button>
                </div>
            </div>
        </div>
    </div>
<comp:ToastManager toasts="${toasts}"/>
</body>
<script>
    function confirmDelete() {
        document.getElementById("deleteForm").submit();
    }
</script>
</html>