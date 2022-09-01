package org.helpme.domain;

import java.util.Date;

import lombok.Data;

@Data
public class PointVO {

	private String userId;
	private int amount;
	private Date validDate;

	
}
