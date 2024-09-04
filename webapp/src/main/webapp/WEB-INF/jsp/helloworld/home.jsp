<%@ taglib prefix="spring2" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Penguin Express</title>
    <%@include file="../components/bootstrap.jsp" %>
</head>
<body class="d-flex flex-column min-vh-100">


<header class="d-flex flex-wrap justify-content-center py-3 mb-4 border-bottom">
    <a href="${pageContext.request.contextPath}/"
       class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-body-emphasis text-decoration-none">
        <span class="fs-4">Logo</span>
    </a>
    <ul class="nav nav-pills">
        <li class="nav-item"><a href="${pageContext.request.contextPath}/login" class="nav-link" aria-current="page"><spring:message code="Login"/></a></li>
        <li class="nav-item"><a href="${pageContext.request.contextPath}/create" class="nav-link"><spring:message code="Register"/></a></li>
    </ul>
</header>
<main>
    <div class="px-4 py-5 my-5 text-center">
        <h1 class="display-5 fw-bold text-body-emphasis">Van N' Go</h1>
        <div class="col-lg-6 mx-auto">
            <p class="lead mb-4"><spring:message code="HomeDesc"/></p>
            <div class="d-grid gap-2 d-sm-flex justify-content-sm-center">
                <button type="button" class="btn btn-primary btn-lg px-4 gap-3"><spring:message code="Search"/></button>
            </div>
        </div>
    </div>
</main>


<footer class="mt-auto">
    <div class="container">
        <p class="float-end mb-1">
            <a href="#"><spring:message code="btt"/></a>
        </p>
        <p class="mb-1">&copy; PAW 2024B G1</p>
    </div>

</footer>
</body>
</html>
