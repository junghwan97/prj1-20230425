package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.domain.Board;
import com.example.demo.mapper.BoardMapper;
import com.example.demo.service.BoardService;

@Controller
@RequestMapping("/")
public class BoardController {

//	@Autowired
//	private BoardMapper mapper;

	@Autowired
	private BoardService service;

	// 경로 : http://localhost:8080
	// 경로 : http://localhost:8080/list
	// 게시물 목록
//	@RequestMapping(value={"/", "list"}, method=RequestMethod.GET)	
	@GetMapping({ "/", "list" })
	public String list(Model model) {
		// 1. request param 수집 / 가공
		// 2. business logic 처리
//		List<Board> list = mapper.selectAll();
		List<Board> list = service.listBoard();

		// 3. add attribute
		model.addAttribute("boardList", list);

		System.out.println(list.size());
		// 4. forward / redirect
		return "list";
	}

	@GetMapping("/id/{id}")
	public String board(@PathVariable("id") Integer id, Model model) {
		// 1. request param 수집 / 가공
		// 2. business logic
		Board board = service.getBoard(id);
		// 3. add attribute
		model.addAttribute("board", board);
//		System.out.println(board);
		// 4. forward / redirect
		return "get";
	}

	@GetMapping("/modify/{id}")
	public String modifyForm(@PathVariable("id") Integer id, Model model) {

		model.addAttribute("board", service.getBoard(id));
		return "modify";
	}

	@PostMapping("/modify/{id}")
	public String modifyProcess(Board board, RedirectAttributes rttr) {
//		System.out.println(board);
		boolean ok = service.modify(board);
		if (ok) {
//			return "redirect:/list";
			// 해당 게시물 보기로 리디렉션
			rttr.addAttribute("success", "success");
			return "redirect:/id/" + board.getId();
		} else {
			// 수정 form으로 리디렉션
			rttr.addAttribute("fail", "fail");
			return "redirect:/modify/" + board.getId();
		}
	}
	
	@PostMapping("remove")
	public String remove(Integer id, RedirectAttributes rttr) {
		boolean ok = service.remove(id);
		if(ok) {
			rttr.addAttribute("success", "remove");
			return "redirect:/list";
		}else {
			rttr.addAttribute("fail", "remove fail");
			return "redirect:/id/" + id;
		}
	}
}
