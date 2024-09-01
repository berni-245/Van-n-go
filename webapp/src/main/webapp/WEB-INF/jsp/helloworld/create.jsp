<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<html>

<body>
<c:url var="postUrl" value="/create"/>
<form action="${postUrl}" method="post">
    <div>
        <label>
            Username:
            <input name="username" type="text"/>
        </label>
    </div>
    <div>
        <input type="submit">
    </div>
</form>
</body>

</html>
