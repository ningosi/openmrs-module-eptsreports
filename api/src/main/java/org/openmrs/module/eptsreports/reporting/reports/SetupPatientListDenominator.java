package org.openmrs.module.eptsreports.reporting.reports;

import org.openmrs.module.eptsreports.reporting.library.datasets.PvlsDenominator;
import org.openmrs.module.eptsreports.reporting.library.datasets.PvlsNumerator;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class SetupPatientListDenominator extends EptsDataExportManager {
	
	@Autowired
	PvlsDenominator pvlsNumerator;
	
	@Override
	public String getExcelDesignUuid() {
		return "eaaf71a2-03a4-11e9-8a91-3bcac4ed491b";
	}
	
	@Override
	public String getUuid() {
		return "f6e57a70-03a4-11e9-87c5-3f499a3b2e5c";
	}
	
	@Override
	public String getName() {
		return "PVLS Denominator List";
	}
	
	@Override
	public String getDescription() {
		return "Denominator List for patients in pvls";
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(pvlsNumerator.getParameters());
		
		rd.addDataSetDefinition("DN", Mapped.mapStraightThrough(pvlsNumerator.constructPvlsNumratorListlsDatset()));
		
		return rd;
	}
	
	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign reportDesign = null;
		try {
			reportDesign = createXlsReportDesign(reportDefinition, "denominator.xls", "Denominator", getExcelDesignUuid(), null);
			Properties props = new Properties();
			props.put("repeatingSections", "sheet:1,row:8,dataset:DN");
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
