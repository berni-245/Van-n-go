<%@ attribute name="code" required="true" type="java.lang.String" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="row">
    <div class="col-12 text-center">
        <p class="mt-5 display-4 font-weight-bold">
            <spring:message code="${code}"/>
        </p>
    </div>
</div>
