import React, { useState, useEffect } from "react"
import "./BookingForm.css"
import ObjectPicker from "../ObjectPicker"
import {Form} from "react-bootstrap"
import DatePicker from "react-datepicker"

export default function BlockerForm({ edit, apt, setApt,customer }) {
    const [name, setName] = useState("")
    const [employees, setEmployees] = useState([])
    const [resources, setResources] = useState([])
    const [startTime, setStartTime] = useState(null)
    const [endTime, setEndTime] = useState(null)

    function setupEdit() {
        if (edit) {
            setEmployees(apt.bookedEmployees)
            setResources(apt.bookedResources)
            setStartTime(apt.bookedStartTime)
            setEndTime(apt.plannedEndtime)
        }
    }

    useEffect(() => {
        setupEdit()
    }, [])

    useEffect(() => {
        setApt(buildApt())
    },[name,employees,resources,startTime,endTime, customer])

    function buildApt() {
        const data = {
            name: name,
            bookedCustomer: customer,
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
                                onChange={e => setStartTime(e)}
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
                                onChange={e => setEndTime(e)}
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