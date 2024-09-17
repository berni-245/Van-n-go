<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Posts</title>
    <%@include file="../lib/bootstrap_css.jsp" %>
    <%@include file="../lib/bootstrap_js.jsp" %>
    <jsp:include page="../lib/tom_select.jsp"/>
    <c:url value="/js/availability.js" var="js"/>
    <c:url value="/css/availability_styles.css" var="css"/>
    <script src="${js}"></script>
    <link rel="stylesheet" href="${css}">
    <%--    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AnchorCard.css">--%>
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>
</head>
<body>
<comp:header/>
<main>
    <div class="container">
        <c:url var="postUrl" value="/availability"/>
        <form:form action="${postUrl}" method="get" modelAttribute="availabilitySearchForm">

            <div class="row g-3">
                <div class="col-sm-7">
                    <spring:message code="driver.add_availability.selectZones" var="selectZones"/>
                    <spring:bind path="zoneId">
                        <form:select path="zoneId" id="select-zones" multiple="false"
                                     placeholder="${selectZones}..." autocomplete="off"
                                     cssClass="form-control ${status.error ? 'is-invalid' : ''}"
                        >
                            <form:options items="${zones}" itemValue="id"/>
                        </form:select>
                    </spring:bind>
                </div>
                <div class="col-sm">
                    <form:select path="size" id="select-zones" multiple="false"
                                 placeholder="${selectZones}..." autocomplete="off"
                                 cssClass="form-control"
                    >
                        <spring:message var="small" code="generic.word.small"/>
                        <form:option value="SMALL" label="${small}"/>
                        <spring:message var="medium" code="generic.word.medium"/>
                        <form:option value="MEDIUM" label="${medium}"/>
                        <spring:message var="large" code="generic.word.large"/>
                        <form:option value="LARGE" label="${large}"/>
                    </form:select>
                    <form:errors path="zoneId" element="p" cssClass="invalid-feedback"/>
                </div>
                <div class="col-sm">
                    <input type="submit" class="btn btn-primary" value="Buscar">
                </div>
            </div>

        </form:form>




        <c:forEach var="driver" items="${drivers}">
            <div class="modal fade" id="modal${driver.id}" tabindex="-1" role="dialog" aria-labelledby="modalLabel${driver.id}" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="modalLabel${driver.id}"><c:out value="${driver.username}"/></h5>
                        </div>
                        <div class="modal-body">
                            <h6 class="card-subtitle mb-2 text-muted">
                                Cantidad de vehículos: <c:out value="${driver.vehicles.size()}"/>
                            </h6>
                            <p class="card-text">
                                Descripción: <c:out value="${driver.extra1}"/>
                            </p>
                            <div>
                                <h6 class="mb-2">Horarios</h6>
                                <c:forEach var="v" items="${driver.vehicles}">
                                    <div>
                                        <h6 class="card-subtitle mb-2 mt-2">
                                            <c:out value="${v.plateNumber}"/> - <c:out value="${v.volume}"/> m³
                                        </h6>
                                        <ul class="list-group">
                                            <c:forEach var="av" items="${v.weeklyAvailability}">
                                                <li class="list-group-item">
                                                    <c:out value="${av.weekDayString}"/> |
                                                    <c:out value="${av.timeStart}"/> to
                                                    <c:out value="${av.timeEnd}"/>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </c:forEach>

                                <div>
                                    <button id="contactButton${driver.id}" class="btn btn-secondary mt-2"
                                            onclick="showMailForm(${driver.id})">
                                        Contactar
                                    </button>
                                    <div class="form-control mt-5" id="contactForm${driver.id}"
                                         style="display: none;">
                                        <form action="${pageContext.request.contextPath}/availability/contact"
                                              method="post">
                                            <label for="clientName">  <spring:message
                                                    code="form.clientName"/></label>
                                            <input type="text" id="clientName" name="clientName" required>
                                            <label for="clientMail"><spring:message code="form.clientMail"/></label>
                                            <input type="email" id="clientMail" name="clientMail" required>
                                            <label for="jobDescription"><spring:message code="form.jobDescription"/></label>
                                            <textarea id="jobDescription" name="jobDescription" rows="4"
                                                      cols="50" required></textarea>
                                            <input type="hidden" name="driverMail" value="${driver.mail}"/>
                                            <input type="hidden" name="driverName" value="${driver.username}"/>
                                            <button type="submit" class="btn btn-primary mt-2">Submit</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>

        <div class="d-flex justify-content-center">
            <c:choose>
                <c:when test="${drivers.isEmpty()}">
                    <p><spring:message code="availability.posts.noPosts"/></p>
                </c:when>
                <c:otherwise>
                    <div class="container">
                        <div class="row mb-4">
                            <c:forEach var="driver" items="${drivers}" varStatus="status">
                            <div class="col-md-3">
                                <div class="card" style="width: 18rem;">
                                    <div class="card-body">
                                        <h5 class="card-title">${driver.username}</h5>
                                        <p class="card-text">Aca va la descripcion</p>
                                        <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modal${driver.id}">
                                            Ver detalles
                                        </button>

                                    </div>
                                </div>
                            </div>
                            <c:if test="${(status.index + 1) % 4 == 0}">
                        </div>
                        <div class="row mb-4">
                            </c:if>
                            </c:forEach>

                        </div>
                    </div>



                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <script>
        new TomSelect("#select-zones");
    </script>
</main>
</body>
</html>
