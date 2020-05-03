import axios from "axios"
import {APIURL} from "./APIConfig"

export const getLogin = (username,password) => axios.post(`${APIURL}/login?username=${username}&password=${password}`)
export const getLogout = () => axios.post(`${APIURL}/logout`)
export const getUserData = userID => axios.get(`${APIURL}/json/user/${userID}`)