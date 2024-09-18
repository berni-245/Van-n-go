<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <%@include file="./lib/bootstrap_css.jsp" %>
    <title>Van n' Go</title>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
<comp:header/>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6 text-center">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h3 class="card-title mb-4"><spring:message code="error.403"/></h3>
                    <p class="card-text">
                        <spring:message code="error.403.desc"/>
                    </p>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-3">
                        <spring:message code="components.header.home"/>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
