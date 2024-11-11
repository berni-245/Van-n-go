<%@ attribute name="toasts" required="true" type="java.util.Collection<ar.edu.itba.paw.models.Toast>" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<footer class="mt-auto">
    <div class="container">
        <comp:ToastManager toasts="${toasts}"/>
        <p class="mb-1">&copy; PAW 2024B G1</p>
    </div>
</footer>
