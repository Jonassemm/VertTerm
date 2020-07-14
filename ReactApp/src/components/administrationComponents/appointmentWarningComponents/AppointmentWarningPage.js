//author: Patrick Venturini
import React, {useState, useEffect} from 'react'
import OverviewPage, {modalTypes} from "../../OverviewPage"
import ObjectPicker from "../../ObjectPicker"
import {Form, Col, Modal, Button } from "react-bootstrap"
import {Link} from 'react-router-dom';
import AppointmentWarningForm from "./AppointmentWarningForm"
import {appointmentStatus} from "../../appointmentComponents/AppointmentStatus"
import {getTranslatedWarning, creatWarningList} from "../../Warnings"
import {ExceptionModal} from "../../ExceptionModal"

import {
    getAllAppointmentsWithWarnings, 
    getAppointmentWithWarning} from "../../requests"
import {
    deleteOverrideAppointment,
    getAppointmentsByID} from "../../appointmentComponents/AppointmentRequests";

var moment = require('moment'); 


export default function AppointmentWarningPage({userStore, warning}) {
    var initialSelectedWarning = []
    if(warning != undefined) {
        initialSelectedWarning = [warning]
    }
    const [selectedKindOfWarning, setSelectedKindOfWarning] = useState(initialSelectedWarning)
    const [appointmentsWithWarnings, setAppointmentsWithWarnings] = useState([])
    const [initialWarning, setInitialWarning] = useState(true)

    const [exception, setException] = useState(null)
    const [showExceptionModal, setShowExceptionModal] = useState(false)
    const [showPreferredAppointmentModal, setShowPreferredAppointmentModal] = useState(false)
    const [preferredAppointment, setPreferredAppointment] = useState(null)
    const [preferredAppointmentStarttime, setPreferredAppointmentStarttime] = useState(null)
    const [overrideDeleteId, setOverrideDeleteId] = useState(null)


    useEffect( () => {
        loadAppointmentsWithWarnings()
    },[selectedKindOfWarning])


    const handleSelectedKindOfWarningChange = (data) => {
        //set to "en" (english) translated warning
        if(data.length > 0) {
           setSelectedKindOfWarning([getTranslatedWarning(data[0])]) 
        }else {
          setSelectedKindOfWarning([])  
        }   
    }


    //-------------------------------ExceptionModal--------------------------------
    const handleExceptionChange = (newException, id) => {
        setException(newException)
        setOverrideDeleteId(id)
        setShowExceptionModal(true)
    }

    const handleOverrideDelete = async () => {
        try{
            await deleteOverrideAppointment(overrideDeleteId);
            userStore.setMessage("Termin erfolgreich gelöscht!")
        } catch (error){
            console.log(Object.keys(error), error.message)
        }
        setShowExceptionModal(false)
        loadAppointments(appointmentsOf)
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


    const loadAppointmentsWithWarnings = async () => {
        var response = []
        var data = []

        try {
            if(initialWarning == undefined || selectedKindOfWarning.length == 0) {
                response = await getAllAppointmentsWithWarnings()
            }else {
                response = await getAppointmentWithWarning(creatWarningList(selectedKindOfWarning))
            }
            data = response.data
            setInitialWarning(false)
        } catch (error) {
            console.log(Object.keys(error), error.message)
        }

        //don't save object with status="deleted"
        var reducedData = []
        data.map((singleAppointment) => {
            if(singleAppointment.status != appointmentStatus.deleted) {
                reducedData.push(singleAppointment)
            }
        })
        setAppointmentsWithWarnings(reducedData)
    }


    //------------------------------------Overview-Page-Components----------------------------------
    const tableBody = 
        appointmentsWithWarnings.map((item, index) => { 
            var warningString = ""
            var bookedCustomer = ""
            var bookedProcedure = ""
            if (item.warnings.length > 0){
                item.warnings.map((singleWarning, index) => {
                    if(index == 0){
                        warningString += getTranslatedWarning(singleWarning)
                    }else {
                        warningString += "; " + getTranslatedWarning(singleWarning)
                    }
                })
            }
            if(item.bookedCustomer != null){
                bookedCustomer = item.bookedCustomer.firstName + ", " + item.bookedCustomer.lastName
            }
            if(item.bookedProcedure != null){
                bookedProcedure = item.bookedProcedure.name
            }

            return ([
                index + 1,
                moment(moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString(),
                moment(moment(item.plannedEndtime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString(),
                bookedProcedure,
                bookedCustomer,
                warningString]
            )
        })
    
    
    const modal = (onCancel,edit,selectedItem) => {
        return (
            <AppointmentWarningForm
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
                            {preferredAppointment != null && preferredAppointmentStarttime != null &&
                            <Link to={`/buchung/${preferredAppointment.id}/${preferredAppointmentStarttime}`}>
                                <Button variant="success" style={{ marginLeft: "10px" }}>Terminumbuchung</Button>
                            </Link>
                            }
                        </div>
                    </Modal.Footer>
                </Modal>
            }
            <div style={{display: "flex",  justifyContent: "center"}}>
                <div style={{margin: "10px 10px 0px 0px", fontWeight: "bold"}}>Konflikte filtern nach: </div>
                <div style={{marginTop: "5px", width: "250px"}}>
                    <ObjectPicker 
                        setState={handleSelectedKindOfWarningChange}
                        DbObject="warning"
                        multiple={false}
                        initial={[getTranslatedWarning(selectedKindOfWarning[0])]} 
                    />
                </div>
            </div>
            <OverviewPage
                pageTitle={"Konfliktansicht"}
                newItemText=""
                tableHeader={["#", "Start", "Ende", "Prozedur", "Kunde", "Konflikte"]}
                tableBody={tableBody}
                modal={modal}
                data={appointmentsWithWarnings}
                refreshData={() => loadAppointmentsWithWarnings()}
                userStore={userStore}
                withoutCreate={true}
                modalSize="lg"
                noTopMargin={true}
                modalType={modalTypes.appointment}
            /> 
        </React.Fragment>
   )
}