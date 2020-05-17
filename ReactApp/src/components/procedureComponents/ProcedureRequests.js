import axios from "axios"
import {APIURL} from "../../APIConfig"

export const getProcedures = () => axios.get(`${APIURL}/api/Procedure`)
export const addProcedure = data => axios.post(`${APIURL}/api/Procedure`)