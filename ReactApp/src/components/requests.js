import axios from "axios"
import { APIURL } from "../APIConfig"
import { getRoles } from "./administrationComponents/roleComponents/RoleRequests"
import { getAllPositions } from "./administrationComponents/positionComponents/PositionRequests"
import { getAllResourceTypes } from "./administrationComponents/resourceTypeComponents/ResourceTypeRequests"
import { getAllRestrictions } from "./administrationComponents/restrictionComponents/RestrictionRequests"

export const getAppointment = (id) => axios.get(`${APIURL}/api/Appointments/${id}`)
export const addBlocker = data => axios.post(`${APIURL}/api/Blocker`, data)

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

function filterDeleted(list) {
    return { data: list.data.filter(item => item.status != "deleted") }
}

//User API calls
export const getCurrentUser = () => axios.get(`${APIURL}/api/Users/Own`)
export const getUsers = async (status) => {
    if (status) {
        if (status == "NOTDELETED") return filterDeleted(await axios.get(`${APIURL}/api/Users`))
        return axios.get(`${APIURL}/api/Users?status=${status}`)
    }
    return axios.get(`${APIURL}/api/Customers`)
}
export const getCustomers = (status) => {
    if (status) return axios.get(`${APIURL}/api/Customers?status=${status}`)
    return axios.get(`${APIURL}/api/Customers`)
}
export const getEmployees = (status, filter) => {
    if (status && !filter) return axios.get(`${APIURL}/api/Employees?status=${status}`)
    if (filter && !status) return axios.get(`${APIURL}/api/Employees?position=${filter}`)
    if (filter && status) return axios.get(`${APIURL}/api/Employees?status=${status}?position=${filter}`)
    return axios.get(`${APIURL}/api/Employees`)
}

//pass through other API Calls
export { getRoles }
export { getAllPositions }
export { getAllRestrictions }

//Position API Calls
export const getPositions = async (status) => {
    if (status) {
        if (status == "NOTDELETED") return filterDeleted(await axios.get(`${APIURL}/api/Positions`))
        return axios.get(`${APIURL}/api/Positions?status=${status}`)
    }
    return axios.get(`${APIURL}/api/Positions`)
}

//Resource API Calls
export const getResourceTypes = async (status) => {
    if (status) {
        if (status == "NOTDELETED") return filterDeleted(await axios.get(`${APIURL}/api/ResourceTypes`))
        return axios.get(`${APIURL}/api/ResourceTypes?status=${status}`)
    }
    return axios.get(`${APIURL}/api/ResourceTypes`)
}
export const getResources = (status, type) => {
    if (type && !status) return axios.get(`${APIURL}/api/Resources/restyp/${type}`)
    if (status && !type) return axios.get(`${APIURL}/api/Resources/status/${status}`)
    if (status && type) return axios.get(`${APIURL}/api/Resources/restyp/${type}`)
    return axios.get(`${APIURL}/api/Resources`)
}

//Procedure API Calls
export const getProcedures = async (status) => {
    if (status) {
        if (status == "NOTDELETED") return filterDeleted(await axios.get(`${APIURL}/api/Procedures`))
        return axios.get(`${APIURL}/api/Procedures/Status/${status}`)
    }
    return axios.get(`${APIURL}/api/Procedures`)
}
export const getPublicProcedures = (status) => {
    return axios.get(`${APIURL}/api/Procedures/Status/${status}?publicProcedure=true`)
}


//Optimization API Calls
export const OptimizeEarlyEnd = (data, index) => axios.post(`${APIURL}/api/Appointmentgroups/Recommend/EarlyEnd/${index}`, data)
export const OptimizeLeastWaitingTime = (data, index) => axios.post(`${APIURL}/api/Appointmentgroups/Recommend/LeastWaitingTime/${index}`, data)
export const OptimizeLeastDays = (data, index) => axios.post(`${APIURL}/api/Appointmentgroups/Recommend/LeastDays/${index}`, data)

