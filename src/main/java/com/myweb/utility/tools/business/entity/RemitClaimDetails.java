package com.myweb.utility.tools.business.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jegatheesh.mageswaran <br>
           Created on <b>05-Oct-2020</b>
 *
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "remit_claim_details")
public class RemitClaimDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer detailId;
	private Integer remitId;
	private Integer remitClaimId;
	private String claimNo;
	private String activityId;
	private Integer activityType;
	private String activityCode;
	private Double netAmount;
	private Double paymentAmount;
	private String denialCode;
	
	public RemitClaimDetails(Integer remitId, Integer remitClaimId, Object claimNo, Object activityId, Object activityType, Object activityCode,
			Object netAmount, Object paymentAmount, Object denialCode) {
		super();
		this.remitId = remitId;
		this.remitClaimId = remitClaimId;
		this.claimNo = (String) claimNo;
		this.activityId = (String) activityId;
		this.activityType = Integer.parseInt((String) activityType);
		this.activityCode = (String) activityCode;
		this.netAmount = Double.parseDouble((String) netAmount);
		this.paymentAmount = Double.parseDouble((String) paymentAmount);
		this.denialCode = (String) denialCode;
	}
}
