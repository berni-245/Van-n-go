<%@ attribute name="paramName" required="true" type="java.lang.String" %>
<%@ attribute name="totalPages" required="true" type="java.lang.Integer" %>
<%@ attribute name="currentPage" required="true" type="java.lang.Integer" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${totalPages > 1}">
    <nav class="mt-4" aria-label="Page navigation">
        <ul class="pagination justify-content-center"
            param-name="${paramName}" current-page="${currentPage}">
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link pagination-prev" aria-disabled="${currentPage == 1}">
                    &laquo;
                </a>
            </li>
            <c:forEach begin="1" end="${totalPages}" var="i">
                <li class="page-item ${i == currentPage ? 'active' : ''}">
                    <a class="page-link pagination-page">${i}</a>
                </li>
            </c:forEach>
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <a class="page-link pagination-next" aria-disabled="${currentPage == totalPages}">
                    &raquo;
                </a>
            </li>
        </ul>
    </nav>
</c:if>