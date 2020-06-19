import axios from "axios"
import {APIURL} from "../../../APIConfig"

export const getAllOptionalAttributes = () => axios.get(`${APIURL}/api/OptionalAttributes`)
export const editOptionalAttributes = (data, id) => axios.put(`${APIURL}/api/OptionalAttributes/${id}`,data)
//export const getOptionalAttribute = (id) =>  axios.get(`${APIURL}/api/OptionalAttributes${id}`)