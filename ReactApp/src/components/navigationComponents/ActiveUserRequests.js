import axios from "axios"
import {APIURL} from "../../APIConfig"

export const getLogin = (username,password) => axios.post(`${APIURL}/api/login?username=${username}&password=${password}`,{},{withCredentials: true})
export const getLogout = () => axios.post(`${APIURL}/api/logout`)
export const getUserData = userID => axios.get(`${APIURL}/api/Users/${userID}`)

export const getAnonymousLogin = (username,password) => axios.get(`${APIURL}/api/?username=${username}&password=${password}`,{withCredentials: true})