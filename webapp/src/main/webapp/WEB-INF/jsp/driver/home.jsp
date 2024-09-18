<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title><spring:message code="siteName"/></title>
    <%@include file="../lib/bootstrap_css.jsp" %>
    <jsp:include page="../lib/bootstrap_js.jsp"/>
    <jsp:include page="../lib/popper.jsp"/>
    <c:url value="/css/styles.css" var="css"/>
    <link rel="stylesheet" href="${css}">
</head>

<body class="d-flex flex-column min-vh-100">
<comp:header inHome="true"/>
<main>
    Driver home
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
