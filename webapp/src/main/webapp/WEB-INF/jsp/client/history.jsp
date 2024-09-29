<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<comp:Head titleCode="siteName" bootstrapjs="true"/>
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
                                        data-bs-target="#reviewModal">
                                    <spring:message code="client.review"/>
                                </button>


                                <div class="modal fade" id="reviewModal" tabindex="-1"
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
                                                <form action="${pageContext.request.contextPath}/sendReview"
                                                      method="POST">

                                                    <input type="hidden" name="bookingId" value="${booking.bookingId}">

                                                    <div class="form-group">
                                                        <label for="rating" class="form-label"><spring:message code="client.select.rating" arguments="${booking.driver.username}"/></label>
                                                        <div id="rating" class="d-flex gap-2">
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="radio"
                                                                       name="rating" id="radio0" value="0" required>
                                                                <label class="form-check-label" for="radio0">0</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="radio"
                                                                       name="rating" id="radio1" value="1">
                                                                <label class="form-check-label" for="radio1">1</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="radio"
                                                                       name="rating" id="radio2" value="2">
                                                                <label class="form-check-label" for="radio2">2</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="radio"
                                                                       name="rating" id="radio3" value="3">
                                                                <label class="form-check-label" for="radio3">3</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="radio"
                                                                       name="rating" id="radio4" value="4">
                                                                <label class="form-check-label" for="radio4">4</label>
                                                            </div>
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="radio"
                                                                       name="rating" id="radio5" value="5">
                                                                <label class="form-check-label" for="radio5">5</label>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <button type="submit" class="btn btn-primary mt-3"><spring:message code="generic.word.confirm"/></button>
                                                </form>
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
