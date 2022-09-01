package org.helpme.domain;

import java.util.Date;

import lombok.Data;

@Data
public class NoticeVO {
	private Integer noticeId;
	private String noticeTitle;
	private String noticeCon;
	private String noticeCat;
	private Date noticeRegdate;

	}
