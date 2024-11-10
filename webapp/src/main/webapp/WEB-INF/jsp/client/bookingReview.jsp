<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<comp:Head titleCode="components.header.review">
</comp:Head>
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
<comp:Header />

<div class="container mt-5">
  <div class="card">
    <div class="card-header text-center">
      <h1><spring:message code="client.rating.header" arguments="${driver.username}"/></h1>
    </div>
    <div class="card-body">
      <div class=" text-center">
          <c:url var="postUrl" value="/client/booking/${bookingId}/review/send" >
              <c:param name="driverId" value="${driver.id}"/>
          </c:url>
        <form:form modelAttribute="bookingReviewForm" method="post"
                   action="${postUrl}">
          <div class="mb-3">
            <form:label path="rating" cssClass="form-label">
              <spring:message
                      code="client.rating.select"
                      arguments="${driver.username}"/>
            </form:label>
            <div class="starRating" id="starRating">
              <span class="star" data-value="1">&#9733;</span>
              <span class="star" data-value="2">&#9733;</span>
              <span class="star" data-value="3">&#9733;</span>
              <span class="star" data-value="4">&#9733;</span>
              <span class="star" data-value="5">&#9733;</span>
            </div>
            <input type="hidden" name="rating" id="rating" value="0">

          </div>

          <div class="mb-3">
            <p>
              <spring:message
                      code="client.make.review"
                      arguments="${driver.username}"/>
            <p>
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
  <comp:ToastManager toasts="${toasts}"/>
</div>

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