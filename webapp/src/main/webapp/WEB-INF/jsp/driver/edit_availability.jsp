<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<comp:Head titleCode="components.header.availability" tomselect="true">
    <c:url value="/css/weekdaySelector.css" var="css"/>
    <link rel="stylesheet" href="${css}">
</comp:Head>

<body>
<comp:Header/>
<div class="container mt-2 p-3 border border-primary rounded">
    <h2 class="mb-4">
        <spring:message
                code="generic.phrase.edit_vehicle"
                arguments="${vehicle.plateNumber}"
        />
    </h2>
    <c:url var="postUrl" value="/driver/availability/edit?plateNumber=${plateNumber}&vehicleId=${vehicle.id}"/>
    <form:form action="${postUrl}" method="post" modelAttribute="availabilityForm">

        <div class="mb-3">
            <comp:WeekdaySelector path="weekDay" radio="true"/>
            <form:errors path="weekDay" element="p" cssStyle="color: red"/>
        </div>

        <div class="mb-3">
            <label class="form-label"><spring:message code="driver.add_availability.timeRange"/></label>
            <div class="row">
                <comp:HourBlockSelector path="hourBlocks"/>
                <form:errors path="hourBlocks" cssStyle="color: red"/>
            </div>
        </div>

        <div class="mb-3">
            <spring:message code="driver.add_availability.selectZones" var="selectZones"/>
            <form:select path="zoneId" id="select-zones" multiple="false"
                         placeholder="${selectZones}..." autocomplete="off"
            >
                <form:options items="${zones}" itemValue="id"/>
            </form:select>
            <form:errors path="zoneId" element="p" cssStyle="color: red"/>
        </div>

        <div class="mt-4">
            <button type="submit" class="btn btn-primary mt-2">
                <spring:message code="generic.word.confirm"/>
            </button>
        </div>

    </form:form>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const zoneSelect = new TomSelect("#select-zones");
        const vehicle = ${vehicle.toJson()};

        const hourBlockInputs = document.querySelectorAll('input[name="hourBlocks"]');

        function setHourBlocks(waFiltered) {
            hourBlockInputs.forEach(input => {
                input.removeAttribute("checked");
                input.checked = false;
                if (waFiltered.find(wa => wa.hourInterval.startHourString == input.value)) {
                    input.setAttribute("checked", "checked");
                    input.checked = true;
                }
            })
        }

        zoneSelect.on("change", (zoneId) => {
            const weekDay = document.querySelector('input[name="weekDay"]:checked').value
            const waFiltered = vehicle.weeklyAvailability.filter(
                wa => wa.zoneId == zoneId && wa.weekDay == weekDay
            );
            setHourBlocks(waFiltered);
        });

        const weekDayInputs = document.querySelectorAll('input[name="weekDay"]');
        weekDayInputs.forEach(input => input.addEventListener('change', (ev) => {
            const waFiltered = vehicle.weeklyAvailability.filter(
                wa => wa.zoneId == zoneSelect.getValue() && wa.weekDay == ev.target.value
            );
            setHourBlocks(waFiltered);
        }))
    });
</script>
</body>

</html>
