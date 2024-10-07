<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="row weekday-toggle-group mt-3 fs-6">
    <c:forEach var="i" begin="0" end="23">
        <comp:SquareToggleButton
                path="${path}"
                id="hb-${i}"
                labelClass="fs-6"
                content="${i} - ${i < 23 ? i + 1 : 0}"
                value="${i < 10 ? 0 : \"\"}${i}:00:00"
        />
    </c:forEach>
</div>