//author: Jonas Semmler
import axios from "axios"
import { APIURL } from "../APIConfig"

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

//Optional Attributes
export const getOptionalAttributes = () => axios.get(`${APIURL}/api/OptionalAttributes`)

//Opening Hours API Calls
export const getOpeningHours = () => axios.get(`${APIURL}/api/OpeningHours/`)

//Waring API CAlls
export const getAllAppointmentWarnings = () => axios.get(`${APIURL}/api/Appointments/warnings`)

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
    if (status) {
        if(status == "NOTDELETED") return filterDeleted(axios.get(`${APIURL}/api/Customers`))
        return axios.get(`${APIURL}/api/Customers?status=${status}`)
    }
    return axios.get(`${APIURL}/api/Customers`)
}
export const getEmployees = (status, filter) => {
    if (status && !filter)
        if (status != "NOTDELETED") return axios.get(`${APIURL}/api/Employees?status=${status}`)
        else return filterDeleted(axios.get(`${APIURL}/api/Employees`))
    if (filter && !status) return axios.get(`${APIURL}/api/Employees?position=${filter}`)
    if (filter && status) 
        if(status != "NOTDELETED") return axios.get(`${APIURL}/api/Employees?status=${status}?position=${filter}`)
        else return filterDeleted(axios.get(`${APIURL}/api/Employees?position=${filter}`))
    return axios.get(`${APIURL}/api/Employees`)
}

//Role/Right API Calls
export const getRights = () => axios.get(`${APIURL}/api/Rights/`)
export const getRoles = (status) => {
    if(status == "NOTDELETED") return filterDeleted(axios.get(`${APIURL}/api/Roles`))
    return axios.get(`${APIURL}/api/Roles`)
}

//Restriciton API Calls
export const getRestrictions = () => axios.get(`${APIURL}/api/Restrictions`)

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
export const getConsumables = () => axios.get(`${APIURL}/api/Consumables`)

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

