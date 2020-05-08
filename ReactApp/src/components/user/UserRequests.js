import axios from "axios"
import {APIURL} from "./../../APIConfig"

//USER
export const addEmployee = data => axios.post(`${APIURL}/json/user/`, data) //forename, surname, username, password, position, status, roles 
export const getEmployeeList = () => axios.get(`${APIURL}/json/user/`) //username, surename, forename, systemstatus
export const getUser = userID => axios.get(`${APIURL}/json/user/${userID}`) //forename, surname, username, password, position, status, roles 
export const removeEmployee = userID => axios.delete(`${APIURL}/json/user/${userID}`)
//ROLES
export const getRoles = () => axios.get(`${APIURL}/Api/Role/`)//[{roleName:...}]

