import React, {useState, useEffect} from 'react'
import {Link} from 'react-router-dom';
import {Form, Col, Container, Tabs, Tab, Button, Modal} from "react-bootstrap"
import OverviewPage from "../OverviewPage"
import AppointmentForm from "./AppointmentForm"
import HomePage from "../calendarComponents/HomePage"
import ObjectPicker from "../ObjectPicker"

var moment = require('moment'); 

import {getAllAppointments, getAppointmentOfUser, getAppointmentOfUserInTimespace} from "./AppointmentRequests";

import {getUser} from "../administrationComponents/userComponents/UserRequests"

export const loadMode = {
    own: "own",
    foreignUnpicked: "foreignUnpicked",
    foreign: "foreign",
    all: "all"
}

export default function AppointmentPage({calendarStore, userStore}) {
    //Tabs
    const [tabKey, setTabKey] = useState('calendar')
    //Modal
    const [userSelectModal, setUserSelectModal] = useState(false)
    const [selectedUser, setSelectedUser] = useState([])
    const [selectedUserInitialized, setSelectedUserInitialized] = useState(false)
    const [loggedInUser, setLoggedInUser] = useState(null)
    //Data
    const [tableAppointments, setTableAppointments] = useState([])
    const [appointmentsOf, setAppointmentsOf] = useState(loadMode.own)


    const handleSelectedUserChange = (data) => {
        setSelectedUser(data)
        setAppointmentsOf(loadMode.foreign)
    }

    useEffect( () => {
        loadAppointments(appointmentsOf)
    },[appointmentsOf, tabKey])


    const setAppointments = (selection) => {
        setAppointmentsOf(selection)
        switch(selection){
            case loadMode.own:
                //loadLoggedInUser(loadMode.own)
                break;
            case loadMode.foreignUnpicked:
                //loadLoggedInUser(loadMode.foreign)
            case loadMode.all:
                setSelectedUser([]) // reset selected user
                break;
            default:
        }
        loadAppointments(selection)
    }

    //---------------------------------LOAD---------------------------------
    const loadAppointments = async (selection) => {
        var data = []
        var response = null
        var currentLoggedInUser

        //LOAD logged in user
        if(selection!= loadMode.all) { // when loading all users, loggedInUser is not necessary
            console.log("userID: " + userStore.userID)
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
                        //setAppointmentsOf(loadMode.own)
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
        }

        //LAOD Appointments for table
        console.log("LOAD Appointments:")
        console.log(selectedUser)
        if(tabKey == "table") {
            try {
                switch(selection){
                    case loadMode.all:
                        response = await getAllAppointments();  
                        break;
                    case loadMode.foreign:
                        console.log("CHECK:")
                        console.log(selectedUser.length > 0)
                        if(selectedUser.length > 0) {
                            response = await getAppointmentOfUser(selectedUser[0].id);  
                        }  
                        break;
                    case loadMode.own:
                        response = await getAppointmentOfUser(currentLoggedInUser.id);  
                        break;
                }
                
                //create and add title to each appointment
                if(response != null) {
                   data = response.data.map(item => {
                        var title = item.bookedProcedure.name + "-" + item.bookedCustomer.username
                        return {
                            ...item,
                            title: title
                        }
                    }) 
                }
                
            }catch (error) {
                console.log(Object.keys(error), error.message)
                //TEST-DATA
                const bookedProcedure = {name: "Prozess1"}
                const plannedStarttime= "04.06.2020 19:30"
                const bookedCustomer= {firstName: "Angelina", lastName: "Jolie", username: "LaraCroft"}
                const title = bookedProcedure.name + "-" + bookedCustomer.username

                data = [{id: "1", description: "Dies ist eine längerer Text um zu verdeutlichen wie lang eine Beschreibung einer Prozedur sein kann. Damit wir auch in die zweite Zeile gelangenen steht hier noch etwas mehr ;)",
                        status: "planned", warning: "", 
                        plannedStarttime: plannedStarttime, plannedEndtime: "04.06.2020 20:30", actualStarttime: null, actualEndtime: null, 
                        bookedProcedure: bookedProcedure, 
                        bookedCustomer: bookedCustomer,
                        bookedEmployees: [{firstName: "Bruce", lastName: "Willis"},{firstName: "Will", lastName: "Smith"}],
                        bookedResources: [{name: "Stift"}, {name: "Papier"}, {name: "Laptop"}],
                        title: title
                    }]
            }
            setTableAppointments(data)
        }
    }

    const tableBody = 
        tableAppointments.map((item, index) => { 
            var status //translated status
            switch(item.status) {
                case "active":
                    return "Laufend"
                break;
                case "planned":
                    status = "Ausstehend"
                break;
                case "done":
                    status = "Erledigt"
                break;
                case "created":
                    status = "Buchung ausstehend"
                break;
                case "cancelled":
                    status = "Storniert"
                break;
                case "deactivated":
                    status = "Deaktiviert"
                break;
                case "deleted":
                    status = "Gelöscht"
                break;
                default: status = "UNDIFINED"
            }
            return ([
                index + 1,
                moment(moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString(),
                moment(moment(item.plannedEndtime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString(),
                item.bookedProcedure.name,
                item.description,
                status]
            )
        })

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
                selectedUser={user}
            />
        )
    }

    const renderfkt = () => {
        console.log("---------Render-PAGE------")
    }

    return (
        <React.Fragment>
            {renderfkt()}
            <div style={{textAlign:"center", display: "flex",  justifyContent: "center"}}>
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
                    <Button style={{margin:"5px 0px 0px 10px"}} variant="success" onClick={() => loadAppointments(appointmentsOf)}>Aktualisieren</Button>
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
                            User={selectedUser.length > 0 ? selectedUser[0]: null}
                            appointmentsOf={appointmentsOf}/>
                        }
                        {(appointmentsOf == loadMode.foreign || appointmentsOf == loadMode.foreignUnpicked) &&
                        <HomePage 
                            calendarStore={calendarStore} 
                            User={selectedUser.length > 0 ? selectedUser[0]: null}
                            appointmentsOf={appointmentsOf}/>
                        }
                        {appointmentsOf == loadMode.own &&
                        <HomePage 
                            calendarStore={calendarStore} 
                            User={loggedInUser}
                            appointmentsOf={appointmentsOf}/>
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
                    /> 
                    </Tab>
                </Tabs>
            }
               
        </React.Fragment>
   )
}