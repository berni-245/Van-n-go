<%@ taglib prefix="spring2" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Penguin Express</title>
    <%@include file="../components/bootstrap.jsp" %>
</head>
<body class="d-flex flex-column min-vh-100">


<%@include file="../components/myHeader.jsp" %>
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
