import React, {useState, useEffect} from 'react'
import {Form, Col, Container, Tabs, Tab, Button, Modal} from "react-bootstrap"
import OverviewPage from "../OverviewPage"
import {Link} from 'react-router-dom';
import AppointmentForm from "./AppointmentForm"
import HomePage from "../calendarComponents/HomePage"
import ObjectPicker from "../ObjectPicker"
import {appointmentStatus, translateStatus} from "./AppointmentStatus"
import {ExceptionModal} from "../ExceptionModal"

var moment = require('moment'); 

import {
    getAllAppointments,
    getAppointmentsByID,
    getAppointmentOfUser, 
    getOwnAppointments, 
    getAllAppointmentInTimespace, 
    getAppointmentOfUserInTimespace,
    deleteOverrideAppointment
} from "./AppointmentRequests";

import {getUser} from "../administrationComponents/userComponents/UserRequests"

export const loadMode = {
    own: "own",
    foreignUnpicked: "foreignUnpicked",
    foreign: "foreign",
    all: "all"
}

export default function AppointmentPage({calendarStore, userStore}) {
    
    const [loading, setLoading] = useState(false) //for loading appointments (could take some time)
    //Tabs
    const [tabKey, setTabKey] = useState('calendar')
    //Modal
    const [selectedUser, setSelectedUser] = useState([])
    const [selectedUserInitialized, setSelectedUserInitialized] = useState(false)
    const [loggedInUser, setLoggedInUser] = useState(null)
    //Data
    const [tableAppointments, setTableAppointments] = useState([])
    const [appointmentsOf, setAppointmentsOf] = useState(loadMode.own)

    //exception needs overriding (ExceptionModal)
    const [exception, setException] = useState(null)
    const [showExceptionModal, setShowExceptionModal] = useState(false)
    
    //preferalbe appointment (preferredAppointmentModal)
    const [showPreferredAppointmentModal, setShowPreferredAppointmentModal] = useState(false)
    const [preferredAppointment, setPreferredAppointment] = useState(null)
    //const [preferredAppointmentId, setPreferredAppointmentId] = useState(null)
    const [preferredAppointmentStarttime, setPreferredAppointmentStarttime] = useState(null)

    const handleSelectedUserChange = (data) => {
        if(data.length > 0){
            loadAppointments(loadMode.foreign)
            setAppointmentsOf(loadMode.foreign)   
        }else {
            setAppointmentsOf(loadMode.foreignUnpicked)
        }
        setSelectedUser(data)
    }


    useEffect( () => {
        loadAppointments(appointmentsOf)
    },[appointmentsOf, tabKey])


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


    //-------------------------------ExceptionModal--------------------------------
    const handleExceptionChange = (newException) => {
        setException(newException)
        setShowExceptionModal(true)
    }

    const handleOverrideDelete = async () => {
        try{
            await deleteOverrideAppointment(selected.id);
        } catch (error){
            console.log(Object.keys(error), error.message)
        }
    }

    //-------------------------PreferredAppointmentModal----------------------------
    //set information of preferable appointment
    const handlePreferredAppointmentChange = (id, starttime) =>{
        //setPreferredAppointmentId(id)
        setPreferredAppointmentStarttime(starttime)
        //load information to this appointment
        loadPreferredAppointment(id)
    }

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

    //--------------------------LOAD-Appointments-------------------------
    const loadAppointments = async (selection) => {
        var data = []
        var response = null
        var currentLoggedInUser

        //LOAD logged in user
        if(selection!= loadMode.all) { // when loading all users, loggedInUser is not necessary
            try {
                const response = await getUser(userStore.userID);
                if(response.data != "") { //logged in user found? ("" means no user is logged in)
                    if(response.data.username != "admin" && response.data.username != "anonymousUser") {
                        //Initial LOAD
                        if(!selectedUserInitialized){
                            setLoggedInUser(response.data)
                            currentLoggedInUser = response.data
                            setSelectedUser([response.data])
                            setSelectedUserInitialized(true)
                        }
                        if(selection == loadMode.own){ //Appointments for logged in user
                            setSelectedUser([response.data]) 
                        }
                        setLoggedInUser(response.data)
                        currentLoggedInUser = response.data
                    } else {
                        alert("Admin und AnonymousUser haben keine eigenen Termine!")
                    }
                }else{
                    console.log("Bitte erst anmelden, diese Funktion steht normal nur angemeldeten Benutzer zur Verfügung!")
                }
            } catch (error) {
                console.log(Object.keys(error), error.message)
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
                        //response = await getOwnAppointments(currentLoggedInUser.id);  
                        response = await getOwnAppointments();
                        break;
                }

                //create and add title to each appointment
                if(response != null) {
                    data = response.data.map(singleAppointment => { 
                        var title = singleAppointment.bookedProcedure.name + "-" + singleAppointment.bookedCustomer.username
                        return {
                            ...singleAppointment,
                            title: title
                        }
                    }) 
                }
                console.log("original data:")
                console.log(data)

                //don't save object with status="deleted"
                data.map((singleAppointment) => {
                    if(singleAppointment.status != appointmentStatus.deleted) {
                        reducedData.push(singleAppointment)
                    }
                })
                
            }catch (error) {
                console.log(Object.keys(error), error.message)
            }
            setTableAppointments(reducedData)

        //LAOD Appointments for calendar  
        }else {
            var today = new Date
            if(selection == loadMode.own){ //own
                if(currentLoggedInUser != undefined) {
                    loadCalendarAppointments(today.getMonth(), today.getFullYear(), currentLoggedInUser.id)
                }
            }else if (selection == loadMode.foreign) { //foreign
                if(selectedUser.length > 0) {
                    loadCalendarAppointments(today.getMonth(), today.getFullYear(), selectedUser[0].id)
                }
            }else { //all
                loadCalendarAppointments(today.getMonth(), today.getFullYear(), null)
            }

        }
    }

    const loadCalendarAppointments = async (month, year, UserID) => { 
        setLoading(true)
        var response = []
        var startDate = new Date
        var endDate = new Date
        startDate.setMonth(month - 1)
        startDate.setFullYear(year)
        endDate.setMonth(month + 1)
        endDate.setFullYear(year)
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
        console.log("original data:")
        console.log(response.data)

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
            return {
                ...item,
                plannedStarttime: moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate(),
                plannedEndtime: moment(item.plannedEndtime, "DD.MM.yyyy HH:mm").toDate(),
                title: item.bookedProcedure.name
            }
        })
        calendarStore.setCalendarEvents(evts)
        setLoading(false)
    }

    //--------------------------Overview-Components-------------------------
    var tableBody = []
    if(tableAppointments.length > 0) {
        tableBody = tableAppointments.map((item, index) => { 
            var status = translateStatus(item.status)
            return ([
                index + 1,
                moment(moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString(),
                moment(moment(item.plannedEndtime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString(),
                item.bookedProcedure.name,
                item.description,
                status]
            )
        })
    }

    const modal = (onCancel,edit,selectedItem) => {
        var user = {}
        if(selectedUser.length == 1){
            user = selectedUser[0]
        }
        return (
            <AppointmentForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
                handleExceptionChange={handleExceptionChange}
                handlePreferredAppointmentChange={handlePreferredAppointmentChange}
            />
        )
    }

    const renderfkt = () => {
        console.log("---------Render-PAGE------")
    }


    return (
        <React.Fragment>
            {renderfkt()}
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
                                    value={preferredAppointment.bookedCustomer.firstName + ", " + preferredAppointment.bookedCustomer.lastName } 
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
                        <Link to={`/buchung/${preferredAppointment.id}/${preferredAppointmentStarttime}`}>
                            <Button variant="success" style={{ marginLeft: "10px" }}>Terminumbuchung</Button>
                        </Link>
                    </div>
                </Modal.Footer>
            </Modal>
            }
            
            <div style={{display: "flex",  justifyContent: "center"}}>
                <div style={{margin: "10px 10px 0px 0px", fontWeight: "bold"}}>Ansicht von: </div>
                <div style={{marginTop: "5px", width: "250px"}}>
                    {appointmentsOf == loadMode.own ? 
                        selectedUser.length > 0 &&
                        <Form.Control
                            readOnly
                            type="text"
                            value={selectedUser[0].firstName + " " + selectedUser[0].lastName || ""}
                        />: (appointmentsOf == loadMode.foreign || appointmentsOf == loadMode.foreignUnpicked) ?
                            loggedInUser != null ?
                            <ObjectPicker 
                                setState={handleSelectedUserChange}
                                DbObject="user"
                                initial={selectedUser} 
                                multiple={false}
                                selectedItem={loggedInUser}
                            />:
                        null:
                        <Form.Control
                            readOnly
                            type="text"
                            value={"Allen Benutzern"}
                        />
                    }
                </div> 
                <div>
                    <Button style={{margin:"5px 0px 0px 10px"}} variant="primary" onClick={() => setAppointments(loadMode.own)}>Eigene</Button>
                    <Button style={{margin:"5px 0px 0px 10px"}} variant="primary" onClick={() => setAppointments(loadMode.foreignUnpicked)}>Fremde</Button>
                    <Button style={{margin:"5px 0px 0px 10px"}} variant="primary" onClick={() => setAppointments(loadMode.all)}>Alle</Button>
                    {/* <Button style={{margin:"5px 0px 0px 10px"}} variant="success" onClick={() => loadAppointments(appointmentsOf)}>Aktualisieren</Button> */}
                </div>
            </div>
            {(selectedUser.length == 1 || appointmentsOf == loadMode.all) &&
                <Tabs
                    id="controlled-tab"
                    activekey={tabKey}
                    onSelect={key => setTabKey(key)}
                    defaultActiveKey={tabKey}
                >
                    <Tab eventKey="calendar" title="Kalender">
                        {appointmentsOf == loadMode.all &&
                        <HomePage 
                            calendarStore={calendarStore} 
                            UserID={selectedUser.length > 0 ? selectedUser[0].id: null}
                            loadAppointments={loadCalendarAppointments}
                            handleExceptionChange={handleExceptionChange}
                            handlePreferredAppointmentChange={handlePreferredAppointmentChange}
                            />
                        }
                        {(appointmentsOf == loadMode.foreign || appointmentsOf == loadMode.foreignUnpicked) &&
                        <HomePage 
                            calendarStore={calendarStore} 
                            UserID={selectedUser.length > 0 ? selectedUser[0].id: null}
                            loadAppointments={loadCalendarAppointments}
                            handleExceptionChange={handleExceptionChange}
                            handlePreferredAppointmentChange={handlePreferredAppointmentChange}/>
                        }
                        {appointmentsOf == loadMode.own &&
                        <HomePage 
                            calendarStore={calendarStore} 
                            UserID={loggedInUser.id}
                            loadAppointments={loadCalendarAppointments}
                            handleExceptionChange={handleExceptionChange}
                            handlePreferredAppointmentChange={handlePreferredAppointmentChange}/>
                        }
                    </Tab>
                    <Tab eventKey="table" title="Tabelle">
                    <OverviewPage
                        pageTitle={"Termine"}
                        newItemText="Termin buchen"
                        tableHeader={["#", "Start", "Ende", "Prozedur", "Beschreibung", "Status"]}
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