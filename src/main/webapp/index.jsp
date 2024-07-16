<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>JSP MVC 게시판</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common.css">
<style type="text/css">
.nav-list {
    list-style-type: none;
    padding: 0;
    text-align: center;
}

.nav-list li {
    margin: 10px;
    display: inline-block;
}

.nav-list li a {
    text-decoration: none;
    padding: 10px 20px;
    color: white;
    border-radius: 5px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    transition: background-color 0.3s ease, transform 0.3s ease;
}

.nav-list li a:hover {
    transform: translateY(-3px);
}

.btn-primary a {
    background-color: #007bff;
    background-image: linear-gradient(to right, #007bff, #0056b3);
}

.btn-secondary a {
    background-color: #6c757d;
    background-image: linear-gradient(to right, #6c757d, #495057);
}
</style>
</head>
<body>
    <div class="container">
        <h2>JSP MVC 게시판 테스트 페이지</h2>
        <ul class="nav-list">
            <li class="btn btn-primary"><a href="/t-board/user/signUp">회원가입</a></li>
            <li class="btn btn-primary"><a href="/t-board/user/signIn">로그인</a></li>
            <li class="btn btn-secondary"><a href="/t-board/user/logOut">로그아웃</a></li>
            <li class="btn btn-primary"><a href="/t-board/board/list">게시판 목록</a></li>
        </ul>
    </div>
</body>
</html>
