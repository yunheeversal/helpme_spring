package org.helpme.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.helpme.domain.Criteria;
import org.helpme.domain.PageMaker;
import org.helpme.domain.RatingVO;
import org.helpme.domain.ReviewVO;
import org.helpme.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
public class ReviewController {

	@Inject
	  private ReviewService service;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	  public ResponseEntity<String> register(@RequestBody ReviewVO reviewVO, HttpServletRequest request) {

	    ResponseEntity<String> entity = null;
	    try {
	    	HttpSession session = request.getSession();
			String userId = (String)session.getAttribute("userId");
	    	reviewVO.setUserId(userId);
	    	reviewVO.setRRegdate(new Date());
	    	
	      service.addReview(reviewVO);
	      entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	    } catch (Exception e) {
	      e.printStackTrace();
	      entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	    return entity;
	  }
	
	 @RequestMapping(value = "/{sNo}/{page}", method = RequestMethod.GET)
	  public ResponseEntity<Map<String, Object>> listPage(
	      @PathVariable("sNo") Integer sNo,
	      @PathVariable("page") Integer page) {

	    ResponseEntity<Map<String, Object>> entity = null;
	    
	    try {
	      Criteria cri = new Criteria();
	      cri.setPage(page);

	      PageMaker pageMaker = new PageMaker();
	      pageMaker.setCri(cri);

	      Map<String, Object> map = new HashMap<String, Object>();
	      
	      List<ReviewVO> list = service.listReviewPage(sNo, cri);
	      map.put("review", list);
	     

	      int reviewCount = service.count(sNo);
	      pageMaker.setTotalCount(reviewCount);
	      map.put("pageMaker", pageMaker);
	      
	      int one=0,two=0,three=0,four=0,five=0;
	      for(ReviewVO r: list) {
	    	  switch(r.getRating()) {
	    	  case 1:
	    		  one++; break;
	    	  case 2:
	    		  two++; break;
	    	  case 3:
	    		  three++; break;
	    	  case 4:
	    		  four++; break;
	    	  case 5:
	    		  five++; break;
	    	  }
	      }
	      RatingVO rating = new RatingVO(one,two,three,four,five);
	      map.put("rating", rating);

	      entity = new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);

	    } catch (Exception e) {
	      e.printStackTrace();
	      entity = new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
	    }
	    return entity;
	  }
	 
	 @RequestMapping(value = "/{rNo}", method = RequestMethod.DELETE)
	  public ResponseEntity<String> remove(@PathVariable("rNo") Integer rNo) {

	    ResponseEntity<String> entity = null;
	    try {
	      service.removeReview(rNo);
	      entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	    } catch (Exception e) {
	      e.printStackTrace();
	      entity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	    return entity;
	  }
	 
	 @RequestMapping(value = "/{rBoardId}", method = { RequestMethod.PUT, RequestMethod.PATCH })
	  public ResponseEntity<String> modify(@RequestBody ReviewVO reviewVO) {

	    ResponseEntity<String> entity = null;
	    try {
	    	reviewVO.setRRegdate(new Date());
	      service.modifyReview(reviewVO);

	      entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	    } catch (Exception e) {
	      e.printStackTrace();
	      entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	    return entity;
	  }
	
}
