'use strict'

import { getResources, getEmployees, getProcedureAvailability, getResourceAppointments, getEmployeeAppointments } from "./SearchCalendarRequests"
import { checkSlotOverlap, checkSlotTouch, getSlotCompression, subtractTimeslots, getOverlapSlot } from "./SlotFunctions"
import moment from "moment"

var resources = []
var employees = []
var procedure = []
var neededResourceTypes = []
var neededPositions = []
var finalResourceTypeSlots = []
var finalPositionSlots = []
var finalSlots = []
var finalSlotsInverse = []

export function setup(p, np, nr) {
    neededPositions = np
    neededResourceTypes = nr
    procedure = p
    return getBounds(p)
}

function reset() {
    resources = neededResourceTypes.map(item => {
        return []
    })
    employees = neededPositions.map(item => {
        return []
    })
    finalResourceTypeSlots = []
    finalPositionSlots = []
    finalSlots = []
    finalSlotsInverse = []
}

function getBounds(procedure){
    const pro = transformAvailabilities(moment().startOf("week"),moment().endOf("week"),procedure)
    let min, max = null
    if(pro.timeSlots.length > 0){
        min = pro.timeSlots[0].startDate
        max = pro.timeSlots[0].endDate
        pro.timeSlots.forEach(item => {
            if(min < item.startDate) min = item.startDate
            if(max > item.endDate) max = item.end
        })
    }else return null
    min = moment(min)
    max = moment(max)
    if(min.hour() >= 2) min = min.hour(min.hour()-1)
    if(max.hour() <= 22) max = max.hour(max.hour() +1)
    return {min: min.toDate(),max: max.toDate()}
}

export async function calculateEvts(start, end) {
    reset()
    if (end && (new Date() > end)) {
        getInverse(start, end)
        return finalSlotsInverse
    }
    if (!end) {
        start = new Date()
        end = moment(start).endOf('week').toDate()
    }

    if (neededPositions.length == 0 && neededResourceTypes.length == 0) {
        if (start < new Date() < end) {
            procedure = transformAvailabilities(new Date(), end, procedure)
        }else{
            procedure = transformAvailabilities(start, end, procedure)
        }
        finalSlots = finalSlots.concat(procedure.timeSlots)
        getInverse(start, end)
        return finalSlotsInverse
    }

    //Get Objects
    // getting all Ressources that have one of the choosen resource type
    for (let i = 0; i < neededResourceTypes.length; i++) {
        const { data } = await getResources(neededResourceTypes[i].id)
        resources[i] = resources[i].concat(data)
    }
    //getting all Employees that have one of the choosen positions
    for (let i = 0; i < neededPositions.length; i++) {
        const { data } = await getEmployees(neededPositions[i].id)
        employees[i] = employees[i].concat(data)
    }

    //filtering for active items
    for (let i = 0; i < employees.length; i++) {
        employees[i] = employees[i].filter(item => item.systemStatus == "active")
    }
    for (let i = 0; i < resources.length; i++) {
        resources[i] = resources[i].filter(item => item.status == "active")
    }

    //getting availabilities
    resources.forEach(resourceList => {
        resourceList.forEach(singleResource => {
            singleResource = transformAvailabilities(new Date(), end, singleResource)
        })
    })
    employees.forEach(employeesList => {
        employeesList.forEach(singleEmployee => {
            singleEmployee = transformAvailabilities(new Date(), end, singleEmployee)
        })
    })
    procedure = transformAvailabilities(start, end, procedure)
    //combining procedure availability with other availabilities
    combineProcedureWithObjects()
    //get appointments
    for (let x = 0; x < resources.length; x++) {
        for (let i = 0; i < resources[x].length; i++) {
            const { data } = await getResourceAppointments(resources[x][i].id, moment(start).format("DD.MM.YYYY HH:mm").toString(), moment(end).format("DD.MM.YYYY HH:mm").toString())
            const newData = data.map(item => {
                return {
                    ...item,
                    startDate: moment(item.plannedStarttime, "DD.MM.YYYY HH:mm"),
                    endDate: moment(item.plannedEndtime, "DD.MM.YYYY HH:mm")
                }
            })
            resources[x][i].appointments = newData
        }
    }
    for (let x = 0; x < employees.length; x++) {
        for (let i = 0; i < employees[x].length; i++) {
            const { data } = await getEmployeeAppointments(employees[x][i].id, moment(start).format("DD.MM.YYYY HH:mm").toString(), moment(end).format("DD.MM.YYYY HH:mm").toString())
            const newData = data.map(item => {
                return {
                    ...item,
                    startDate: moment(item.plannedStarttime, "DD.MM.YYYY HH:mm"),
                    endDate: moment(item.plannedEndtime, "DD.MM.YYYY HH:mm")
                }
            })
            employees[x][i].appointments = newData
        }
    }

    // subtract appointments from available time slots
    subtractAptsFromSlots()
    //finding Slots that are available for all needed Objects
    compressTimeSlots()

    //build inverse Slots
    getInverse(start, end)
    return finalSlotsInverse;
}

function compressTimeSlotsObject(obj) {
    function findNextIntersection() {
        for (let i = 0; i < obj.length; i++) {
            for (let x = i + 1; x < obj.length; x++) {
                if (checkSlotTouch(obj[i], obj[x])) {
                    const newSlot = getSlotCompression(obj[i], obj[x])
                    return [i, x, newSlot]
                }
            }
        }
        return null
    }
    let erg = []
    while (erg = findNextIntersection()) {
        if (erg[0] > erg[1]) {
            obj.splice(erg[0], 1)
            obj.splice(erg[1], 1)
            obj.push(erg[2])
        } else {
            obj.splice(erg[1], 1)
            obj.splice(erg[0], 1)
            obj.push(erg[2])
        }
    }
    return obj
}
function getInverse(start, end) {
    start = moment(start).startOf('week')
    finalSlotsInverse = [{ startDate: start.toDate(), endDate: end }]
    finalSlots.forEach(slot => {
        finalSlotsInverse.forEach((inverseSlot, index) => {
            if (checkSlotOverlap(slot, inverseSlot)) {
                const temp = subtractTimeslots(inverseSlot, slot)
                finalSlotsInverse.splice(index, 1)
                finalSlotsInverse = finalSlotsInverse.concat(temp)
            }
        })
    })
}

function compressTimeSlots() {
    function concatSlots(object) {
        let slots = []
        object.forEach(obj => {
            const tempSlots = obj.timeSlots.map(slot => {
                return {
                    ...slot,
                    id: obj.id
                }
            })
            slots = slots.concat(tempSlots)
        })
        return slots
    }

    finalResourceTypeSlots = resources.map(resourceList => {
        return concatSlots(resourceList)
    })
    finalPositionSlots = employees.map(employeeList => {
        return concatSlots(employeeList)
    })

    const allObjects = finalPositionSlots.concat(finalResourceTypeSlots)
    finalSlots = []
    if (allObjects.length == 1) {
        finalSlots = finalSlots.concat(allObjects[0])
    }
    for (let i = 0; i < allObjects.length - 1; i++) {
        if ((allObjects[i].length == 0) || (allObjects[i + 1].length == 0)) {
            finalSlots = []
            return
        }
        for (let x = 0; x < allObjects[i].length; x++) {
            for (let xx = 0; xx < allObjects[i + 1].length; xx++)
                if (checkSlotOverlap(allObjects[i][x], allObjects[i + 1][xx])) {
                    if (allObjects[i][x].id != allObjects[i + 1][xx].id) {
                        finalSlots.push(getOverlapSlot(allObjects[i][x], allObjects[i + 1][xx]))
                    }
                }
        }
    }
    finalSlots = compressTimeSlotsObject(finalSlots)
}

function subtractAptsFromSlots() {
    function findNextCollision(item) {
        for (let i = 0; i < item.timeSlots.length; i++) {
            for (let x = 0; x < item.appointments.length; x++) {
                if (checkSlotOverlap(item.timeSlots[i], item.appointments[x])) {
                    let newSlots = subtractTimeslots(item.timeSlots[i], item.appointments[x])
                    return [i, newSlots]
                }
            }
        }
        return null
    }

    const calc = (object) => {
        const temp = object.map(item => {
            return {
                ...item
            }
        })
        temp.forEach((objectItem, index) => {
            let erg = []
            while (erg = findNextCollision(objectItem)) {
                objectItem.timeSlots.splice(erg[0], 1)
                if (erg[1]) {
                    erg[1].forEach(item => {
                        objectItem.timeSlots.push(item)
                    })
                }
            }
        })
        return temp
    }
    employees.forEach((employee, index) => {
        employees[index] = calc(employee)
    })
    resources.forEach((resource, index) => {
        resources[index] = calc(resource)
    })
}

function transformAvailabilities(rangeStart, rangeEnd, object) {
    rangeEnd = moment(rangeEnd).add(1, 'd').hour(0).minute(0).second(0).millisecond(0).toDate()
    let timespans = []
    if (object) {
        object.availabilities.forEach(item => {
            const startDate = moment(item.startDate, "DD.MM.yyyy HH:mm")
            const endDate = moment(item.endDate, "DD.MM.yyyy HH:mm")
            if (startDate < rangeEnd) {
                let rhy = ''
                switch (item.rhythm) {
                    case 'daily': rhy = 'd'; break;
                    case 'weekly': rhy = 'w'; break;
                    case 'monthly': rhy = 'M'; break;
                    case 'yearly': rhy = 'y'; break;
                    case 'oneTime': rhy = 'exit'
                }
                let loopVar = startDate
                while (loopVar < rangeEnd) {
                    if (item.endOfSeries && (loopVar > moment(item.endOfSeries, "DD.MM.yyyy HH:mm"))) {
                        break;
                    }
                    if ((loopVar < rangeStart) && (loopVar.year() == moment(rangeStart).year()) && (loopVar.dayOfYear() == moment(rangeStart).dayOfYear())) {
                        if ((endDate.hour() > moment(rangeStart).hour())
                            || ((endDate.hour() == moment(rangeStart).hour()) && (endDate.minute() > moment(rangeStart).minute()))) {
                            const tempEnd = moment(loopVar).hour(endDate.hour()).minute(endDate.minute())
                            timespans.push({ startDate: moment(rangeStart).toDate(), endDate: moment(tempEnd).toDate() })
                        }
                    } else
                        if ((loopVar >= rangeStart) && (loopVar < rangeEnd)) {
                            const tempEnd = moment(loopVar).hour(endDate.hour()).minute(endDate.minute())
                            timespans.push({ startDate: loopVar.toDate(), endDate: moment(tempEnd).toDate() })
                        }
                    if (rhy == "exit") break;
                    loopVar = loopVar.add(item.frequency, rhy)
                }
            }
        })
        object.timeSlots = timespans
        return object
    }
}

function combineProcedureWithObjects() {
    const combine = (object) => {
        const temp = object.map(item => {
            return {
                ...item,
                timeSlots: []
            }
        })
        object.forEach((ObjectItem, index) => {
            ObjectItem.timeSlots.forEach(ObjectSlot => {
                procedure.timeSlots.forEach(procedureSlot => {
                    if (checkSlotOverlap(ObjectSlot, procedureSlot)) {
                        temp[index].timeSlots.push(getOverlapSlot(ObjectSlot, procedureSlot))
                    }
                })
            })
        })
        return temp
    }
    resources.forEach((resourceList, index) => {
        resources[index] = combine(resourceList)
    })
    employees.forEach((employee, index) => {
        employees[index] = combine(employee)
    })

}
