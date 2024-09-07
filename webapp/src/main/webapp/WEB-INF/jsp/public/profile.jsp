<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>

<body>
<h1><spring:message code="public.profile.title"/></h1>
<h2><spring:message code="public.profile.greeting" arguments="${username}" htmlEscape="true"/></h2>
<h5><spring:message code="public.profile.userId" arguments="${userId}" htmlEscape="true"/></h5>
</body>

</html>
