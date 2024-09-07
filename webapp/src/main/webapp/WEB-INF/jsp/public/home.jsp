<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="siteName"/></title>
    <%@include file="../lib/bootstrap_css.jsp" %>
</head>

<body class="d-flex flex-column min-vh-100">
<%@include file="../components/header.jsp" %>
<main>
    <div class="px-4 py-5 my-5 text-center">
        <h1 class="display-5 fw-bold text-body-emphasis"><spring:message code="siteName"/></h1>
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
</main>

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
