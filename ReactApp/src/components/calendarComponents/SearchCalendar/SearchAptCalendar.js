//author: Jonas Semmler
import React, { useState, useEffect } from "react"
import "react-big-calendar/lib/css/react-big-calendar.css"
import { Calendar, momentLocalizer } from "react-big-calendar"
import moment from "moment"
import { calculateEvts, setup } from "./SearchSlots"
import styled from "styled-components"

const Style = styled.div`
    .rbc-event-label {
        display: none;
    }
  .filtering  .rbc-time-view, .filtering .rbc-month-view {
    opacity: 0.5;
  }
`

const localizer = momentLocalizer(moment)
function SearchAptCalendar({ procedure, neededResourceTypes, neededPositions, setLoading, setSelectedTime }) {
    const [events, setEvents] = useState([])
    const [filtering, setFiltering] = useState(false)
    const [bounds, setBounds] = useState({})

    useEffect(() => {
        const bounds = setup(procedure, neededPositions, neededResourceTypes)
        console.log(bounds)
        if (bounds) setBounds(bounds)
        setNewRange()
    }, [])

    async function setNewRange(range) {
        setFiltering(true)
        setLoading(true)
        let temp = []
        if (range) {
            const start = range[0]
            const end = moment(range[range.length - 1]).endOf("week")
            temp = await calculateEvts(start, end.toDate())
        } else {
            temp = await calculateEvts()
        }
        setEvents(temp)
        setLoading(false)
        setFiltering(false)
    }

    return (
        <React.Fragment>
            <Style>
                <div className={filtering ? 'filtering' : null}>
                    <Calendar
                        localizer={localizer}
                        views={{ week: true }}
                        min={bounds.min}
                        max={bounds.max}
                        showMultiDayTimes
                        events={events}
                        defaultView={"week"}
                        startAccessor="startDate"
                        selectable={true}
                        onSelectSlot={slot => setSelectedTime(procedure, slot)}
                        onRangeChange={setNewRange}
                        eventPropGetter={(event) => {
                            var style = {
                                backgroundColor: "red",
                                borderRadius: '0px',
                                opacity: 0.6,
                                color: 'black',
                                border: '0px',
                                display: 'block'
                            }
                            return {
                                style: style
                            }
                        }}
                        endAccessor="endDate"
                        style={{ height: "80vh" }}
                        culture='ge'
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
        </React.Fragment>
    )
}

export default SearchAptCalendar