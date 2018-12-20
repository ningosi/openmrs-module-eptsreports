package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.calculation.pvls.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.LastVlCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.LastVlDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.PreviousVlCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.PreviousVlDateCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationDataDefinition;
import org.openmrs.module.eptsreports.reporting.converter.CalculationResultDataConverter;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxPvlsCohortQueries;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PvlsNumerator extends BaseDataSet {
	
	@Autowired
	TxPvlsCohortQueries txPvlsCohortQueries;
	
	public DataSetDefinition constructPvlsNumratorListlsDatset() {
		
		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		dsd.setName("L");
		dsd.addParameters(getParameters());
		dsd.addRowFilter(txPvlsCohortQueries.getPatientsWithViralLoadSuppression(), mappings);
		
		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		
		dsd.addColumn("id", new PatientIdDataDefinition(), "");
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Age", new AgeDataDefinition(), "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("ArtDate", artInitialDate(), "onDate=${endDate}", new CalculationResultDataConverter());
		dsd.addColumn("CVL", lastVl(), "onDate=${endDate}", new CalculationResultDataConverter());
		dsd.addColumn("CVLD", lastVlDate(), "onDate=${endDate}", new CalculationResultDataConverter());
		dsd.addColumn("PVL", previousVlValue(), "onDate=${endDate}", new CalculationResultDataConverter());
		dsd.addColumn("PVLD", previousVlDate(), "onDate=${endDate}", new CalculationResultDataConverter());
		return dsd;
		
	}
	
	private DataDefinition artInitialDate() {
		CalculationDataDefinition cdf = new CalculationDataDefinition("artDate", new InitialArtStartDateCalculation());
		cdf.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cdf;
	}
	
	private DataDefinition lastVl() {
		CalculationDataDefinition cdf = new CalculationDataDefinition("lastVl", new LastVlCalculation());
		cdf.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cdf;
	}
	
	private DataDefinition lastVlDate() {
		CalculationDataDefinition cdf = new CalculationDataDefinition("lastVlDate", new LastVlDateCalculation());
		cdf.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cdf;
	}
	
	private DataDefinition previousVlDate() {
		CalculationDataDefinition cdf = new CalculationDataDefinition("previousVlDate", new PreviousVlDateCalculation());
		cdf.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cdf;
	}
	
	private DataDefinition previousVlValue() {
		CalculationDataDefinition cdf = new CalculationDataDefinition("lastVl", new PreviousVlCalculation());
		cdf.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cdf;
	}
}
