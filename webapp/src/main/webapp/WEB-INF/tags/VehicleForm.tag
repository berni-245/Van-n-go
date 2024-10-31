<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ attribute name="modelAttribute" required="true" type="java.lang.String" %>
<%@ tag body-content="scriptless" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<c:url var="postUrl" value="${action}"/>
<form:form action="${postUrl}" method="post" modelAttribute="${modelAttribute}" enctype="multipart/form-data">
    <div class="mb-4">
        <label class="form-label w-100">
            <spring:message code="driver.add_vehicle.image"/>
            <input type="file" id="vehicle.imgId" name="vehicleImg"
                   accept="image/png, image/jpeg" class="form-control mt-2"
                   onchange="previewVehicleImage(event)"
            />
        </label>
        <div class="form-group mt-4 d-flex align-items-center">
            <div class="me-3">
                <c:choose>
                    <c:when test="${vehicle.imgId ne null}">
                        <img id="vehicleImagePreview" src="<c:url value='/vehicle/image?imgId=${vehicle.imgId}' />"
                             alt="" class="img-fluid border rounded" style="max-width: 150px;"/>
                    </c:when>
                    <c:otherwise>
                        <img id="vehicleImagePreview"
                             src="${pageContext.request.contextPath}/images/defaultVehicle.png"
                             alt="Vehicle Image Preview" class="img-fluid border rounded"
                             style="max-width: 150px;"
                        />
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <div>
        <label class="form-label w-100">
            <spring:message code="driver.add_vehicle.plateNumber"/>
            <form:input path="plateNumber" cssClass="form-control"/>
        </label>
        <form:errors path="plateNumber" element="p" cssStyle="color: red"/>
    </div>
    <div>
        <label class="form-label w-100">
            <spring:message code="driver.add_vehicle.volume"/>
            <form:input path="volume" cssClass="form-control" type="number" min="1" max="100" step="0.01"/>
        </label>
        <form:errors path="volume" element="p" cssStyle="color: red"/>
    </div>
    <div>
        <label class="form-label w-100">
            <spring:message code="driver.add_vehicle.hourly_rate"/>
            <form:input path="rate" cssClass="form-control" type="number" min="100" max="100000" step="1"/>
        </label>
        <form:errors path="rate" element="p" cssStyle="color: red"/>
    </div>
    <form:hidden path="imgId" value="${vehicle.imgId}"/>
    <div>
        <label class="form-label w-100">
            <spring:message code="driver.add_vehicle.description"/>
            <form:input path="description" cssClass="form-control"/>
        </label>
        <form:errors path="description" element="p" cssStyle="color: red"/>
    </div>
    <div class="mb-3">
        <label class="form-label w-100">
            <spring:message code="driver.add_availability.selectZones"/>
            <spring:message code="generic.phrase.workingZones" var="selectZones"/>
            <form:select path="zoneIds" id="select-zones" multiple="true"
                         placeholder="${selectZones}..." autocomplete="off"
            >
                <form:options items="${zones}" itemValue="id"/>
            </form:select>
        </label>
        <form:errors path="zoneIds" element="p" cssStyle="color: red"/>
    </div>
    <jsp:doBody/>
    <div class="mt-4">
        <button type="submit" class="btn btn-primary mt-2">
            <spring:message code="generic.word.confirm"/>
        </button>
    </div>
</form:form>

<script type="text/javascript">
    new TomSelect("#select-zones", {
        sortField: {
            field: 'text',
            direction: 'asc'
        },
        onItemAdd: function(){
            this.setTextboxValue('');
            this.refreshOptions();
        }
    });

    function previewVehicleImage(event) {
        const fileInput = event.target;
        const file = fileInput.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                const previewImage = document.getElementById("vehicleImagePreview");
                previewImage.src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    }
</script>