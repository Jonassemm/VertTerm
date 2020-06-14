'use strict'

import React, {useState, useEffect} from "react"
import { Calendar, momentLocalizer } from "react-big-calendar"
import moment from "moment"
import Modal from "react-bootstrap/Modal"
import CalendarForm from "./CalendarForm"
import AppointmentForm from "../appointmentComponents/AppointmentForm"
import { observer } from "mobx-react"
import { getCalendar } from "./calendarRequests"
import {getAppointmentOfUserInTimespace, getAppointmentOfUser, deleteAppointment} from "../appointmentComponents/AppointmentRequests"
import styled from "styled-components"
import Axios from "axios"
import "react-big-calendar/lib/css/react-big-calendar.css"

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
}
`

moment.locale('de'); 
const localizer = momentLocalizer(moment)
const views = {day: "day", week: "week", month: "month"};
function HomePage({ calendarStore, User} ){

    //const [showAddModal, setShowAddModal] = useState(false)
    const [showEditModal, setShowEditModal] = useState(false)
    const [calendarEvent, setCalendarEvent] = useState({})
    const [initialized, setInitialized] = useState(false)
    const [loading, setLoading] = useState(false) //for loading appointments (could take some time)
    const [currentTimeView, setCurrentTimeView] = useState(views.month) //inital view
    const [referenceDateOfView, setReferenceDateOfView] = useState(new Date) //actual date for view 
    const [newReferenceDateOfView, setNewReferenceDateOfView] = useState(new Date) //new picked date for view

    const handleCurrentTimeViewChange = (newView) =>{
        setCurrentTimeView(newView)
    }

    const handleNavigationChange = (newReferenceDate) => {
        setReferenceDateOfView(newReferenceDateOfView)
        setNewReferenceDateOfView(newReferenceDate)
    }

     const handleSelectEvent = (event, e) => {
        setCalendarEvent(event)
        //setShowAddModal(false)
        setShowEditModal(true)
    }

    useEffect(() => {
        const loadDifferentMonth = (
            newReferenceDateOfView.getFullYear() != referenceDateOfView.getFullYear() ||
            newReferenceDateOfView.getMonth() != referenceDateOfView.getMonth()
        )
        console.log()
        //let source = Axios.CancelToken.source()
        if(!initialized || loadDifferentMonth) {
            getCalendarEvents()
        }
    },[referenceDateOfView])


    const hideModals = () => {
        //setShowAddModal(false)
        setShowEditModal(false)
    }

    const getCalendarEvents = async () => { 
        setLoading(true)
        //ONLY FOR TESTING
        /* const plannedStarttime = moment("04.06.2020 19:30", "DD.MM.yyyy HH:mm").toDate();
        const plannedEndtime = moment("04.06.2020 20:30", "DD.MM.yyyy HH:mm").toDate();
        const bookedProcedure = {name: "Prozess1"}
        const bookedCustomer= {firstName: "Angelina", lastName: "Jolie", username: "LaraCroft"}
        const title = bookedProcedure.name + "-" + bookedCustomer.username

        const evts = [{id: "1", description: "Dies ist eine längerer Text um zu verdeutlichen wie lang eine Beschreibung einer Prozedur sein kann. Damit wir auch in die zweite Zeile gelangenen steht hier noch etwas mehr ;)",
                status: "planned", warning: "", 
                plannedStarttime: plannedStarttime, plannedEndtime: plannedEndtime, actualStarttime: null, actualEndtime: null, 
                bookedProcedure: bookedProcedure, 
                bookedCustomer: bookedCustomer,
                bookedEmployees: [{firstName: "Bruce", lastName: "Willis"},{firstName: "Will", lastName: "Smith"}],
                bookedResources: [{name: "Stift"}, {name: "Papier"}, {name: "Laptop"}],
                title: title
            }]
            calendarStore.setCalendarEvents(evts)
            setInitialized(true) */
        //END OF TESTING

        try {
            var startDate = new Date
            var endDate = new Date
            const month = referenceDateOfView.getMonth()
            const year =  referenceDateOfView.getFullYear()
            startDate.setMonth(month - 1)
            startDate.setFullYear(year)
            endDate.setMonth(month + 1)
            endDate.setFullYear(year)
            const response = await getAppointmentOfUserInTimespace(
                User.id, 
                moment(startDate).format("DD.MM.YYYY HH:mm").toString(), 
                moment(endDate).format("DD.MM.YYYY HH:mm").toString()
            )
            const evts = response.data.map(item => {
                return {
                    ...item
                }
            })
            calendarStore.setCalendarEvents(evts)
            setInitialized(true) 
        } catch (error) {
            if(Axios.isCancel(error)){
                console.log("caught cancel")
            }else{
                console.log(Object.keys(error), error.message)
            }
        }
        setLoading(false)
    }

    /* const handleSelect = (event, e) => {
        const {start, end} = event
        const data = {title: "", start, end, allDay: false}
        setShowAddModal(true)
        setShowEditModal(false)
        setCalendarEvent(data)
    } */

    const renderfkt = () => {
        console.log("------Render-CALENDAR------")
    }

    return (
        <Style>
        {renderfkt()}
        <div className="page">
            {loading ? <div className="loadview"><div>Loading</div></div> : null}
            {/* <Modal show={showAddModal} onHide={hideModals}>
                <Modal.Header closeButton>
                    <Modal.Title>Add Calendar Event</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <CalendarForm
                        calendarStore={calendarStore}
                        calendarEvent={calendarEvent}
                        onCancel={hideModals}
                        edit={false}
                    />
                </Modal.Body>
            </Modal> */}
            <Modal size="lg" show={showEditModal} onHide={hideModals}>
                <Modal.Header closeButton>
                    <Modal.Title>{calendarEvent.title}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <AppointmentForm
                        selected={calendarEvent}
                        onCancel={hideModals}
                        edit={true}
                    />
                </Modal.Body>
            </Modal>
            <Calendar
                localizer={localizer}
                events={calendarStore.calendarEvents}
                startAccessor="plannedStarttime"
                endAccessor="plannedEndtime"
                selectable={true}
                style={{height:"80vh"}}
                //onSelectSlot={handleSelect}
                onSelectEvent={handleSelectEvent}
                culture = 'ge'
                view={currentTimeView}
                onView={handleCurrentTimeViewChange}
                onNavigate={handleNavigationChange}
                messages={{
                    previous: 'Zurück',
                    next: 'Weiter',
                    year: 'Jahr',
                    month: 'Monat',
                    week: 'Woche',
                    day: 'Tag',
                    today: 'Heute',
                    date: 'Datum',
                    time: 'Zeit',
                    event: 'Event',
                    allDay: 'Ganztägig',
                    work_week: 'Arbeitswoche',
                    yesterday: 'Gestern',
                    tomorrow: 'Morgen',
                    agenda: 'Agenda'
                  }}
                  eventPropGetter={
                    (event, plannedStarttime, plannedEndtime, isSelected) => {
                      let newStyle = {
                        backgroundColor: "#0066f5",
                        color: 'white',
                        borderRadius: "0px",
                        border: "none"
                      };
                
                      if(event.warning == "warning"){
                        newStyle.backgroundColor = "red"
                      }else{
                        //newStyle.backgroundColor = "green"
                      }
                
                      return {
                        className: "",
                        style: newStyle
                      };
                    }
                  }
            />
        </div> 
        </Style>
    )

}

export default observer(HomePage)