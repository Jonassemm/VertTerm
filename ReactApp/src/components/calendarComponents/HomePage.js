'use strict'

import React, {useState, useEffect} from "react"
import { Calendar, momentLocalizer } from "react-big-calendar"
import moment from "moment"
import Modal from "react-bootstrap/Modal"
import CalendarForm from "./CalendarForm"
import AppointmentForm from "../appointmentComponents/AppointmentForm"
import { observer } from "mobx-react"
import {getAppointmentOfUserInTimespace, getAllAppointmentInTimespace} from "../appointmentComponents/AppointmentRequests"
import styled from "styled-components"
import "react-big-calendar/lib/css/react-big-calendar.css"
import {appointmentStatus} from "../appointmentComponents/AppointmentStatus"

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


function HomePage({
    calendarStore, 
    UserID, 
    loadAppointments = undefined,
    handleExceptionChange,              //pass to AppointmentForm
    handlePreferredAppointmentChange    //pass to AppointmentForm
    }){
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
        if(!initialized || loadDifferentMonth) {
            refreshCalendarAppointments()
        }
    },[referenceDateOfView])
    
    const refreshCalendarAppointments = () => {
        if(loadAppointments != undefined) {
            loadAppointments(referenceDateOfView.getMonth(), referenceDateOfView.getFullYear(), UserID)  
        }else
        {
            loadCalendarEvents()
        }
        setInitialized(true)
    }

    /* const handleLoadAppointments = () => {
        setInitialized(true)
        if(loadAppointments != undefined) {
            loadAppointments(referenceDateOfView.getMonth(), referenceDateOfView.getFullYear(), UserID)  
        }else
        {
            loadCalendarEvents()
        }
    } */

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
                response = await getAppointmentOfUserInTimespace(
                    UserID, 
                    startDateString, 
                    endDateString
                )
            } catch (error) {
                console.log(Object.keys(error), error.message)
            }
        }
        console.log("original data:")
        console.log(response.data)

         //don't save object with status="deleted"
         var reducedData = []
         response.data.map((singleAppointment) => {
             if(singleAppointment.status != appointmentStatus.deleted) {
                 reducedData.push(singleAppointment)
             }
         })

        //prepare response for calendar
        const evts = reducedData.map(item => {
            return {
                ...item,
                plannedStarttime: moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate(),
                plannedEndtime: moment(item.plannedEndtime, "DD.MM.yyyy HH:mm").toDate(),
                title: item.bookedProcedure.name + "() "
            }
        })
        calendarStore.setCalendarEvents(evts)
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
            <Modal size="lg" show={showEditModal} onHide={hideModal} backdrop={"static"}>
                <Modal.Header closeButton>
                    <Modal.Title>{calendarEvent.title}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <AppointmentForm
                        selected={calendarEvent}
                        onCancel={hideModal}
                        edit={true}
                        handleExceptionChange={handleExceptionChange}
                        handlePreferredAppointmentChange={handlePreferredAppointmentChange}
                        refreshData={refreshCalendarAppointments}
                        month={referenceDateOfView.getMonth()}
                        year={referenceDateOfView.getFullYear()}
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
                step={5}
                timeslots={3}
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
                      let currentStyle = {
                        backgroundColor: "#5384cf",
                        color: 'white',
                        borderRadius: "5px",
                        border: "#bbb 1px",
                        borderStyle: "solid"
                      };    
                
                      if(event.status == "done"){ //appointment is done
                        currentStyle.backgroundColor = "#17c250"
                      }else if(event.actualStarttime != null && event.actualEndtime == null) { //appointment was started but not completed 
                        currentStyle.backgroundColor = "#00b0b3"
                      }else if(event.warnings.length > 0){ //with warnings!!!
                        currentStyle.backgroundColor = "#d1342a"
                      }

                      return {
                        className: "",
                        style: currentStyle
                      };
                    }
                  }
            />
        </div> 
        </Style>
    )

}

export default observer(HomePage)