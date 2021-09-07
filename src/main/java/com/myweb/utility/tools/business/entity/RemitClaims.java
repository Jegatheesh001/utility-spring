package com.myweb.utility.tools.business.entity;

import java.util.Map;

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
@Table(name = "remit_claims")
public class RemitClaims {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer remitClaimId;
	private Integer remitId;
	private String claimNo;
	private String providerId;
	private String paymentReference;
	private String settlementDate;
	private String comments;
	
	public RemitClaims(Integer remitId, String claimNo, Map<String, Object> claim) {
		super();
		this.remitId = remitId;
		this.claimNo = claimNo;
		this.providerId = (String) claim.get("ProviderID");
		this.paymentReference = (String) claim.get("PaymentReference");
		this.settlementDate = (String) claim.get("DateSettlement");
		this.comments = (String) claim.get("Comments");
	}
}
