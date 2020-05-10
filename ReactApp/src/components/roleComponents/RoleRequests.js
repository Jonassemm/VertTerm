import axios from "axios"
import {APIURL} from "../../APIConfig"

export const getRights = () => axios.get(`${APIURL}/api/Right/`)
export const getRoles = () => axios.get(`${APIURL}/api/Role`)
export const addRole = data => axios.post(`${APIURL}/api/Role`,data)
export const deleteRole = id => axios.delete(`${APIURL}/api/Role/${id}`)
export const editRole = (id,data) => axios.put(`${APIURL}/api/Role/${id}`,data)