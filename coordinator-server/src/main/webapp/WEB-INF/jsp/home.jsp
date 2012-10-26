<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<p>
    Welcome to Google App Engine for Java!
</p>

<p>
<ul>
    <c:forEach var="message" items="${messages}">
        <li>
            <div><c:out value="${message.text}"/></div>
            <div><a href="index?id=<c:out value="${message.id}"/>">Delete</a></div>
        </li>
    </c:forEach>
</ul>
</p>
<p>

<form action="index" method="post">
    <input type="text" name="text"/>
    <input type="submit" value="Create"/>
</form>
</p>
