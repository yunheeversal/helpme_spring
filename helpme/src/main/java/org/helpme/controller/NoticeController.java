package org.helpme.controller;


import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.helpme.domain.SearchCriteria;
import org.helpme.domain.NoticeVO;
import org.helpme.domain.PageMaker;
import org.helpme.service.NoticeService;

@Controller
@RequestMapping("/notice/*")
public class NoticeController {

	private static final Logger logger = LoggerFactory.getLogger(NoticeController.class);

	@Inject
	 private NoticeService service;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	  public void listPage(@ModelAttribute("cri") SearchCriteria cri, Model model) throws Exception {

	    logger.info(cri.toString());

	    // model.addAttribute("list", service.listCriteria(cri));
	    model.addAttribute("list", service.listSearchCriteria(cri));

	    PageMaker pageMaker = new PageMaker();
	    pageMaker.setCri(cri);

	    // pageMaker.setTotalCount(service.listCountCriteria(cri));
	    pageMaker.setTotalCount(service.listSearchCount(cri));

	    model.addAttribute("pageMaker", pageMaker);
	  }

	  @RequestMapping(value = "/read", method = RequestMethod.GET)
	  public void read(@RequestParam("noticeId") int noticeId, @ModelAttribute("cri") SearchCriteria cri, Model model) throws Exception {

	    model.addAttribute(service.read(noticeId));
	  }
		  
	
	  @RequestMapping(value = "/remove", method = RequestMethod.GET)
	  public String remove(@RequestParam("noticeId") int noticeId, SearchCriteria cri, RedirectAttributes rttr) throws Exception {

	    service.remove(noticeId);
	
	    rttr.addAttribute("page", cri.getPage());
	    rttr.addAttribute("perPageNum", cri.getPerPageNum());
	    rttr.addAttribute("searchType", cri.getSearchType());
	    rttr.addAttribute("keyword", cri.getKeyword());
	    rttr.addFlashAttribute("msg", "SUCCESS");

	    return "redirect:/notice/list";
	  }

	  @RequestMapping(value = "/modify", method = RequestMethod.GET)
	  public void modifyGET(int noticeId,@ModelAttribute("cri") SearchCriteria cri,  Model model) throws Exception {

	    model.addAttribute(service.read(noticeId));
	  }

	  @RequestMapping(value = "/modify", method = RequestMethod.POST)
	  public String modifyPOST(NoticeVO notice,  SearchCriteria cri, RedirectAttributes rttr) throws Exception {

	    
	    logger.info(cri.toString());
	    service.modify(notice);
	    
	    rttr.addAttribute("page", cri.getPage());
	    rttr.addAttribute("perPageNum", cri.getPerPageNum());
	    rttr.addAttribute("searchType", cri.getSearchType());
	    rttr.addAttribute("keyword", cri.getKeyword());
	    
	    rttr.addFlashAttribute("msg", "SUCCESS");

	    logger.info(rttr.toString());
	    
	    return "redirect:/notice/list";
	  }

	  @RequestMapping(value = "/register", method = RequestMethod.GET)
	  public void registGET() throws Exception {

	    logger.info("regist get ...........");
	  }

	  @RequestMapping(value = "/register", method = RequestMethod.POST)
	  public String registPOST(NoticeVO notice, RedirectAttributes rttr) throws Exception {

	    logger.info("regist post ...........");
	    logger.info(notice.toString());

	    service.create(notice);

	    rttr.addFlashAttribute("msg", "SUCCESS");

	    return "redirect:/notice/list";
	  }
	  
	  }  
