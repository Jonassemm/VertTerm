import {getAllUsers, getCustomerList, getEmployeeList} from "./user/UserRequests"
import {getProcedures} from "./procedureComponents/ProcedureRequests"
import {getRoles} from "./roleComponents/RoleRequests"

export const getUsers = getAllUsers
export const getEmployees = getEmployeeList
export const getCustomers = getCustomerList
export {getProcedures}
export {getRoles}
