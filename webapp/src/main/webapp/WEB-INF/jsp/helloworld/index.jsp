<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>

<body>
<h2><spring:message code="hwc.home.greeting" arguments="${username}"/></h2>
<h5><spring:message code="hwc.home.userId" arguments="${userId}"/></h5>
</body>

</html>
