/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 **/
package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.openmrs.module.eptsreports.reporting.library.datasets.TxNewDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupTxNew extends EptsDataExportManager {
	
	@Autowired
	private TxNewDataset txNewDataset;
	
	public SetupTxNew() {
	}
	
	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
	
	@Override
	public String getUuid() {
		return "74698e1c-cda9-49cf-a58f-cc6771574ee6";
	}
	
	@Override
	public String getExcelDesignUuid() {
		return "05b84f1b-fd23-4b37-8185-aca65be91875";
	}
	
	@Override
	public String getName() {
		return "TX_NEW Report";
	}
	
	@Override
	public String getDescription() {
		return "Number of adults and children newly enrolled on antiretroviral therapy (ART).";
	}
	
	@Override
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(ReportingConstants.START_DATE_PARAMETER);
		parameters.add(ReportingConstants.END_DATE_PARAMETER);
		return parameters;
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		
		reportDefinition.addDataSetDefinition(txNewDataset.constructTxNewDatset(getParameters()),
		    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign reportDesign = null;
		try {
			reportDesign = createRowPerPatientXlsOverviewReportDesign(reportDefinition, "TXNEW.xls", "TXNEW.xls_", null);
			Properties props = new Properties();
			props.put("repeatingSections", "sheet:1,dataset:TX_NEW Data Set");
			props.put("sortWeight", "5000");
			reportDesign.setProperties(props);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Arrays.asList(reportDesign);
	}
	
}
