import axios from "axios"
import {APIURL} from "../../APIConfig"


//Appointment
export const updateAppointment = (id,data) => axios.put(`${APIURL}/api/Appointments/${id}`, data) 
export const getAllAppointments = () => axios.get(`${APIURL}/api/Appointments/`) 
export const getAllAppointmentInTimespace = (starttime, endtime) => axios.get(`${APIURL}/api/Appointments/`, {params: {starttime: starttime, endtime: endtime}}) 
export const getAppointmentOfUserInTimespace = (id, starttime, endtime) => axios.get(`${APIURL}/api/Appointments/user/${id}`, {params: {starttime: starttime, endtime: endtime}}) 
export const getAppointmentOfUser = id => axios.get(`${APIURL}/api/Appointments/user/${id}`) 
export const getAppointmentWithStatusInTimespace = (status, starttime, endtime) => axios.get(`${APIURL}/api/Appointments/status/${status}`, {params: {starttime: starttime, endtime: endtime}}) 
export const updateCustomerIsWaiting = (id, customerIsWaiting) => axios.put(`${APIURL}/api/Appointments/${id}/${customerIsWaiting}`) 
export const deleteAppointment = id => axios.delete(`${APIURL}/api/Appointments/${id}/`)


//Appointmentgroup
export const getGroupOfAppointment = id => axios.get(`${APIURL}/api/Appointmentgroups//Appointment/${id}`) 

