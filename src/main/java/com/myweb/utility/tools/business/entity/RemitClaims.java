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
@Table(name = "remit_claims")
public class RemitClaims {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer remitClaimId;
	private Integer remitId;
	private String claimNo;
	private String comments;
	
	public RemitClaims(Integer remitId, String claimNo, Object comments) {
		super();
		this.remitId = remitId;
		this.claimNo = claimNo;
		this.comments = (String) comments;
	}
}
