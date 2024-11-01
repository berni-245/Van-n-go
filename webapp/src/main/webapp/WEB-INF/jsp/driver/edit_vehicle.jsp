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
        <div class="w-50">
            <comp:VehicleForm
                    action="/driver/vehicle/${vehicle.plateNumber}/edit"
                    modelAttribute="vehicleForm"
                    deleteAction="/driver/vehicle/${vehicle.plateNumber}/delete"
            >
                <form:input type="hidden" path="id" cssClass="form-control"/>
                <input type="hidden" name="ogPlateNumber" value="${vehicle.plateNumber}">
            </comp:VehicleForm>
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
    <comp:ToastManager toasts="${toasts}"/>
</body>
</html>