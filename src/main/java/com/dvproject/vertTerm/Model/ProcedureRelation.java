package com.dvproject.vertTerm.Model;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ProcedureRelation {
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Duration minDifference;
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Duration maxDifference;
	@DBRef
	@NotNull
	private Procedure procedure;

	public Duration getMinDifference() {
		return minDifference;
	}

	public void setMinDifference(Duration minDifference) {
		this.minDifference = minDifference;
	}

	public Duration getMaxDifference() {
		return maxDifference;
	}

	public void setMaxDifference(Duration maxDifference) {
		this.maxDifference = maxDifference;
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public boolean testConformatyOfDates(Calendar endDate, Calendar startDate) {
		// calculate the time between endDate and startDate
		Duration timeBetween = Duration.between(endDate.toInstant(), startDate.toInstant());

		return testMinDifference(timeBetween) && testMaxDifference(timeBetween);
	}

	private boolean testMinDifference(Duration duration) {
		return minDifference == null || minDifference.compareTo(duration) <= 0;
	}

	private boolean testMaxDifference(Duration duration) {
		return maxDifference == null || duration.compareTo(maxDifference) <= 0;
	}

	private boolean hasSameDifferences(ProcedureRelation procedureRelation) {
		return equalsMinDifference(procedureRelation.getMinDifference())
				&& equalsMaxDifference(procedureRelation.getMaxDifference());
	}

	private boolean equalsMinDifference(Duration duration) {
		return durationIsDifferent(minDifference, duration);
	}

	private boolean equalsMaxDifference(Duration duration) {
		return durationIsDifferent(maxDifference, duration);
	}

	private boolean durationIsDifferent(Duration duration1, Duration duration2) {
		if (duration1 == null && duration2 == null)
			return false;
		else
			if (duration1 == null ^ duration2 == null)
				return true;
			else
				return duration1.toMillis() != duration2.toMillis();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProcedureRelation) {
			ProcedureRelation other = (ProcedureRelation) obj;
			return procedure.getId().equals(other.getProcedure().getId()) && hasSameDifferences(other);
		}

		return false;
	}

}
