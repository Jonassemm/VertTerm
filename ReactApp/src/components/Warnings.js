//author: Patrick Venturini
export const kindOfWarning = {
    appointmenttime: "AppointmenttimeWarning",
    appointment: "AppointmentWarning",
    procedure: "ProcedureWarning",
    procedureRelation: "ProcedureRelationWarning",
    resource: "ResourceWarning",
    resourceType: "ResourceTypeWarning",
    availability: "AvailabilityWarning",
    restriction: "RestrictionWarning",
    position: "Position_Warning",
    employee: "EmplyeeWarning",
    user: "UserWarning"
}

export const kindOfWarningList = [
    "AppointmenttimeWarning",
    "AppointmentWarning",
    "ProcedureWarning",
    "ProcedureRelationWarning",
    "ResourceWarning",
    "ResourceTypeWarning",
    "AvailabilityWarning",
    "RestrictionWarning",
    "Position_Warning",
    "EmplyeeWarning",
    "UserWarning"
]


//translation en <-> ge
export function getTranslatedWarning(warning) {
    var translated = ""
    switch(warning){
        case "AppointmenttimeWarning": 
            translated = "Terminzeit"
            break;
        case "Terminzeit": 
            translated = "AppointmenttimeWarning"
            break;

        case "AppointmentWarning":
            translated = "Termin"
            break;
        case "Termin":
            translated = "AppointmentWarning"
            break;

        case "ProcedureWarning":
            translated = "Prozedur"
            break;
        case "Prozedur":
            translated = "ProcedureWarning"
            break;

        case "ProcedureRelationWarning": 
            translated = "Prozedurbeziehung"
            break;
        case "Prozedurbeziehung": 
            translated = "ProcedureRelationWarning"
            break;

        case "ResourceWarning":
            translated = "Ressource"
            break;
        case "Ressource":
            translated = "ResourceWarning"
            break;

        case "ResourceTypeWarning":
            translated = "Ressourcentyp"
            break;
        case "Ressourcentyp":
            translated = "ResourceTypeWarning"
            break;

        case "AvailabilityWarning":
            translated = "Verfügbarkeit"
            break;
        case "Verfügbarkeit":
            translated = "AvailabilityWarning"
            break;

        case "RestrictionWarning":
            translated = "Einschränkung"
            break;
        case "Einschränkung":
            translated = "RestrictionWarning"
            break;

        case "Position_Warning":
            translated = "Position"
            break;
        case "Position":
            translated = "Position_Warning"
            break;

        case "EmplyeeWarning":
            translated = "Mitarbeiter"
            break;
        case "Mitarbeiter":
            translated = "EmplyeeWarning"
            break;

        case "UserWarning": 
            translated = "Benutzer"
            break;
        case "Benutzer": 
            translated = "UserWarning"
            break;
        default:  translated ="undefined translation: " + warning
    }
    return translated
}


export const getWarningsAsString = (warnings) =>{
    var warningString = ""
    if(warnings.length > 0){
        warnings.map((singleWarning, index )=> {
            if(index == 0) {
                warningString += getTranslatedWarning(singleWarning)
            }else {
                warningString += "; " + getTranslatedWarning(singleWarning)
            }
        })
    }
    return warningString
}


export const creatWarningList = (list) => {
    var warningList = ""
    if(list.length > 0) {
        for(var i=0; i<list.length; i++){
            if(i == 0){
                warningList += list[i]
            } else {
                warningList += "," +list[i]
            }
        }
    }
    return warningList  
}
