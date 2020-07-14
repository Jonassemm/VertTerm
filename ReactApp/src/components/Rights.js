//author: Patrick, Venturini
export const ownUserRights = [
    "OWN_USER_READ",
    "OWN_USER_WRITE"
]

export const userRights = [
    "USER_READ", 
    "USER_WRITE"
]

export const positionRights = [
    "POSITION_READ",
    "POSITION_WRITE"
]

export const roleRights = [
    "ROLE_READ",
    "ROLE_WRITE"
]

export const resourceRights = [
    "RESOURCE_READ",
    "RESOURCE_WRITE"
]

export const resourceTypeRights = [
    "RESOURCE_TYPE_READ",
    "RESOURCE_TYPE_WRITE"
]

export const procedureRights = [
    "PROCEDURE_READ",
    "PROCEDURE_WRITE"
]

export const ownAvailabilityRights = [
    "", //read is not defined
    "OWN_AVAILABILITY_WRITE"
]

export const availabilityRights = [
    "", //read is not defined
    "OWN_AVAILABILITY_WRITE"
]

export const ownAppointmentRights = [
    "OWN_APPOINTMENT_READ",
    "OWN_APPOINTMENT_WRITE"
]

export const appointmentRights = [
    "APPOINTMENT_READ",
    "APPOINTMENT_WRITE"
]

export const ownBookingRights = [
    "OWN_APPOINTMENT_BOOK"
]


export const overrideRight = [
    "OVERRIDE"
]


//---------------------special combinations-------------------------

export const managementRights = () =>{
    var rights = ownUserRights.concat(
        userRights).concat(
            positionRights).concat(
                roleRights).concat(
                    resourceRights).concat(
                        resourceTypeRights).concat(
                            procedureRights)             
    return rights
} 


export const adminRole = "Admin_role"