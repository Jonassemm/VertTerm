import React, { useState, useEffect } from "react"
import {momentLocalizer } from "react-big-calendar"
import moment from "moment"
import { Container, Form, Col, Row, Button, Modal, Spinner } from "react-bootstrap"
import ObjectPicker from "../ObjectPicker"
import "./BookingForm.css"
import DatePicker from "react-datepicker"
import { addAppointmentGroup, addAppointmentGroupOverride,getAppointmentGroupByApt,editAppointmentGroup, addAppointmentGroupAny, getAppointment } from "../requests"
import { useHistory } from "react-router-dom"
import { getErrorMessage } from "./bookingErrors"
import SearchAptCalendar from "./SearchCalendar/SearchAptCalendar"
import AppointmentQR from "./AppointmentQR"

const localizer = momentLocalizer(moment)

function BookingForm(props) {
    const [calendarEvents, setCalendarEvents] = useState([])
    const [selectedProcedures, setSelectedProcedures] = useState([])
    const [selectedCustomer, setSelectedCustomer] = useState([])
    const [custom, setCustom] = useState(false)
    const [apts, setApts] = useState([])
    const [formComplete, setFormComplete] = useState(false)
    const [formEmpty, setFormEmpty] = useState(true)
    const [exception, setException] = useState("")
    const [exceptionMessage, setExceptionMessage] = useState("")
    const [showExceptionModal, setShowExceptionModal] = useState(false)
    const [showSelectCalendarModal, setShowSelectCalendarModal] = useState(false)
    const [searchProcedureApt, setSearchProcedureApt] = useState({})
    const history = useHistory()
    const [loading, setLoading] = useState(false)
    const [editMode, setEditMode] = useState(false)
    const [showMode, setShowMode] = useState(false)
    const [QRCred, setQRCred] = useState("")
    const [editApt, setEditApt] = useState({})

    useEffect(() => {
        checkCompletion()
        checkFormEmpty()
    })

    useEffect(() => {
        setupEdit()
    }, [])

    async function setupEdit() {
        if (!(Object.keys(props).length === 0 && props.constructor === Object)) {
            setEditMode(true)
            const { appointmentID } = props.match.params
            let {data} = await getAppointmentGroupByApt(appointmentID)
            data = data.appointments.map(item => {
                return {...item,
                plannedStarttime: moment(item.plannedStarttime,  "DD.MM.YYYY HH:mm").toDate(),
                plannedEndtime: moment(item.plannedEndtime,  "DD.MM.YYYY HH:mm").toDate()}
            })
            const tempProcedures = data.map(item => {
                return item.bookedProcedure
            })
            const tempApt = await getAppointment(appointmentID)
            setSelectedCustomer([data[0].bookedCustomer])
            setEditApt(tempApt.data)
            setSelectedProcedures(tempProcedures)
            setApts(data)
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
            tempApts.push({
                bookedProcedure: data[data.length - 1],
                bookedEmployees: [],
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
            if ((item.plannedEndtime != null) &&
                (item.plannedStarttime != null)) {
                empty = true
                for (let i = 0; i < item.bookedResources.length; i++) {
                    if (!(item.bookedResources[i] === undefined || Object.keys(item.bookedResources[i]).length === 0)) empty = false
                }
                for (let i = 0; i < item.bookedEmployees.length; i++) {
                    if (!(item.bookedEmployees[i] === undefined || Object.keys(item.bookedEmployees[i]).length === 0)) empty = false
                }
            } else {
                empty = false
            }
        })
        setFormEmpty(empty)
    }

    function setSelectedTime(procedure, timeslot){
        setApts(apts.map(apt => {
            if(apt.bookedProcedure.id == procedure.id){
                return {
                    ...apt,
                    plannedStarttime: timeslot.start,
                    plannedEndtime: timeslot.end
                }
            }else return {...apt}
        }))
        setShowSelectCalendarModal(false)
    }

    function optimizeAppointment() {
        //TODO missing API
    }

    function searchAppointment() {
        //TODO missing API
    }

    function buildFinalData() {
        let finalData = apts.map(item => {
            return {
                ...item,
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
        if(selectedCustomer.length != 0){
            finalData = finalData.map(item => {
                return {
                    ...item,
                    bookedCustomer: { id: selectedCustomer[0].id, ref: "user" },
                }
            })
        }
        const aptGroup = { appointments: finalData, status: "active" }
        return aptGroup
    }

    function handleSearchApt(procedure) {
        setSearchProcedureApt(procedure)
        setShowSelectCalendarModal(true)
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
            const aptGroup = buildFinalData()
            try {
            if(editMode){
                    await editAppointmentGroup(aptGroup, selectedCustomer[0].id)
                    history.push("/appointment")
              
            }else{
               
                    if(selectedCustomer.length != 0){
                        await addAppointmentGroup(aptGroup, selectedCustomer[0].id)
                        history.push("/appointment")
                    } else {
                        console.log("anonym")
                        const res = await addAppointmentGroupAny(aptGroup)
                        // obscuring the link
                        const data = btoa(res.data)
                        console.log(data)
                        setQRCred(data)
                        setShowMode(true)
                    }
            }
        } catch (error) {
            setException(error.response.headers.exception)
            setExceptionMessage(error.response.data)
            setShowExceptionModal(true)
        }
    }

    const selectedAptStyle = {padding:"2px",backgroundColor:"#FFEEC7"}

    return (
        <div className="page">
            <Container>
                <Form onSubmit={handleSubmit}>
                    <Row style={{ alignItems: "baseline" }}>
                        <Col>
                            <h1>{editMode? "Termin ändern" : showMode ? "VertTerm Terminbestätigung" : "Termin Buchen"}</h1>
                        </Col>
                        {!editMode && !showMode &&
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
                        }
                    </Row>
                    <hr />
                    {!showMode &&
                        <Form.Row>
                            <Form.Group as={Col} style={{ textAlign: "bottom" }}>
                                <Form.Label>Kunde:</Form.Label>
                            </Form.Group>
                            <Form.Group as={Col}>
                                <ObjectPicker
                                    DbObject="activeUser"
                                    initial={editMode && selectedCustomer}
                                    disabled={editMode}
                                    setState={setSelectedCustomer} />
                            </Form.Group>
                        </Form.Row>
                    }
                    <Form.Row>
                        <Form.Group as={Col} style={{ textAlign: "bottom" }}>
                            <Form.Label>Prozeduren:</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col}>
                            <ObjectPicker
                                initial={editMode && selectedProcedures}
                                DbObject="procedure"
                                disabled={editMode}
                                setState={setProcedures}
                                multiple={true} />
                        </Form.Group>
                    </Form.Row>
                    {!custom && apts.map(item => {
                        return (
                            <React.Fragment>
                                <hr />
                                <div className="parent" style={editMode ? (item.bookedProcedure.id == editApt.bookedProcedure.id) ? selectedAptStyle : null : null}>
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
                                                    value={item.description || ""}
                                                    placeholder="Beschreibung"
                                                    onChange={e => {
                                                        setApts(apts.map(innerItem => {
                                                            if (innerItem.bookedProcedure.id == e.target.parentElement.parentElement.id)
                                                                return {
                                                                    ...innerItem,
                                                                    description: e.target.value
                                                                }
                                                            else
                                                                return { ...innerItem }

                                                        }))
                                                    }}
                                                />
                                                <hr />
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
                                                            initial = {editMode && [item.bookedEmployees[index]]}
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
                                                            initial={editMode && [item.bookedResources[index]]}
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
                                        <div style={{ textAlign: "right" }}>
                                            {!showMode && <Button style={{ marginTop: "10px", width: "40%", padding:"2px" }} onClick={e => handleSearchApt(item.bookedProcedure)}>Zeit suchen</Button>}
                                        </div>
                                    </div>
                                </div>
                            </React.Fragment>
                        )
                    })}
                    <hr />
                    <Row>
                        {!editMode && !showMode && (selectedProcedures.length != 0) &&
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
                        }
                        {!showMode &&
                        <Col style={{ textAlign: "right" }}>
                            {selectedProcedures.length != 0 && (formComplete ?
                                <Button variant="success" type="submit" style={{ marginLeft: "5px" }}>{editMode ? "Speichern" : "Buchen"}</Button> :
                                (formEmpty && <Button onClick={searchAppointment} style={{ marginLeft: "5px" }}>Suchen</Button>)
                            )}
                        </Col>
                        }
                        {showMode &&
                            <Col>
                                <AppointmentQR cred={QRCred}/>      
                            </Col>
                        }
                    </Row>
                    <hr />
                </Form>
            </Container>

            <Modal show={showExceptionModal} onHide={() => setShowExceptionModal(false)}>
                <Modal.Header>
                    <Modal.Title>
                        {exception} Exception
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {getErrorMessage(exception)}
                    <hr/>
                    <span>{exceptionMessage}</span>
                </Modal.Body>
                <Modal.Footer>
                    {exception != "customer" && <Button variant="danger" onClick={overrideSubmit}>Trotzdem buchen</Button>}
                    <Button onClick={() => setShowExceptionModal(false)} variant="secondary">OK</Button>
                </Modal.Footer>
            </Modal>

            <Modal size="xl" show={showSelectCalendarModal} onHide={() => setShowSelectCalendarModal(false)}>
                <Modal.Header>
                    <Modal.Title>
                        Termin suchen
                       
                    </Modal.Title>
                    {loading && 
                    <Spinner animation="border" role="status">
                            <span className="sr-only">Loading...</span>
                        </Spinner>
                    } 
                </Modal.Header>
                <Modal.Body>
                    <SearchAptCalendar procedure={searchProcedureApt} 
                    neededPositions={searchProcedureApt.neededEmployeePositions} 
                    neededResourceTypes={searchProcedureApt.neededResourceTypes} 
                    setLoading={setLoading} 
                    setSelectedTime={setSelectedTime}/>
                </Modal.Body>
            </Modal>

        </div >
    )
}

export default BookingForm