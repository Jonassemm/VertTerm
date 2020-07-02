import axios from "axios"
import {APIURL} from "../../../APIConfig"


export const getAllResourceTypes = () => axios.get(`${APIURL}/api/ResourceTypes`)
export const addResourceType = data => axios.post(`${APIURL}/api/ResourceTypes`,data)
export const deleteResourceType = id => axios.delete(`${APIURL}/api/ResourceTypes/${id}`)
export const editResourceType = (id, data) => axios.put(`${APIURL}/api/ResourceTypes/${id}`,data)