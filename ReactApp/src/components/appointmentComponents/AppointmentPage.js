import React, {useState, useEffect} from 'react'
import {Link} from 'react-router-dom';
import {Form, Col, Container, Tabs, Tab, Button, Modal} from "react-bootstrap"
import OverviewPage from "../OverviewPage"
import AppointmentForm from "./AppointmentForm"
import HomePage from "../calendarComponents/HomePage"
import ObjectPicker from "../ObjectPicker"

var moment = require('moment'); 

import {
    getAllAppointments
  } from "./AppointmentRequests";


export default function AppointmentPage({calendarStore, userSelect}) {
    //Tabs
    const [tabKey, setTabKey] = useState('table')
    //Modal
    const [userSelectModal, setUserSelectModal] = useState(userSelect)
    const [selectedUser, setSelectedUser] = useState([])
    const [showContent, setShowContent] = useState(false)
    //Data
    const [appointments, setAppointments] = useState([])

    useEffect( () => {
        loadAppointments()
    },[])

    const hideModal = () => {
        setUserSelectModal(false)
    }

    const loadOtherAppointments = async event => {
        if(selectedUser.length == 1){
            setShowContent(true)
        }else
        {
            alert("Es konnte kein Benutzer zur Ansicht der Termine ausgemacht werden")
        }
        hideModal()
        //load the appointments of the specific user
    }

    //---------------------------------LOAD---------------------------------
    const loadAppointments = async () => {
        var data = []
        try {
            const response = await getAllAppointments();
            data = response.data.map(item => {
                var title = item.bookedProcedure.name + "-[" + item.plannedStarttime + "]: " + item.bookedCustomer.username
                return {
                    ...item,
                    title: title
                }
            })
        }catch (error) {
            console.log(Object.keys(error), error.message)
        }
        const bookedProcedure = {name: "Prozess1"}
        const plannedStarttime= "04.06.2020 19:30"
        const bookedCustomer= {firstName: "Angelina", lastName: "Jolie", username: "LaraCroft"}
        const title = bookedProcedure.name + "-[" + plannedStarttime + "]: " + bookedCustomer.username

        data = [{id: "1", description: "Dies ist eine längerer Text um zu verdeutlichen wie lang eine Beschreibung einer Prozedur sein kann. Damit wir auch in die zweite Zeile gelangenen steht hier noch etwas mehr ;)",
                    status: "created", warning: "", 
                    plannedStarttime: plannedStarttime, plannedEndtime: "04.06.2020 20:30", actualStarttime: null, actualEndtime: null, 
                    bookedProcedure: bookedProcedure, 
                    bookedCustomer: bookedCustomer,
                    bookedEmployees: [{firstName: "Bruce", lastName: "Willis"},{firstName: "Will", lastName: "Smith"}],
                    bookedResources: [{name: "Stift"}, {name: "Papier"}, {name: "Laptop"}],
                    title: title
                }]
        setAppointments(data)
    }

    const tableBody = 
        appointments.map((item, index) => { 
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

    return (
        <React.Fragment>
            <Modal size={"lg"} show={userSelectModal} onHide={hideModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Kalenderauswahl</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Row>
                            <Form.Group as={Col} md="12" >
                                <Form.Label>Kalender des Benutzers: </Form.Label>
                                <ObjectPicker 
                                    setState={setSelectedUser}
                                    DbObject="allowedAppointmentsOfUser"
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
                        <Button variant="success" onClick={loadOtherAppointments}>Zur Ansicht</Button>
                        </Container>
                    </Form>
                </Modal.Body>
            </Modal>
            {(showContent || !userSelect) && //view content if Modal returns successfull or own appointments is selected
                <Tabs
                    id="controlled-tab"
                    activekey={tabKey}
                    onSelect={key => setTabKey(key)}
                >
                    <Tab eventKey="table" title="Tabelle">
                    <OverviewPage
                        pageTitle={userSelect ? "Terminansicht - (" + selectedUser[0].labelKey + ")": "Terminansicht"}
                        newItemText="Termin buchen"
                        tableHeader={["#", "Start", "Ende", "Prozedur", "Beschreibung", "Status"]}
                        tableBody={tableBody}
                        modal={modal}
                        data={appointments}
                        modalSize="lg"
                        refreshData={loadAppointments}
                    /> 
                    </Tab>
                    <Tab eventKey="calendar" title="Kalender">
                        <HomePage calendarStore={calendarStore}/>
                    </Tab>
                </Tabs>
             }
        </React.Fragment>
   )
}