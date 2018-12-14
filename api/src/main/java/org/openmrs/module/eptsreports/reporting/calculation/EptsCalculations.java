package org.openmrs.module.eptsreports.reporting.calculation;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.common.VitalStatus;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.VitalStatusDataDefinition;
import org.openmrs.util.OpenmrsUtil;

import java.util.Collection;
import java.util.HashMap;

/**
 * Utility class of common base calculations
 */
public class EptsCalculations {
	
	/**
	 * Evaluates alive-ness of each patient
	 * 
	 * @param cohort the patient ids
	 * @param context the calculation context
	 * @return the alive-nesses in a calculation result map
	 */
	public static CalculationResultMap alive(Collection<Integer> cohort, PatientCalculationContext context) {
		VitalStatusDataDefinition def = new VitalStatusDataDefinition("alive");
		CalculationResultMap vitals = EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
		
		CalculationResultMap ret = new CalculationResultMap();
		for (int ptId : cohort) {
			boolean alive = false;
			if (vitals.get(ptId) != null) {
				VitalStatus vs = (VitalStatus) vitals.get(ptId).getValue();
				alive = !vs.getDead() || OpenmrsUtil.compareWithNullAsEarliest(vs.getDeathDate(), context.getNow()) > 0;
			}
			ret.put(ptId, new BooleanResult(alive, null, context));
		}
		return ret;
	}
	
	/**
	 * Evaluates all obs of a given type of each patient
	 * 
	 * @param concept the obs' concept
	 * @param cohort the patient ids
	 * @param context the calculation context
	 * @return the obss in a calculation result map
	 */
	public static CalculationResultMap allObs(Concept concept, Collection<Integer> cohort, PatientCalculationContext context) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition("all obs", TimeQualifier.ANY, concept, context.getNow(), null);
		return EptsCalculationUtils
		        .ensureEmptyListResults(EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context), cohort);
	}
	
	/**
	 * Evaluates the first obs of a given type of each patient
	 * 
	 * @param concept the obs' concept
	 * @param cohort the patient ids
	 * @param context the calculation context
	 * @return the obss in a calculation result map
	 */
	public static CalculationResultMap firstObs(Concept concept, Collection<Integer> cohort, PatientCalculationContext context) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition("first obs", TimeQualifier.FIRST, concept, context.getNow(),
		        null);
		return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
	}
	
	/**
	 * Evaluates the last obs of a given type of each patient
	 * 
	 * @param concept the obs' concept
	 * @param cohort the patient ids
	 * @param context the calculation context
	 * @return the obss in a calculation result map
	 */
	public static CalculationResultMap lastObs(Concept concept, Collection<Integer> cohort, PatientCalculationContext context) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition("last obs", TimeQualifier.LAST, concept, context.getNow(),
		        null);
		return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
	}
	
	/**
	 * Evaluates the first program enrollment of the specified program
	 * 
	 * @param program the program
	 * @param cohort the patient ids
	 * @param context the calculation context
	 * @return the enrollments in a calculation result map
	 */
	public static CalculationResultMap firstProgramEnrollment(Program program, Collection<Integer> cohort,
	        PatientCalculationContext context) {
		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition();
		def.setName("first in " + program.getName());
		def.setWhichEnrollment(TimeQualifier.FIRST);
		def.setProgram(program);
		def.setEnrolledOnOrBefore(context.getNow());
		CalculationResultMap results = EptsCalculationUtils.evaluateWithReporting(def, cohort, new HashMap<String, Object>(), null,
		    context);
		return EptsCalculationUtils.ensureEmptyListResults(results, cohort);
	}
	
	/**
	 * Evaluates the first encounter of a given type of each patient
	 * 
	 * @param encounterType the encounter type
	 * @param cohort the patient ids
	 * @param context the calculation context
	 * @return the encounters in a calculation result map
	 */
	public static CalculationResultMap firstEncounter(EncounterType encounterType, Collection<Integer> cohort,
	        PatientCalculationContext context) {
		EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition();
		if (encounterType != null) {
			def.setName("first encounter of type " + encounterType.getName());
			def.addType(encounterType);
		} else {
			def.setName("first encounter of any type");
		}
		def.setWhich(TimeQualifier.FIRST);
		def.setOnOrBefore(context.getNow());
		return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
	}

}