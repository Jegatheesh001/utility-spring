package com.myweb.utility.tools.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
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
@SuppressWarnings("unchecked")
@Slf4j
@Repository
public class ToolsDaoImpl implements ToolsDao {
	@PersistenceContext
	EntityManager em;
	
	@Value("${spring.datasource.name}")
	private String schemaName;
	
	@Transactional(rollbackOn = Exception.class)
	@Override
	public void saveRemittanceDetails(List<Map<String, Object>> claimList, String fileName) {
		log.info("File : {}", fileName);
		/* Duplicate check */
		long count = (long) em.createQuery("select count(*) from RemitFiles where remitFileName=:fileName")
				.setParameter("fileName", fileName).getSingleResult();
		if(count > 0) {
			// throw new DataIntegrityViolationException("File already scanned from folder - " + fileName);
			log.info("File already scanned from folder - {}", fileName);
			return;
		}
		RemitFiles remitFile = new RemitFiles(fileName);
		em.persist(remitFile);
		for (Map<String, Object> claim : claimList) {
			log.info("Current Element : {}", claim);
			String claimNo = (String) claim.get("ID");
			RemitClaims remitClaim = new RemitClaims(remitFile.getRemitId(), claimNo, claim);
			em.persist(remitClaim);
			List<Map<String, Object>> activityList = (List<Map<String, Object>>) claim.getOrDefault("activityList", new ArrayList<>());
			for (Map<String, Object> activity : activityList) {
				em.persist(new RemitClaimDetails(remitFile.getRemitId(), remitClaim.getRemitClaimId(), claimNo,
						activity.get("ID"), activity.get("Type"), activity.get("Code"), activity.get("Net"),
						activity.get("PaymentAmount"), activity.get("DenialCode")));
			}
		}
	}

	@Override
	public List<String> getAllTables() {
		return em.createNativeQuery("select TABLE_NAME from information_schema.tables where table_schema = :schemaName")
				.setParameter("schemaName", schemaName).getResultList();
	}

	@Override
	public List<String> getAllColumns(String tableName) {
		return em.createNativeQuery("select COLUMN_NAME from information_schema.columns where table_schema = :schemaName and table_name = :tableName")
				.setParameter("schemaName", schemaName).setParameter("tableName", tableName).getResultList();
	}

	@Override
	public Number getDataCountForQuery(String tableName, String query) {
		return (Number) em.createNativeQuery("select count(*) from " + tableName + " where " + query).getSingleResult();
	}

}
