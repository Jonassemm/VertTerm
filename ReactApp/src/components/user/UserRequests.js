import axios from "axios"
import {APIURL} from "./../../APIConfig"

//Employee
export const addEmployee = data => axios.post(`${APIURL}/api/User/`, data) 
export const updateEmployee = (userID,data) => axios.put(`${APIURL}/api/User/${userID}`, data) 
export const getEmployeeList = () => axios.get(`${APIURL}/api/User/`) 
export const getEmployee = userID => axios.get(`${APIURL}/api/User/${userID}`) 
export const removeEmployee = userID => axios.delete(`${APIURL}/api/User/${userID}`)
//Customers
export const addCustomer = data => axios.post(`${APIURL}/api/Customer/`, data) 
export const updateCustomer = (userID,data) => axios.put(`${APIURL}/api/Customer/${userID}`, data) 
export const getCustomerList = () => axios.get(`${APIURL}/api/Customer/`) 
export const getCustomer = userID => axios.get(`${APIURL}/api/Customer/${userID}`) 
export const removeCustomer = userID => axios.delete(`${APIURL}/api/Customer/${userID}`)

//USERS
export const getAllUsers = () => axios.get(`${APIURL}/api/User/`) 

//ROLES
export const getAllRoles = () => axios.get(`${APIURL}/api/Role/`)

//POSITIONS
export const getAllPositions = () => axios.get(`${APIURL}/api/Position/`)

