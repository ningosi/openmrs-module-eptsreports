package org.openmrs.module.eptsreports.reporting.converter;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalculationResultDataConverter implements DataConverter {
	
	@Override
	public Object convert(Object obj) {
		
		if (obj == null) {
			return "";
		}
		
		Object value = ((CalculationResult) obj).getValue();
		
		if (value instanceof Boolean) {
			return (Boolean) value ? "Yes" : "No";
		} else if (value instanceof Date) {
			return formatDate((Date) value);
		} else if (value instanceof Concept) {
			
			return ((Concept) value).getName();
		} else if (value instanceof String) {
			return value.toString();
		} else if (value instanceof Double) {
			return ((Double) value);
		} else if (value instanceof Integer) {
			return ((Integer) value);
		} else if (value instanceof Location) {
			return ((Location) value).getName();
		} else if (value instanceof SimpleResult) {
			return ((SimpleResult) value).getValue();
		} else if (value instanceof Person) {
			return ((Person) value).getPersonName().getFullName();
		} else if (value instanceof PersonAttribute) {
			return ((PersonAttribute) value).getValue();
		}
		
		return null;
	}
	
	@Override
	public Class<?> getInputDataType() {
		return CalculationResult.class;
	}
	
	@Override
	public Class<?> getDataType() {
		return String.class;
	}
	
	private String formatDate(Date date) {
		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		return date == null ? "" : dateFormatter.format(date);
	}
}