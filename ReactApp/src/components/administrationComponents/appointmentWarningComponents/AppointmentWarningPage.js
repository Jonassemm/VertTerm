import React, {useState, useEffect} from 'react'
import {Link} from 'react-router-dom';
import {Table, Form, Col, Row, Container, Tabs, Tab, Button, Modal} from "react-bootstrap"
import OverviewPage from "../../OverviewPage"
import ObjectPicker from "../../ObjectPicker"
import AppointmentWarningForm from "./AppointmentWarningForm"
import {appointmentStatus} from "../../appointmentComponents/AppointmentStatus"
import {getTranslatedWarning, creatWarningList} from "../../Warnings"

var moment = require('moment'); 

import {getAppointmentsWithWarning, getAllAppointmentsWithWarning} from "./AppointmentWarningRequests";

export default function AppointmentWarningPage(props) {

    var initialSelectedWarning = []
    if(Object.keys(props).length != 0) {
        initialSelectedWarning = [props.match.params.initialWarning]
    }

    const [selectedKindOfWarning, setSelectedKindOfWarning] = useState(initialSelectedWarning)
    const [appointmentsWithWarnings, setAppointmentsWithWarnings] = useState([])
    
    const [initialWarning, setInitialWarning] = useState(true)

    const handleSelectedKindOfWarningChange = (data) => {
        //set to "en" translated warning
        if(data.length > 0) {
           setSelectedKindOfWarning([getTranslatedWarning(data[0])]) 
        }else {
          setSelectedKindOfWarning([])  
        }   
    }

    useEffect( () => {
        loadAppointmentsWithWarnings()
    },[selectedKindOfWarning])


    const loadAppointmentsWithWarnings = async () => {
        var response = []
        var data = []

        try {
            if((Object.keys(props).length == 0 && initialWarning) || selectedKindOfWarning.length == 0) {
                console.log("CALL -> Initial (all)")
                response = await getAllAppointmentsWithWarning()
            }else {
                console.log("CALL -> Selected")
                console.log(selectedKindOfWarning)
                response = await getAppointmentsWithWarning(creatWarningList(selectedKindOfWarning))
            }
            data = response.data
            setInitialWarning(false)
        } catch (error) {
            console.log(Object.keys(error), error.message)
            /* const bookedProcedure = {name: "Prozess1"}
            const plannedStarttime= "04.06.2020 19:30"
            const bookedCustomer= {firstName: "Angelina", lastName: "Jolie", username: "LaraCroft"}

            data = [{id: "1", description: "Dies ist eine längerer Text um zu verdeutlichen wie lang eine Beschreibung einer Prozedur sein kann. Damit wir auch in die zweite Zeile gelangenen steht hier noch etwas mehr ;)",
                    status: "planned", warning: "", 
                    plannedStarttime: plannedStarttime, plannedEndtime: "04.06.2020 20:30", actualStarttime: null, actualEndtime: null, 
                    bookedProcedure: bookedProcedure, 
                    bookedCustomer: bookedCustomer,
                    bookedEmployees: [{firstName: "Bruce", lastName: "Willis"},{firstName: "Will", lastName: "Smith"}],
                    bookedResources: [{name: "Stift"}, {name: "Papier"}, {name: "Laptop"}],
                    warnings: ["AppointmenttimeWarning", "ProcedureRelationWarning"]
                }] */
        }
        console.log("original data:")
        console.log(data)

        //don't save object with status="deleted"
        var reducedData = []
        data.map((singleAppointment) => {
            if(singleAppointment.status != appointmentStatus.deleted) {
                reducedData.push(singleAppointment)
            }
        })
        setAppointmentsWithWarnings(reducedData)
    }


    const tableBody = 
        appointmentsWithWarnings.map((item, index) => { 
            var warningString = ""
            if (item.warnings.length > 0){
                item.warnings.map((singleWarning, index) => {
                    if(index == 0){
                        warningString += getTranslatedWarning(singleWarning)
                    }else {
                        warningString += "; " + getTranslatedWarning(singleWarning)
                    }
                })
            }
            return ([
                index + 1,
                moment(moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString(),
                moment(moment(item.plannedEndtime, "DD.MM.yyyy HH:mm").toDate()).format("DD.MM.YYYY / HH:mm").toString(),
                item.bookedProcedure.name,
                item.bookedCustomer.firstName + ", " + item.bookedCustomer.lastName,
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
                withoutCreate={true}
                modalSize="lg"
                noTopMargin={true}
            /> 
        </React.Fragment>
   )
}