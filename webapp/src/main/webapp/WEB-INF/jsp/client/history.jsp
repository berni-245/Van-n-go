<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<comp:Head titleCode="siteName"/>
<body class="d-flex flex-column min-vh-100">
    <style>
        .star {
            font-size: 2rem;
            cursor: pointer;
            color: gray;
        }
        .star.selected {
            color: gold;
        }
    </style>
<comp:Header inHistory="true"/>
<main>
    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <h1 class="text-left"><spring:message code="generic.word.history"/></h1>
            </div>
        </div>
        <c:choose>
            <c:when test="${empty history}">
                <div class="row">
                    <div class="col-12 text-center">
                        <p class="mt-5 display-4 font-weight-bold"><spring:message code="call_to_action.client_history"/></p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
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

                            <c:if test="${booking.rating.isEmpty()}">
                                <button type="button" class="btn btn-primary" data-bs-toggle="modal"
                                        data-bs-target="#reviewModal${booking.id}">
                                    <spring:message code="client.review"/>
                                </button>


                                <div class="modal fade" id="reviewModal${booking.id}" tabindex="-1"
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
                                                           action="/client/history/send/review">

                                                    <form:input type="hidden" path="bookingID"
                                                                value="${booking.id}"/>

                                                    <div class="mb-3">
                                                        <form:label path="rating" cssClass="form-label">
                                                            <spring:message
                                                                code="client.rating.select"
                                                                arguments="${booking.driver.username}"/>
                                                        </form:label>
                                                       <!-- <form:input path="rating" type="text" /> -->
                                                        <div class="starRating" id="starRating${booking.id}">
                                                            <span class="star" data-value="1">&#9733;</span>
                                                            <span class="star" data-value="2">&#9733;</span>
                                                            <span class="star" data-value="3">&#9733;</span>
                                                            <span class="star" data-value="4">&#9733;</span>
                                                            <span class="star" data-value="5">&#9733;</span>
                                                        </div>
                                                        <input type="hidden" name="rating" id="rating${booking.id}" value="0">

                                                    </div>

                                                    <div class="mb-3">
                                                        <form:label path="review" cssClass="form-label">
                                                            <spring:message
                                                                    code="client.make.review"
                                                                    arguments="${booking.driver.username}"/>
                                                        </form:label>
                                                        <form:input path="review" type="text"/>
                                                        <form:errors path="review" cssClass="text-danger" element="p"/>
                                                        <form:errors element="div" cssClass="alert alert-danger"/>
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
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${currentPage - 1}" tabindex="-1" aria-disabled="${currentPage == 0}">&laquo; Previous</a>
                            </li>
                            <c:forEach begin="0" end="${totalPages - 1}" var="i">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="?page=${i}">${i + 1}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${currentPage + 1}" aria-disabled="${currentPage == totalPages - 1}">Next &raquo;</a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </c:otherwise>
        </c:choose>
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

<script>
    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('.starRating').forEach(starContainer => {
            const stars = starContainer.querySelectorAll('.star');
            const ratingInput = starContainer.nextElementSibling;

            stars.forEach(star => {
                star.addEventListener('click', () => {
                    const rating = parseInt(star.getAttribute('data-value'), 10);
                    ratingInput.value = rating;


                    stars.forEach(s => s.classList.remove('selected'));
                    for (let i = 0; i < rating; i++) {
                        stars[i].classList.add('selected');
                    }
                });
            });
        });
    });
</script>

</html>