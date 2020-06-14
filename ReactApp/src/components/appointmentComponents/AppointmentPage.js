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

export default function AppointmentPage({calendarStore, userStore}) {
    
    //Tabs
    const [tabKey, setTabKey] = useState('calendar')
    //Modal
    const [userSelectModal, setUserSelectModal] = useState(false)
    const [selectedUser, setSelectedUser] = useState([])
    //Data
    const [tableAppointments, setTableAppointments] = useState([])
    const [tableInitialized, setTableInitialized] = useState(false)
    const [calendarAppointments, setCalendarAppointments] = useState([])
    const [calendarInitialized, setCalendarInitialized] = useState(false)

    const [ownAppointments, setOwnAppointments] = useState(true)
    const [enableUserLoad, setEnableUserLoad] = useState(true)

    const setForeignAppointments = () => {
        setOwnAppointments(false)
        setSelectedUser([]) // reset selected user
        loadAppointments()
    }

    useEffect( () => {
        if(enableUserLoad) {
           loadLoggedInUser() 
        }
        loadAppointments()
    },[selectedUser, tabKey])

    /* const hideModal = () => {
        setUserSelectModal(false)
    } */

    const loadLoggedInUser = async () => {
        const response = await getUser(userStore.userID);
        if(response.data != "") { //logged in user found? ("" means no user is logged in)
            if(response.data.username != "admin" && response.data.username != "anonymousUser") {
                setOwnAppointments(true)
                setSelectedUser([response.data])
            } else {
                alert("Admin und AnonymousUser haben keine eigenen Termine!")
            }
        }else{
            console.log("Bitte erst anmelden, diese Funktion steht normal nur angemeldeten Benutzer zur Verfügung!")
        }
        setEnableUserLoad(false)
    }

    //---------------------------------LOAD---------------------------------
    const loadAppointments = async () => {
        var data = []
        var response 
        try {
            if(tabKey == "table") {
                response = await getAppointmentOfUser(selectedUser[0].id);
            }else {
                var start = new Date
                var end = new Date
                start.setMonth(start.getMonth()-1)
                end.setMonth(end.getMonth()+1)
                response = await getAppointmentOfUserInTimespace(
                    selectedUser[0].id,
                    moment(start).format("DD.MM.YYYY HH:mm").toString(),
                    moment(end).format("DD.MM.YYYY HH:mm").toString());
            }
            //create and add title to each appointment
            data = response.data.map(item => {
                var title = item.bookedProcedure.name + "-" + item.bookedCustomer.username
                return {
                    ...item,
                    title: title
                }
            })
        }catch (error) {
            console.log(Object.keys(error), error.message)

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
        if(tabKey == "table") {
            setTableAppointments(data)
        } else {
            setCalendarAppointments(data)
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
            {/* <Modal size={"lg"} show={userSelectModal} onHide={hideModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Terminansicht wechseln</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Row>
                            <Form.Group as={Col} md="12" >
                                <Form.Label>Termine des Benutzers: </Form.Label>
                                <ObjectPicker 
                                    setState={setSelectedUser}
                                    DbObject="user"
                                    initial={selectedUser} 
                                    multiple={false}
                                />
                            </Form.Group>
                        </Form.Row>
                        <hr style={{ border: "0,5px solid #999999" }}/>
                        <Container style={{textAlign: "right"}}>
                        <Link to='/'>
                            <Button style={{ marginRight: "10px" }} variant="secondary">Abbrechen</Button>
                        </Link> 
                        <Button variant="success" onClick={loadAppointments}>Zur Ansicht</Button>
                        </Container>
                    </Form>
                </Modal.Body>
            </Modal> */}
                <div style={{textAlign:"center", display: "flex",  justifyContent: "center"}}>
                    <div style={{margin: "10px 10px 0px 0px", fontWeight: "bold"}}>Ansicht von: </div>
                    <div style={{marginTop: "5px", width: "250px"}}>
                        {ownAppointments ? 
                            selectedUser.length > 0 &&
                            <Form.Control
                            readOnly
                            type="text"
                            value={selectedUser[0].firstName + " " + selectedUser[0].lastName || ""}
                            />:
                            <ObjectPicker 
                            setState={setSelectedUser}
                            DbObject="user"
                            initial={selectedUser} 
                            multiple={false}
                            />
                        }
                    </div> 
                    <div>
                    {ownAppointments ?
                        <Button style={{margin:"5px 0px 0px 10px"}} variant="info" onClick={setForeignAppointments}>Fremde Termine</Button>:
                        <Button style={{margin:"5px 0px 0px 10px"}} variant="info" onClick={loadLoggedInUser}>Eigene Termine</Button>
                    }
                           
                    </div>
                    {/* <Button style={{marginTop:"5px"}} variant="info" onClick={e => setUserSelectModal(true)}>Terminansicht wechseln</Button> */}
                </div>
            {(selectedUser.length == 1) &&
                <Tabs
                    id="controlled-tab"
                    activekey={tabKey}
                    onSelect={key => setTabKey(key)}
                >
                    <Tab eventKey="calendar" title="Kalender">
                        <HomePage calendarStore={calendarStore} User={selectedUser[0]}/>
                    </Tab>
                    <Tab eventKey="table" title="Tabelle">
                    <OverviewPage
                        //pageTitle={selectedUser.length > 0 ? "Termine - (" + selectedUser[0].labelKey + ")": "Termine"}
                        pageTitle={"Termine"}
                        newItemText="Termin buchen"
                        tableHeader={["#", "Start", "Ende", "Prozedur", "Beschreibung", "Status"]}
                        tableBody={tableBody}
                        modal={modal}
                        data={calendarAppointments}
                        modalSize="lg"
                        refreshData={loadAppointments}
                        withoutCreate={true}
                    /> 
                    </Tab>
                </Tabs>
            }
               
        </React.Fragment>
   )
}