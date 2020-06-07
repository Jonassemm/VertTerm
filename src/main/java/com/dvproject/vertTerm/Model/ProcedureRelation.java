package com.dvproject.vertTerm.Model;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

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

	public List<Appointment> getAppointmentRecommendationByEarliestEnd(Date date, Customer customer) {
		return this.getProcedure().getAppointmentRecommendationByEarliestEnd(
				new Date(date.getTime() + this.getMinDifference().toMillis()), customer);
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

}
