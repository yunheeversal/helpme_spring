package org.helpme.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.helpme.domain.PageMaker;
import org.helpme.domain.SearchCriteria;
import org.helpme.domain.ServiceVO;
import org.helpme.dto.DealDTO;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/service/*")
public class ServiceController {

	private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

	@Resource(name = "uploadPath")
	private String uploadPath;

	@Inject
	private ServiceService serviceS;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public void listPage(@ModelAttribute("cri") SearchCriteria cri, Model model) throws Exception {
		System.out.println("keyword=" + cri.getKeyword());
		model.addAttribute("list", serviceS.listSearchCriteria(cri));

		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(cri);

		pageMaker.setTotalCount(serviceS.listSearchCount(cri));

		model.addAttribute("pageMaker", pageMaker);
	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public void read(@RequestParam("sNo") int sNo, @ModelAttribute("cri") SearchCriteria cri, Model model,
			HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		String logIn = (String)session.getAttribute("userId");
		
		model.addAttribute("service", serviceS.read(sNo));
//		model.addAttribute("logIn", null);

		boolean completeCheck = false;
		if(logIn!=null) {
			completeCheck = serviceS.purchaseCheck(sNo, logIn);
		}

		model.addAttribute("completeId", completeCheck);
		model.addAttribute("serviceAttach", serviceS.getAttach(sNo));
		
		boolean likeIt =true;
		
		// ?????? ??? ????????? HttpServletRequest??? ?????? - ??????
		if(logIn!=null) {
			ArrayList<Integer>sNoList = (ArrayList<Integer>)session.getAttribute("sNo");
			boolean check = true;
			
			if(sNoList==null) {
				sNoList = new ArrayList<Integer>();
			}else {
				check = !sNoList.contains(sNo);
			}
			
			if(check) {
				if(sNoList.size()==8) {
					sNoList.remove(0);
					sNoList.add(sNo);
				}else {
					sNoList.add(sNo);
				}
			}
			session.setAttribute("sNo", sNoList);
			
			likeIt = serviceS.likeServiceCheck(logIn,sNo);
		}
		
		model.addAttribute("likeService", likeIt);
		session.setAttribute("currentsNo", sNo);
		
		
	}

	@RequestMapping(value = "/remove", method = RequestMethod.GET)
	public String remove(@RequestParam("sNo") int sNo, RedirectAttributes rttr) throws Exception {

		deleteFile(serviceS.read(sNo).getSAttach());
		String[] serviceAttach = serviceS.getAttach(sNo);
		if (serviceAttach != null && serviceAttach.length != 0) {
			for (String s : serviceAttach)
				deleteDetailFile(s);
		}
		serviceS.remove(sNo);
		serviceS.deleteDetail(sNo);

		rttr.addFlashAttribute("msg", "SUCCESS");

		return "redirect:/service/list";
	}

	@RequestMapping(value = "/modifyForm", method = RequestMethod.GET)
	public void modifyPagingGET(@RequestParam("sNo") int sNo, @ModelAttribute("cri") SearchCriteria cri, Model model)
			throws Exception {

		model.addAttribute("service", serviceS.read(sNo));
		model.addAttribute("serviceAttach", serviceS.getAttach(sNo));
	}

	@RequestMapping(value = "/modifyForm", method = RequestMethod.POST)
	public String modifyPagingPOST(ServiceVO service,
			@RequestParam(name = "attachFile", required = false) MultipartFile file,
			@RequestParam(name = "detailFile", required = false) List<MultipartFile> detailFile,
			RedirectAttributes rttr) throws Exception {

		if (file.getOriginalFilename() != null && file.getOriginalFilename() != "" && checkFile(file)) {
			String serviceFileName = serviceS.read(service.getSNo()).getSAttach();

			deleteFile(serviceFileName);
			service.setSAttach(uploadFile(file.getOriginalFilename(), file.getBytes()));

		}

		boolean detailCheck = detailFile.get(0).getOriginalFilename() != null
				&& detailFile.get(0).getOriginalFilename() != "";

		if (detailCheck) {
			String[] serviceAttach = serviceS.getAttach(service.getSNo());
			if (serviceAttach != null && serviceAttach.length != 0) {
				for (String s : serviceAttach)
					deleteDetailFile(s);
			}
			serviceS.deleteDetail(service.getSNo());
			for (MultipartFile m : detailFile) {

				if (m.getOriginalFilename() != null && m.getOriginalFilename() != "" && checkFile(m))
					serviceS.detailAttach(service.getSNo(), uploadDetailFile(m.getOriginalFilename(), m.getBytes()));
			}

		}

		service.setSRegdate(new Date());

		serviceS.modify(service);

		rttr.addFlashAttribute("msg", "SUCCESS");

		logger.info(rttr.toString());

		return "redirect:/service/detail?sNo=" + service.getSNo();
	}

	@RequestMapping(value = "/newService", method = RequestMethod.GET)
	public void registGET(HttpSession session, Model model) throws Exception {

//		logger.info("regist get ...........");
//
//		Object obj = session.getAttribute("login");
//		model.addAttribute("logIn", obj);

	}

	@RequestMapping(value = "/newService", method = RequestMethod.POST)
	public String registPOST(ServiceVO service, @RequestParam(name = "attachFile", required = false) MultipartFile file,
			@RequestParam(name = "detailFile", required = false) List<MultipartFile> detailFile,
			RedirectAttributes rttr, HttpServletRequest request) throws Exception {

		logger.info("regist post ...........");
		logger.info(service.toString());

		if (file != null && checkFile(file)) {
			service.setSAttach(uploadFile(file.getOriginalFilename(), file.getBytes()));
		} else {
			service.setSAttach("basic.jpg");
		}

		HttpSession session = request.getSession();
		String userId = (String)session.getAttribute("userId");
		service.setUserId(userId);
		service.setSRegdate(new Date());
		serviceS.regist(service);

		for (MultipartFile m : detailFile) {
			if (m.getOriginalFilename() != null && m.getOriginalFilename() != "" && checkFile(m)) {
				serviceS.detailAttach(serviceS.getLastSNo(), uploadDetailFile(m.getOriginalFilename(), m.getBytes()));
			}
		}

		rttr.addFlashAttribute("msg", "SUCCESS");

		return "redirect:/service/list";
	}

	@RequestMapping(value = "/{sNo}", method = { RequestMethod.PUT, RequestMethod.PATCH })
	public ResponseEntity<String> likeIt(@PathVariable("sNo") Integer sNo, HttpServletRequest request)
			throws Exception {

		ResponseEntity<String> entity = null;

		HttpSession session = request.getSession();
		String userId = (String)session.getAttribute("userId");		

		try {
			if (userId != null) {
				if (!serviceS.likeCheck(sNo, userId)) {
					serviceS.likeIt(sNo, userId);
					entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	@RequestMapping(value = "/{sNo}/{price}", method = { RequestMethod.POST })
	public ResponseEntity<String> purchase(@PathVariable("sNo") Integer sNo, @PathVariable("price") Integer price,
			HttpServletRequest request) throws Exception {

		ResponseEntity<String> entity = null;

		HttpSession session = request.getSession();
		String userId = (String)session.getAttribute("userId");	

		try {
			if (!serviceS.purchaseCheck(sNo, userId)) {
				DealDTO deal = new DealDTO();
				deal.setsNo(sNo);
				deal.setdPrice(price);
				deal.setdDate(new Date());
				deal.setUserId(userId);
				serviceS.purchase(deal);
				entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

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

	@ResponseBody
	@RequestMapping("/displayAttach")
	public ResponseEntity<byte[]> displayAttach(String fileName) throws Exception {

		InputStream in = null;
		ResponseEntity<byte[]> entity = null;

		logger.info("FILE NAME: " + fileName);

		try {

			String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);

			MediaType mType = MediaUtils.getMediaType(formatName);

			HttpHeaders headers = new HttpHeaders();

			in = new FileInputStream(uploadPath + "/detail/" + fileName);

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

	private String uploadFile(String originalName, byte[] fileData) throws Exception {

		UUID uid = UUID.randomUUID();

		String savedName = uid.toString() + "_" + originalName;

		File target = new File(uploadPath, savedName);

		FileCopyUtils.copy(fileData, target);

		return savedName;

	}

	private String uploadDetailFile(String originalName, byte[] fileData) throws Exception {

		UUID uid = UUID.randomUUID();

		String savedName = uid.toString() + "_" + originalName;

		File target = new File(uploadPath + "\\detail", savedName);

		FileCopyUtils.copy(fileData, target);

		return savedName;

	}

	private void deleteFile(String fileName) {
		if(!fileName.equals("basic.jpg"))
		new File(uploadPath + "\\" + fileName).delete();
	}

	private void deleteDetailFile(String fileName) {

		new File(uploadPath + "\\detail\\" + fileName).delete();
	}

	private boolean checkFile(MultipartFile m) {
		if(m.getOriginalFilename()==null||m.getOriginalFilename()=="") {
			return false;
		}else {
			if (m.getOriginalFilename().substring(m.getOriginalFilename().length() - 3).toUpperCase().equals("JPG")
					|| m.getOriginalFilename().substring(m.getOriginalFilename().length() - 3).toUpperCase().equals("JPEG")
					|| m.getOriginalFilename().substring(m.getOriginalFilename().length() - 3).toUpperCase().equals("GIF")
					|| m.getOriginalFilename().substring(m.getOriginalFilename().length() - 3).toUpperCase().equals("PNG"))
				return true;
			return false;
		}
		
	}

	// ??????????????? ??????
	@RequestMapping(value = "/kakaopay")
	@ResponseBody
	public String kakaopay(HttpServletRequest request) throws Exception {
		
		HttpSession session = request.getSession();
		Integer sNo = (Integer)session.getAttribute("currentsNo");	
		
		ServiceVO service = serviceS.read(sNo); // ????????? ?????? 
		String itemName = service.getSName();
		String order_id = service.getSNo() + itemName;
		
		String userId = (String)session.getAttribute("userId");
		
		int totalAmount = service.getPrice();
		int vat = (int) totalAmount * 10;


		try {
			// ????????? ??????
			URL address = new URL("https://kapi.kakao.com/v1/payment/ready");
			HttpURLConnection connection = (HttpURLConnection) address.openConnection(); // ????????????
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "KakaoAK a20c2086f2478cc2e8ce8c7456f78a55"); // ????????? ???
			connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			connection.setDoOutput(true); // ???????????? ???????????? ????????? ?????????

			String parameter = "cid=TC0ONETIME" // ????????? ??????
					+ "&partner_order_id="+order_id// ????????? ????????????
					+ "&partner_user_id="+userId// ????????? ?????? id
					+ "&item_name="+URLEncoder.encode(itemName, "utf-8") // ?????????
					+ "&quantity=1" // ?????? ??????
					+ "&total_amount="+totalAmount // ??? ??????
					+ "&vat_amount=0" // ?????????
					+ "&tax_free_amount=0" // ?????? ????????? ??????
					+ "&approval_url=http://localhost:8080/order/orderSuccess" // ?????? ?????? ???
					+ "&fail_url=http://localhost:8080/order/orderFail" // ?????? ?????? ???
					+ "&cancel_url=http://localhost:8080/main/index"; // ?????? ?????? ???
			OutputStream send = connection.getOutputStream(); // ?????? ????????? ??? ??? ??? ??????.
			DataOutputStream dataSend = new DataOutputStream(send); // ?????? ???????????? ??? ??? ??????.
			dataSend.writeBytes(parameter); // OutputStream??? ???????????? ????????? ???????????? ?????? ????????? ???????????? ??????. (?????????)
			dataSend.close(); // flush??? ???????????? ????????? ?????? ?????????. (????????? ????????? ??????)
			
			int result = connection.getResponseCode(); // ?????? ??? ?????? ????????? ????????? ?????????.
			InputStream receive; // ??????
			
			if(result == 200) {
				receive = connection.getInputStream();
			}else {
				receive = connection.getErrorStream(); 
			}
			// ?????? ??????
			InputStreamReader read = new InputStreamReader(receive); // ????????? ?????????.
			BufferedReader change = new BufferedReader(read); // ???????????? ?????? ?????? ????????? ??????????????? ????????? ???????????? ?????? ???????????? ???????????? ?????????.
			// ?????? ??????
			return change.readLine(); // ???????????? ???????????? ????????? ????????? ???????????? ????????? ????????? ????????????.
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@RequestMapping(value = "/likeService/{sNo}/{checkNum}", method = { RequestMethod.PUT, RequestMethod.PATCH })
	public ResponseEntity<String> likeService(@PathVariable("sNo") Integer sNo,
			@PathVariable("checkNum") Integer checkNum,HttpServletRequest request)
			throws Exception {

		ResponseEntity<String> entity = null;

		HttpSession session = request.getSession();
		String userId = (String)session.getAttribute("userId");		

		try {
			if (userId != null) {
				if(checkNum!=0) {
					serviceS.likeService(userId, sNo);
				}else {
					serviceS.deleteLikeService(userId, sNo);
				}
				entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	

}
