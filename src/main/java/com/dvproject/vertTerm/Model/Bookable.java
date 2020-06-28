package com.dvproject.vertTerm.Model;

import com.dvproject.vertTerm.Service.AppointmentServiceImpl;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Bookable {

    protected Bookable(){
        this.availabilities = new ArrayList<>();
        this.appointments = new ArrayList<>();
    }

    protected Bookable(String id){
        this();
        this.id = id;
    }

    @Id
    private String id;

    @DBRef
    private List<Availability> availabilities;

    @Transient
    private List<Appointment> appointments;

    protected Date getEarliestAvailableDateByAvailability(Date date, Duration duration){
        Date earliestDate = null;
        for(Availability availability : this.getAvailabilities()){
            Date currentBestAvailability = availability.getEarliestAvailability(date, duration);
            if(currentBestAvailability != null){
                if(earliestDate == null){
                    earliestDate = currentBestAvailability;
                }
                else if(earliestDate.before(earliestDate)){
                    earliestDate = currentBestAvailability;
                }
            }
        }
        return earliestDate;
    }

    protected Date getEarliestAvailableDateByAppointments(Date date, Duration duration) {
        Date plannedEnd = new Date(date.getTime() + duration.toMillis());
        for (Appointment appointment : this.getAppointments()) {
            if (appointment.getPlannedStarttime().before(date)) {
                if (appointment.getPlannedEndtime().after(date)) {
                    return getEarliestAvailableDate(appointment.getPlannedEndtime(), duration);
                }
            }
            else{
                if(appointment.getPlannedStarttime().before(plannedEnd))
                    return getEarliestAvailableDate(appointment.getPlannedEndtime(), duration);
            }
        }
        return date;
    }

    protected Date getLatestAvailableDateByAvailability(Date date, Duration duration){
        Date latestDate = null;
        for(Availability availability : this.getAvailabilities()){
            Date currentBestAvailability = availability.getLatestAvailability(date, duration);
            if(currentBestAvailability != null){
                if(latestDate == null){
                    latestDate = currentBestAvailability;
                }
                else if(latestDate.after(latestDate)){
                    latestDate = currentBestAvailability;
                }
            }
        }
        return latestDate;
    }

    protected Date getLatestAvailableDateByAppointments(Date date, Duration duration) {
        Date plannedEnd = new Date(date.getTime() + duration.toMillis());
        for (Appointment appointment : this.getAppointments()) {
            if (appointment.getPlannedStarttime().before(date)) {
                if (appointment.getPlannedEndtime().after(date)) {
                    Date startTimeWithoutDuration = new Date(appointment.getPlannedStarttime().getTime() - duration.toMillis());
                    return getEarliestAvailableDate(startTimeWithoutDuration, duration);
                }
            }
            else{
                if(appointment.getPlannedStarttime().before(plannedEnd)) {
                    Date startTimeWithoutDuration = new Date(appointment.getPlannedStarttime().getTime() - duration.toMillis());
                    return getEarliestAvailableDate(startTimeWithoutDuration, duration);
                }
            }
        }
        return date;
    }

    public Date getEarliestAvailableDate(Date date, Duration duration) {
        Date dateByAvailability = this.getEarliestAvailableDateByAvailability(date, duration);
        Date dateByAppointment = this.getEarliestAvailableDateByAppointments(date, duration);
        if(dateByAvailability == null){
            return null;
        }
        if(dateByAvailability.after(date)){
            return this.getEarliestAvailableDate(dateByAvailability, duration);
        }
        if(dateByAppointment.after(date)){
            return this.getEarliestAvailableDate(dateByAppointment, duration);
        }
        return date;
    }

    public Date getLatestAvailableDate(Date date, Duration duration){

        Date dateByAvailability = this.getLatestAvailableDateByAvailability(date, duration);
        Date dateByAppointment = this.getLatestAvailableDateByAppointments(date, duration);
        if(dateByAvailability == null){
            return null;
        }
        if(dateByAvailability.before(date)){
            return this.getLatestAvailableDate(dateByAvailability, duration);
        }
        if(dateByAppointment.before(date)){
            return this.getLatestAvailableDate(dateByAppointment, duration);
        }
        return date;
    }

    public boolean isAvailable(Date date, Duration duration){
        return date.equals(this.getEarliestAvailableDate(date, duration));
    }

    public void populateAppointments(AppointmentServiceImpl service){
        this.getAppointments().clear();
        this.getAppointments().addAll(service.getAll(this));
    }

    public List<Availability> getAvailabilities() {
        return availabilities;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
