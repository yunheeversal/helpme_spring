package org.helpme.mapper;

import java.util.List;

import org.helpme.domain.NoticeVO;
import org.helpme.domain.Criteria;
import org.helpme.domain.SearchCriteria;



public interface NoticeMapper  {

	  public void create(NoticeVO vo) throws Exception;

	  public NoticeVO read(Integer noticeId) throws Exception;

	  public void modify(NoticeVO vo) throws Exception;

	  public void remove(Integer noticeId) throws Exception;

	  public List<NoticeVO> listAll() throws Exception;

/*	  public List<NoticeVO> listPage(int page) throws Exception;
	  	  
	  public List<NoticeVO> listCriteria(Criteria cri) throws Exception;

	  public int countPaging(Criteria cri) throws Exception; */

	  public int listSearchCount(SearchCriteria cri) throws Exception;
	  
	  public List<NoticeVO> listSearch(SearchCriteria cri)throws Exception;
	  	
	}
	
