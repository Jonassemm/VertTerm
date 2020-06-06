import {getAllUsers, getCustomerList, getEmployeeList} from "./administrationComponents/userComponents/UserRequests"
import {getProcedures} from "./administrationComponents/procedureComponents/ProcedureRequests"
import {getRoles} from "./administrationComponents/roleComponents/RoleRequests"
import {getAllPositions} from "./administrationComponents/positionComponents/PositionRequests"
import {getAllResourceTypes} from "./administrationComponents/resourceTypeComponents/ResourceTypeRequests"
import {getAllResources} from "./administrationComponents/resourceComponents/ResourceRequests"
import {getAllRestrictions} from "./administrationComponents/restrictionComponents/RestrictionRequests"


export const getUsers = getAllUsers
export const getEmployees = getEmployeeList
export const getCustomers = getCustomerList
export const getPositions = getAllPositions
export const getResourcetypes = getAllResourceTypes
export const getResources = getAllResources
export const getRestrictions = getAllRestrictions
export {getProcedures}
export {getRoles}