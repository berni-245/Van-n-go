<%@ attribute name="toasts" required="true" type="java.util.Collection<ar.edu.itba.paw.models.Toast>" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>

<div aria-live="polite" aria-atomic="true">
    <div class="toast-container position-fixed bottom-0 end-0 p-3">
        <c:forEach var="toast" items="${toasts}">
            <comp:Toast type="${toast.type}" titleCode="${toast.titleCode}" descriptionCode="${toast.descriptionCode}"
                        delay="${toast.delay}"/>
        </c:forEach>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        const toasts = document.querySelectorAll('.toast-container > .toast');
        toasts.forEach(toast => (new bootstrap.Toast(toast)).show());
    })
</script>