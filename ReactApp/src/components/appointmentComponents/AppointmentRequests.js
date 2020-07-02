import axios from "axios"
import {APIURL} from "../../APIConfig"


//Appointment
export const updateAppointment = (id,data) => axios.put(`${APIURL}/api/Appointments/${id}`, data)
export const getAppointmentsByID = (id) => axios.get(`${APIURL}/api/Appointments/${id}`) 
export const getAllAppointments = () => axios.get(`${APIURL}/api/Appointments/`) 
export const getAllAppointmentInTimespace = (starttime, endtime) => axios.get(`${APIURL}/api/Appointments/`, {params: {starttime: starttime, endtime: endtime}}) 
export const getAppointmentOfUserInTimespace = (id, starttime, endtime) => axios.get(`${APIURL}/api/Appointments/user/${id}`, {params: {starttime: starttime, endtime: endtime}}) 
export const getAppointmentOfUser = id => axios.get(`${APIURL}/api/Appointments/user/${id}`) 
export const getAppointmentWithStatusInTimespace = (status, starttime, endtime) => axios.get(`${APIURL}/api/Appointments/status/${status}`, {params: {starttime: starttime, endtime: endtime}}) 
export const updateCustomerIsWaiting = (id, customerIsWaiting) => axios.put(`${APIURL}/api/Appointments/${id}/${customerIsWaiting}`) 
export const getOwnAppointments = () => axios.get(`${APIURL}/api/Appointments/Own`)
export const testPreferredAppointment = (id) => axios.get(`${APIURL}/api/Appointments/pull/${id}`)


//Appointmentgroup
export const getGroupOfAppointment = appointmentId => axios.get(`${APIURL}/api/Appointmentgroups/appointment/${appointmentId}`) 
export const startAppointment = appointmentId => axios.put(`${APIURL}/api/Appointmentgroups/start/${appointmentId}`)  
export const stopAppointment = appointmentId => axios.put(`${APIURL}/api/Appointmentgroups/stop/${appointmentId}`)  
export const deleteAppointment = id => axios.delete(`${APIURL}/api/Appointmentgroups/Appointment/${id}`)
export const deleteOverrideAppointment = id => axios.delete(`${APIURL}/api/Appointmentgroups/override/Appointment/${id}`)
export const testAppointment = appointmentId => axios.put(`${APIURL}/api/Appointmentgroups/test/${appointmentId}`) 