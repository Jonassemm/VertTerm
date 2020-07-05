export function getErrorMessage(exceptionType) {
    switch (exceptionType) {
        case 'Resource': return "Zu wenige/viele Ressourcen für einen der Termine"; break;
        case 'Employee': return "Zu wenige/viele Angestellte für einen der Termine"; break;
        case 'Restriction': return "Die nötigen Prozedurrelationen wurden nicht richtig umgesetzt"; break;
        case 'Appointment': return "Diese Zeit ist durch einen anderen Termin blockiert!"; break;
        case 'Availability': return "Soll-Zeiten des Termins stimmen nicht mit den Verfügbarkeiten der Procedure/Angestellten/Ressourcen überein"; break;
        case 'removedAvailability': return "Bei dieser Änderung kam es zu Konflikten bei bestehenden Terminen"; break;
        case 'ResourceType': return "Ressourcen des Termins stimmen nicht mit den Ressourcentypen der Prozedur überein"; break;
        case 'ProcedureRelation': return "Soll-Zeiten des Termins stimmen nicht mit den vorgegebenen Zeitabständen zwischen den Terminen überein"; break;
        case 'Procedure': return "Bei einem Termin gibt es ein Problem mit der Prozedur!"; break;
        case 'Position': return "Angestellte des Termins stimmen nicht mit den Positionen der Prozedur überein"; break;
        case 'AppointmentTime': return "Zeiten des Termins sind nicht valide"; break;
    }
}