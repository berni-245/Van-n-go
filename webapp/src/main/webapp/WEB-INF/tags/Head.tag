<%@ attribute name="titleCode" required="true" type="java.lang.String" %>
<%@ attribute name="bootstrapjs" required="false" type="java.lang.Boolean" %>
<%@ attribute name="popper" required="false" type="java.lang.Boolean" %>
<%@ attribute name="tomselect" required="false" type="java.lang.Boolean" %>

<%@ tag body-content="scriptless" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<head>
    <spring:message var="title" code="${titleCode}"/>
    <title>${title}</title>

    <c:url value="/css/bootstrap.min.css" var="bscss"/>
    <link rel="stylesheet" href="${bscss}">

    <c:if test="${bootstrapjs}">
        <c:url value="/js/bootstrap.min.js" var="bsjs"/>
        <script src="${bsjs}"></script>
    </c:if>

    <c:if test="${popper}">
        <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
                integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r"
                crossorigin="anonymous"
        ></script>
    </c:if>

    <c:if test="${tomselect}">
        <link href="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/css/tom-select.bootstrap5.min.css"
              rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/tom-select/dist/js/tom-select.complete.min.js"></script>
    </c:if>

    <c:url value="/favicon.ico" var="favicon"/>
    <link href="${favicon}" rel="shortcut icon">

    <c:url value="/js/bootstrap-theme-toggler.js" var="bsjs"/>
    <script src="${bsjs}"></script>

    <jsp:doBody/>
</head>