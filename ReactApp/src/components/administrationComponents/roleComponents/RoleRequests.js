import axios from "axios"
import {APIURL} from "../../../APIConfig"

export const getRights = () => axios.get(`${APIURL}/api/Rights/`)
export const getRoles = () => axios.get(`${APIURL}/api/Roles`)
export const addRole = data => axios.post(`${APIURL}/api/Roles`,data)
export const deleteRole = id => axios.delete(`${APIURL}/api/Roles/${id}`)
export const editRole = (id,data) => axios.put(`${APIURL}/api/Roles/${id}`,data)