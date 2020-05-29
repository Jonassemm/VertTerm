import axios from "axios"
import {APIURL} from "../../APIConfig"

import {getRoles} from "../roleComponents/RoleRequests"
import {getAllPositions} from "../positionComponents/PositionRequests"

//Employee
export const addEmployee = data => axios.post(`${APIURL}/api/Employees/`, data) 
export const updateEmployee = (userID,data) => axios.put(`${APIURL}/api/Employees/${userID}`, data) 
export const getEmployeeList = () => axios.get(`${APIURL}/api/Employees/`) 
export const getEmployee = userID => axios.get(`${APIURL}/api/Employees/${userID}`) 
export const deleteEmployee = userID => axios.delete(`${APIURL}/api/Employees/${userID}`)
//Customers
export const addCustomer = data => axios.post(`${APIURL}/api/Customers/`, data) 
export const updateCustomer = (userID,data) => axios.put(`${APIURL}/api/Customers/${userID}`, data) 
export const getCustomerList = () => axios.get(`${APIURL}/api/Customers/`) 
export const getCustomer = userID => axios.get(`${APIURL}/api/Customers/${userID}`) 
export const deleteCustomer = userID => axios.delete(`${APIURL}/api/Customers/${userID}`)

//USERS
export const getAllUsers = () => axios.get(`${APIURL}/api/User/`) 

//ROLES
//export const getAllRoles = () => axios.get(`${APIURL}/api/Role/`)
export const getAllRoles = getRoles

//POSITIONS
//export const getAllPositions = () => axios.get(`${APIURL}/api/Position/`)
export {getAllPositions}


//RESTRICTIONS
export const getAllRestrictions = () => axios.get(`${APIURL}/api/Restriction/`)
//export {getAllPositions}

