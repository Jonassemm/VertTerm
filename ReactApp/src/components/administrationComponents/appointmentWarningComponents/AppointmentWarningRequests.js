import axios from "axios"
import {APIURL} from "../../../APIConfig"

//AppointmentWarnings
//export const getAppointmentsWithWarning = (warningList) => axios.get(`${APIURL}/api/Appointments/warnings`, {params: {warnings: warningList}})
// warningList example: "APPOINTMENT_WARNING,USER_WARNING"
export const getAppointmentsWithWarning = (warningList) => axios.get(`${APIURL}/api/Appointments/warnings?warnings=${warningList}`)
export const getAllAppointmentsWithWarning = () => axios.get(`${APIURL}/api/Appointments/warnings`)