//author: Jonas Semmler
import axios from "axios"
import {APIURL} from "../../../APIConfig"

//Resources
export const getResources = resType => axios.get(`${APIURL}/api/Resources/restyp/${resType}`)

//Employees
export const getEmployees = positionID => axios.get(`${APIURL}/api/Employees?position=${positionID}`)

//Availabilies
export const getResourceAvailability = resID => axios.get(`${APIURL}/api/Resource/Availability/${resID}`)
export const getEmployeeAvailability = emplID => axios.get(`${APIURL}/api/Employees/`)
export const getProcedureAvailability = ProID => axios.get(`${APIURL}/api/Procedures/${ProID}/Availability`)

//Appointments
export const getResourceAppointments = (resID,start,end) => axios.get(`${APIURL}/api/Appointments/Resources/${resID}?starttime=${start}&endtime=${end}`)
export const getEmployeeAppointments = (emplID,start,end) => axios.get(`${APIURL}/api/Appointments/user/${emplID}?starttime=${start}&endtime=${end}`)