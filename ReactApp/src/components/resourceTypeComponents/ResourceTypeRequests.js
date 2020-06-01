import axios from "axios"
import {APIURL} from "../../APIConfig"

export const getAllResourceTypes = () => axios.get(`${APIURL}/api/ResourceType`)
export const addResourceType = data => axios.post(`${APIURL}/api/ResourceType`,data)
export const deleteResourceType = id => axios.delete(`${APIURL}/api/ResourceType/${id}`)
export const editResourceType = (id, data) => axios.put(`${APIURL}/api/ResourceType/${id}`,data)