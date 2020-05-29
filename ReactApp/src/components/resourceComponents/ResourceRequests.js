import axios from "axios"
import {APIURL} from "../../APIConfig"

export const getAllResources = () => axios.get(`${APIURL}/api/Resource`)
export const addResource = data => axios.post(`${APIURL}/api/Resource`,data)
//export const deleteResource = id => axios.delete(`${APIURL}/api/Resource/${id}`)
export const editResource = (id, data) => axios.put(`${APIURL}/api/Resource/${id}`,data)