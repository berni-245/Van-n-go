<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="radio" required="false" type="java.lang.Boolean" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:message code="driver.add_availability.monday" var="mon"/>
<spring:message code="driver.add_availability.tuesday" var="tue"/>
<spring:message code="driver.add_availability.wednesday" var="wed"/>
<spring:message code="driver.add_availability.thursday" var="thu"/>
<spring:message code="driver.add_availability.friday" var="fri"/>
<spring:message code="driver.add_availability.saturday" var="sat"/>
<spring:message code="driver.add_availability.sunday" var="sun"/>

<div class="row weekday-toggle-group mt-3">
    <comp:SquareToggleButton path="${path}" radio="${radio}" content="${fn:substring(mon, 0, 1)}"
                             tooltip="${mon}" value="MONDAY"/>
    <comp:SquareToggleButton path="${path}" radio="${radio}" content="${fn:substring(tue, 0, 1)}"
                             tooltip="${tue}" value="TUESDAY"/>
    <comp:SquareToggleButton path="${path}" radio="${radio}" content="${fn:substring(wed, 0, 1)}"
                             tooltip="${wed}" value="MONDAY"/>
    <comp:SquareToggleButton path="${path}" radio="${radio}" content="${fn:substring(thu, 0, 1)}"
                             tooltip="${thu}" value="MONDAY"/>
    <comp:SquareToggleButton path="${path}" radio="${radio}" content="${fn:substring(fri, 0, 1)}"
                             tooltip="${fri}" value="MONDAY"/>
    <comp:SquareToggleButton path="${path}" radio="${radio}" content="${fn:substring(sat, 0, 1)}"
                             tooltip="${sat}" value="MONDAY"/>
    <comp:SquareToggleButton path="${path}" radio="${radio}" content="${fn:substring(sun, 0, 1)}"
                             tooltip="${sun}" value="MONDAY"/>
</div>

<script>
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
    const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))
</script>