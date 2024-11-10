<%@ attribute name="titleCode" required="true" type="java.lang.String" %>
<%@ attribute name="tomselect" required="false" type="java.lang.Boolean" %>
<%@ attribute name="calendar" required="false" type="java.lang.Boolean" %>
<%@ attribute name="bsIcons" required="false" type="java.lang.Boolean" %>
<%@ attribute name="goBack" required="false" type="java.lang.Boolean" %>
<%@ attribute name="passInput" required="false" type="java.lang.Boolean" %>

<%@ tag body-content="scriptless" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<head>
    <spring:message var="title" code="${titleCode}"/>
    <title>${title}</title>

    <c:url value="/css/bootstrap.min.css" var="bscss"/>
    <link rel="stylesheet" href="${bscss}">

    <c:url value="/js/popper.min.js" var="popperjs"/>
    <script src="${popperjs}"></script>

    <c:url value="/js/bootstrap.min.js" var="bsjs"/>
    <script src="${bsjs}"></script>

    <c:url value="/css/global.css" var="globalCss"/>
    <link rel="stylesheet" href="${globalCss}">

    <c:if test="${tomselect}">
        <link href="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/css/tom-select.bootstrap5.min.css"
              rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/tom-select/dist/js/tom-select.complete.min.js"></script>
    </c:if>

    <c:if test="${calendar}">
        <c:url value="/js/full-callendar.min.js" var="calendarJs"/>
        <script src="${calendarJs}"></script>
    </c:if>

    <c:if test="${bsIcons}">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    </c:if>

    <c:url value="/favicon.ico" var="favicon"/>
    <link href="${favicon}" rel="shortcut icon">

    <c:url value="/js/bootstrap-theme-toggler.js" var="bsjs"/>
    <script src="${bsjs}"></script>

    <c:if test="${goBack}">
        <c:url value="/js/goBackButton.js" var="goBackJs"/>
        <script src="${goBackJs}"></script>
    </c:if>

    <c:if test="${passInput}">
        <c:url value="/js/showHidePassInput.js" var="passInputJs"/>
        <script src="${passInputJs}"></script>
    </c:if>

    <jsp:doBody/>
</head>