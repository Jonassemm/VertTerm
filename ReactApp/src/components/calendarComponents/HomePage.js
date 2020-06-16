'use strict'

import React, {useState, useEffect} from "react"
import { Calendar, momentLocalizer } from "react-big-calendar"
import moment from "moment"
import Modal from "react-bootstrap/Modal"
import CalendarForm from "./CalendarForm"
import AppointmentForm from "../appointmentComponents/AppointmentForm"
import { observer } from "mobx-react"
import { getCalendar } from "./calendarRequests"
import {getAppointmentOfUserInTimespace, getAllAppointmentInTimespace} from "../appointmentComponents/AppointmentRequests"
import styled from "styled-components"
import Axios from "axios"
import "react-big-calendar/lib/css/react-big-calendar.css"
import {loadMode} from "../appointmentComponents/AppointmentPage"

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


function HomePage({calendarStore, User} ){

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
        setShowEditModal(true)
    }

    const hideModal = () => {
            setShowEditModal(false)
        }


    useEffect(() => {
        const loadDifferentMonth = (
            newReferenceDateOfView.getFullYear() != referenceDateOfView.getFullYear() ||
            newReferenceDateOfView.getMonth() != referenceDateOfView.getMonth()
        )
        console.log()
        if(!initialized || loadDifferentMonth) {
            loadCalendarEvents()
        }
    },[referenceDateOfView])
    

    const loadCalendarEvents = async () => { 
        setLoading(true)
        var response = []
        var startDate = new Date
        var endDate = new Date
        const month = referenceDateOfView.getMonth()
        const year =  referenceDateOfView.getFullYear()
        startDate.setMonth(month - 1)
        startDate.setFullYear(year)
        endDate.setMonth(month + 1)
        endDate.setFullYear(year)
        const startDateString = moment(startDate).format("DD.MM.YYYY HH:mm").toString();
        const endDateString =  moment(endDate).format("DD.MM.YYYY HH:mm").toString();
        if(User == null){//case all appointments
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
                if(User != null) {
                    response = await getAppointmentOfUserInTimespace(
                        User.id, 
                        startDateString, 
                        endDateString
                    )
                } 
            } catch (error) {
                console.log(Object.keys(error), error.message)
            }
        }
        //prepare response for calendar
        const evts = response.data.map(item => {
            return {
                ...item,
                plannedStarttime: moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate(),
                plannedEndtime: moment(item.plannedEndtime, "DD.MM.yyyy HH:mm").toDate(),
                title: item.bookedProcedure.name
            }
        })
        calendarStore.setCalendarEvents(evts)
        setInitialized(true)
        setLoading(false)
    }

    const renderfkt = () => {
        console.log("------Render-CALENDAR------")
    }

    return (
        <Style>
        {renderfkt()}
        <div className="page">
            {loading ? <div className="loadview"><div>Loading</div></div> : null}
            <Modal size="lg" show={showEditModal} onHide={hideModal}>
                <Modal.Header closeButton>
                    <Modal.Title>{calendarEvent.title}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <AppointmentForm
                        selected={calendarEvent}
                        onCancel={hideModal}
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
                        backgroundColor: "#5384cf",
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