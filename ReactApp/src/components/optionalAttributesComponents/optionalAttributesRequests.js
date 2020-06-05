import axios from "axios"
import {APIURL} from "../../APIConfig"

export const getAllOptionalAttributes = () => axios.get(`${APIURL}/api/OptionalAttribute`)
export const deleteOptionalAttributes = id => axios.delete(`${APIURL}/api/OptionalAttribute/${id}`)
export const addOptionalAttributes = data => axios.post(`${APIURL}/api/OptionalAttribute`,data)
export const editOptionalAttributes = (id, data) => axios.put(`${APIURL}/api/OptionalAttribute`,data)