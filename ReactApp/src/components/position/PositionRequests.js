import axios from "axios"
import {APIURL} from "../../APIConfig"

export const getAllPositions = () => axios.get(`${APIURL}/api/Position`)
export const addPosition = data => axios.post(`${APIURL}/api/Position`,data)
export const deletePosition = id => axios.delete(`${APIURL}/api/Position/${id}`)
export const editPosition = (id, data) => axios.put(`${APIURL}/api/Postion/${id}`,data)