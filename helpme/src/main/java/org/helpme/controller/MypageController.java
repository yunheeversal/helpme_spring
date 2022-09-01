package org.helpme.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.helpme.domain.MemberVO;
import org.helpme.domain.MypageCriteria;
import org.helpme.domain.MypagePageMaker;
import org.helpme.domain.PointVO;
import org.helpme.domain.ServiceVO;
import org.helpme.service.MypageService;
import org.helpme.service.ServiceService;
import org.helpme.util.MediaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping(value = "/my/*")
public class MypageController {

	private static final Logger logger = LoggerFactory.getLogger(MypageController.class);

	// 이미지 가져오기
	@Resource(name = "uploadPath")
	private String uploadPath;
	
	@Inject
	private MypageService service;

	/*
	 * 거래내역 보기 최근 본 서비스 내가 쓴 후기 1:1 문의 내역 보기 찜 목록 차단 목록 신고 목록 적립금 조회
	 */

	// 회원 정보 조회 

	
  @RequestMapping(value = "/mypage", method = RequestMethod.GET) public void
  modify(HttpServletRequest request, Model model,HttpSession session, RedirectAttributes rttr) throws Exception {
  
	  MemberVO login = (MemberVO) request.getSession().getAttribute("member");
		if (login != null) {
			String userId = login.getUserId();
			session = request.getSession();	
			MemberVO memberVO = service.selectId(userId);
			model.addAttribute("member", memberVO);
			
			PointVO pointVO = service.selectMyPoint(userId);
			model.addAttribute("point", pointVO);
			
		}
		else {
			model.addAttribute("member", null);
			model.addAttribute("point", null);
		}

		
  }


	// 거래 내역 보기 -페이징

	@RequestMapping(value = "/mydeal", method = RequestMethod.GET)
	public void mydeal(@ModelAttribute("cri") MypageCriteria cri, HttpServletRequest request,Model model)
			throws Exception {

		logger.info(cri.toString());
		
		MemberVO login = (MemberVO) request.getSession().getAttribute("member");

		if (login != null) {
			String userId = login.getUserId();
		List<?> deal = service.listCriteria(userId);
		model.addAttribute("deal", deal);

		MypagePageMaker pageMaker = new MypagePageMaker();
		pageMaker.setCri(cri);

		pageMaker.setTotalCount(service.listSearchCount(cri));

		model.addAttribute("pageMaker", pageMaker);
		}else {
			model.addAttribute("member", null);
		}

	}

	// 리뷰 페이징
	@RequestMapping(value = "/myreview", method = RequestMethod.GET)
	public void myreview(@ModelAttribute("cri") MypageCriteria cri, HttpServletRequest request,Model model)
			throws Exception {
		logger.info(cri.toString());
		
		MemberVO login = (MemberVO) request.getSession().getAttribute("member");
		
		if (login != null) {
			String userId = login.getUserId();
		
		List<?> review = service.listReviewCriteria(userId);
		model.addAttribute("review", review);

		MypagePageMaker pageMaker = new MypagePageMaker();
		pageMaker.setCri(cri);

		pageMaker.setTotalCount(service.listReviewSearchCount(cri));

		model.addAttribute("pageMaker", pageMaker);
		
		ServiceService serviceS;
		
		
		}else {
			model.addAttribute("member", null);
		}

	}

	// 이미지 가져오기
	@ResponseBody
	@RequestMapping("/displayFile")
	public ResponseEntity<byte[]> displayFile(String fileName) throws Exception {

		InputStream in = null;
		ResponseEntity<byte[]> entity = null;

		logger.info("FILE NAME: " + fileName);

		try {

			String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);

			MediaType mType = MediaUtils.getMediaType(formatName);

			HttpHeaders headers = new HttpHeaders();

			in = new FileInputStream(uploadPath + "/" + fileName);

			if (mType != null) {
				headers.setContentType(mType);
			} else {

				fileName = fileName.substring(fileName.indexOf("_") + 1);
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.add("Content-Disposition",
						"attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
			}

			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
			System.out.println("entity=" + entity);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		} finally {
			in.close();
		}
		return entity;
	}

	// 찜 목록 페이징
	@RequestMapping(value = "/likelist", method = RequestMethod.GET)
	public void likelist(@ModelAttribute("cri") MypageCriteria cri, HttpServletRequest request,Model model)
			throws Exception {
		logger.info(cri.toString());
		
		MemberVO login = (MemberVO) request.getSession().getAttribute("member");
		
		if (login != null) {
		
			String userId = login.getUserId();
		List<?> likelist = service.listLikeCriteria(userId);
		model.addAttribute("like", likelist);

		MypagePageMaker pageMaker = new MypagePageMaker();
		pageMaker.setCri(cri);

		pageMaker.setTotalCount(service.listLikeSearchCount(cri));

		model.addAttribute("pageMaker", pageMaker);
		}else {
			model.addAttribute("member", null);
		}

	}

	// 적립금 조회
	@RequestMapping(value = "/mypoint", method = RequestMethod.GET)
	public void mypoint(HttpServletRequest request, Model model) throws Exception {
		
		MemberVO login = (MemberVO) request.getSession().getAttribute("member");
		String userId = login.getUserId();
		
		PointVO pointVO = service.selectMyPoint(userId);
		model.addAttribute("point", pointVO);

	}

	// 최근 본 서비스

	@RequestMapping(value = "/latestservice", method = RequestMethod.GET)
	public void latestservice(@ModelAttribute("cri") MypageCriteria cri,HttpServletRequest request, Model model) throws Exception {

		MemberVO login = (MemberVO) request.getSession().getAttribute("member");
		
		if (login != null) {
			String userId = login.getUserId();
		try {
			ArrayList<Integer> list = (ArrayList)request.getSession().getAttribute("sNo");
			ArrayList<ServiceVO> result = null;
			if(list!=null) {
				result = new ArrayList<>();
				for(Integer i: list) {
					ServiceVO serviceOne = service.listLatestCriteria(i);
					result.add(serviceOne);
				}
			}
			
			model.addAttribute("list", result);

			MypagePageMaker pageMaker = new MypagePageMaker();
			pageMaker.setCri(cri);

			pageMaker.setTotalCount(service.listLatestSearchCount(cri));

			model.addAttribute("pageMaker", pageMaker);
			
			
			MemberVO memberVO = service.selectId(userId);
			model.addAttribute("member", memberVO);


		} catch (Exception e) {
			List<?> list = null;
			model.addAttribute("list", list);

		}
		}else {
			model.addAttribute("member", null);
		}

		
		
		
	}

}
