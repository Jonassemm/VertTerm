import axios from "axios"
import {APIURL} from "../../APIConfig"

// Set the AUTH token for any request
//axios.defaults.withCredentials = true;

/* const token = localStorage.getItem('JSESSIONID');
const authAxios = axios.create({
    withCredentials: true, 
    headers: {
        JSESSIONID: token
      },
    baseURL: "http://localhost:8080",
    timeout: 5000
  }) */


//Appointment
export const updateAppointment = (id,data) => axios.put(`${APIURL}/api/Appointments/${id}`, data) 
export const getAllAppointments = () => axios.get(`${APIURL}/api/Appointments/`) 
export const getAllAppointmentInTimespace = (starttime, endtime) => axios.get(`${APIURL}/api/Appointments/`, {params: {starttime: starttime, endtime: endtime}}) 
export const getAppointmentOfUserInTimespace = (id, starttime, endtime) => axios.get(`${APIURL}/api/Appointments/user/${id}`, {params: {starttime: starttime, endtime: endtime}}) 
export const getAppointmentOfUser = id => axios.get(`${APIURL}/api/Appointments/user/${id}`) 
export const getAppointmentWithStatusInTimespace = (status, starttime, endtime) => axios.get(`${APIURL}/api/Appointments/status/${status}`, {params: {starttime: starttime, endtime: endtime}}) 
export const updateCustomerIsWaiting = (id, customerIsWaiting) => axios.put(`${APIURL}/api/Appointments/${id}/${customerIsWaiting}`) 
export const deleteAppointment = id => axios.delete(`${APIURL}/api/Appointments/${id}/`)
export const getOwnAppointments = () => axios.get(`${APIURL}/api/Appointments/Own`) 
//export const getOwnAppointments = () => axios.get(`${APIURL}/api/Appointments/Own`,{withCredentials: true}) 
//export const getOwnAppointments = () => authAxios.get(`/api/Appointments/Own`) 



//Appointmentgroup
export const getGroupOfAppointment = appointmentId => axios.get(`${APIURL}/api/Appointmentgroups/Appointment/${appointmentId}`) 
export const startAppointment = appointmentId => axios.put(`${APIURL}/api/Appointmentgroups/start/${appointmentId}`)  
export const stopAppointment = appointmentId => axios.put(`${APIURL}/api/Appointmentgroups/stop/${appointmentId}`)  
