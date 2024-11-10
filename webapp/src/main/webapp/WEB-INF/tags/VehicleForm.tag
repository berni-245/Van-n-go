<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ attribute name="modelAttribute" required="true" type="java.lang.String" %>
<%@ attribute name="deleteAction" required="false" type="java.lang.String" %>
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
                   accept="image/png, image/jpeg, image/webp, image/heic" class="form-control mt-2"
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
            <form:input path="plateNumber" cssClass="form-control" required="true"/>
        </label>
        <form:errors path="plateNumber" element="p" cssStyle="color: red"/>
            <%--        No path because the already used plate number validation is a class level--%>
            <%--        validation now and those are shown by specifying an empty path.--%>
        <form:errors path="" element="p" cssStyle="color: red"/>
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
    <div class="mt-4 ${not empty deleteAction ? "d-flex justify-content-around" : ""}">
        <button type="submit" class="btn btn-primary">
            <spring:message code="generic.word.confirm"/>
        </button>

        <c:if test="${not empty deleteAction}">
            <button type="button" class="btn btn-danger"
                    data-bs-toggle="modal"
                    data-bs-target="#confirmDeleteModal">
                <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor"
                     class="bi bi-trash" viewBox="0 0 16 16">
                    <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z"/>
                    <path d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4zM2.5 3h11V2h-11z"/>
                </svg>
            </button>
        </c:if>
    </div>
</form:form>
<c:if test="${not empty deleteAction}">
    <div class="modal fade" id="confirmDeleteModal" tabindex="-1"
         aria-labelledby="confirmDeleteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-body">
                    <spring:message code="vehicle.delete.confirmation"/>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <spring:message code="generic.word.cancel"/>
                    </button>
                    <c:url var="deleteUrl" value="${deleteAction}"/>
                    <form id="deleteForm" method="POST" action="${deleteUrl}">
                        <button type="submit" class="btn btn-danger">
                            <spring:message code="generic.word.delete"/>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</c:if>

<script type="text/javascript">
    document.addEventListener('DOMContentLoaded', () => {
        new TomSelect("#select-zones", {
            sortField: {
                field: 'text',
                direction: 'asc'
            },
            onItemAdd: function () {
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
    })
</script>