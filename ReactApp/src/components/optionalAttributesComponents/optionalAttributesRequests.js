import axios from "axios"
import {APIURL} from "../../APIConfig"

export const getAllOptionalAttributes = () => axios.get(`${APIURL}/api/OptionalAttribute`)
export const editOptionalAttributes = (data) => axios.put(`${APIURL}/api/OptionalAttribute`,data)
//export const getOptionalAttribute = (id) =>  axios.get(`${APIURL}/api/OptionalAttribute${id}`)