import React, { useState, useEffect } from "react"
import { Calendar, momentLocalizer } from "react-big-calendar"
import moment from "moment"
import { Container, Form, Col, Row, Button } from "react-bootstrap"
import { observer } from "mobx-react"
import "react-big-calendar/lib/css/react-big-calendar.css"
import ObjectPicker from "../ObjectPicker"
import "./BookingForm.css"
import DatePicker from "react-datepicker"
import {addAppointmentGroup} from "./requests"

const localizer = momentLocalizer(moment)

function BookingForm() {
    const [calendarEvents, setCalendarEvents] = useState([])
    const [selectedProcedures, setSelectedProcedures] = useState([])
    const [selectedCustomer, setSelectedCustomer] = useState({})
    const [custom, setCustom] = useState(false)
    const [apts, setApts] = useState([])
    const [timeDif, setTimeDif] = useState(0)

    async function handleSubmit(event) {
        event.preventDefault()
        const finalData = apts.map(item => {
            return {
                ...item,
                bookedCustomer: selectedCustomer
            }
        })
        console.log(finalData)
        res = await addAppointmentGroup(finalData)
        console.log(res)

    }

    function setupPresetTime(item) {
        
    }

    function validateTime(data) {
        //calculating the current date rounded to 5 minutes
        let currentDate = new Date()
        let mod = currentDate.getMinutes() % 10
        currentDate.setMinutes(currentDate.getMinutes() - mod)
        if (mod >= 5) currentDate.setMinutes(currentDate.getMinutes() + 10)

        //copie of apts
        let tempApts = apts.map(item => {
            return { ...item }
        })

        // defining function
        const calculateDifference = (ref1, ref2, end) => {
            if (ref1) {
                //checking if selected date is in the future
                if (data.date.getTime() - currentDate.getTime() > 0) {
                    //checking if selected date is in the right place
                    if ((data.date.getTime() - ref1.getTime()) < 0) {
                        if (end) ref2 = ref1
                        else ref2 = data.date
                    } else {
                        if (end) ref2 = data.date
                        else ref2 = ref1
                    }
                } else {
                    if (end) ref2 = ref1
                    else ref2 = currentDate
                }
            } else {
                if (data.date.getTime() - currentDate.getTime() > 0) ref2 = date.date
                else ref2 = currentDate
            }
            return ([ref1, ref2])
        }
        // checking wheter a selected time is in the past and if the differece between start and end is positive
        const refIndex = tempApts.findIndex(item => item.procedure.id == data.ref.procedure.id)
        if (data.ident == "start") {
            const [ref1, ref2] = calculateDifference(tempApts[refIndex].plannedEndTime, tempApts[refIndex].plannedStartTime)
            tempApts[refIndex].plannedEndTime = ref1
            tempApts[refIndex].plannedStartTime = ref2
        } else {
            const [ref1, ref2] = calculateDifference(tempApts[refIndex].plannedStartTime, tempApts[refIndex].plannedEndTime, true)
            tempApts[refIndex].plannedEndTime = ref2
            tempApts[refIndex].plannedStartTime = ref1
        }
        console.log(tempApts)
        setApts(tempApts)
    }

    function handleChange(data) {
        const temp = apts.map(item => {
            if (item.procedure.name == data.ident.ident) {
                if (data.DbObject == "employee") {
                    const temp = item.bookedEmployees.map((innerItem, index) => {
                        if (index == data.ident.ix) {
                            return data.data[0]
                        } else {
                            return {
                                ...innerItem
                            }
                        }
                    })
                    return {
                        ...item,
                        bookedEmployees: temp
                    }
                } else {
                    const temp = item.bookedResources.map((innerItem, index) => {
                        if (index == data.ident.ix) {
                            return data.data[0]
                        } else {
                            return {
                                ...innerItem
                            }
                        }
                    })
                    return {
                        ...item,
                        bookedResources: data.data
                    }
                }
            } else {
                return {
                    ...item
                }
            }
        })
        setApts(temp)
    }

    function setProcedures(data) {
        setSelectedProcedures(data)
        const tempApts = apts.map(item => {
            return {
                ...item
            }
        })
        const dif = apts.length - data.length
        if (dif > 1) {
            // delete all
            tempApts.splice(0, tempApts.length)
        } else if (dif > 0) {
            // delete one
            for (let i = 0; i < apts.length; i++) {
                if (!data.find(item => item.name == apts[i].procedure.name)) {
                    tempApts.splice(i, 1)
                }
            }
        } else {
            // add one
            const tempEmployees = data[data.length - 1].neededEmployeePositions.map(item => {
                return {}
            })
            tempApts.push({
                procedure: data[data.length - 1],
                bookedEmployees: tempEmployees,
                bookedCustomer: {},
                bookedResources: [],
                plannedStartTime: null,
                plannedEndTime: null
            })
        }
        setApts(tempApts)
    }

    return (
        <div className="page">
            <Container>
                <Form onSubmit={handleSubmit}>
                    <Row style={{ alignItems: "baseline" }}>
                        <Col>
                            <h1>Termin Buchen</h1>
                        </Col>
                        <Col className="selectType">
                            <Form.Check
                                type="switch"
                                id="custom-switch"
                                label="Benutzerdefinierte Eingabe"
                                value={custom || 0}
                                onChange={e => setCustom(!custom)}
                                checked={custom || false}
                            />
                        </Col>

                    </Row>
                    <hr />
                    <Form.Row>
                        <Form.Group as={Col} style={{ textAlign: "bottom" }}>
                            <Form.Label>Kunde:</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col}>
                            <ObjectPicker
                                DbObject="user"
                                setState={setSelectedCustomer} />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} style={{ textAlign: "bottom" }}>
                            <Form.Label>Prozeduren:</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col}>
                            <ObjectPicker
                                DbObject="procedure"
                                setState={setProcedures}
                                multiple={true} />
                        </Form.Group>
                    </Form.Row>
                    {!custom && apts.map(item => {
                        return (
                            <React.Fragment>
                                <hr />
                                <div className="parent">
                                    <div className="namebox">
                                        <h4>{item.procedure.name}</h4>
                                    </div>
                                    <div className="box wrap">
                                        {item.procedure.neededEmployeePositions && item.procedure.neededEmployeePositions.map((innerItem, index) => {
                                            return (
                                                <div className="middleBox">
                                                    <div className="middleBoxLeft">
                                                        <span>{innerItem.name}</span>
                                                    </div>
                                                    <div className="middleBoxRight">
                                                        <ObjectPicker
                                                            className="input"
                                                            ident={{ "ident": item.procedure.name, "ix": index }}
                                                            DbObject="employee"
                                                            setState={handleChange} />
                                                    </div>
                                                </div>
                                            )
                                        })}
                                        {item.procedure.neededResourceTypes && item.procedure.neededResourceTypes.map((innerItem, index) => {
                                            return (
                                                <div className="middleBox">
                                                    <div className="middleBoxLeft">
                                                        <span>{innerItem.name}</span>
                                                    </div>
                                                    <div className="middleBoxRight">
                                                        <ObjectPicker
                                                            className="input"
                                                            ident={{ "ident": item.procedure.name, "ix": index }}
                                                            DbObject="resource"
                                                            setState={handleChange} />
                                                    </div>
                                                </div>
                                            )
                                        })}
                                    </div>
                                    <div className="box rightBox">
                                        <div className="rightBoxTop">
                                            <div className="rightBoxLeft">
                                                <span>Start</span>
                                            </div>
                                            <div className="rightBoxRight">
                                                <DatePicker
                                                    required
                                                    popperPlacement="left"
                                                    className="input"
                                                    selected={item.plannedStartTime}
                                                    onChange={date => validateTime({ date: date, ident: "start", ref: item })}
                                                    showTimeSelect
                                                    timeFormat="HH:mm"
                                                    timeIntervals={5}
                                                    timeCaption="Uhrzeit"
                                                    dateFormat="dd.M.yyyy / HH:mm"
                                                />
                                            </div>
                                        </div>
                                        <div className="rightBoxBottom">
                                            <div className="rightBoxLeft">
                                                <span>Ende</span>
                                            </div>
                                            <div className="rightBoxRight">
                                                <DatePicker
                                                    required
                                                    className="input"
                                                    popperPlacement="left"
                                                    selected={item.plannedEndTime}
                                                    onChange={date => {
                                                        validateTime({ date: date, ident: "end", ref: item })
                                                    }}
                                                    showTimeSelect
                                                    timeFormat="HH:mm"
                                                    timeIntervals={5}
                                                    timeCaption="Uhrzeit"
                                                    dateFormat="dd.M.yyyy / HH:mm"
                                                />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </React.Fragment>
                        )
                    })}
                    <hr />
                    <div style={{ textAlign: "right" }}>
                        <Button>Terminvorschlag</Button>
                        <Button variant="success" type="submit" style={{ marginLeft: "5px" }}>Buchen</Button>
                    </div>
                    <hr />
                </Form>
                <Calendar
                    localizer={localizer}
                    events={calendarEvents}
                    startAccessor="start"
                    endAccessor="end"
                    selectable={true}
                    style={{ height: "80vh" }}
                />
            </Container>
        </div >
    )
}

export default BookingForm