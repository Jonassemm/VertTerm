<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
	<title>User list</title>
</head>

<body>

	<div id="userList"></div>
	<script src="built/userListDisplay.js"></script>
	<h2>All Users in System</h2>

	<table border="1">
		<tr>
			<th>User Id</th>
			<th>First Name</th>
			<th>Last Name</th>
		<c:forEach items="${user}" var="users">
			<tr>
				<td>${user.id}</td>
				<td>${user.firstName}</td>
				<td>${user.lastName}</td>
			</tr>
		</c:forEach>
	</table>

</body>
</html>