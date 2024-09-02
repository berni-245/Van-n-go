<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  Created by IntelliJ IDEA.
  User: franco
  Date: 9/1/24
  Time: 5:03 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Posts</title>
    <%@include file="../components/bootstrap.jsp"%>
</head>
<body>

<header class="d-flex flex-wrap justify-content-center py-3 mb-4 border-bottom">
    <a href="${pageContext.request.contextPath}/"
       class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-body-emphasis text-decoration-none">
        <span class="fs-4">Van N' Go Logo</span>
    </a>
    <ul class="nav nav-pills">
        <li class="nav-item"><a href="${pageContext.request.contextPath}/" class="nav-link" aria-current="page">Home</a>
        </li>
        <li class="nav-item"><a href="${pageContext.request.contextPath}/register" class="nav-link">Registerse como fletero</a></li>
    </ul>
</header>

<main>
<div class="d-flex justify-content-start">
    <div class="nav flex-column nav-pills me-3" id="v-pills-tab" role="tablist" aria-orientation="vertical">
        <c:forEach items="${users}" var="user">
            <button class="nav-link" id="${user.id}-tab" data-bs-toggle="pill" data-bs-target="#${user.id}" type="button" role="tab" aria-controls="${user.id}" aria-selected="false">
                <c:out value="${user.username}"/>
            </button>
        </c:forEach>
    </div>
    <div class="tab-content px-2" id="v-pills-tabContent">
        <c:forEach items="${users}" var="user">
            <div class="tab-pane fade show" id="${user.id}" role="tabpanel" aria-labelledby="v-pills-home-tab" tabindex="${user.id}">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">
                            <c:out value="${user.username}" />
                        </h5>
                        <p class="card-text">
                            Descripción: <c:out value="${user.desc}" />
                        </p>
                        <p class="card-text text-body-secondary">
                            Modelo de vehículo: <c:out value="${user.vehicleModel}" />
                        </p>
                        <p class="card-text text-body-secondary">
                            Peso máximo disponible: <c:out value="${user.maxWeight}" />
                        </p>
                        <a href="#" class="card-link">
                            ¡Contactar!
                        </a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
</main>
</body>
</html>
