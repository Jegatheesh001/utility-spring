package com.myweb.utility.tools.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author jegatheesh.mageswaran <br>
           Created on <b>05-Oct-2020</b>
 *
 */
public class RemittanceSAXHandler extends DefaultHandler {
	private List<Map<String, Object>> claimList;
	private List<Map<String, Object>> activityList;
	private Map<String, Object> claimContent;
	private Map<String, Object> activityContent;
	private StringBuilder valueBuilder;
	private boolean activity;
	
	public RemittanceSAXHandler() {
		claimList = new ArrayList<>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		valueBuilder = new StringBuilder();
		if (qName.equalsIgnoreCase("Claim")) {
			claimContent = new HashMap<>();
			activityList = new ArrayList<>(); 
		} else if (qName.equalsIgnoreCase("Activity")) {
			activity = true;
			activityContent = new HashMap<>();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (claimContent != null) {
			if (activity) {
				activityContent.put(qName, valueBuilder.toString());
			} else {
				claimContent.put(qName, valueBuilder.toString());
			}
			if (qName.equalsIgnoreCase("Claim")) {
				claimList.add(claimContent);
				claimContent = null;
			} else if (qName.equalsIgnoreCase("Activity")) {
				activityList.add(activityContent);
				activity = false;
				activityContent = null;
				claimContent.put("activityList", activityList);
			}
		}
		valueBuilder = new StringBuilder();
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		valueBuilder.append(new String(ch, start, length));
	}

	public List<Map<String, Object>> getClaimList() {
		return claimList;
	}

	public void setClaimList(List<Map<String, Object>> claimList) {
		this.claimList = claimList;
	}
}
