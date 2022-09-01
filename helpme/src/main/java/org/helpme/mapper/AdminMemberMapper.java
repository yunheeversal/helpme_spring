package org.helpme.mapper;

import java.util.List;
import java.util.Map;

import org.helpme.domain.Criteria;
import org.helpme.domain.MemberVO;
import org.helpme.domain.NoticeVO;
import org.helpme.domain.SearchCriteria;

public interface AdminMemberMapper  {
	

	 //회원 목록
	 public List<MemberVO> memberList();
	 //회원 입력
	 public void insertMember(MemberVO vo);
	 // 회원 정보 상세보기
	 public MemberVO viewMember(String userId);
	 // 회원삭제
	 public void deleteMember(String userId);
	// 회원정보 수정
	 public void updateMember(MemberVO vo);

//	  public int listSearchCount(SearchCriteria cri) throws Exception;
	  
	//  public List<MemberVO> listSearch(SearchCriteria cri)throws Exception;
	 
	
		
}