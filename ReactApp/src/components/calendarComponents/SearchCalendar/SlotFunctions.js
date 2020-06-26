'use strict'

import moment from "moment"

export function checkSlotTouch(Slot1, Slot2) {
    return ((Slot1.startDate <= Slot2.endDate) && (Slot1.endDate >= Slot2.startDate))
}

export function checkSlotOverlap(Slot1, Slot2) {
    return ((Slot1.startDate < Slot2.endDate) && (Slot1.endDate > Slot2.startDate))

}


export function getOverlapSlot(Slot1, Slot2) {
    if (Slot1.startDate >= Slot2.startDate) {
        if (Slot1.endDate <= Slot2.endDate) return { startDate: Slot1.startDate, endDate: Slot1.endDate }
        else return { startDate: Slot1.startDate, endDate: Slot2.endDate }
    } else {
        if (Slot2.endDate < Slot1.endDate) return { startDate: Slot2.startDate, endDate: Slot2.endDate }
        return { startDate: Slot2.startDate, endDate: Slot1.endDate }
    }
}

export function subtractTimeslots(minuend, subtrahend) {
    if ((subtrahend.startDate <= minuend.startDate) && (subtrahend.endDate < minuend.endDate))
        return [{ startDate: moment(subtrahend.endDate).toDate(), endDate: moment(minuend.endDate).toDate() }]
    if ((subtrahend.startDate < minuend.startDate) && (subtrahend.endDate == minuend.endDate))
        return null
    if ((subtrahend.startDate > minuend.startDate) && (subtrahend.endDate < minuend.endDate))
        return [{ startDate: moment(minuend.startDate).toDate(), endDate: moment(subtrahend.startDate).toDate() }, { startDate: moment(subtrahend.endDate).toDate(), endDate: moment(minuend.endDate).toDate() }]
    if ((subtrahend.startDate == minuend.startDate) && (subtrahend.endDate == minuend.endDate))
        return null
    if ((subtrahend.startDate > minuend.startDate) && (subtrahend.endDate >= minuend.endDate))
        return [{ startDate: moment(minuend.startDate).toDate(), endDate: moment(subtrahend.startDate).toDate() }]
    if ((subtrahend.startDate == minuend.startDate) && (subtrahend.endDate > minuend.endDate))
        return null
    if ((subtrahend.startDate < minuend.startDate) && (subtrahend.endDate > minuend.endDate))
        return null
    return null
}

export function getSlotCompression(Slot1, Slot2) {
    if (Slot1.startDate <= Slot2.startDate) {
        if (Slot1.endDate <= Slot2.endDate) return { startDate: Slot1.startDate, endDate: Slot2.endDate }
        else return { startDate: Slot1.startDate, endDate: Slot1.endDate }
    } else {
        if (Slot1.endDate <= Slot2.endDate) return { startDate: Slot2.startDate, endDate: Slot2.endDate }
        else return { startDate: Slot2.startDate, endDate: Slot1.endDate }
    }
}