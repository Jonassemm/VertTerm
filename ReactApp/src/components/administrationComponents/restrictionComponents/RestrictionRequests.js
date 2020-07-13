//author: Patrick Venturini
import axios from "axios"
import {APIURL} from "../../../APIConfig"

export const addRestriction = data => axios.post(`${APIURL}/api/Restrictions`,data)
export const deleteRestriction = id => axios.delete(`${APIURL}/api/Restrictions/${id}`)
export const editRestriction = data => axios.put(`${APIURL}/api/Restrictions`,data)