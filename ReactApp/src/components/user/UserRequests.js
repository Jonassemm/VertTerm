import axios from "axios"
import {APIURL} from "./../../APIConfig"

export const addEmployee = data => axios.post(`${APIURL}/json/user/`, data) //forename, surname, username, password, position, status, roles, usertype
export const getEmployeeList = () => axios.get(`${APIURL}/json/user/`) //id, username, surename, forename, systemstatus
export const getUser = userID => axios.get(`${APIURL}/json/user/${userID}`) //forename, surname, username, password, position, status, roles, usertype
export const getRoles = () => axios.get(`${APIURL}/Api/Role/`)//[{roleName:...}]

