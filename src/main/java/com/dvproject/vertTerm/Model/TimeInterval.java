package com.dvproject.vertTerm.Model;

import java.util.Date;

/**
 * @author Joshua Müller
 */
public class TimeInterval {
	private Date starttime;
	private Date endtime;

	public TimeInterval (Date starttime, Date endtime) {
		setStarttime(starttime);
		setEndtime(endtime);
	}

	public Date getStarttime() {
		return starttime;
	}

	private void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getEndtime() {
		return endtime;
	}

	private void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	/**
	 * Tests, whether the timeinterval (represented by the given times) overlaps with this Time interval
	 */
	public boolean isInTimeInterval(Date starttime, Date endtime) {
		return (isAfterStarttimeAndStrictlyBeforeEndtime(endtime) 
				|| isStrictlyAfterStarttimeAndBeforeEndtime(starttime)
				|| (isBeforeStarttime(starttime) && isAfterEndtime(endtime)));
	}

	private boolean isStrictlyAfterStarttimeAndBeforeEndtime(Date date) {
		return date.after(starttime) && endtime.after(date);
	}

	private boolean isAfterStarttimeAndStrictlyBeforeEndtime(Date date) {
		return starttime.before(date) && endtime.after(date);
	}

	private boolean isBeforeStarttime(Date date) {
		return starttime.after(date);
	}

	private boolean isAfterEndtime(Date date) {
		return endtime.before(date);
	}

}
