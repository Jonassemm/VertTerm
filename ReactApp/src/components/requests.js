import axios from "axios"
import {APIURL} from "../APIConfig"
import {getAllUsers, getCustomerList, getEmployeeList} from "./administrationComponents/userComponents/UserRequests"
import {getProcedures} from "./administrationComponents/procedureComponents/ProcedureRequests"
import {getRoles} from "./administrationComponents/roleComponents/RoleRequests"
import {getAllPositions} from "./administrationComponents/positionComponents/PositionRequests"
import {getAllResourceTypes} from "./administrationComponents/resourceTypeComponents/ResourceTypeRequests"
import {getAllResources} from "./administrationComponents/resourceComponents/ResourceRequests"
import {getAllRestrictions} from "./administrationComponents/restrictionComponents/RestrictionRequests"

export const addAppointmentGroup = (data, userID) => axios.post(`${APIURL}/api/Appointmentgroups/${userID}`,data)
export const addAppointmentGroupOverride = (data, userID) => axios.post(`${APIURL}/api/Appointmentgroups/override/${userID}`,data)
export const getAppointmentGroup = (id) => axios.get(`${APIURL}/api/Appointmentgroups/${id}`)

export const getUsers = getAllUsers
export const getActiveUsers = () => axios.get(`${APIURL}/api/Users?status=ACTIVE`)
export const getCustomers = getCustomerList

export const getEmployees = getEmployeeList
export const getEmployeesOfPosition = positionID => axios.get(`${APIURL}/api/Employees?position=${positionID}`)
 export {getRoles}
export const getPositions = getAllPositions

export const getResourcetypes = getAllResourceTypes
export const getResources = getAllResources
export const getResourcesOfType = type => axios.get(`${APIURL}/api/Resource/restyp/${type}`)

export {getProcedures}
export const getRestrictions = getAllRestrictions