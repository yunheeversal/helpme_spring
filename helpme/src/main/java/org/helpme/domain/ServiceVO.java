	package org.helpme.domain;

import java.util.Date;

import lombok.Data;

@Data
public class ServiceVO {
	private Integer sNo;
	private String sName;
	private Integer categoryNo;
	private Integer price;
	private String sCon;
	private Integer likeIt;
	private Date sRegdate;
	private Integer viewCount;
	private Integer sCount;
	private String userId;
	private String sAttach;
	
}