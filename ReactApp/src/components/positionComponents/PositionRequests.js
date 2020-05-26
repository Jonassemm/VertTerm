import axios from "axios"
import {APIURL} from "../../APIConfig"

export const getAllPositions = () => axios.get(`${APIURL}/api/Positions`)
export const addPosition = data => axios.post(`${APIURL}/api/Positions`,data)
export const deletePosition = id => axios.delete(`${APIURL}/api/Positions/${id}`)
export const editPosition = (id, data) => axios.put(`${APIURL}/api/Positions`,data)