//author: Patrick Venturini
import axios from "axios"
import {APIURL} from "../../../APIConfig"


export const updateOpeningHours = (openingHours) => axios.put(`${APIURL}/api/OpeningHours/`, openingHours)
export const getOpeningHours = () => axios.get(`${APIURL}/api/OpeningHours/`)