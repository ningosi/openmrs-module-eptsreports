package org.openmrs.module.eptsreports.reporting.reports;

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
public class SetupPatientList extends EptsDataExportManager {
	
	@Autowired
	PvlsNumerator pvlsNumerator;
	
	@Override
	public String getExcelDesignUuid() {
		return "b796f3e8-0396-11e9-9666-6fd10573719f";
	}
	
	@Override
	public String getUuid() {
		return "a77fd51a-0396-11e9-93dc-93d9cb2faa5b";
	}
	
	@Override
	public String getName() {
		return "PVLS Numerator List";
	}
	
	@Override
	public String getDescription() {
		return "Numerator List for patients in pvls";
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(pvlsNumerator.getParameters());
		
		rd.addDataSetDefinition("L", Mapped.mapStraightThrough(pvlsNumerator.constructPvlsNumratorListlsDatset()));
		
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
			reportDesign = createXlsReportDesign(reportDefinition, "numerator.xls", "Numerator", getExcelDesignUuid(), null);
			Properties props = new Properties();
			props.put("repeatingSections", "sheet:1,row:5,dataset:L");
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
