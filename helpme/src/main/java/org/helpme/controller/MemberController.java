package org.helpme.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.helpme.domain.AccountInfoVO;
import org.helpme.domain.Criteria;
import org.helpme.domain.MemberVO;
import org.helpme.domain.PageMaker;
import org.helpme.domain.Password;
import org.helpme.domain.ServiceVO;
/*import org.helpme.domain.PageMaker;
import org.helpme.domain.ServiceVO;*/
import org.helpme.dto.LoginDTO;
import org.helpme.dto.ReviewDTO;
/*import org.helpme.dto.ReviewDTO;*/
import org.helpme.service.MemberService;
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
@RequestMapping(value = "/main")
public class MemberController {

	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

	// 이미지 가져오기
	@Resource(name = "uploadPath")
	private String uploadPath;
	
	
	@Inject
	private MemberService service;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public void loginGET(@ModelAttribute("dto") LoginDTO dto,HttpServletRequest request) {
		logger.info("// /main/login");
		
		String referer = request.getHeader("Referer");
		System.out.println("aaaaaaaaaaa");
		System.out.println(referer.getClass());
		if(!referer.equals("http://localhost:8080/main/login")&&
				!referer.equals("http://localhost:8080/main/register")) {
	    request.getSession().setAttribute("referer", referer);
		}
		if(referer.equals("http://localhost:8080/main/register")) {
			request.getSession().setAttribute("referer", "http://localhost:8080/main/index");
		}
		// model.addAttribute("list", list);
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(@ModelAttribute("cri") Criteria cri, Model model) throws Exception {
		logger.info(cri.toString());
		// index 메인 
		
		// bestHelper
		List<ReviewDTO> best = service.bestHelper();
		while(best.size()!=8) {
				ReviewDTO r = new ReviewDTO();
				r.setsAttach("basic.jpg");
				r.setsName("서비스 없음");
				r.setRating(1);
				best.add(r);
			
		}
		model.addAttribute("best", best);
		
		PageMaker bestpageMaker = new PageMaker();
		bestpageMaker.setCri(cri);

		bestpageMaker.setTotalCount(8);

		model.addAttribute("bestpageMaker", bestpageMaker);
		
		// New Helper
		List<ServiceVO> newhelper = service.newHelper();
		while(newhelper.size()!=8) {
			ServiceVO s = new ServiceVO();
			s.setSAttach("basic.jpg");
			s.setSName("서비스 없음");
			newhelper.add(s);
		
		}
		model.addAttribute("newhelper", newhelper);
		
		// 리뷰 
		List<ReviewDTO> review = service.mainReview();
		model.addAttribute("review", review); 
		
		return "/main/index";
	}

	@RequestMapping(value = "/index", method = RequestMethod.POST)
	public String indexPost(Model model) throws Exception {

		// index 메인 
		
		// bestHelper
		List<ReviewDTO> best = service.bestHelper();
		model.addAttribute("best", best);
		
		// New Helper
		List<ServiceVO> newhelper = service.newHelper();
		model.addAttribute("newhelper", newhelper);
		
		// 리뷰 
		List<ReviewDTO> review = service.mainReview();
		model.addAttribute("review", review); 
		
		return "/main/index";
	}

	// 세션 만들기
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginPOST(LoginDTO dto, HttpServletRequest request, HttpSession session, Model model, RedirectAttributes rttr)
			throws Exception {
		
		 session = request.getSession();
		 MemberVO memberVO = service.login(dto);
		 	 
		 if (memberVO == null) {
			session.setAttribute("member", null);
		    rttr.addFlashAttribute("msg", false);
			return "redirect:/main/login";
			}
		 else {
			 session.setAttribute("member", memberVO);
			 // userId 세션 추가 
			 session.setAttribute("userId", memberVO.getUserId());
//			 return "redirect:/main/index";
			 String referer = (String)session.getAttribute("referer");
			 return "redirect:"+ referer;
		 }
		 
		}

	@ResponseBody
	@RequestMapping(value = "/idCheck", method = RequestMethod.POST)
	public int idCheck(String userId) throws Exception {
		return service.idCheck(userId);
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			RedirectAttributes rttr) throws Exception {

		Object obj = session.getAttribute("member");

		if (obj != null) {
			MemberVO vo = (MemberVO) obj;

			session.removeAttribute("member");
			session.invalidate();

		}
		String referer = request.getHeader("Referer");	
		return "redirect:"+referer;
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public void register() {

	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(MemberVO member, String userId) throws Exception {
		
		int idResult = service.idCheck(userId);

        try {
            if (idResult == 1) {
                return "/main/register";
            } else if(idResult == 0){
                service.create(member);
                logger.info("register success");
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

		return "redirect:/main/login";
	}
	
	@RequestMapping(value="/changePwd", method=RequestMethod.GET)
	public String changePwd() throws Exception{
		return "/main/changePwd";
	}

	@RequestMapping(value="/pwCheck" , method=RequestMethod.POST)
	@ResponseBody
	public int pwCheck(MemberVO memberVO, Password password)  throws Exception{
		
		String userPw = service.pwCheck(memberVO.getUserId());
		String currentPw = password.getCurrentPw();
		
		logger.info("userPw ="+userPw);
		logger.info("currentPw="+currentPw);
		
		if( !(userPw.equals(currentPw))) {
			return 0;
		}
		    return 1;
	}
	
	@RequestMapping(value="/changePwd" , method=RequestMethod.POST)
	public String changePwd(Password password, String userId, RedirectAttributes rttr, HttpSession session) throws Exception{
				
		String newPw = password.getNewPw();
		
		service.changePwd(userId, newPw);
		
		session.invalidate();
		rttr.addFlashAttribute("msg", "비밀번호 변경이 완료되었습니다. 다시 로그인해주세요.");
		
		return "/main/login";
	}
	
	
	/*
	 * @RequestMapping(value = "/sendMail", method = RequestMethod.POST) public
	 * String sendMail (HttpServletRequest request, HttpServletResponse response) {
	 * String email = request.getParameter("userEmail");
	 * 
	 * service.sendMail();
	 * 
	 * return "main/searchPwd"; }
	 */

	// mypage

	@RequestMapping(value = "/modify", method = RequestMethod.GET)
	public void modify(HttpServletRequest request, RedirectAttributes rttr, Model model) throws Exception {
		 
		MemberVO login = (MemberVO) request.getSession().getAttribute("member");
			
		if (login != null) {
			String userId = login.getUserId();
			MemberVO memberVO = service.selectOne(userId);
			model.addAttribute("vo", memberVO);
			
			AccountInfoVO accountVO = service.selectAccount(userId);
			
			model.addAttribute("accountVO", accountVO);
			//return "redirect:/main/modify";
		}else {
			model.addAttribute("member", null);
		}

		
		
	}

	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public String modify(MemberVO vo, AccountInfoVO accountVO, RedirectAttributes rttr) throws Exception {
		
		service.update(vo);
		
		if(accountVO.getAccountNo() != null && accountVO.getBankName()!=null) {
            if(service.getAccount(accountVO)) {
                service.updateAccount(accountVO);
            }else {
                service.accountCreate(accountVO);

            }
		}
		
		rttr.addFlashAttribute("vo", vo);
	

		return "redirect:/main/modify";
	}

	

	@RequestMapping(value = "/deleteId", method = RequestMethod.GET)
	public String deleteId(@RequestParam("userId") String userId, RedirectAttributes rttr,  HttpSession session) throws Exception {
		service.delete(userId);
		session.invalidate();
		
		rttr.addFlashAttribute("msg", "비밀번호 변경이 완료되었습니다. 다시 로그인해주세요.");
		
		return "/main/login";
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
	
	@RequestMapping(value = "/findpw", method = RequestMethod.GET)
	public void findPwGET() throws Exception{
	}

	@RequestMapping(value = "/findpw", method = RequestMethod.POST)
	public void findPwPOST(@ModelAttribute MemberVO member, HttpServletResponse response) throws Exception{
		System.out.println("aaaaA:"+member.getUserId()+"aaaaaa:"+member.getUserEmail());
		service.findPw(response, member);
	}

	
}

	