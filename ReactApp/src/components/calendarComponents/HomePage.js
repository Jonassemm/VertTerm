'use strict'

import React, {useState, useEffect} from "react"
import { Calendar, momentLocalizer } from "react-big-calendar"
import moment from "moment"
import Modal from "react-bootstrap/Modal"
import CalendarForm from "./CalendarForm"
import { observer } from "mobx-react"
import { getCalendar } from "./calendarRequests"
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
function HomePage({ calendarStore }){
    const [showAddModal, setShowAddModal] = useState(false)
    const [showEditModal, setShowEditModal] = useState(false)
    const [calendarEvent, setCalendarEvent] = useState({})
    const [initialized, setInitialized] = useState(false)
    const [loading, setLoading] = useState(false)

    const hideModals = () => {
        setShowAddModal(false)
        setShowEditModal(false)
    }

    const getCalendarEvents = async source => { 
        setLoading(true)
        try {
            const response = await getCalendar({cancelToken: source.token})
            const evts = response.data.map(item => {
                return {
                    ...item,
                    start: new Date(item.start),
                    end: new Date(item.end)
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

    const handleSelect = (event, e) => {
        const {start, end} = event
        const data = {title: "", start, end, allDay: false}
        setShowAddModal(true)
        setShowEditModal(false)
        setCalendarEvent(data)
    }

    const handleSelectEvent = (event, e) => {
        setShowAddModal(false)
        setShowEditModal(true)
        let { id, title, start, end, allDay} = event
        start = new Date(start)
        end = new Date(end)
        const data = {id, title, start, end, allDay}
        setCalendarEvent(data)
    }

    useEffect(() => {
        let source = Axios.CancelToken.source()
        if(!initialized) {
           getCalendarEvents(source)
        }
        return () => {
            source.cancel();
        }
    },[])

    return (
        <Style>
        <div className="page">
            {loading ? <div className="loadview"><div>Loading</div></div> : null}
        
            <Modal show={showAddModal} onHide={hideModals}>
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
            </Modal>
            <Modal show={showEditModal} onHide={hideModals}>
                <Modal.Header closeButton>
                    <Modal.Title>Edit Calendar Event</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <CalendarForm
                        calendarStore={calendarStore}
                        calendarEvent={calendarEvent}
                        onCancel={hideModals}
                        edit={true}
                    />
                </Modal.Body>
            </Modal>
            <Calendar
                localizer={localizer}
                events={calendarStore.calendarEvents}
                startAccessor="start"
                endAccessor="end"
                selectable={true}
                style={{height:"80vh"}}
                onSelectSlot={handleSelect}
                onSelectEvent={handleSelectEvent}
                culture = 'ge'
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
            />
        </div> 
        </Style>
    )

}

export default observer(HomePage)