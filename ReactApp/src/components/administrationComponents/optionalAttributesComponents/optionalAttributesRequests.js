import axios from "axios"
import {APIURL} from "../../../APIConfig"

export const getAllOptionalAttributes = () => axios.get(`${APIURL}/api/OptionalAttribute`)
export const editOptionalAttributes = (data, id) => axios.put(`${APIURL}/api/OptionalAttribute/${id}`,data)
//export const getOptionalAttribute = (id) =>  axios.get(`${APIURL}/api/OptionalAttribute${id}`)