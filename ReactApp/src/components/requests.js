import {getAllUsers, getCustomerList, getEmployeeList} from "./user/UserRequests"
import {getProcedures} from "./procedureComponents/ProcedureRequests"
import {getRoles} from "./roleComponents/RoleRequests"
import {getAllPositions} from "./position/PositionRequests"


export const getUsers = getAllUsers
export const getEmployees = getEmployeeList
export const getCustomers = getCustomerList
export const getPositions = getAllPositions
export {getProcedures}
export {getRoles}