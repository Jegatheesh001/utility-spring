package com.myweb.utility.tools.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.myweb.utility.tools.business.entity.RemitClaimDetails;
import com.myweb.utility.tools.business.entity.RemitClaims;
import com.myweb.utility.tools.business.entity.RemitFiles;
import com.myweb.utility.tools.persistence.ToolsDao;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jegatheesh.mageswaran <br>
           Created on <b>05-Oct-2020</b>
 *
 */
@Slf4j
@Repository
public class ToolsDaoImpl implements ToolsDao {
	@PersistenceContext
	EntityManager em;
	
	@Transactional(rollbackOn = Exception.class)
	@Override
	public void saveRemittanceDetails(List<Map<String, Object>> claimList, String fileName) {
		log.info("File : {}", fileName);
		RemitFiles remitFile = new RemitFiles(fileName);
		em.persist(remitFile);
		for (Map<String, Object> claim : claimList) {
			log.info("Current Element : {}", claim);
			String claimNo = (String) claim.get("ID");
			RemitClaims remitClaim = new RemitClaims(remitFile.getRemitId(), claimNo, (String) claim.get("Comments"));
			em.persist(remitClaim);
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> activityList = (List<Map<String, Object>>) claim.getOrDefault("activityList", new ArrayList<>());
			for (Map<String, Object> activity : activityList) {
				em.persist(new RemitClaimDetails(remitFile.getRemitId(), remitClaim.getRemitClaimId(), claimNo,
						activity.get("ID"), activity.get("Type"), activity.get("Code"), activity.get("Net"),
						activity.get("PaymentAmount"), activity.get("DenialCode")));
			}
		}
	}

}
