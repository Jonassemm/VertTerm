//author: Jonas Semmler
import React, { useState, useEffect } from "react"
import "./BookingForm.css"
import ObjectPicker from "../ObjectPicker"
import {Form} from "react-bootstrap"
import DatePicker from "react-datepicker"
import moment from "moment"

export default function BlockerForm({ edit, apt, setApt}) {
    const [name, setName] = useState("")
    const [employees, setEmployees] = useState([])
    const [resources, setResources] = useState([])
    const [startTime, setStartTime] = useState(null)
    const [endTime, setEndTime] = useState(null)

    function setupEdit() {
        if (edit) {
            setName(apt.name)
            setEmployees(apt.bookedEmployees)
            setResources(apt.bookedResources)
            setStartTime(moment(apt.plannedStarttime, "DD.MM.YYYY HH:mm").toDate())
            setEndTime(moment(apt.plannedEndtime,"DD.MM.YYYY HH:mm").toDate())
        }
    }

    useEffect(() => {
        setupEdit()
    },[])

    useEffect(() => {
        setApt(buildApt())
    },[name,employees,resources,startTime,endTime])

    function buildApt() {
        const data = {
            name: name,
            bookedResources: resources,
            bookedEmployees: employees,
            plannedStarttime: startTime,
            plannedEndtime: endTime
        }
        return data
    }

    return (
        <React.Fragment>
            <hr />
            <div className="parent">
                <div className="box wrap" style={{width:"75%"}}>
                    <div className="middleBox">
                        <div className="middleBoxLeft">
                            <span>Name</span>
                        </div>
                        <div className="middleBoxRight">
                            <Form.Control
                                required
                                type="text"
                                value={name || ""}
                                placeholder="Terminbezeichnung"
                                onChange={e => setName(e.target.value)}
                            />
                            <hr />
                        </div>
                    </div>
                    {/* all ObjectPickers for needed Employees */}
                    <div className="middleBox">
                        <div className="middleBoxLeft">
                            <span>Mitarbeiter</span>
                        </div>
                        <div className="middleBoxRight">
                            <ObjectPicker
                                className="input"
                                DbObject="employee"
                                initial={edit && employees}
                                multiple={true}
                                setState={setEmployees} />
                        </div>
                    </div>
                    {/* all ObjectPickers for needed resources */}
                    <div className="middleBox">
                        <div className="middleBoxLeft">
                            <span>Ressourcen</span>
                        </div>
                        <div className="middleBoxRight">
                            <ObjectPicker
                                className="input"
                                DbObject="resource"
                                initial={edit && resources}
                                multiple={true}
                                setState={setResources} />
                        </div>
                    </div>
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
                                selected={startTime}
                                onChange={e => {
                                    setStartTime(e)
                                    if(e > endTime) setEndTime(e)
                                }}
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
                                selected={endTime}
                                onChange={e => {
                                    if(e > startTime) setEndTime(e)
                                    else setEndTime(startTime)
                                }}
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
}