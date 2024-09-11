<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title><spring:message code="siteName"/></title>
    <%@include file="../lib/bootstrap_css.jsp" %>
    <c:url value="${pageContext.request.contextPath}/css/weekdaySelector.css" var="css"/>
    <link rel="stylesheet" href="${css}">
</head>

<body class="d-flex flex-column min-vh-100">
<comp:header/>
<main>
    <div class="welcome-main-div px-4 py-5 my-5 text-center">
        <img class="background-img" src="${pageContext.request.contextPath}/images/welcome_picture.webp" alt="Background" class="background-img">
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
                <h2 class="display-4"><spring:message code="public.home.aboutUs"/></h2>
                <p class="lead"><spring:message code="public.home.aboutUs.text"/></p>
            </div>
            <div class="col-lg-4 mb-4">
                <h2 class="display-4"><spring:message code="public.home.services"/></h2>
                <ul>
                    <li><i class="bi bi-envelope"></i><spring:message code="public.home.service.1"/> </li>
                    <li><i class="bi bi-truck"></i><spring:message code="public.home.service.2"/> </li>
                    <li><i class="bi bi-people"></i><spring:message code="public.home.service.3"/> </li>
                </ul>
            </div>
            <div class="col-lg-4 mb-4">
                <h2 class="display-4"><spring:message code="public.home.clientOpinion"/></h2>
                <blockquote class="blockquote">
                    <p><spring:message code="public.home.clientOpinion.text"/></p>
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
