package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.domain.Board;
import com.example.demo.domain.Like;
import com.example.demo.mapper.BoardMapper;
import com.example.demo.service.BoardService;

@Controller
@RequestMapping("/")
public class BoardController {

//	@Autowired
//	private BoardMapper mapper;

	@Autowired
	private BoardService service;

	// 경로 : http://localhost:8080?page=3
	// 경로 : http://localhost:8080/list?page=3
	// 게시물 목록
//	@RequestMapping(value={"/", "list"}, method=RequestMethod.GET)	
	@GetMapping({ "/", "list" })
	public String list(Model model, 
					   @RequestParam(value = "page", defaultValue = "1") Integer page,
					   @RequestParam(value = "search", defaultValue = "") String search,
					   @RequestParam(value = "type", required = false/* type 파라미터가 null도 가능 */) String type) {
		// 1. request param 수집 / 가공
		// 2. business logic 처리
//		List<Board> list = mapper.selectAll();
//		List<Board> list = service.listBoard(); // 페이지 처리 전
		Map<String, Object> result = service.listBoard(page, search, type); // 페이지 처리

		// 3. add attribute
//		model.addAttribute("boardList", result.get("boardList"));
//		model.addAttribute("pageInfo", result.get("pageInfo"));
		model.addAllAttributes(result);

		// 4. forward / redirect
		return "list";
	}

	@GetMapping("/id/{id}")
	public String board(
			@PathVariable("id") Integer id, 
			Model model,
			Authentication authentication) {
		// 1. request param 수집 / 가공
		// 2. business logic
		Board board = service.getBoard(id, authentication);
		// 3. add attribute
		model.addAttribute("board", board);
//		System.out.println(board);
		// 4. forward / redirect
		return "get";
	}

	@GetMapping("/modify/{id}")
	@PreAuthorize("isAuthenticated()and @customSecurityChecker.checkBoardWriter(authentication, #id)")
	public String modifyForm(@PathVariable("id") Integer id, Model model) {

		model.addAttribute("board", service.getBoard(id));
		return "modify";
	}

	@PostMapping("/modify/{id}")
	@PreAuthorize("isAuthenticated() and @customSecurityChecker.checkBoardWriter(authentication, #board.id)")
	// 수정하려는 게시물 id : board.getId
	public String modifyProcess(Board board,
								@RequestParam(value = "files", required = false) MultipartFile[] addFiles,
								@RequestParam(value = "modifyFiles", required = false) List<String> modifyFileNames,
								RedirectAttributes rttr) throws Exception {

		boolean ok = service.modify(board, modifyFileNames, addFiles);
		if (ok) {
//			return "redirect:/list";
			// 해당 게시물 보기로 리디렉션
//			rttr.addAttribute("success", "success");
			rttr.addFlashAttribute("message", board.getId() + "번 게시물이 수정되었습니다.");
			return "redirect:/id/" + board.getId();
		} else {
			// 수정 form으로 리디렉션
//			rttr.addAttribute("fail", "fail");
			rttr.addFlashAttribute("message", board.getId() + "번 게시물이 수정되지 않았습니다.");
			return "redirect:/modify/" + board.getId();
		}
	}

	@PostMapping("remove")
	@PreAuthorize("isAuthenticated() and @customSecurityChecker.checkBoardWriter(authentication, #id)")
	public String remove(Integer id, RedirectAttributes rttr) {
		boolean ok = service.remove(id);
		if (ok) {
			// queryString에 추가
//			rttr.addAttribute("success", "remove");

			// 모델에 추가
			rttr.addFlashAttribute("message", id + "번 게시물이 삭제되었습니다.");
			return "redirect:/list";
		} else {
			rttr.addAttribute("fail", "remove fail");
			return "redirect:/id/" + id;
		}
	}

	@GetMapping("add")
	@PreAuthorize("isAuthenticated()")
	public String addForm(Board board, Model model) {
		// 게시물 작성 form(view)로 포워드

		return "add";
	}

	@PostMapping("add")
	@PreAuthorize("isAuthenticated()")
	public String addProcess(@RequestParam("files") MultipartFile[] files, Board board, RedirectAttributes rttr,
			Authentication authentication) throws Exception {
		// 새 게시물 db에 추가
		// 1.
		// 2.
		board.setWriter(authentication.getName());
		boolean ok = service.addBoard(board, files);
		// 3.
		if (ok) {
//			rttr.addAttribute("success", "addSuccess");
//			return "redirect:/list";
			rttr.addFlashAttribute("message", board.getId() + "번 게시물이 등록되었습니다.");
			return "redirect:/id/" + board.getId();
		} else {
//			rttr.addAttribute("fail", "addFail");
//			rttr.addFlashAttribute("board",board);
			rttr.addFlashAttribute("message", board.getId() + "번 게시물 등록 중 문제가 발생하였습니다.");
			return "redirect:/add";
		}
		// 4.

	}

	@PostMapping("/like")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> like(@RequestBody Like like, Authentication authentication) {

		if (authentication == null) {
			return ResponseEntity.status(403)
								 .body(Map.of("message", "로그인 후 좋아요 클릭해주세요!"));
		} else {
			return ResponseEntity.ok()
								 .body(service.like(like, authentication));
		}
	}
}
