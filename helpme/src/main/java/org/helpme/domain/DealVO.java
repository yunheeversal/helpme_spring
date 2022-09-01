package org.helpme.domain;

import java.util.Date;

import lombok.Data;

@Data
public class DealVO {

	private int dNo;
	private int sNo;
	private String dPrice;
	private Date dDate;
	private String userId;
	
}
