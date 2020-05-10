import axios from "axios"
import {APIURL} from "./../../APIConfig"

//USERS
export const addEmployee = data => axios.post(`${APIURL}/json/user/`, data) //forename, surname, username, password, position, status, roles 
export const updateEmployee = (userID,data) => axios.put(`${APIURL}/json/user/${userID}`, data) //forename, surname, username, password, position, status, roles 
export const getEmployeeList = () => axios.get(`${APIURL}/json/user/`) //username, surename, forename, systemstatus
export const getUser = userID => axios.get(`${APIURL}/json/user/${userID}`) //forename, surname, username, password, position, status, roles 
export const removeEmployee = userID => axios.delete(`${APIURL}/json/user/${userID}`)
//ROLES
export const getAllRoles = () => axios.get(`${APIURL}/Api/Role/`)//[{roleName:...}]

//POSITIONS
export const getAllPositions = () => axios.get(`${APIURL}/Api/Position/`)//[{roleName:...}]

