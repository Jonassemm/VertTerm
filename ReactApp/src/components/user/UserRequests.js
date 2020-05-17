import axios from "axios"
import {APIURL} from "./../../APIConfig"

import {getRoles} from "./../roleComponents/RoleRequests"
import {getAllPositions} from "./../position/PositionRequests"

//Employee
export const addEmployee = data => axios.post(`${APIURL}/api/Employee/`, data) 
export const updateEmployee = (userID,data) => axios.put(`${APIURL}/api/Employee/${userID}`, data) 
export const getEmployeeList = () => axios.get(`${APIURL}/api/Employee/`) 
export const getEmployee = userID => axios.get(`${APIURL}/api/Employee/${userID}`) 
export const deleteEmployee = userID => axios.delete(`${APIURL}/api/Employee/${userID}`)
//Customers
export const addCustomer = data => axios.post(`${APIURL}/api/Customer/`, data) 
export const updateCustomer = (userID,data) => axios.put(`${APIURL}/api/Customer/${userID}`, data) 
export const getCustomerList = () => axios.get(`${APIURL}/api/Customer/`) 
export const getCustomer = userID => axios.get(`${APIURL}/api/Customer/${userID}`) 
export const removeCustomer = userID => axios.delete(`${APIURL}/api/Customer/${userID}`)

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

