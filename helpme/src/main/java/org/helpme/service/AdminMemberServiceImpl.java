package org.helpme.service;


import java.util.List;

import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.helpme.domain.MemberVO;
import org.helpme.mapper.AdminMemberMapper;



@Service
public class AdminMemberServiceImpl implements AdminMemberService {

@Inject
AdminMemberMapper adminMemberMapper;
	

	@Override
	public List<MemberVO> memberList() {
		// TODO Auto-generated method stub
		return adminMemberMapper.memberList();
	}

	@Override
	public void insertMember(MemberVO vo) {
		// TODO Auto-generated method stub
	   adminMemberMapper.insertMember(vo);
	}

	@Override
	public MemberVO viewMember(String userId) {
		return adminMemberMapper.viewMember(userId);
	}

	@Override
	public void deleteMember(String userId) {
		// TODO Auto-generated method stub
		adminMemberMapper.deleteMember(userId);
	}

	@Override
	public void updateMember(MemberVO vo) {
		// TODO Auto-generated method stub
		adminMemberMapper.updateMember(vo);
	}

	/*
	@Override
	public List<MemberVO> listSearchCriteria(SearchCriteria cri) throws Exception {
		// TODO Auto-generated method stub
		List<MemberVO> list = adminMemberMapper.listSearch(cri);
		System.out.println("/*** list.size()=" + list.size());
		//return mapper.listSearch(cri);
		
		return list;

	}

	@Override
	public int listSearchCount(SearchCriteria cri) throws Exception {
		// TODO Auto-generated method stub
		return  adminMemberMapper.listSearchCount(cri);
	} */
	}








