package org.helpme.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.helpme.domain.Criteria;
import org.helpme.domain.PageMaker;
import org.helpme.domain.ReplyVO;
import org.helpme.service.ReplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/reply/*")
public class ReplyController {
	private static final Logger logger = LoggerFactory.getLogger(CommunityController.class);
	
	@Inject
	private ReplyService ReplyService;
	
	// 댓글 작성
	@RequestMapping(value = "/replyWrite", method = RequestMethod.POST)
	public ResponseEntity<String> replywrite(@RequestBody ReplyVO ReplyVO) {
		ResponseEntity<String> entity = null;

		try {
			ReplyService.replywrite(ReplyVO);
			entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	// 댓글 전체 보기
	@RequestMapping(value = "/all/{cBoardId}", method = RequestMethod.GET)
	public ResponseEntity<List<ReplyVO>> replylist(@PathVariable("cBoardId") Integer cBoardId) {
		ResponseEntity<List<ReplyVO>> entity = null;
		try {
			
			entity = new ResponseEntity<>(ReplyService.replylist(cBoardId), HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	
	// 댓글 수정
	@RequestMapping(value = "/{replyNo}", method = { RequestMethod.PUT, RequestMethod.PATCH })
	public ResponseEntity<String> replymodify(@PathVariable("replyNo") Integer replyNo, @RequestBody ReplyVO ReplyVO) {
		ResponseEntity<String> entity = null;
		try {
			
			ReplyVO.setReplyNo(replyNo);
			ReplyService.replymodify(ReplyVO);
			entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	// 댓글 삭제
	@RequestMapping(value = "/{replyNo}", method = RequestMethod.DELETE)
	public ResponseEntity<String> replyremove(@PathVariable("replyNo") Integer replyNo) {
		ResponseEntity<String> entity = null;
		try {
			ReplyService.replyremove(replyNo);
			entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
		} catch (Exception e) {
		e.printStackTrace();
		entity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	// 댓글 페이지 처리... 모르겠음 ㅠㅠㅠ
	@RequestMapping(value = "/{cBoardId}/{page}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> listPage(
		@PathVariable("cBoardId") Integer cBoardId,
		@PathVariable("page") Integer page) {
		ResponseEntity<Map<String, Object>> entity = null;
	 
		try {
			Criteria cri = new Criteria();
			cri.setPage(page);
			PageMaker pageMaker = new PageMaker();
			pageMaker.setCri(cri);
			Map<String, Object> map = new HashMap<String, Object>();
			
			List<ReplyVO> list = ReplyService.replylistpage(cBoardId, cri);
			map.put("list", list);
			
			int replycount = ReplyService.replycount(cBoardId);
			pageMaker.setTotalCount(replycount);
			map.put("pageMaker", pageMaker);
			
			entity = new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
}
