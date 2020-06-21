import axios from "axios"
import {APIURL} from "../../../APIConfig"

export const getAllResources = () => axios.get(`${APIURL}/api/Resources`)
export const addResource = data => axios.post(`${APIURL}/api/Resources`,data)
export const deleteResource = id => axios.delete(`${APIURL}/api/Resources/${id}`)
export const editResource = (id, data) => axios.put(`${APIURL}/api/Resources/${id}`,data)

export const getAllConsumables = () => axios.get(`${APIURL}/api/Consumables`)
export const addConsumable = data => axios.post(`${APIURL}/api/Consumables`,data)
export const deleteConsumable = id => axios.delete(`${APIURL}/api/Consumables/${id}`)
export const editConsumable = (id, data) => axios.put(`${APIURL}/api/Consumables/${id}`,data)