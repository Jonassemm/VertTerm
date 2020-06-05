import {getAllUsers, getCustomerList, getEmployeeList} from "./userComponents/UserRequests"
import {getProcedures} from "./procedureComponents/ProcedureRequests"
import {getRoles} from "./roleComponents/RoleRequests"
import {getAllPositions} from "./positionComponents/PositionRequests"
import {getAllResourceTypes} from "./resourceTypeComponents/ResourceTypeRequests"
import {getAllResources} from "./resourceComponents/ResourceRequests"
import {getAllRestrictions} from "./restrictionComponents/RestrictionRequests"


export const getUsers = getAllUsers
export const getEmployees = getEmployeeList
export const getCustomers = getCustomerList
export const getPositions = getAllPositions
export const getResourcetypes = getAllResourceTypes
export const getResources = getAllResources
export const getRestrictions = getAllRestrictions
export {getProcedures}
export {getRoles}