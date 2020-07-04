//author: Patrick Venturini
import axios from "axios"
import {APIURL} from "../../../APIConfig"


//AppointmentWarnings
export const getAppointmentsWithWarning = (warningList) => axios.get(`${APIURL}/api/Appointments/warnings?warnings=${warningList}`)
export const getAllAppointmentsWithWarning = () => axios.get(`${APIURL}/api/Appointments/warnings`)