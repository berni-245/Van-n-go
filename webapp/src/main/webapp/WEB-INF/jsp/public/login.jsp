<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<comp:Head titleCode="public.login.login"/>
<body class="d-flex flex-column min-vh-100">
<c:url value="/login" var="loginUrl"/>
<comp:Header/>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="w-auto">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title text-center mb-4">
                        <spring:message code="public.login.greeting"/>
                    </h5>
                    <form action="${pageContext.request.contextPath}/login" method="post"
                          enctype="application/x-www-form-urlencoded">
                        <div class="mb-3">
                            <label for="username" class="form-label">
                                <spring:message code="generic.word.username"/>
                            </label>
                            <input id="username" name="j_username" type="text" class="form-control"/>
                        </div>
                        <div class="mb-3">
                            <label for="password-input" class="form-label">
                                <spring:message code="generic.word.password"/>
                            </label>
                            <div class="input-group">
                                <input id="password-input" name="j_password" type="password" class="form-control"/>
                                <button type="button" class="btn btn-outline-secondary" id="show-pass-btn">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor"
                                         class="bi bi-eye" viewBox="0 0 16 16">
                                        <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8M1.173 8a13 13 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5s3.879 1.168 5.168 2.457A13 13 0 0 1 14.828 8q-.086.13-.195.288c-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5s-3.879-1.168-5.168-2.457A13 13 0 0 1 1.172 8z"/>
                                        <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5M4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0"/>
                                    </svg>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor"
                                         class="bi bi-eye-slash d-none" viewBox="0 0 16 16">
                                        <path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7 7 0 0 0-2.79.588l.77.771A6 6 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13 13 0 0 1 14.828 8q-.086.13-.195.288c-.335.48-.83 1.12-1.465 1.755q-.247.248-.517.486z"/>
                                        <path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829"/>
                                        <path d="M3.35 5.47q-.27.24-.518.487A13 13 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7 7 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12z"/>
                                    </svg>
                                </button>
                            </div>
                        </div>
                        <div class="mb-3 form-check">
                            <input id="rememberMe" name="j_rememberme" type="checkbox" class="form-check-input"/>
                            <label for="rememberMe" class="form-check-label">
                                <spring:message code="public.login.rememberMe"/>
                            </label>
                        </div>
                        <div class="d-grid">
                            <spring:message code="public.login.login" var="login"/>
                            <input type="submit" class="btn btn-primary" value="${login}">
                        </div>
                    </form>
                    <c:if test="${not empty param.error}">
                        <div class="alert alert-danger" role="alert">
                            <c:choose>
                                <c:when test="${param.error == 'user'}">
                                    <spring:message code="public.login.error.userNotExist"/>
                                </c:when>
                                <c:when test="${param.error == 'password'}">
                                    <spring:message code="public.login.error.incorrectPassword"/>
                                </c:when>
                                <c:otherwise>
                                    <spring:message code="public.login.error.unknown"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        const passInput = document.getElementById('password-input');
        const showPassBtn = document.getElementById('show-pass-btn');
        const showIcon = showPassBtn.children[0];
        const hideIcon = showPassBtn.children[1];
        showPassBtn.addEventListener('click', () => {
            if (passInput.type === 'password') {
                passInput.type = 'text';
                showIcon.classList.add('d-none');
                hideIcon.classList.remove('d-none');
            } else {
                passInput.type = 'password';
                showIcon.classList.remove('d-none');
                hideIcon.classList.add('d-none');
            }
        })

        function togglePasswordVisibility() {
            const passwordField = document.getElementById('password');
        }
    })
</script>
</body>
</html>
