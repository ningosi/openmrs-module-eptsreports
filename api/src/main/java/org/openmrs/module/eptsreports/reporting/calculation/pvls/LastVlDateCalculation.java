package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class LastVlDateCalculation extends AbstractPatientCalculation {
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	        PatientCalculationContext context) {
		
		CalculationResultMap map = new CalculationResultMap();
		Concept viralLoad = Context.getConceptService().getConceptByUuid("e1d6247e-1d5f-11e0-b929-000c29ad1d07");
		CalculationResultMap lastVl = EptsCalculations.lastObs(viralLoad, cohort, context);
		for (Integer pId : cohort) {
			Date hasVl = null;
			Obs lastVlObs = EptsCalculationUtils.obsResultForPatient(lastVl, pId);
			if (lastVlObs != null && lastVlObs.getValueNumeric() != null) {
				hasVl = lastVlObs.getObsDatetime();
			}
			map.put(pId, new SimpleResult(hasVl, this));
		}
		return map;
	}
}
