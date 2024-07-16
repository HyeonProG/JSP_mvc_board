<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${board.title}</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/view.css">
</head>
<body>
	<div class="container">
		<h2>${board.title}</h2>
		<p>${board.content}</p>
		<p><fmt:formatDate value="${board.createdAt}" pattern="yyyy-MM-dd HH:mm"/></p>
	</div>
	<c:if test="${board.userId == principal.id}">
		<a href="${pageContext.request.contextPath}/board/edit?id=${board.id}" class="btn btn-edit">수정</a>
		<a href="${pageContext.request.contextPath}/board/delete?id=${board.id}" class="btn btn-delete">삭제</a>
	</c:if>
	<a href="${pageContext.request.contextPath}/board/list?page=1" class="btn btn-list">목록으로 돌아가기</a>
	<h3>댓글</h3>
	<!-- 댓글 리스트 작성 -->
	<div class="comment-list">     
    	<div class="comment">
   			<div class="comment-author"></div>
            	<div class="comment-date"></div>
   				<div class="comment-content"></div>
        </div>
    </div>	
	<!-- 댓글 작성 폼 -->
	<div>
		<form class="comment-form" action="${pageContext.request.contextPath}/comment/add" method="post">
    		<textarea name="content" rows="4" placeholder="댓글을 작성하세요..." required></textarea>
    		<button type="submit">댓글 작성</button>
    	</form>
    </div>
</body>
</html>