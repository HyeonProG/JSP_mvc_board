package com.tenco.tboard.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.tenco.tboard.model.Board;
import com.tenco.tboard.model.Comment;
import com.tenco.tboard.model.User;
import com.tenco.tboard.repository.BoardRepositoryImpl;
import com.tenco.tboard.repository.CommentRepositoryImpl;
import com.tenco.tboard.repository.interfaces.BoardRepository;
import com.tenco.tboard.repository.interfaces.CommentRepository;

@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BoardRepository boardRepository;
	private CommentRepository commentRepository;
       
	@Override
	public void init() throws ServletException {
		// BoardRepository 추가 예정
		boardRepository = new BoardRepositoryImpl();
		commentRepository = new CommentRepositoryImpl();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getPathInfo();
		HttpSession session = request.getSession(false);
		if(session == null || session.getAttribute("principal") == null) {
			response.sendRedirect(request.getContextPath() + "/user/signIn");
			return;
		}
		
		switch (action) {
		case "/create":
			// 게시글 생성 페이지 이동 처리
			showCreateBoardForm(request, response, session);
			break;
		case "/list":
			// 게시글 리스트 페이지 이동 처리
			handleListBoards(request, response, session);
			break;
		case "/delete":
			// 게시글 삭제 기능 처리
			handleDeleteBoard(request, response, session);
			break;
		case "/update":
			// 게시글 업데이트 페이지 이동 처리
			showEditBoardForm(request, response, session);
			break;
		case "/view":
			// 게시글 상세 페이지 이동 처리
			showViewBoard(request, response, session);
			break;
		case "/editComment":
			// 댓글 수정 처리
			request.setAttribute("editCommentId", request.getParameter("commentId"));
			showViewBoard(request, response, session);
			break;
		case "/deleteComment":
			// 댓글 삭제 기능 처리
			handleDeleteComment(request, response, session);
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			break;
		}
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getPathInfo();
		HttpSession session = request.getSession(false);
		if(session == null || session.getAttribute("principal") == null) {
			response.sendRedirect(request.getContextPath() + "/user/signIn");
			return;
		}
		
		switch (action) {
		case "/create":
			// 게시글 생성 처리
			handleCreateBoard(request, response, session);
			break;
		case "/edit":
			// 댓글 수정 처리
			handleEditComment(request, response, session);
			break;
		case "/addComment":
			// 댓글 추가 처리
			handleCreateComment(request, response, session);
			break;
		case "/update":
			// 게시글 수정 처리
			handleUpdateBoard(request, response, session);
			break;
			
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			break;
		}
		
	}
	
	/**
	 * 게시글 수정 기능(POST 방식 처리)
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 */
	private void handleUpdateBoard(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		int boardId = Integer.parseInt(request.getParameter("id"));
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		
		Board board = Board.builder()
				.title(title)
				.content(content)
				.id(boardId)
				.build();
		
		boardRepository.updateBoard(board);
		
		response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId);
		
	}

	/**
	 * 댓글 수정 기능(POST 방식 처리)
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 */
	private void handleEditComment(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		int commentId = Integer.parseInt(request.getParameter("commentId")); 
		int boardId = Integer.parseInt(request.getParameter("boardId"));
		String content = request.getParameter("content");
		
		try {
			
			User user = (User) session.getAttribute("principal");
			
			Comment comment = Comment.builder()
					.content(content)
					.id(commentId)
					.userId(user.getId())
					.build();
			
			commentRepository.updateComment(comment);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId);
	}

	/**
	 * 댓글 추가 기능(POST 방식 처리)
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 */
	private void handleCreateComment(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		String content = request.getParameter("content");
		User user = (User) session.getAttribute("principal");
		int boardId = Integer.parseInt(request.getParameter("boardId"));
		
		Comment comment = Comment.builder()
				.boardId(boardId)
				.userId(user.getId())
				.content(content)
				.build();
		
		commentRepository.addComment(comment);
		
		response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId);
		
	}

	/**
	 * 댓글 삭제 기능(GET 방식 처리)
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 */
	private void handleDeleteComment(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		 try {
		        int commentId = Integer.parseInt(request.getParameter("id"));
		        int boardId = Integer.parseInt(request.getParameter("board_id"));

		        // 현재 로그인한 사용자의 ID
		        User user = (User) session.getAttribute("principal");

		        // 댓글 정보 가져오기
		        Comment comment = commentRepository.getCommentById(commentId);

		        if (comment != null && comment.getUserId() == user.getId()) {
		            commentRepository.deleteComment(commentId);
		            response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId);
		        } else {
		            // 유효하지 않은 접근
		            response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId + "&error=invalid");
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
	}
	
	/**
	 * 게시글 상세 보기 화면 이동(GET 방식 처리)
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 */
	private void showViewBoard(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		try {
			int boardId = Integer.parseInt(request.getParameter("id"));
			Board board = boardRepository.getBoardById(boardId);
			if (board == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}			
			// 현재 로그인한 사용자의 ID
			User user = (User) session.getAttribute("principal");
			if (user != null) {
				request.setAttribute("userId", user.getId());
			}
			
			// 댓글 조회 및 권한 확인 추가
			List<Comment> commentList = commentRepository.getCommentsByBoardId(boardId);
			
			request.setAttribute("board", board);
			request.setAttribute("commentList", commentList);
			request.getRequestDispatcher("/WEB-INF/views/board/view.jsp").forward(request, response);
		} catch (Exception e) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script> alert('잘못된 요청입니다'); history.back(); </script>");
		}
	}
	
	/**
	 * 수정 폼 화면 이동(인증 검사 반드시 필요)
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void showEditBoardForm(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {
		
		int boardId = Integer.parseInt(request.getParameter("id"));
		
		Board board = boardRepository.getBoardById(boardId);
		
		request.setAttribute("board", board);
		request.getRequestDispatcher("/WEB-INF/views/board/update.jsp").forward(request, response);
		
		
	}
	
	/**
	 * 게시글 삭제 기능 만들기
	 * @param request
	 * @param response
	 * @param session
	 */
	private void handleDeleteBoard(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
		try {
	        int boardId = Integer.parseInt(request.getParameter("id"));
	        Board board = boardRepository.getBoardById(boardId);
	        // 현재 로그인한 사용자의 ID
	        User user = (User) session.getAttribute("principal");
	        if (board.getUserId() == user.getId()) {
	            boardRepository.deleteBoard(boardId);
	            response.sendRedirect(request.getContextPath() + "/board/list?page=1");
	        } else {
	            // 유효하지 않은 접근
	            response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId + "&error=invalid");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
	}
	
	/**
	 * 게시글 생성 처리
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 */
	private void handleCreateBoard(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		
		// 유효성 검사 생략
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		User user = (User) session.getAttribute("principal");
		
		Board board = Board.builder()
				.userId(user.getId())
				.title(title)
				.content(content)
				.build();
		
		boardRepository.addBoard(board);
		response.sendRedirect(request.getContextPath() + "/board/list?page=1");
		
	}

	/**
	 * 게시글 생성 화면 이동
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void showCreateBoardForm(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
		
		request.getRequestDispatcher("/WEB-INF/views/board/create.jsp").forward(request, response);	
		
	}

	/**
	 * 게시글 리스트 페이지
	 * 페이징 처리 하기
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void handleListBoards(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
		
		// 게시글 목록 조회 기능
		int page = 1; // 기본 페이지 번호
		int pageSize = 3; // 한 페이지당 보여질 게시글의 수
		
		try {
			String pageStr = request.getParameter("page");
			if (pageStr != null) {
				page = Integer.parseInt(pageStr);
			}
		} catch (Exception e) {
			// 유효하지 않은 번호를 마음대로 보낼 경우
			page = 1;
		}	
		// pageSize = 3
		// page 1, page 2, page 3 요청시 동적으로 시작값을 계산하는 산수 공식 넣기
		int offset = (page - 1) * pageSize; // 시작 위치 계산(offset 값 계산)
		// pageSize <-- 한 페이지당 보여줄 게시글 수(limit로 바라볼 수 있다)
		List<Board> boardList = boardRepository.getAllBoards(pageSize, offset);
			
		// 페이징 처리 1단계(현재 페이지 번호, pageSize, offset)
		////////////////////////////////////////////////////////////
		// 전체 게시글 수 조회
		int totalBoards = boardRepository.getTotalBoardCount();
		
		// 총 페이지 수 계산 --> [1][2][3][...]
		int totalPages = (int) Math.ceil((double)totalBoards / pageSize);
		
		request.setAttribute("boardList", boardList);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("currentPage", page);
		
		// 현재 로그인한 사용자 ID 설정
		if (session != null) {
			User user =  (User) session.getAttribute("principal");
			if (user != null) {
				request.setAttribute("userId", user.getId());
			}
		}
		
		request.getRequestDispatcher("/WEB-INF/views/board/list.jsp").forward(request, response);
	}
}
