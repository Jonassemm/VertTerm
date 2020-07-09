import React, { useState, useEffect } from "react"
import {momentLocalizer } from "react-big-calendar"
import moment from "moment"
import { Container, Form, Col, Row, Button, Modal, Spinner } from "react-bootstrap"
import ObjectPicker from "../ObjectPicker"
import "./BookingForm.css"
import DatePicker from "react-datepicker"
import { addAppointmentGroup, addAppointmentGroupAnyOverride, addAppointmentGroupOverride,getAppointmentGroupByApt,editAppointmentGroup, editAppointmentGroupOverride, addAppointmentGroupAny, getAppointment, searchAppointmentGroup, addBlocker, OptimizeEarlyEnd } from "../requests"
import { useHistory } from "react-router-dom"
import { getErrorMessage } from "./bookingErrors"
import SearchAptCalendar from "./SearchCalendar/SearchAptCalendar"
import AppointmentQR from "./AppointmentQR"
import BlockerForm from "./BlockerForm"

const localizer = momentLocalizer(moment)

function BookingForm({editData,userStore}) {
    const [calendarEvents, setCalendarEvents] = useState([])
    const [selectedProcedures, setSelectedProcedures] = useState([])
    const [selectedCustomer, setSelectedCustomer] = useState([])
    const [custom, setCustom] = useState(false)
    const [apts, setApts] = useState([])
    const [customApt, setCustomApt] = useState({bookedCustomer: []})
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
        if (editData) {
            console.log(editData)
            setEditMode(true)
            const appointmentID = editData.match.params.appointmentID
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
            if(editData.match.params.startTime){
                console.log("yes Start Time")
                validateTime({date: editData.match.params.startTime, ident: "start", ref: tempApt})
                
                userStore.setInfoMessage("Optimal Zeit wurde bereits übernommen!")
            }
            setEditApt(tempApt.data)
            setSelectedProcedures(tempProcedures)
            setSelectedCustomer([data[0].bookedCustomer])
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
        console.log(data)
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
        if(!custom){
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
        }else {
            if((customApt.plannedStarttime != null) && (customApt.plannedEndtime != null)) complete = true
        }
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
                if(apt.bookedProcedure.duration != null){
                    return {
                        ...apt,
                        plannedStarttime: timeslot.start,
                        plannedEndtime: moment(timeslot.start).add(apt.bookedProcedure.duration,"seconds").toDate()
                    }
                }else 
                return {
                    ...apt,
                    plannedStarttime: timeslot.start,
                    plannedEndtime: timeslot.end
                }
            }else return {...apt}
        }))
        setShowSelectCalendarModal(false)
    }

    async function optimizeAppointment() {
        const aptGroup = buildFinalData()
        const res = await OptimizeEarlyEnd(aptGroup)
    }

    async function searchAppointment() {
        const aptGroup = buildFinalData()
        let {data} = await searchAppointmentGroup(aptGroup)
        data = data.appointments
        console.log(data)
        const temp = apts.map((apt,index) => {
            return {
                ...apt,
                bookedEmployees: data[index].bookedEmployees,
                bookedResources: data[index].bookedResources
            }
        })
        console.log(temp)
        setApts(temp)
    }

    function buildFinalData() {
        let aptGroup = null
        if(!custom){
        let finalData = apts.map(item => {
            return {
                ...item,
                bookedProcedure: item.bookedProcedure,
                bookedEmployees: item.bookedEmployees.map(item => {
                    if(item) return { id: item.id, ref: "user" }
                    else return {...item}
                }),
                bookedResources: item.bookedResources.map(item => {
                    if(item) return { id: item.id, ref: "resource" }
                    else return {...item}
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
        aptGroup = { appointments: finalData, status: "active" }
    }else{
        aptGroup = {
            name: customApt.name,
            bookedResources: customApt.bookedResources.map(item => {
                return {id: item.id, ref:"resource"}
            }),
            bookedEmployees: customApt.bookedEmployees.map(item => {
                return {id: item.id, ref:"user"}
            }),
            plannedEndtime: moment(customApt.plannedEndtime).format("DD.MM.YYYY HH:mm").toString(),
            plannedStarttime: moment(customApt.plannedStarttime).format("DD.MM.YYYY HH:mm").toString(),
            status: "planned"
            }
    }
        return aptGroup
    }

    function handleSearchApt(procedure) {
        setSearchProcedureApt(procedure)
        setShowSelectCalendarModal(true)
    }

    async function overrideSubmit() {
            const aptGroup = buildFinalData()
            try{
            if(!editMode){
                if(selectedCustomer.length != 0){
                    await addAppointmentGroupOverride(aptGroup, selectedCustomer[0].id)
                    userStore.setMessage("Termin erfolgreich gebucht!")
                    history.push("/appointment")
                } else {
                    const res = await addAppointmentGroupAnyOverride(aptGroup)
                    const data = btoa(res.data)
                    userStore.setMessage("Termin erfolgreich gebucht!")
                    setQRCred(data)
                    setShowMode(true)
                }
            }else{
                await editAppointmentGroupOverride(aptGroup, selectedCustomer[0].id)
                userStore.setMessage("Termin erfolgreich geändert!")
            }
            history.push("/appointment")
        }catch(error){
            userStore.setMessage("Error!")
        }
    }


    async function handleSubmit(event) {
        event.preventDefault()
            const aptGroup = buildFinalData()
            console.log(aptGroup)
            try {
            if(editMode){
                await editAppointmentGroup(aptGroup, selectedCustomer[0].id)
                userStore.setMessage("Termin erfolgreich gebucht!")
                history.push("/appointment")
            }else if(custom){
                await addBlocker(aptGroup)
                userStore.setMessage("Termin erfolgreich gebucht!")
                history.push("/appointment")
            }else {
                if(selectedCustomer.length != 0){
                    await addAppointmentGroup(aptGroup, selectedCustomer[0].id)
                    userStore.setMessage("Termin erfolgreich gebucht!")
                    history.push("/appointment")
                } else {
                    const res = await addAppointmentGroupAny(aptGroup)
                    const data = btoa(res.data)
                    userStore.setMessage("Termin erfolgreich gebucht!")
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
            <Container id="bookingForm">
                <Form onSubmit={handleSubmit}>
                    <Row style={{ alignItems: "baseline" }}>
                        <Col>
                            <h1>{editMode? "Termin ändern" : showMode ? "betabook.me Terminbestätigung" : "Termin Buchen"}</h1>
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
                    
                    {custom && <BlockerForm apt={customApt} setApt={setCustomApt} edit={editMode} customer={selectedCustomer}/>}
                    {!custom &&
                    <React.Fragment>
                    <hr />
                    {!showMode && !editMode &&
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
                    {editMode &&
                        <Form.Row>
                        <Form.Group as={Col} style={{ textAlign: "bottom" }}>
                            <Form.Label>Kunde:</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col}>
                            <Form.Control
                                type="text"
                                value={selectedCustomer[0] && (`${selectedCustomer[0].firstName} ${selectedCustomer[0].lastName}`)}
                            />
                        </Form.Group>
                    </Form.Row>
                    }
                    <React.Fragment>
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
                    {apts.map(apt => {
                        return (
                            <React.Fragment>
                                <hr />
                                <div className="parent" style={editMode ? (apt.bookedProcedure.id == editApt.bookedProcedure.id) ? selectedAptStyle : null : null}>
                                    <div className="namebox">
                                        <h4>{apt.bookedProcedure.name}</h4>
                                        {apt.bookedProcedure.duration != null && <p>{secondsToMinutes(apt.bookedProcedure.duration)} Minuten Dauer</p>}
                                    </div>
                                    <div className="box wrap">
                                        <div className="middleBox" id={apt.bookedProcedure.id}>
                                            <div className="middleBoxLeft">
                                                <span>Beschreibung</span>
                                            </div>
                                            <div className="middleBoxRight">
                                                <Form.Control
                                                    type="text"
                                                    value={apt.description || ""}
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
                                        {apt.bookedProcedure.neededEmployeePositions && apt.bookedProcedure.neededEmployeePositions.map((innerItem, index) => {
                                            return (
                                                <div className="middleBox">
                                                    <div className="middleBoxLeft" style={{paddingTop: "7px"}}>
                                                        <span style={{}}>{innerItem.name}</span>
                                                    </div>
                                                    <div className="middleBoxRight">
                                                        <ObjectPicker
                                                            className="input"
                                                            ident={{ "ident": apt.bookedProcedure.name, "ix": index }}
                                                            initial = {[apt.bookedEmployees[index]]}
                                                            filter={innerItem.id}
                                                            DbObject="employee"
                                                            setState={handleChange} />
                                                    </div>
                                                </div>
                                            )
                                        })}
                                        {/* all ObjectPickers for needed resources */}
                                        {apt.bookedProcedure.neededResourceTypes && apt.bookedProcedure.neededResourceTypes.map((innerItem, index) => {
                                            return (
                                                <div className="middleBox">
                                                    <div className="middleBoxLeft" style={{paddingTop: "7px"}}>
                                                        <span>{innerItem.name}</span>
                                                    </div>
                                                    <div className="middleBoxRight">
                                                        <ObjectPicker
                                                            className="input"
                                                            filter={innerItem.id}
                                                            initial={[apt.bookedResources[index]]}
                                                            ident={{ "ident": apt.bookedProcedure.name, "ix": index }}
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
                                                    selected={apt.plannedStarttime}
                                                    onChange={date => validateTime({ date: date, ident: "start", ref: apt })}
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
                                                    selected={apt.plannedEndtime}
                                                    onChange={date => {
                                                        validateTime({ date: date, ident: "end", ref: apt })
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
                                            {!showMode && <Button style={{ marginTop: "10px", width: "40%", padding:"2px" }} onClick={e => handleSearchApt(apt.bookedProcedure)}>Zeit suchen</Button>}
                                        </div>
                                    </div>
                                </div>
                            </React.Fragment>
                        )
                    })}
                    </React.Fragment>
                    </React.Fragment>
                    }
                    <hr />
                    <Row>
                        {!editMode && !showMode && !custom && (selectedProcedures.length != 0) &&
                        <Col xs={7} style={{ textAlign: "left", display: "flex", alignItems: "baseline", justifyContent: "space-between" }}>
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
                            {(custom || selectedProcedures.length != 0) && (formComplete ?
                                <Button variant="success" type="submit" style={{ marginLeft: "5px" }}>{editMode ? "Speichern" : "Buchen"}</Button> :
                                (formEmpty && !custom && <Button onClick={searchAppointment} style={{ marginLeft: "5px" }}>Suchen</Button>)
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