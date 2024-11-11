<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<html>
<comp:Head titleCode="components.header.review">
    <c:url value="/css/client/bookingReview.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <c:url value="/js/client/bookingReview.js" var="js"/>
    <script src="${js}"></script>
</comp:Head>
<body class="d-flex flex-column min-vh-100">
<comp:Header/>
<c:url var="finishedBookingsUrl" value="/client/bookings?activeTab=FINISHED"/>
<comp:GoBackButton path="${finishedBookingsUrl}"/>
<div class="container mt-5">
    <div class="card">
        <div class="card-header text-center">
            <h1>
                <spring:message code="client.rating.header" arguments="${driver.username}" var="usernameHeader"/>
                <c:out value="${usernameHeader}"/>
            </h1>
        </div>
        <div class="card-body">
            <div class=" text-center">
                <c:url var="postUrl" value="/client/booking/${bookingId}/review/send"/>
                <form:form modelAttribute="bookingReviewForm" method="post" action="${postUrl}">
                    <div class="mb-3">
                        <form:label path="rating" cssClass="form-label">
                            <spring:message code="client.rating.select" arguments="${driver.username}" var="usernameSelect"/>
                            <c:out value="${usernameSelect}"/>
                        </form:label>
                        <div id="star-rating" class="d-flex justify-content-evenly">
                            <span class="star" data-value="1">&#9733;</span>
                            <span class="star" data-value="2">&#9733;</span>
                            <span class="star" data-value="3">&#9733;</span>
                            <span class="star" data-value="4">&#9733;</span>
                            <span class="star" data-value="5">&#9733;</span>
                            <form:input path="rating" type="number" name="rating"
                                        id="rating-input" min="1"/>
                        </div>
                        <form:errors path="rating" cssClass="text-danger" element="p"/>
                    </div>

                    <div class="mb-3">
                        <p>
                            <spring:message code="client.make.review" arguments="${driver.username}" var="usernameReview"/>
                            <c:out value="${usernameReview}"/>
                        </p>
                        <form:textarea path="review" rows="4" cols="50" maxlength="255"
                                       htmlEscape="true" required="true"/>
                        <form:errors path="review" cssClass="text-danger" element="p"/>
                    </div>

                    <button type="submit" class="btn btn-primary mt-3">
                        <spring:message code="generic.word.confirm"/>
                    </button>

                </form:form>
            </div>
        </div>
    </div>
    <comp:ToastManager toasts="${toasts}"/>
</div>

</body>
</html>