//author: Patrick Venturini
export const appointmentStatus ={
    planned: "planned",
    done: "done",
    deleted: "deleted"
}

export const translateStatus = (status) => {
    switch(status) {
    case appointmentStatus.planned:
        return "Gebucht"
    break;
    case appointmentStatus.done:
        return "Erledigt"
    break;
    case appointmentStatus.deleted:
        return "Gelöscht"
    break;
    default: return "UNDEFINED"
    }
}
