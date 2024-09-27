<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title><c:out value="${loggedUser.username}"/></title>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6 text-center">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h3 class="card-title mb-4"><c:out value="${loggedUser.username}"/></h3>

                    <!-- Profile Picture Section -->
                    <form:form action="${pageContext.request.contextPath}/upload/pfp" method="post"
                               enctype="multipart/form-data">
                        <div class="mb-3">
                            <label for="profilePic">
                                <img id="profilePicPreview"
                                     src="${pageContext.request.contextPath}/images/${profilePic.fileName}"
                                     alt="Profile Picture" class="img-thumbnail"
                                     style="cursor: pointer; width: 150px; height: 150px;">
                            </label>
                            <input type="file" id="profilePic" name="file" class="d-none"
                                   onchange="document.getElementById('profilePicPreview').src = window.URL.createObjectURL(this.files[0])">
                        </div>
                        <div class="d-grid">
                            <input type="submit" class="btn btn-primary" value="Update Profile Picture">
                        </div>
                    </form:form>

                    <!-- User Information -->
                    <div class="mt-4">
                        <p><strong>Email:</strong> <c:out value="${loggedUser.mail}"/></p>
                    </div>

                    <!-- Back to Home Button -->
                    <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary mt-3">Return to Home</a>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    // Automatically open file dialog when clicking the image
    document.getElementById('profilePicPreview').onclick = function () {
        document.getElementById('profilePic').click();
    };
</script>
</body>
</html>
