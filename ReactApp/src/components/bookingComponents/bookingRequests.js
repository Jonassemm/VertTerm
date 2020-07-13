// author: Jonas Semmler
import axios from "axios"
import { APIURL } from "../../APIConfig"

//Optimization API Calls
export const OptimizeEarlyEnd = (data, index) => axios.post(`${APIURL}/api/Appointmentgroups/Recommend/EarlyEnd/${index}`, data)
export const OptimizeLeastWaitingTime = (data, index) => axios.post(`${APIURL}/api/Appointmentgroups/Recommend/LeastWaitingTime/${index}`, data)
export const OptimizeLeastDays = (data, index) => axios.post(`${APIURL}/api/Appointmentgroups/Recommend/LeastDays/${index}`, data)

//AppointmentGroup API Calls
export const addAppointmentGroup = (data, userID) => axios.post(`${APIURL}/api/Appointmentgroups/${userID}`, data)
export const addAppointmentGroupAny = (data) => axios.post(`${APIURL}/api/Appointmentgroups/`, data)
export const addAppointmentGroupAnyOverride = (data) => axios.post(`${APIURL}/api/Appointmentgroups/override/`, data)
export const addAppointmentGroupOverride = (data, userID) => axios.post(`${APIURL}/api/Appointmentgroups/override/${userID}`, data)
export const getAppointmentGroup = (id) => axios.get(`${APIURL}/api/Appointmentgroups/${id}`)
export const getAppointmentGroupByApt = (AptID) => axios.get(`${APIURL}/api/Appointmentgroups/Appointment/${AptID}`)
export const editAppointmentGroup = (data, userID) => axios.put(`${APIURL}/api/Appointmentgroups/${userID}`, data)
export const editAppointmentGroupOverride = (data, userID) => axios.put(`${APIURL}/api/Appointmentgroups/override/${userID}`, data)
export const searchAppointmentGroup = (data) => axios.post(`${APIURL}/api/Appointments/ResEmp`, data)

export const getAppointment = (id) => axios.get(`${APIURL}/api/Appointments/${id}`)
export const addBlocker = data => axios.post(`${APIURL}/api/Blocker`, data)