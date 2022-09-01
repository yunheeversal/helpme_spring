package org.helpme.service;

import java.util.List;

import org.helpme.domain.Criteria;
import org.helpme.domain.DealVO;
import org.helpme.domain.MemberVO;
import org.helpme.domain.MypageCriteria;
import org.helpme.domain.PointVO;
import org.helpme.domain.ServiceVO;
import org.helpme.dto.DealDTO;
import org.helpme.dto.LikeserviceDTO;
import org.helpme.dto.ReviewDTO;

public interface MypageService {


	  // 회원 정보 조회
	public MemberVO selectId(String userId) throws Exception;
	  
	/* // 거래내역 보기 public List<DealVO> selectMyDeal(String userId) throws Exception;
	 * 
	 * // 내가 쓴 후기 public List<ReviewVO> selectMyReview(String userId) throws
	 * Exception;
	 * 
	 * // 1:1 문의 내역 보기 public List<QnaVO> selectMyQna(String userId) throws
	 * Exception;
	 * 
	 */

	// like한 서비스 정보 불러오기
	public ServiceVO selectMyLikeService(int sNo) throws Exception;


	// 적립금 조회
	public PointVO selectMyPoint(String userId) throws Exception;

	
	// 페이징  - 거래  ok
		public List<DealDTO> listCriteria(String userId) throws Exception;

		public int listSearchCount(MypageCriteria cri) throws Exception;

		// 페이징  - 리뷰  ok
		public List<ReviewDTO> listReviewCriteria(String userId) throws Exception;

		public int listReviewSearchCount(MypageCriteria cri) throws Exception;
		
		// 페이징  - 찜 목록 ok
		public List<LikeserviceDTO> listLikeCriteria(String userId) throws Exception;

		public int listLikeSearchCount(MypageCriteria cri) throws Exception;
		

		
		// 최근 본 서비스
		public ServiceVO listLatestCriteria(int sNo) throws Exception;
		
		public int listLatestSearchCount(MypageCriteria cri) throws Exception;
		// public List<ServiceVO> selectMyLatest(String userId) throws Exception;
		
		
}
