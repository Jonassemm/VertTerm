//author: Jonas Semmler
import axios from "axios"
import {APIURL} from "../../../APIConfig"

export const deleteProcedure = id => axios.delete(`${APIURL}/api/Procedures/${id}`)
export const addProcedure = data => axios.post(`${APIURL}/api/Procedures`,data)
export const editProcedure = (id, data) => axios.put(`${APIURL}/api/Procedures`,data)