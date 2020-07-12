//author: Patrick Venturini, Jonas Semmler
import React, {useState, useEffect} from "react"
import { Calendar, momentLocalizer } from "react-big-calendar"
import moment from "moment"
import Modal from "react-bootstrap/Modal"
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


function CalendarPage({
    calendarStore, 
    userStore,
    UserID, 
    loadAppointments = undefined,       //optional extern loading of appointments
    handleExceptionChange,              //pass to AppointmentForm
    handlePreferredAppointmentChange    //pass to AppointmentForm
    }){
    const [showEditModal, setShowEditModal] = useState(false)
    const [calendarEvent, setCalendarEvent] = useState({})
    const [initialized, setInitialized] = useState(false)
    const [loading, setLoading] = useState(false) //for loading appointments (could take some time)
    const [currentTimeView, setCurrentTimeView] = useState(views.month) //inital view
    const [referenceDateOfView, setReferenceDateOfView] = useState(new Date) //actual date for view 
    const [earliestAppointmentDate, setEarliestAppointmentDate] = useState(null)


    useEffect(() => {
        if((!initialized && loadAppointments == undefined)) {
            refreshCalendarAppointments()
        }
    },[referenceDateOfView])


    const handleCurrentTimeViewChange = (newView) =>{
        setCurrentTimeView(newView)

        //set earliestAppointmentDate
        if(newView == views.day){
            setEarliestAppointmentDate(getEarliestDateOfDay(referenceDateOfView, null))
        }else if(newView == views.week){
            setEarliestAppointmentDate(getEarliestDateOfWeek(referenceDateOfView, null))
        }
    }


    const handleNavigationChange = (newReferenceDate) => {
        setReferenceDateOfView(newReferenceDate)
        
        //set earliestAppointmentDate
        if(currentTimeView == views.day){
            setEarliestAppointmentDate(getEarliestDateOfDay(newReferenceDate, null))
        }else if(currentTimeView == views.week){
            setEarliestAppointmentDate(getEarliestDateOfWeek(newReferenceDate, null))
        }
        
        if(loadAppointments != undefined) {
            loadAppointments(newReferenceDate, UserID)  
        }else
        {
            loadCalendarEvents()
        }
    }


    const handleSelectEvent = (event, e) => {
        setCalendarEvent(event)
        setShowEditModal(true)
    }


    //--------------------------------------LOAD-------------------------------------
    const loadCalendarEvents = async () => { 
        setLoading(true)
        const numberOfMonthsBefore = 1
        const numberOfMontsAfter = 1
        const numberOfDaysBefore = 6
        const numberOfDaysAfter = 6
        var response = []
        var startDate = new Date
        var endDate = new Date
        //28 is the min of days a month can have
        startDate.setDate(28 - numberOfDaysBefore)
        startDate.setMonth(referenceDateOfView.getMonth() - numberOfMonthsBefore)
        startDate.setFullYear(referenceDateOfView.getFullYear())
        endDate.setDate(numberOfDaysAfter)
        endDate.setMonth(referenceDateOfView.getMonth() + numberOfMontsAfter)
        endDate.setFullYear(referenceDateOfView.getFullYear())
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
        

        //don't save object with status="deleted"
        var reducedData = []
        response.data.map((singleAppointment) => {
            if(singleAppointment.status != appointmentStatus.deleted) {
                reducedData.push(singleAppointment)
            }
        })


        //prepare response for calendar
        const evts = reducedData.map(item => {
            var title
            if(item.bookedCustomer.firstName != null && item.bookedCustomer.lastName != null){
                title = item.bookedProcedure.name + " (" + item.bookedCustomer.username + ")"
            }else {
                title = item.bookedProcedure.name + " (anonym)"
            }
            return {
                ...item,
                plannedStarttime: moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate(),
                plannedEndtime: moment(item.plannedEndtime, "DD.MM.yyyy HH:mm").toDate(),
                title: title
            }
        })
        calendarStore.setCalendarEvents(evts)
        setLoading(false)
    }


    const refreshCalendarAppointments = () => {
        if(loadAppointments != undefined) {
            loadAppointments(referenceDateOfView, UserID)  
            
        }else
        {
            loadCalendarEvents()
        }
        setInitialized(true)
    }


    //-------------------------------Help-Funcitons-----------------------------------
    const hideModal = () => {
        setShowEditModal(false)
    }


    const getEarliestDateOfDay = (referenceDate, earliestDate) => {
        var earliestTime = null 
        var newTime = null
        if(calendarStore.calendarEvents.length > 0 ){
            calendarStore.calendarEvents.map(item => {
            if(earliestDate == null){//initial early date
                if(moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate().getDate() == referenceDate.getDate()){
                    earliestDate = moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate()
                }
            }else{//new early date
                earliestTime = earliestDate.getHours() * 60 + earliestDate.getMinutes()
                newTime = (moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate().getHours() * 60) + moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate().getMinutes()
                if(newTime < earliestTime){
                    if(moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate().getDate() == referenceDate.getDate()){
                        earliestDate = moment(item.plannedStarttime, "DD.MM.yyyy HH:mm").toDate()
                    }
                }
            }
            })
        }
        return earliestDate
    }


    const getEarliestDateOfWeek = (referenceDate, earliestDate) =>{
        var referenceWeekDay = null
        var newDate = new Date
        newDate.setDate(referenceDate.getDate())
        newDate.setMonth(referenceDate.getMonth())
        newDate.setFullYear(referenceDate.getFullYear())

        if(moment(referenceDateOfView, "DD.MM.yyyy HH:mm").toDate().getDay() != 0){
            referenceWeekDay = moment(referenceDateOfView, "DD.MM.yyyy HH:mm").toDate().getDay()
        }else{
            referenceWeekDay = 7 //save sunday as 7 not as 0
        }

        //save all days of this week earlier than referenceDate
        for(var i=referenceWeekDay-1; i>0; i--){
            newDate.setDate(referenceDate.getDate() - (referenceWeekDay - i))
            earliestDate = getEarliestDateOfDay(newDate, earliestDate) 
        }

        //reset
        newDate.setDate(referenceDate.getDate())
        newDate.setMonth(referenceDate.getMonth())
        newDate.setFullYear(referenceDate.getFullYear())
        //save the referenceDay
        earliestDate = getEarliestDateOfDay(newDate, earliestDate) 

        //save all days of this week later than referenceDate
        for(var i=referenceWeekDay+1; i<8; i++){
            newDate.setDate(referenceDate.getDate() + (i - referenceWeekDay))
            earliestDate = getEarliestDateOfDay(newDate, earliestDate) 
        }
        setEarliestAppointmentDate(earliestDate)
        return earliestDate
    }


    return (
        <Style>
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
                        handleExceptionChange={handleExceptionChange}
                        handlePreferredAppointmentChange={handlePreferredAppointmentChange}
                        userStore={userStore}
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
                onSelectEvent={handleSelectEvent}
                culture = 'ge'
                view={currentTimeView}
                onView={handleCurrentTimeViewChange}
                onNavigate={handleNavigationChange}
                step={5}
                timeslots={3}
                scrollToTime={earliestAppointmentDate}
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
                    (event) => {
                      let currentStyle = {
                        backgroundColor: "#5384cf",
                        color: 'white',
                        borderRadius: "5px",
                        border: "#bbb 1px",
                        borderStyle: "solid"
                      };    
                
                      if(event.status == "done"){ //appointment is done
                        currentStyle.backgroundColor = "#0ba12c"
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

export default observer(CalendarPage)