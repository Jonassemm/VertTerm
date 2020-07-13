//author: Jonas Semmler
import axios from "axios"
import { APIURL } from "../APIConfig"

function filterDeleted(list) {
    console.log(list)
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
export const getCustomers = async (status) => {
    if (status) {
        if (status == "NOTDELETED") return filterDeleted(await axios.get(`${APIURL}/api/Customers`))
        return axios.get(`${APIURL}/api/Customers?status=${status}`)
    }
    return axios.get(`${APIURL}/api/Customers`)
}
export const getEmployees = async (status, filter) => {
    if (status && !filter)
        if (status != "NOTDELETED") return axios.get(`${APIURL}/api/Employees?status=${status}`)
        else return filterDeleted(await axios.get(`${APIURL}/api/Employees`))
    if (filter && !status) return axios.get(`${APIURL}/api/Employees?position=${filter}`)
    if (filter && status)
        if (status != "NOTDELETED") return axios.get(`${APIURL}/api/Employees?status=${status}?position=${filter}`)
        else return filterDeleted(await axios.get(`${APIURL}/api/Employees?position=${filter}`))
    return axios.get(`${APIURL}/api/Employees`)
}

//Role/Right API Calls
export const getRights = () => axios.get(`${APIURL}/api/Rights/`)
export const getRoles = async (status) => {
    if (status == "NOTDELETED") return filterDeleted(await axios.get(`${APIURL}/api/Roles`))
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
export const getResources = async (status, type) => {
    if (type && !status) return axios.get(`${APIURL}/api/Resources/restyp/${type}`)
    if (status && !type) {
        if (status == "NOTDELETED") return filterDeleted(await axios.get(`${APIURL}/api/Resources`))
        return axios.get(`${APIURL}/api/Resources/status/${status}`)
    }
    if (status && type) {
        if (status == "NOTDELETED") return filterDeleted(await axios.get(`${APIURL}/api/Resources/restyp/${type}`))
        return axios.get(`${APIURL}/api/Resources/ResbyRTandStatus?status=${status}?RTid=${type}`)
    }
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
