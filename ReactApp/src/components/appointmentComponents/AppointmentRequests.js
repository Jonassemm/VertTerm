import axios from "axios"
import {APIURL} from "../../APIConfig"


//Appointment
export const updateAppointment = (userID,data) => axios.put(`${APIURL}/api/Appointments/${userID}`, data) 
export const getAllAppointments = () => axios.get(`${APIURL}/api/Appointments/`) 


//AppointmentGroup
export const updateCustomer = (userID,data) => axios.put(`${APIURL}/api/Appointmentgroups/${userID}`, data) 
export const getCustomerList = () => axios.get(`${APIURL}/api/Appointmentgroups/`) 





