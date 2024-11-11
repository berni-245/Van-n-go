<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<a href="${path}" id="go-back-button" class="btn me-auto">
    <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="bi bi-arrow-left"
         viewBox="0 0 16 16">
        <path fill-rule="evenodd"
              d="M5.854 4.146a.5.5 0 0 1 0 .708L3.707 7H13.5a.5.5 0 0 1 0 1H3.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 0 1 .708 0z"></path>
    </svg>
</a>
