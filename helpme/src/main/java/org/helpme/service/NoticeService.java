package org.helpme.service;

import java.util.List;

import org.helpme.domain.NoticeVO;
import org.helpme.domain.Criteria;
import org.helpme.domain.SearchCriteria;


public interface NoticeService {

	  public void create(NoticeVO vo) throws Exception;

	  public   NoticeVO read(int noticeId) throws Exception;

	  public void modify(NoticeVO vo) throws Exception;

	  public void remove(Integer noticeId) throws Exception;

	  public List<NoticeVO> listAll() throws Exception;

		// 페이징

	  public List<NoticeVO> listSearchCriteria(SearchCriteria cri) throws Exception;

	  public int listSearchCount(SearchCriteria cri) throws Exception;

}
