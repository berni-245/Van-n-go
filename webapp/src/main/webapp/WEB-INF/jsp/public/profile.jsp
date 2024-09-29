<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<comp:Head titleCode="siteName">
</comp:Head>
<body class="d-flex flex-column min-vh-100">
<comp:Header/>

<div class="container mt-5">
    <div class="card">
        <div class="card-header text-center">
            <h1><c:out value="${loggedUser.username}"/></h1>
        </div>
        <div class="card-body">
            <div class="text-center mb-4">
                <form id="uploadProfilePicForm" method="post" action="<c:url value='/upload/pfp'/>" enctype="multipart/form-data">
                    <input type="file" id="profilePicInput" name="profilePicture" class="d-none" accept="image/png, image/jpeg" onchange="document.getElementById('uploadProfilePicForm').submit();">
                    <label for="profilePicInput" style="cursor: pointer;">
                        <c:if test="${empty profilePic}">
                            <img src="${pageContext.request.contextPath}/images/defaultUserPfp.png" alt="No Profile Picture" class="rounded-circle border mb-2" width="150" height="150">
                        </c:if>
                        <c:if test="${not empty profilePic}">
                            <img src="<c:url value='/profile/picture'/>" alt="Profile Picture" class="rounded-circle border" width="150" height="150">
                        </c:if>
                    </label>
                </form>
            </div>
            <div class="user-info text-center">
                <p class="mb-1"><strong>Email:</strong> <c:out value="${loggedUser.mail}"/></p>
            </div>
        </div>
    </div>
</div>

<footer class="mt-auto bg-dark text-white py-3 text-center">
    <div class="container">
        <p class="mb-0">2024 VanNGo. All Rights Reserved.</p>
    </div>
</footer>

</body>
</html>
