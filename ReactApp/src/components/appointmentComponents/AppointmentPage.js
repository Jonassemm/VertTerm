//author: Patrick Venturini
import React, {useState, useEffect} from 'react'
import {Form, Col, Tabs, Tab, Button, Modal, Spinner, Toast} from "react-bootstrap"
import OverviewPage from "../OverviewPage"
import {Link} from 'react-router-dom';
import AppointmentForm from "./AppointmentForm"
import CalendarPage from "../calendarComponents/CalendarPage"
import ObjectPicker from "../ObjectPicker"
import {appointmentStatus, translateStatus} from "./AppointmentStatus"
import {ExceptionModal} from "../ExceptionModal"
import styled from "styled-components"
import { hasRight } from "../../auth"
import {ownAppointmentRights, appointmentRights, overrideRight} from "../Rights"

import {
    getAllAppointments,
    getAppointmentsByID,
    getAppointmentOfUser, 
    getOwnAppointments, 
    getAllAppointmentInTimespace, 
    getAppointmentOfUserInTimespace,
    deleteOverrideAppointment
} from "./AppointmentRequests";


export const loadMode = {
    own: "own",
    foreignUnpicked: "foreignUnpicked",
    foreign: "foreign",
    all: "all"
}

var moment = require('moment'); 

const Style = styled.div`
.loadview {
    position: fixed;
    top: 0;
    bottom: 0;
    right: 0;
    left: 0;
    background-color: black;
    opacity: 0.6;
    z-index: 300;
    display: flex;
    align-items: center;
    justify-content: center;
}`


export default function AppointmentPage({calendarStore, userStore}) {
    var initialUser = []
    var initialLoadMode = loadMode.own

    if(userStore.user != null && userStore.firstName != null){
        initialUser = [userStore.user]
    }else{
        if(userStore.username == "admin"){
            initialLoadMode = loadMode.all
        }
    }

    const rightNameOwn = ownAppointmentRights[0] //read right
    const rightName = appointmentRights[0] //read right
    const rightOverride = overrideRight[0] // array with just one right
    const [loading, setLoading] = useState(false) //for loading appointments (could take some time)
    const [calendarLoaded, setCalendarLoaded] = useState(false)
    const [tabKey, setTabKey] = useState('calendar')
    const [selectedUser, setSelectedUser] = useState(initialUser)
    const [tableAppointments, setTableAppointments] = useState([])
    const [appointmentsOf, setAppointmentsOf] = useState(initialLoadMode)
    const [exception, setException] = useState(null)
    const [showExceptionModal, setShowExceptionModal] = useState(false)
    const [showPreferredAppointmentModal, setShowPreferredAppointmentModal] = useState(false)
    const [preferredAppointment, setPreferredAppointment] = useState(null)
    const [preferredAppointmentStarttime, setPreferredAppointmentStarttime] = useState(null)
    const [overrideDeleteId, setOverrideDeleteId] = useState(null)
    const [allowOwnView, setAllowOwnView] = useState(true)
    const [allowEntireView, setAllowEntireView] = useState(false)


    useEffect( () => {
        if(appointmentsOf ==  loadMode.foreign){
            loadAppointments(appointmentsOf)
        }
    },[appointmentsOf])


    useEffect( () => {
        loadAppointments(appointmentsOf)
    },[tabKey])


    useEffect( () => {
        if(hasRight(userStore, [rightName])){
            setAllowEntireView(true)
        }else if(hasRight(userStore, [rightNameOwn])){
            setAllowOwnView(true)
        }else{
            setAllowEntireView(false)
            setAllowOwnView(false)
        }
    },[])


    const handleSelectedUserChange = (data) => {
        if(data.length > 0){
            loadAppointments(loadMode.foreign)
            setAppointmentsOf(loadMode.foreign)   
        }else {
            setAppointmentsOf(loadMode.foreignUnpicked)
            setCalendarLoaded(false)
        }
        setSelectedUser(data)
    }


    //-------------------------------ExceptionModal--------------------------------
    const handleExceptionChange = (newException, id) => {
        setException(newException)
        setOverrideDeleteId(id)
        setShowExceptionModal(true)
    }


    const handleOverrideDelete = async () => {
        if(hasRight(userStore, [rightOverride])){
            try{
                await deleteOverrideAppointment(overrideDeleteId);
            } catch (error){
                console.log(Object.keys(error), error.message)
            }
            setShowExceptionModal(false)
            loadAppointments(appointmentsOf)
        }else{
            userStore.setWarningMessage("Ihnen fehlt das Recht:\n"+ rightOverride)
        }
    }


    //-------------------------PreferredAppointmentModal----------------------------
    const handlePreferredAppointmentChange = (id, starttime) =>{
        setPreferredAppointmentStarttime(starttime)
        //load information to this appointment
        loadPreferredAppointment(id)
    }


    //--------------------------------------LOAD--------------------------------------
    const loadPreferredAppointment = async (id) => {
        var data = null
        try {
            const response = await getAppointmentsByID(id);
            data = response.data
        } catch (error) {
            console.log(Object.keys(error), error.message)
        }  

        if (data != null) {
            setPreferredAppointment(data)
        }

        //show modal
        setShowPreferredAppointmentModal(true)  
    }


    const loadAppointments = async (selection) => {
        setLoading(true)
        var data = []
        var response = null

        //LOAD logged in user
        if(selection!= loadMode.all) { // when loading all users, loggedInUser is not necessary
            if(selection == loadMode.own && userStore.user != null){ //Appointments for logged in user
                setSelectedUser([userStore.user]) 
            }
        }
        //LAOD Appointments for table 
        var reducedData = []
        if(tabKey == "table") {
            try {
                switch(selection){
                    case loadMode.all:
                        response = await getAllAppointments();  
                        break;
                    case loadMode.foreign:
                        if(selectedUser.length > 0) {
                            response = await getAppointmentOfUser(selectedUser[0].id);  
                        }  
                        break;
                    case loadMode.own: 
                        response = await getOwnAppointments();
                        break;
                }

                //create and add title to each appointment
                if(response != null) {
                    data = response.data.map(singleAppointment => { 
                        var title = ""
                        if(singleAppointment.bookedCustomer != null && singleAppointment.bookedProcedure != null){
                            if(singleAppointment.bookedCustomer.firstName != null && singleAppointment.bookedCustomer.lastName != null){
                                title = singleAppointment.bookedProcedure.name + " (" + singleAppointment.bookedCustomer.username + ")"
                            }else {
                                title = singleAppointment.bookedProcedure.name + " (anonym)"
                            }
                        }else if(singleAppointment.name != null){
                                title = singleAppointment.name
                        }
                        return {
                            ...singleAppointment,
                            title: title
                        }
                    }) 
                }

                //don't save object with status="deleted"
                data.map((singleAppointment) => {
                    if(singleAppointment.status != appointmentStatus.deleted) {
                        //don't save blocker ("Buchung -> Benutzerdefiniert Eingabe")
                        if(singleAppointment.bookedCustomer != null && singleAppointment.bookedProcedure != null){
                          reducedData.push(singleAppointment)  
                        }
                    }
                })
                
            }catch (error) {
                console.log(Object.keys(error), error.message)
            }
            setTableAppointments(reducedData)
            setLoading(false)
        //LAOD Appointments for calendar  
        }else {
            var today = new Date
            if(selection == loadMode.own){ //own
                if(userStore.user != null) {
                    loadCalendarAppointments(today, userStore.userID)
                }else{
                    setLoading(false)
                }
            }else if (selection == loadMode.foreign) { //foreign
                if(selectedUser.length > 0) {
                    loadCalendarAppointments(today, selectedUser[0].id)
                }
            }else if(selection == loadMode.all){ //all
                loadCalendarAppointments(today, null)
            }else {
                setLoading(false)
            }
        }
    }


    const loadCalendarAppointments = async (refereceDate, UserID) => { 
        const numberOfMonthsBefore = 1
        const numberOfMontsAfter = 1
        const numberOfDaysBefore = 6
        const numberOfDaysAfter = 6
        var response = []
        var startDate = new Date
        var endDate = new Date
        //28 is the min of days a month can have
        startDate.setDate(28 - numberOfDaysBefore)
        startDate.setMonth(refereceDate.getMonth() - numberOfMonthsBefore)
        startDate.setFullYear(refereceDate.getFullYear())
        endDate.setDate(numberOfDaysAfter)
        endDate.setMonth(refereceDate.getMonth() + numberOfMontsAfter)
        endDate.setFullYear(refereceDate.getFullYear())
        const startDateString = moment(startDate).format("DD.MM.YYYY HH:mm").toString();
        const endDateString =  moment(endDate).format("DD.MM.YYYY HH:mm").toString();
        if(UserID == null){//case all appointments
            try{
               response = await getAllAppointmentInTimespace( 
                    startDateString, 
                    endDateString
               )
            }catch (error) {
                    console.log(Object.keys(error), error.message)
            }
        }else {//case "own" and "foreign" appointments
            try {
                if(UserID != null) {
                    response = await getAppointmentOfUserInTimespace(
                        UserID, 
                        startDateString, 
                        endDateString
                    )
                } 
            } catch (error) {
                console.log(Object.keys(error), error.message)
            }
        }

        //don't save object with status="deleted"
        var reducedData = []
        if(response.data != undefined) {
            response.data.map((singleAppointment) => {
                if(singleAppointment.status != appointmentStatus.deleted) {
                    reducedData.push(singleAppointment)
                }
            })
        }

        //prepare response for calendar
        const evts = reducedData.map(item => {
            var title
            if(item.bookedCustomer != null && item.bookedProcedure != null){
                if(item.bookedCustomer.firstName != null && item.bookedCustomer.lastName != null){
                    title = item.bookedProcedure.name + " (" + item.bookedCustomer.username + ")"
                }else {
                    title = item.bookedProcedure.name + " (anonym)"
                }
                return {
                    ...item,
                    plannedStarttime: moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate(),
                    plannedEndtime: moment(item.plannedEndtime, "DD.MM.yyyy HH:mm").toDate(),
                    title: title
                }
            }else{
                return {
                    ...item,
                    plannedStarttime: moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate(),
                    plannedEndtime: moment(item.plannedEndtime, "DD.MM.yyyy HH:mm").toDate(),
                    title: item.name
                }
            }
        })
        calendarStore.setCalendarEvents(evts)
        setLoading(false)
        setCalendarLoaded(true)
    }


    //----------------------------------Help-Functions------------------------------------
    const setAppointments = (selection) => {
        setAppointmentsOf(selection)
        switch(selection){
            case loadMode.own:
                break;
            case loadMode.foreignUnpicked:
            case loadMode.all:
                setSelectedUser([]) // reset selected user
                break;
            default:
        }
        loadAppointments(selection)
    }


    //------------------------------------OverviewPage-Components----------------------------------
    var tableBody = []
    if(tableAppointments.length > 0) {
        tableBody = tableAppointments.map((item, index) => { 
            var bookedCustomer = ""
            var bookedProcedure = ""
            if(item.bookedCustomer != null){
                if(item.bookedCustomer.firstName != null || item.bookedCustomer.lastName != null){
                    bookedCustomer = item.bookedCustomer.firstName + ", " + item.bookedCustomer.lastName
                }else{
                    bookedCustomer = "anonymer Kunder"
                }
            }
            if(item.bookedProcedure != null){
                bookedProcedure = item.bookedProcedure.name
            }
            var status = translateStatus(item.status)
            return ([
                index + 1,
                moment(moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString(),
                moment(moment(item.plannedEndtime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString(),
                bookedCustomer,
                bookedProcedure,
                item.description,
                status]
            )
        })
    }


    const modal = (onCancel,edit,selectedItem) => {
        return (
            <AppointmentForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
                handleExceptionChange={handleExceptionChange}
                handlePreferredAppointmentChange={handlePreferredAppointmentChange}
                userStore={userStore}
            />
        )
    }


    return (
        <React.Fragment>
            <Style>
                {loading ? <div className="loadview"><Spinner animation="border" variant="light" /></div> : null}
            </Style>
            {exception != null && 
                <ExceptionModal //modal for deleting appointments
                    showExceptionModal={showExceptionModal} 
                    setShowExceptionModal={setShowExceptionModal} 
                    overrideSubmit={handleOverrideDelete}
                    exception={exception}
                    overrideText="Trotzdem löschen"
                />
            }
            {preferredAppointment != null &&
            <Modal size="lg" show={showPreferredAppointmentModal} onHide={() => setShowPreferredAppointmentModal(false)}>
                <Modal.Header>
                    <Modal.Title>
                        Folgender Termin kann vorgezogen werden!
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Row>
                            <Form.Group as={Col} md="4"  style={{ textAlign: "bottom" }}>
                                <Form.Label>Kunde:</Form.Label>
                            </Form.Group>
                            <Form.Group as={Col} md="8" >
                                <Form.Control
                                    readOnly
                                    style={{background: "white"}}
                                    name="customer"
                                    type="text"
                                    value={preferredAppointment.bookedCustomer.firstName == null ? 
                                            "Anonymer Benutzer": 
                                            preferredAppointment.bookedCustomer.firstName + ", " + preferredAppointment.bookedCustomer.lastName}
                                />
                            </Form.Group>
                        </Form.Row>
                        <Form.Row>
                            <Form.Group as={Col} md="4"  style={{ textAlign: "bottom" }}>
                                <Form.Label>Prozedur:</Form.Label>
                            </Form.Group>
                            <Form.Group as={Col} md="8" >
                                <Form.Control
                                    readOnly
                                    style={{background: "white"}}
                                    name="customer"
                                    type="text"
                                    value={preferredAppointment.bookedProcedure.name} 
                                />
                            </Form.Group>
                        </Form.Row>
                        <Form.Row>
                            <Form.Group as={Col} md="4"  style={{ textAlign: "bottom" }}>
                                <Form.Label>Datum:</Form.Label>
                            </Form.Group>
                            <Form.Group as={Col} md="8" >
                                <Form.Control
                                    readOnly
                                    style={{background: "white"}}
                                    name="customer"
                                    type="text"
                                    value={preferredAppointment.plannedStarttime} 
                                />
                            </Form.Group>
                        </Form.Row>
                        <hr/>
                        <Form.Row>
                            <Form.Group as={Col} md="4"  style={{ textAlign: "bottom" }}>
                                <Form.Label>Neues Datum:</Form.Label>
                            </Form.Group>
                            <Form.Group as={Col} md="8" >
                                <Form.Control
                                    readOnly
                                    style={{background: "white"}}
                                    name="customer"
                                    type="text"
                                    value={preferredAppointmentStarttime} 
                                />
                            </Form.Group>
                        </Form.Row>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <div style={{ textAlign: "right" }}>
                        <Button onClick={() => setShowPreferredAppointmentModal(false)} variant="secondary">Nicht vorziehen</Button>
                        {preferredAppointment != null && preferredAppointmentStarttime != null &&
                        <Link to={`/booking/${preferredAppointment.id}/${preferredAppointmentStarttime}`}>
                            <Button variant="success" style={{ marginLeft: "10px" }}>Terminumbuchung</Button>
                        </Link>
                        }
                    </div>
                </Modal.Footer>
            </Modal>
            }
            <div style={{display: "flex",  justifyContent: "center"}}>
                <div style={{margin: "10px 10px 0px 0px", fontWeight: "bold"}}>
                    {(allowEntireView || allowOwnView) && (userStore.username != "admin" || appointmentsOf != loadMode.own)?
                        "Ansicht von": 
                        (userStore.username != "admin" || appointmentsOf != loadMode.own) &&
                        "Ihnen fehlt das Recht " + rightNameOwn + " oder " + rightName + " zur Ansicht von Terminen!"}
                </div>
                <div style={{marginTop: "5px", width: "250px"}}>
                    {appointmentsOf == loadMode.own ? 
                        selectedUser.length > 0 && (allowEntireView || allowOwnView) && userStore.username != "admin" &&
                        <Form.Control
                            readOnly
                            type="text"
                            value={(userStore.firstName == null || userStore.lastName == null) ? userStore.username: selectedUser[0].firstName + " " + selectedUser[0].lastName || ""}
                        />: (appointmentsOf == loadMode.foreign || appointmentsOf == loadMode.foreignUnpicked) ?
                            userStore.user != null && allowEntireView &&
                                <ObjectPicker 
                                    setState={handleSelectedUserChange}
                                    DbObject="user"
                                    initial={selectedUser} 
                                    multiple={false}
                                    selectedItem={userStore.user}
                                />: allowEntireView &&
                                <Form.Control
                                    readOnly
                                    type="text"
                                    value={"Allen Benutzern"}
                                />
                    }
                </div> 
                <div>
                    {allowEntireView && userStore.firstName != null && //Admin and anonym user have no appointments
                        <Button style={{margin:"5px 0px 0px 10px"}} variant="primary" onClick={() => setAppointments(loadMode.own)}>Eigene</Button>
                    }
                    {allowEntireView &&
                        <Button style={{margin:"5px 0px 0px 10px"}} variant="primary" onClick={() => setAppointments(loadMode.foreignUnpicked)}>Fremde</Button>
                    }
                    {allowEntireView &&
                        <Button style={{margin:"5px 0px 0px 10px"}} variant="primary" onClick={() => setAppointments(loadMode.all)}>Alle</Button>
                    }
                </div>
            </div>
            {(selectedUser.length == 1 || (appointmentsOf == loadMode.all && selectedUser.length == 0)) && 
                <Tabs
                    id="controlled-tab"
                    activekey={tabKey}
                    onSelect={key => setTabKey(key)}
                    defaultActiveKey={tabKey}
                >
                    <Tab eventKey="calendar" title="Kalender">
                    {calendarLoaded &&
                        <CalendarPage 
                            calendarStore={calendarStore} 
                            userStore={userStore}
                            UserID={selectedUser.length > 0 ? appointmentsOf == loadMode.own ? userStore.userID : selectedUser[0].id: null}
                            loadAppointments={loadCalendarAppointments}
                            handleExceptionChange={handleExceptionChange}
                            handlePreferredAppointmentChange={handlePreferredAppointmentChange}
                        />
                    }
                    </Tab>
                    <Tab eventKey="table" title="Tabelle">
                    <OverviewPage
                        pageTitle={"Termine"}
                        newItemText="Termin buchen"
                        tableHeader={["#", "Start", "Ende", "Kunde", "Prozedur", "Beschreibung", "Status"]}
                        tableBody={tableBody}
                        modal={modal}
                        data={tableAppointments}
                        modalSize="lg"
                        refreshData={() => loadAppointments(appointmentsOf)}
                        withoutCreate={true}
                        noTopMargin={true}
                    /> 
                    </Tab>
                </Tabs>
            }
        </React.Fragment>
   )
}