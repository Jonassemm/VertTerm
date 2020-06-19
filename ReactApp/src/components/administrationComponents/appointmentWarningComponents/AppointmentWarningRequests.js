import axios from "axios"
import {APIURL} from "../../../APIConfig"

//AppointmentWarnings
export const getAppointmentsWithWarning = (warnings) => axios.get(`${APIURL}/api/Appointments/Warnings/${warnings}`) 
export const getAllWarnings = () => axios.get(`${APIURL}/api/Appointments/Warnings`)