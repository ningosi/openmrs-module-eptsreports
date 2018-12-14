package org.openmrs.module.eptsreports.reporting.utils;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.calculation.result.ResultUtil;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EptsCalculationUtils {
	
	/**
	 * Ensures all patients exist in a result map. If map is missing entries for any of patientIds, they
	 * are added with an empty list result
	 * 
	 * @param results the calculation result map
	 * @param cohort the patient ids
	 */
	public static CalculationResultMap ensureEmptyListResults(CalculationResultMap results, Collection<Integer> cohort) {
		for (Integer ptId : cohort) {
			if (!results.containsKey(ptId) || results.get(ptId) == null) {
				results.put(ptId, new ListResult());
			}
		}
		return results;
	}
	
	/**
	 * Evaluates a data definition on each patient using a reporting context
	 * 
	 * @param dataDefinition the data definition
	 * @param cohort the patient ids
	 * @param parameterValues the parameters for the reporting context
	 * @param calculation the calculation (optional)
	 * @param calculationContext the calculation context
	 * @return the calculation result map
	 */
	public static CalculationResultMap evaluateWithReporting(DataDefinition dataDefinition, Collection<Integer> cohort,
	        Map<String, Object> parameterValues, PatientCalculation calculation, PatientCalculationContext calculationContext) {
		try {
			EvaluationContext reportingContext = ensureReportingContext(calculationContext, cohort, parameterValues);
			Map<Integer, Object> data;
			
			if (dataDefinition instanceof PersonDataDefinition) {
				EvaluatedPersonData result = Context.getService(PersonDataService.class)
				        .evaluate((PersonDataDefinition) dataDefinition, reportingContext);
				data = result.getData();
			} else if (dataDefinition instanceof PatientDataDefinition) {
				EvaluatedPatientData result = Context.getService(PatientDataService.class)
				        .evaluate((PatientDataDefinition) dataDefinition, reportingContext);
				data = result.getData();
			} else {
				throw new RuntimeException("Unknown DataDefinition type: " + dataDefinition.getClass());
			}
			
			CalculationResultMap ret = new CalculationResultMap();
			for (Integer ptId : cohort) {
				Object reportingResult = data.get(ptId);
				ret.put(ptId, toCalculationResult(reportingResult, calculation, calculationContext));
			}
			
			return ret;
		}
		catch (EvaluationException ex) {
			throw new APIException(ex);
		}
	}
	
	/**
	 * Returns the reporting {@link EvaluationContext} stored in calculationContext, creating and
	 * storing a new one if necessary. (Note: for now we never store this, and always return a new one)
	 * 
	 * @param calculationContext the calculation context
	 * @param cohort the patient ids
	 * @param parameterValues the parameters for the reporting context
	 * @return the reporting evaluation context
	 */
	protected static EvaluationContext ensureReportingContext(PatientCalculationContext calculationContext, Collection<Integer> cohort,
	        Map<String, Object> parameterValues) {
		EvaluationContext ret = new EvaluationContext();
		ret.setEvaluationDate(calculationContext.getNow());
		ret.setBaseCohort(new Cohort(cohort));
		ret.setParameterValues(parameterValues);
		calculationContext.addToCache("reportingEvaluationContext", ret);
		return ret;
	}
	
	/**
	 * Convenience method to wrap a plain object in the appropriate calculation result subclass
	 * 
	 * @param obj the plain object
	 * @param calculation the calculation (optional)
	 * @param calculationContext the calculation context
	 * @return the calculation result
	 */
	protected static CalculationResult toCalculationResult(Object obj, PatientCalculation calculation,
	        PatientCalculationContext calculationContext) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Obs) {
			return new ObsResult((Obs) obj, calculation, calculationContext);
		} else if (obj instanceof Collection) {
			ListResult ret = new ListResult();
			for (Object item : (Collection) obj) {
				ret.add(toCalculationResult(item, calculation, calculationContext));
			}
			return ret;
		} else if (obj instanceof Boolean) {
			return new BooleanResult((Boolean) obj, calculation, calculationContext);
		} else {
			return new SimpleResult(obj, calculation, calculationContext);
		}
	}
	
	/**
	 * Convenience method to fetch a patient result value
	 * 
	 * @param results the calculation result map
	 * @param patientId the patient id
	 * @return the result value
	 */
	public static <T> T resultForPatient(CalculationResultMap results, Integer patientId) {
		CalculationResult result = results.get(patientId);
		if (result != null && !result.isEmpty()) {
			return (T) result.getValue();
		}
		return null;
	}
	
	/**
	 * Convenience method to fetch a patient result as a coded obs value
	 * 
	 * @param results the calculation result map
	 * @param patientId the patient id
	 * @return the coded obs value
	 */
	public static Concept codedObsResultForPatient(CalculationResultMap results, Integer patientId) {
		Obs o = obsResultForPatient(results, patientId);
		return o == null ? null : o.getValueCoded();
	}
	
	/**
	 * Convenience method to fetch a patient result as an obs
	 * 
	 * @param results the calculation result map
	 * @param patientId the patient id
	 * @return the obs result
	 */
	public static Obs obsResultForPatient(CalculationResultMap results, Integer patientId) {
		return resultForPatient(results, patientId);
	}
	
	/**
	 * Convenience method to fetch a patient result as an encounter
	 * 
	 * @param results the calculation result map
	 * @param patientId the patient id
	 * @return the encounter result
	 */
	public static Encounter encounterResultForPatient(CalculationResultMap results, Integer patientId) {
		return resultForPatient(results, patientId);
	}
	/**
	 * Extracts patients from calculation result map with matching results
	 * @param results calculation result map
	 * @param requiredResult the required result value
	 * @return the extracted patient ids
	 */
	public static Set<Integer> patientsThatPass(CalculationResultMap results, Object requiredResult) {
		Set<Integer> ret = new HashSet<Integer>();
		for (Map.Entry<Integer, CalculationResult> e : results.entrySet()) {
			CalculationResult result = e.getValue();

			// If there is no required result, just check trueness of result, otherwise check result matches required result
			if ((requiredResult == null && ResultUtil.isTrue(result)) || (result != null && result.getValue().equals(requiredResult))) {
				ret.add(e.getKey());
			}
		}
		return ret;
	}
}