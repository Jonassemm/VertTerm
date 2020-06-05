import axios from "axios"
import {APIURL} from "../../APIConfig"

export const getAllRestrictions = () => axios.get(`${APIURL}/api/Restriction`)
export const addRestriction = data => axios.post(`${APIURL}/api/Restriction`,data)
export const deleteRestriction = id => axios.delete(`${APIURL}/api/Restriction/${id}`)
export const editRestriction = data => axios.put(`${APIURL}/api/Restriction`,data)