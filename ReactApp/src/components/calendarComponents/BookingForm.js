import React, { useState, useEffect } from "react"
import { Calendar, momentLocalizer } from "react-big-calendar"
import moment from "moment"
import { Container, Form, Col, Row, Button } from "react-bootstrap"
import { observer } from "mobx-react"
import "react-big-calendar/lib/css/react-big-calendar.css"
import ObjectPicker from "../ObjectPicker"
import "./BookingForm.css"
import DatePicker from "react-datepicker"

const localizer = momentLocalizer(moment)

function BookingForm() {
    const [calendarEvents, setCalendarEvents] = useState([])
    const [selectedProcedures, setSelectedProcedures] = useState([])
    const [selectedCustomer, setSelectedCustomer] = useState({})
    const [startDate, setStartDate] = useState(null)
    const [endDate, setEndDate] = useState(null)
    const [custom, setCustom] = useState(false)
    const [apts, setApts] = useState([])
    const [timeDif, setTimeDif] = useState(0)

    function handleSubmit(event) {
        event.preventDefault()
        const finalData = apts.map(item => {
            return {
                ...item,
                bookedCustomer: selectedCustomer,
                plannedStartTime: startDate,
                plannendEndTime: endDate
            }
        })
        console.log()
        console.log(finalData)
    }

    function validateTime(date) {         
        let currentDate = new Date()
        let mod = currentDate.getMinutes() % 10
        currentDate.setMinutes(currentDate.getMinutes() - mod)
        if(mod >=5 ) currentDate.setMinutes(currentDate.getMinutes() + 10)

        if(date.ident == "start"){
            if(date.date.getTime() - currentDate.getTime() < 0){
                setStartDate(currentDate)
            }else {
                setStartDate(date.date)
            }
        }else {
            const dif = Math.round((date.date.getTime() - startDate.getTime()) / 1000 / 60)
            console.log(dif)
            if(dif < 0 ){
                setEndDate(currentDate)
            }else {
                setEndDate(date.date)
            }
        }
      
        
    }

    function handleChange(data) {
        console.log(data)
        const temp = apts.map(item => {
            if (item.procedure.name == data.ident.ident) {
                if (data.DbObject == "employee") {
                    const temp = item.bookedEmployees.map((innerItem, index) => {
                        if (index == data.ident.ix) {
                            return data.data[0]
                        } else {
                            return {
                                ...innerItem
                            }
                        }
                    })
                    return {
                        ...item,
                        bookedEmployees: temp
                    }
                } else {
                    const temp = item.bookedResources.map((innerItem, index) => {
                        if (index == data.ident.ix) {
                            return data.data[0]
                        } else {
                            return {
                                ...innerItem
                            }
                        }
                    })
                    return {
                        ...item,
                        bookedResources: data.data
                    }
                }
            } else {
                return {
                    ...item
                }
            }
        })
        setApts(temp)
    }

    function setProcedures(data) {
        setSelectedProcedures(data)
        const temp = apts.map(item => {
            return {
                ...item
            }
        })
        const dif = apts.length - data.length
        if (dif > 1) {
            // delete all
            temp.splice(0, temp.length)
        } else if (dif > 0) {
            // delete one
            for (let i = 0; i < apts.length; i++) {
                if (!data.find(item => item.name == apts[i].procedure.name)) {
                    temp.splice(i, 1)
                }
            }
        } else {
            // add one
            const tempEmployees = data[data.length - 1].neededEmployeePositions.map(item => {
                return {}
            })
            temp.push({
                procedure: data[data.length - 1],
                bookedEmployees: tempEmployees,
                bookedCustomer: {},
                bookedResources: [],
                plannedStartTime: "",
                plannendEndTime: ""
            })
        }
        setApts(temp)
    }

    return (
        <div className="page">
            <Container>
                <Form onSubmit={handleSubmit}>
                    <Row style={{ alignItems: "baseline" }}>
                        <Col>
                            <h1>Termin Buchen</h1>
                        </Col>
                        <Col className="selectType">
                            <Form.Check
                                type="switch"
                                id="custom-switch"
                                label="Benutzerdefinierte Eingabe"
                                value={custom || 0}
                                onChange={e => setCustom(!custom)}
                                checked={custom || false}
                            />
                        </Col>

                    </Row>
                    <hr />
                    <Form.Row>
                        <Form.Group as={Col} style={{ textAlign: "bottom" }}>
                            <Form.Label>Kunde:</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col}>
                            <ObjectPicker
                                DbObject="user"
                                setState={setSelectedCustomer} />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} style={{ textAlign: "bottom" }}>
                            <Form.Label>Prozeduren:</Form.Label>
                        </Form.Group>
                        <Form.Group as={Col}>
                            <ObjectPicker
                                DbObject="procedure"
                                setState={setProcedures}
                                multiple={true} />
                        </Form.Group>
                    </Form.Row>
                    {!custom && selectedProcedures.map(item => {
                        return (
                            <React.Fragment>
                                <hr />
                                <div className="parent">
                                    <div className="namebox">
                                        <h4>{item.name}</h4>
                                    </div>
                                    <div className="box wrap">
                                        {item.neededEmployeePositions && item.neededEmployeePositions.map((innerItem, index) => {
                                            return (
                                                <div className="middleBox">
                                                    <div className="middleBoxLeft">
                                                        <span>{innerItem.name}</span>
                                                    </div>
                                                    <div className="middleBoxRight">
                                                        <ObjectPicker
                                                            className="input"
                                                            ident={{ "ident": item.name, "ix": index }}
                                                            DbObject="employee"
                                                            setState={handleChange} />
                                                    </div>
                                                </div>
                                            )
                                        })}
                                        {item.neededResourceTypes && item.neededResourceTypes.map((innerItem, index) => {
                                            return (
                                                <div className="middleBox">
                                                    <div className="middleBoxLeft">
                                                        <span>{innerItem.name}</span>
                                                    </div>
                                                    <div className="middleBoxRight">
                                                        <ObjectPicker
                                                            className="input"
                                                            ident={{ "ident": item.name, "ix": index }}
                                                            DbObject="resource"
                                                            setState={handleChange} />
                                                    </div>
                                                </div>
                                            )
                                        })}
                                    </div>
                                    <div className="box rightBox">
                                        <div className="rightBoxTop">
                                            <div className="rightBoxLeft">
                                                <span>Start</span>
                                            </div>
                                            <div className="rightBoxRight">
                                                <DatePicker
                                                    required
                                                    popperPlacement="left"
                                                    className="input"
                                                    selected={startDate || ""}
                                                    onChange={date => validateTime({date: date, ident: "start"})}
                                                    showTimeSelect
                                                    timeFormat="HH:mm"
                                                    timeIntervals={5}
                                                    timeCaption="Uhrzeit"
                                                    dateFormat="dd.M.yyyy / HH:mm"
                                                />
                                            </div>
                                        </div>
                                        <div className="rightBoxBottom">
                                            <div className="rightBoxLeft">
                                                <span>Ende</span>
                                            </div>
                                            <div className="rightBoxRight">
                                                <DatePicker
                                                    required
                                                    className="input"
                                                    popperPlacement="left"
                                                    selected={endDate || ""}
                                                    onChange={date => { 
                                                        validateTime({date: date, ident: "end"})}}
                                                    showTimeSelect
                                                    timeFormat="HH:mm"
                                                    timeIntervals={5}
                                                    timeCaption="Uhrzeit"
                                                    dateFormat="dd.M.yyyy / HH:mm"
                                                />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </React.Fragment>
                        )
                    })}
                    <hr />
                    <div style={{ textAlign: "right" }}>
                        <Button>Terminvorschlag</Button>
                        <Button variant="success" type="submit" style={{ marginLeft: "5px" }}>Buchen</Button>
                    </div>
                    <hr />
                </Form>
                <Calendar
                    localizer={localizer}
                    events={calendarEvents}
                    startAccessor="start"
                    endAccessor="end"
                    selectable={true}
                    style={{ height: "80vh" }}
                />
            </Container>
        </div >
    )
}

export default BookingForm