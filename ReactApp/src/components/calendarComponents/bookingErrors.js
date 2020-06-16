export function getErrorMessage(exceptionType) {
    switch (exceptionType) {
        case 'customer': return "Kein Kunde ausgewählt!"; break;
        case 'ProcedureRelation': return "Die Prozedurrelationen wurden nicht richtig umgesetzt!"; break;
        case 'Ressource' : return "Eine/Mehrere der ausgewählten Ressourcen ist zu diesem Zeitpunkt nicht verfügbar!"; break;
        case 'Employee' : return "Einer/Mehrere der ausgewählten Mitarbeiter ist zu diesem Zeitpunkt nicht verfübar!"; break;
    }
}