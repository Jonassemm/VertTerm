import React, {useState, useEffect} from 'react'
import {Link} from 'react-router-dom';
import {Table, Form, Col, Row, Container, Tabs, Tab, Button, Modal} from "react-bootstrap"
import OverviewPage from "../../OverviewPage"
import ObjectPicker from "../../ObjectPicker"
import AppointmentWarningForm from "./AppointmentWarningForm"


var moment = require('moment'); 

import {getAppointmentsWithWarning} from "./AppointmentWarningRequests";

export default function AppointmentWarningPage() {

    const kindOfWarning = {
        appointmenttime: "AppointmenttimeWarning",
	    restriction: "RestrictionWarning",
        procedureRelation: "ProcedureRelationWarning",
        availability: "AvailabilityWarning",
	    resourceType: "ResourceTypeWarning",
	    position: "Position_Warning",
	    resource: "ResourceWarning",
	    employee: "EmplyeeWarning",
	    procedure: "ProcedureWarning",
	    user: "UserWarning",
	    appointment: "AppointmentWarning"
    }

    const translatedWarning = {
        appointmenttime: "Terminzeit-Warnung",
	    restriction: "Einschränkungs-Warnung",
        procedureRelation: "ProcedureRelationWarning",
        availability: "AvailabilityWarning",
	    resourceType: "ResourceTypeWarning",
	    position: "Position_Warning",
	    resource: "ResourceWarning",
	    employee: "EmplyeeWarning",
	    procedure: "ProcedureWarning",
	    user: "UserWarning",
	    appointment: "AppointmentWarning"
    }

    const [slectedKindOfWarning, setSelectedKindOfWarning] = useState(null)
    const [appointmentsWithWarnings, setAppointmentsWithWarnings] = useState([])
    const [selectedAppointment, setSelectedAppointment] = useState(null)

    const handleSelectedKindOfWarningChange = (data) => {
        setSelectedKindOfWarning(data)
    }

    useEffect( () => {
        loadAppointmentsWithWarnings()
    },[])


    const loadAppointmentsWithWarnings = async () => {
        var response = []
        try {
            response = await XgetAppointmentsWithWarning(selectedKindOfWarning)
        } catch (error) {
            console.log(Object.keys(error), error.message)
            const bookedProcedure = {name: "Prozess1"}
            const plannedStarttime= "04.06.2020 19:30"
            const bookedCustomer= {firstName: "Angelina", lastName: "Jolie", username: "LaraCroft"}

            response = [{id: "1", description: "Dies ist eine längerer Text um zu verdeutlichen wie lang eine Beschreibung einer Prozedur sein kann. Damit wir auch in die zweite Zeile gelangenen steht hier noch etwas mehr ;)",
                    status: "planned", warning: "", 
                    plannedStarttime: plannedStarttime, plannedEndtime: "04.06.2020 20:30", actualStarttime: null, actualEndtime: null, 
                    bookedProcedure: bookedProcedure, 
                    bookedCustomer: bookedCustomer,
                    bookedEmployees: [{firstName: "Bruce", lastName: "Willis"},{firstName: "Will", lastName: "Smith"}],
                    bookedResources: [{name: "Stift"}, {name: "Papier"}, {name: "Laptop"}],
                    warnings: ["AppointmenttimeWarning", "ProcedureRelationWarning"]
                }]
        }
        setAppointmentsWithWarnings(response)
    }

/*     const handleSelectAppointment = () => {
        let x = (event.target.parentElement.firstChild.textContent) - 1
        setSelectedAppointment(appointmentsWithWarnings[x])
    } */

    const tableBody = 
    appointmentsWithWarnings.map((item, index) => { 
        var warningString = "" //translated status
        if (item.warnings.length > 0){
            item.warnings.map((singleWarning, index) => {
            if(index == 0){
                warningString += singleWarning
            }else {
                warningString += "; " + singleWarning
            }
            })
        }
        return ([
            index + 1,
            moment(moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString(),
            moment(moment(item.plannedEndtime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString(),
            item.bookedProcedure.name,
            item.bookedCustomer.name,
            warningString]
        )
    })


    const modal = (onCancel,edit,selectedItem) => {
        return (
            <AppointmentWarningForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
            />
        )
    }


    const renderfkt = () => {
        console.log("---------Render-PAGE------")
    }


    return (
        <React.Fragment>
            {renderfkt()}
            <div style={{display: "flex",  justifyContent: "center"}}>
                <div style={{margin: "10px 10px 0px 0px", fontWeight: "bold"}}>Konflikte filtern nach: </div>
                <div style={{marginTop: "5px", width: "250px"}}>
                    <ObjectPicker 
                        setState={handleSelectedKindOfWarningChange}
                        DbObject="warnings"
                        multiple={false}
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
                withoutCreate={true}
                noTopMargin={true}
            /> 
        </React.Fragment>
   )
}