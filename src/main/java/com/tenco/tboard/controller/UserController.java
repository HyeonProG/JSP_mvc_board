package com.tenco.tboard.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import com.tenco.tboard.model.User;
import com.tenco.tboard.repository.UserRepositoryImpl;
import com.tenco.tboard.repository.interfaces.UserRepository;

@WebServlet("/user/*")
public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	// UserController --> UserRepository
	private UserRepository userRepository;	
	
	@Override
	public void init() throws ServletException {
		userRepository = new UserRepositoryImpl();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String action = request.getPathInfo();
		
		switch (action) {
		case "/signUp":
			request.getRequestDispatcher("/WEB-INF/views/user/signUp.jsp").forward(request, response);
			break;
		case "/signIn":
			request.getRequestDispatcher("/WEB-INF/views/user/signIn.jsp").forward(request, response);
			break;
		case "/logOut":
			handleLogout(request, response);
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			break;
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String action = request.getPathInfo();
		
		switch (action) {
		case "/signUp":
			handleSignUp(request, response);
			break;
		case "/signIn":
			handleSignIn(request, response);
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			break;
		}
		
	}
	
	/**
	 * 로그인 기능 처리
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void handleSignIn(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		User principal = userRepository.getUserByUsernameAndPassword(username, password);
		if(principal != null && principal.getPassword().equals(password)) {
			HttpSession session = request.getSession();
			session.setAttribute("principal", principal);
			
			// 302(브라우저) --> 바로 서클릿 클래스(BoardController) -- (JSP 내부이동)
			response.sendRedirect(request.getContextPath() + "/board/list");
		} else {
			request.setAttribute("errorMessage", "잘못된 요청입니다.");
			request.getRequestDispatcher("/WEB-INF/views/user/signIn.jsp").forward(request, response);
		}
		
	}

	/**
	 * 회원가입 기능 처리
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void handleSignUp(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 데이터 추출
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		
		// 데이터 유효성 검사(생략)
		
		User user = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.build();
		
		System.out.println(user);
		
		int result = userRepository.addUser(user);
		System.out.println("result : " + result);
		if (result != 0) {
			response.sendRedirect(request.getContextPath() + "/user/signIn");
		} else {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script> alert('잘못된 요청입니다'); history.back(); </script>");
		}
		
	}

	/**
	 * 로그아웃 기능 처리
	 * @param request
	 * @param response
	 * @throws IOException
	 * http://localhost:8080/t-board/user/logOut 
	 */
	private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		session.invalidate();
		response.sendRedirect(request.getContextPath() + "/user/signIn");
	}

}
