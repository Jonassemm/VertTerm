import axios from "axios"
import {APIURL} from "./APIConfig"

export const getLogin = (username,password) => axios.post(`${APIURL}/api/login?username=${username}&password=${password}`)
export const getLogout = () => axios.post(`${APIURL}/api/logout`)
export const getUserData = userID => axios.get(`${APIURL}/api/Users/${userID}`)