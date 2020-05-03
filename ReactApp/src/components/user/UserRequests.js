const APIURL = "http://localhoast:8080"
const axios = require("axios")
export const addEmployee = data => axios.post(`${APIURL}/json/user/`, data) //forename, surname, username, password, position, status, roles, usertype
export const getEmployeeList = () => axios.get(`${APIURL}/json/user/`) //id, username, surename, forename, systemstatus
export const getUser = id => axios.get(`${APIURL}/json/user/`+ id) //forename, surname, username, password, position, status, roles, usertype
export const getRolesOfUser = id => axios.get(`${APIURL}/Api/Role`)//[{roleName:...}]
export const getRoles = id => axios.get(`${APIURL}/Api/Role/`+ id)//[{roleName:...}]
