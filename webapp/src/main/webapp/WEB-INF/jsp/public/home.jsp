<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <title><spring:message code="siteName"/></title>
    <%@include file="../lib/bootstrap_css.jsp" %>
    <style>
        <%@ include file="/css/styles.css" %>
    </style>
</head>

<body class="d-flex flex-column min-vh-100">
<comp:header/>
<main>
    <div class="welcome-main-div px-4 py-5 my-5 text-center">
        <div class="welcome-page-div-text">
            <h1><spring:message code="siteName"/></h1>
            <div class="col-lg-6 mx-auto">
                <p class="lead mb-4"><spring:message code="public.home.greeting"/></p>
                <div class="d-grid gap-2 d-sm-flex justify-content-sm-center">
                    <a class="btn btn-primary btn-lg px-4 gap-3"
                       href="${pageContext.request.contextPath}/availability"
                       role="button">
                        <spring:message code="public.home.search"/>
                    </a>
                </div>
            </div>
        </div>
    </div>
</main>

<section class="company-presentation py-5">
    <div class="container">
        <div class="row text-center">
            <div class="col-lg-4 mb-4">
                <h2 class="display-4">Sobre Nosotros</h2>
                <p class="lead">Somos una empresa dedicada a hacer la busqueda de fletes mas facíl.</p>
            </div>
            <div class="col-lg-4 mb-4">
                <h2 class="display-4">Nuestros Servicios</h2>
                <ul class="list-unstyled">
                    <li><i class="bi bi-envelope"></i>- Encuentra fletes en tu zona</li>
                    <li><i class="bi bi-truck"></i>- Facil interacción con el fletero</li>
                    <li><i class="bi bi-people"></i>- Ve y escribe reseñas</li>
                </ul>
            </div>
            <div class="col-lg-4 mb-4">
                <h2 class="display-4">Lo que dicen nuestros usuarios</h2>
                <blockquote class="blockquote">
                    <p>"Es fantastico, contratar un flete nunca fue tan facíl como cuando uso Van N' Go"</p>
                    <footer class="blockquote-footer">Elon Musk, <cite title="Source Title">Space X</cite></footer>
                </blockquote>
            </div>
        </div>
    </div>
</section>

<footer class="mt-auto">
    <div class="container">
        <p class="float-end mb-1">
            <a href="#"><spring:message code="public.home.backToTop"/></a>
        </p>
        <p class="mb-1">&copy; PAW 2024B G1</p>
    </div>
</footer>

</body>
</html>
