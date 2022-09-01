package org.helpme.domain;

import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ReviewVO {
	private Integer rBoardId;
	private Integer sNo;
	private String rTitle;
	private String rContent;
	private Date rRegdate;
	private Integer rating;
	private Integer likeCheck;
	private String userId;
	
	
}
