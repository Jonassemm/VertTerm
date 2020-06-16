import React, { useState, useEffect } from "react"
import { Calendar, momentLocalizer } from "react-big-calendar"
import moment from "moment"
import { Container, Form, Col, Row, Button, Modal } from "react-bootstrap"
import { observer } from "mobx-react"
import "react-big-calendar/lib/css/react-big-calendar.css"
import ObjectPicker from "../ObjectPicker"
import "./BookingForm.css"
import DatePicker from "react-datepicker"
import { addAppointmentGroup, getAppointmentGroup, addAppointmentGroupOverride } from "../requests"
import { Redirect } from "react-router"
import { useHistory } from "react-router-dom"
import { getErrorMessage } from "./bookingErrors"

const localizer = momentLocalizer(moment)

function BookingForm(props) {
    const [calendarEvents, setCalendarEvents] = useState([])
    const [selectedProcedures, setSelectedProcedures] = useState([])
    const [selectedCustomer, setSelectedCustomer] = useState({})
    const [custom, setCustom] = useState(false)
    const [apts, setApts] = useState([])
    const [formComplete, setFormComplete] = useState(false)
    const [formEmpty, setFormEmpty] = useState(true)
    const [exception, setException] = useState("")
    const [showExceptionModal, setShowExceptionModal] = useState(false)
    const history = useHistory()

    useEffect(() => {
        checkCompletion()
        checkFormEmpty()
    })

    useEffect(() => {
        setupEdit()
    }, [])

    async function setupEdit() {
        if (!(Object.keys(props).length === 0 && props.constructor === Object)) {
            const { appointmentID, appointmentGroupID } = props.match.params
            const { data } = await getAppointmentGroup(appointmentGroupID)
            setApts(data.appointments)
        }
    }


    const secondsToMinutes = (time) => {
        if (time === null) {
            return null
        } else {
            return (time / 60)
        }
    }
    //function for setting the times and checking if they are valid
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
        // defining function for Appointments without a fixed duration
        const calculateDifference = (ref1, ref2, end) => {
            if (ref1) {
                //checking if selected date is in the future
                if (data.date.getTime() - currentDate.getTime() > 0) {
                    //checking if selected date is in the right place on the timescale
                    if ((data.date.getTime() - ref1.getTime()) < 0) {
                        if (end) ref2 = ref1
                        else ref2 = data.date
                    } else {
                        if (end) ref2 = data.date
                        else {
                            ref2 = data.date
                            ref1 = data.date
                        }
                    }
                } else {
                    if (end) ref2 = ref1
                    else ref2 = currentDate
                }
            } else {
                if (data.date.getTime() - currentDate.getTime() > 0) ref2 = data.date
                else ref2 = currentDate
            }
            return ([ref1, ref2])
        }
        // checking wheter a selected time is in the past and if the differece between start and end is positive
        const refIndex = tempApts.findIndex(item => item.bookedProcedure.id == data.ref.bookedProcedure.id)
        const procedureDuration = tempApts[refIndex].bookedProcedure.duration / 60
        if (data.ident == "start") {
            const [ref1, ref2] = calculateDifference(tempApts[refIndex].plannedEndtime, tempApts[refIndex].plannedStarttime)
            tempApts[refIndex].plannedStarttime = ref2
            //adjusting times to fit the right duration
            if (tempApts[refIndex].bookedProcedure.duration != null) {
                const temp = new Date(ref2)
                temp.setMinutes(ref2.getMinutes() + procedureDuration)
                tempApts[refIndex].plannedEndtime = temp
            } else {
                tempApts[refIndex].plannedEndtime = ref1
            }

        } else {
            // same as above but when setting the end time
            const [ref1, ref2] = calculateDifference(tempApts[refIndex].plannedStarttime, tempApts[refIndex].plannedEndtime, true)
            tempApts[refIndex].plannedEndtime = ref2
            if (tempApts[refIndex].bookedProcedure.duration != null) {
                const temp = new Date(ref2)
                temp.setMinutes(ref2.getMinutes() - procedureDuration)
                if (temp.getTime() - currentDate.getTime() > 0) tempApts[refIndex].plannedStarttime = temp
                else tempApts[refIndex].plannedStarttime = null
            } else {
                tempApts[refIndex].plannedStarttime = ref1
            }
        }
        setApts(tempApts)
    }

    // function to set selected employee for postion or resource for resource type
    function handleChange(data) {
        const temp = apts.map(item => {
            if (item.bookedProcedure.name == data.ident.ident) {
                if (data.DbObject == "employee") {
                    const temp = item.bookedEmployees.map((innerItem, index) => {
                        return { ...innerItem }
                    })
                    temp[data.ident.ix] = data.data[0]
                    return {
                        ...item,
                        bookedEmployees: temp
                    }
                } else {
                    const temp = item.bookedResources.map(innerItem => {
                        return { ...innerItem }
                    })
                    temp[data.ident.ix] = data.data[0]
                    return {
                        ...item,
                        bookedResources: temp
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

    //prepares appointment objects after seleting a new procedure or removing one from the list
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
                if (!data.find(item => item.name == apts[i].bookedProcedure.name)) {
                    tempApts.splice(i, 1)
                }
            }
        } else {
            // add one
            const tempEmployees = data[data.length - 1].neededEmployeePositions.map(item => {
                return {}
            })
            tempApts.push({
                bookedProcedure: data[data.length - 1],
                bookedEmployees: tempEmployees,
                bookedCustomer: {},
                bookedResources: [],
                plannedStarttime: null,
                plannedEndtime: null
            })
        }
        setApts(tempApts)
    }

    //checking if all necessary field are filled --> for booking function
    function checkCompletion() {
        let complete = false
        apts.forEach((item) => {
            if ((item.bookedEmployees.length != item.bookedProcedure.neededEmployeePositions.length) ||
                (item.bookedResources.length != item.bookedProcedure.neededResourceTypes.length) ||
                (item.plannedEndtime === null) ||
                (item.plannedStarttime === null) ||
                (item.bookedCustomer === null)) {
                complete = false
            } else {
                complete = true
                for (let i = 0; i < item.bookedResources.length; i++) {
                    if (item.bookedResources[i] === undefined) complete = false
                }
                for (let i = 0; i < item.bookedEmployees.length; i++) {
                    if (item.bookedEmployees[i] === undefined) complete = false
                }
            }
        })
        setFormComplete(complete)
    }

    //checking if all form fields are emtpy --> for search function
    function checkFormEmpty() {
        let empty = false
        apts.forEach((item) => {
            if ((item.plannedEndtime === null) &&
                (item.plannedStarttime === null)) {
                empty = true
                for (let i = 0; i < item.bookedResources.length; i++) {
                    if (item.bookedResources[i] != undefined) empty = false
                }
                for (let i = 0; i < item.bookedEmployees.length; i++) {
                    if (item.bookedEmployees[i] != undefined) empty = false
                }
            } else {
                empty = false
            }
        })
        setFormEmpty(empty)
    }

    function optimizeAppointment() {
        //TODO missing API
    }

    function searchAppointment() {
        //TODO missing API
    }

    function buildFinalData() {
        const finalData = apts.map(item => {
            return {
                ...item,
                bookedCustomer: { id: selectedCustomer[0].id, ref: "user" },
                bookedProcedure: { id: item.bookedProcedure.id, ref: "procedure" },
                bookedEmployees: item.bookedEmployees.map(item => {
                    return { id: item.id, ref: "user" }
                }),
                bookedResources: item.bookedResources.map(item => {
                    return { id: item.id, ref: "resource" }
                }),
                plannedEndtime: moment(item.plannedEndtime).format("DD.MM.YYYY HH:mm").toString(),
                plannedStarttime: moment(item.plannedStarttime).format("DD.MM.YYYY HH:mm").toString(),
            }
        })
        const aptGroup = { appointments: finalData, status: "active" }
        return aptGroup
    }

    async function overrideSubmit() {
        if (selectedCustomer.length != 0 || selectedCustomer === {}) {
            const aptGroup = buildFinalData()
            await addAppointmentGroupOverride(aptGroup, selectedCustomer[0].id)
            history.push("/appointment")
        } else {
            setException("customer")
            setShowExceptionModal(true)
        }
    }

    async function handleSubmit(event) {
        event.preventDefault()
        if (selectedCustomer.length != 0 || selectedCustomer === {}) {
            const aptGroup = buildFinalData()
            console.log(aptGroup)
            try {
                const res = await addAppointmentGroup(aptGroup, selectedCustomer[0].id)
                history.push("/appointment")
            } catch (error) {
                setException(error.response.headers.exception)
                setShowExceptionModal(true)
            }

        } else {
            setException("customer")
            setShowExceptionModal(true)
        }
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
                                DbObject="activeUser"
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
                                        <h4>{item.bookedProcedure.name}</h4>
                                        {item.bookedProcedure.duration != null && <p>{secondsToMinutes(item.bookedProcedure.duration)} Minuten Dauer</p>}
                                    </div>
                                    <div className="box wrap">
                                        <div className="middleBox" id={item.bookedProcedure.id}>
                                            <div className="middleBoxLeft">
                                                <span>Beschreibung</span>
                                            </div>
                                            <div className="middleBoxRight">
                                                <Form.Control
                                                    type="text"
                                                    value={item.description ||""}
                                                    placeholder="Beschreibung"
                                                    onChange={e => {setApts(apts.map(innerItem => {
                                                        if(innerItem.bookedProcedure.id = e.target.parentElement.parentElement.id)
                                                        return {
                                                            ...innerItem,
                                                            description: e.target.value
                                                        }
                                                        else 
                                                        return {...innerItem}
                                                       
                                                    }))}}
                                                />
                                                <hr/>
                                            </div>
                                        </div>
                                        {/* all ObjectPickers for needed Employees */}
                                        {item.bookedProcedure.neededEmployeePositions && item.bookedProcedure.neededEmployeePositions.map((innerItem, index) => {
                                            return (
                                                <div className="middleBox">
                                                    <div className="middleBoxLeft">
                                                        <span>{innerItem.name}</span>
                                                    </div>
                                                    <div className="middleBoxRight">
                                                        <ObjectPicker
                                                            className="input"
                                                            ident={{ "ident": item.bookedProcedure.name, "ix": index }}
                                                            filter={innerItem.id}
                                                            DbObject="employee"
                                                            setState={handleChange} />
                                                    </div>
                                                </div>
                                            )
                                        })}
                                        {/* all ObjectPickers for needed resources */}
                                        {item.bookedProcedure.neededResourceTypes && item.bookedProcedure.neededResourceTypes.map((innerItem, index) => {
                                            return (
                                                <div className="middleBox">
                                                    <div className="middleBoxLeft">
                                                        <span>{innerItem.name}</span>
                                                    </div>
                                                    <div className="middleBoxRight">
                                                        <ObjectPicker
                                                            className="input"
                                                            filter={innerItem.id}
                                                            ident={{ "ident": item.bookedProcedure.name, "ix": index }}
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
                                                    selected={item.plannedStarttime}
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
                                                    selected={item.plannedEndtime}
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
                    <Row>
                        <Col xs={8} style={{ textAlign: "left", display: "flex", alignItems: "baseline", justifyContent: "space-between" }}>
                            <Form.Control style={{ width: "50%" }} as="select">
                                <option>Frühstes Ende</option>
                                <option>Wenig Wartezeit</option>
                                <option>Wenige Teiltermine</option>
                            </Form.Control>
                            <DatePicker
                                className="endDate"
                                selected={new Date()}
                                timeFormat="HH:mm"
                                dateFormat="dd.M.yyyy"
                            />
                            <Button onClick={optimizeAppointment}>Terminvorschlag</Button>
                        </Col>
                        <Col style={{ textAlign: "right" }}>
                            {selectedProcedures.length != 0 && (formComplete ?
                                <Button variant="success" type="submit" style={{ marginLeft: "5px" }}>Buchen</Button> :
                                (formEmpty && <Button onClick={searchAppointment} style={{ marginLeft: "5px" }}>Suchen</Button>)
                            )}
                        </Col>
                    </Row>

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
            <Modal show={showExceptionModal} onHide={() => setShowExceptionModal(false)}>
                <Modal.Header>
                    <Modal.Title>
                        {exception} Exception
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {getErrorMessage(exception)}
                </Modal.Body>
                <Modal.Footer>
                    {exception != "customer" && <Button variant="danger" onClick={overrideSubmit}>Trotzdem buchen</Button>}
                    <Button onClick={() => setShowExceptionModal(false)} variant="secondary">OK</Button>
                </Modal.Footer>
            </Modal>

        </div >
    )
}

export default BookingForm