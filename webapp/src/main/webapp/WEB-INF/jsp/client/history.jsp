<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<comp:Head titleCode="siteName"/>
<body class="d-flex flex-column min-vh-100">
<comp:Header inHistory="true"/>
<main>
    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <h1 class="text-left"><spring:message code="generic.word.history"/></h1>
            </div>
        </div>

        <div class="row row-cols-3">
            <c:forEach var="booking" items="${history}">
                <div class="col mb-4">
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5 class="card-title"><c:out
                                    value="${booking.date}"/></h5>
                            <p class="card-text"><c:out
                                    value="${booking.driver.username}"/></p>
                            <p class="card-text"><c:out
                                    value="${booking.driver.mail}"/></p>

                            <c:if test="${booking.rating == null}">
                                <button type="button" class="btn btn-primary" data-bs-toggle="modal"
                                        data-bs-target="#reviewModal${booking.bookingId}">
                                    <spring:message code="client.review"/>
                                </button>


                                <div class="modal fade" id="reviewModal${booking.bookingId}" tabindex="-1"
                                     aria-labelledby="reviewModalLabel"
                                     aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="reviewModalLabel"><c:out
                                                        value="${booking.date}"/></h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                        aria-label="Close"></button>
                                            </div>
                                            <div class="modal-body">
                                                <form:form modelAttribute="bookingReviewForm" method="post"
                                                           action="${pageContext.request.contextPath}">

                                                    <form:input type="hidden" path="bookingID"
                                                                value="${booking.bookingId}"/>

                                                    <div class="mb-3">
                                                        <form:label path="rating" cssClass="form-label">
                                                            <spring:message
                                                                code="client.select.rating"
                                                                arguments="${booking.driver.username}"/>
                                                        </form:label>
                                                        <form:input path="rating" type="text" />

                                                    </div>

                                                    <div class="mb-3">
                                                        <form:label path="review" cssClass="form-label">
                                                            <spring:message
                                                                    code="client.make.review"
                                                                    arguments="${booking.driver.username}"/>
                                                        </form:label>
                                                        <form:input path="review" type="text"/>
                                                    </div>


                                                    <button type="submit" class="btn btn-primary mt-3">
                                                        <spring:message code="generic.word.confirm"/>
                                                    </button>

                                                </form:form>


                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
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
