package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils.addMoths;

public class PreviousVlDateCalculation extends AbstractPatientCalculation {
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	        PatientCalculationContext context) {
		CalculationResultMap map = new CalculationResultMap();
		Concept viralLoad = Context.getConceptService().getConceptByUuid("e1d6247e-1d5f-11e0-b929-000c29ad1d07");
		CalculationResultMap patientHavingVL = EptsCalculations.allObs(viralLoad, cohort, context);
		for (Integer pId : cohort) {
			Date vlValue = null;
			
			List<Obs> viralLoadForPatientTakenWithin12Months = new ArrayList<Obs>();
			ListResult vlObsResult = (ListResult) patientHavingVL.get(pId);
			Date latestVlLowerDateLimit = addMoths(context.getNow(), -12);
			if (vlObsResult != null && !vlObsResult.isEmpty()) {
				List<Obs> vLoadList = EptsCalculationUtils.extractResultValues(vlObsResult);
				
				if (vLoadList.size() > 0) {
					for (Obs obs : vLoadList) {
						if (obs != null && obs.getObsDatetime().after(latestVlLowerDateLimit)
						        && obs.getObsDatetime().before(context.getNow())) {
							viralLoadForPatientTakenWithin12Months.add(obs);
						}
					}
				}
			}
			if (viralLoadForPatientTakenWithin12Months.size() > 1) {
				Collections.sort(viralLoadForPatientTakenWithin12Months, new Comparator<Obs>() {
					
					public int compare(Obs obs1, Obs obs2) {
						return obs1.getObsId().compareTo(obs2.getObsId());
					}
				});
				Obs previousObs = viralLoadForPatientTakenWithin12Months.get(viralLoadForPatientTakenWithin12Months.size() - 2);
				if (previousObs != null && previousObs.getValueNumeric() != null) {
					vlValue = previousObs.getObsDatetime();
				}
			}
			map.put(pId, new SimpleResult(vlValue, this));
		}
		return map;
	}
	
}
