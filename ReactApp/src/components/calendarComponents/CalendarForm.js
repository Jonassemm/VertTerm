import React ,{useState, useEffect} from "react";
import Form from "react-bootstrap/Form";
import Col from "react-bootstrap/Col";
import DatePicker from "react-datepicker";
import Button from "react-bootstrap/Button";
import Loader from "react-loader-spinner"
import {
  addCalendar,
  editCalendar,
  getCalendar,
  deleteCalendar
} from "./requests";
import { observer } from "mobx-react"

const buttonStyle = {marginRight: 10}

function CalendarForm({calendarStore, calendarEvent, onCancel, edit}){
    const [start, setStart] = useState(null)
    const [end, setEnd] = useState(null)
    const [title, setTitle] = useState("")
    const [resource, setResource] = useState("")
    const [id, setId] = useState(null)
    const [loading, setLoading] = useState(false)

    
    useEffect( () =>{
        setTitle(calendarEvent.title)
        setStart(calendarEvent.start)
        setEnd(calendarEvent.end)
        setId(calendarEvent.id)
        console.log("useEffect")
    }, [calendarEvent.title, calendarEvent.start, calendarEvent.end, calendarEvent.id])

    const handleSubmit = async event => {
        event.preventDefault()
        if(!title || !start || !end){
            return;
        }
        if(+start > +end){
            alert("Start date must be earlier than end date")
            return
        }
        const data = { id, title, start, resource, end };
        setLoading(true);
        try {
            if (!edit) {
                await addCalendar(data);
              } else {
                 console.log(id)
                await editCalendar(data);
              }
        }catch (error){
            console.log(Object.keys(error), error.message)
            alert("Network Error")
            onCancel()
        }
        refrehEvents()
        setLoading(false)
        onCancel()
    }

    const deleteCalendarEvent = async () => {
        await deleteCalendar(calendarEvent.id)
        refrehEvents()
        onCancel()
    }

    const handleStartChange= date => setStart(date)
    const handleEndChange= date => setEnd(date)
    const handleTitleChange = ev => setTitle(ev.target.value)
    const handleResourceChange = ev => setResource(ev.target.value)
  
    async function refrehEvents(){
        const response = await getCalendar()
        const evts = response.data.map(item => {
            return {
                ...item,
            }  
        })
        calendarStore.setCalendarEvents(evts)
    }

    return(
        <Form noValidate onSubmit={handleSubmit}>
            <Form.Row>
                <Form.Group as={Col} md="12" controlId="title">
                    <Form.Label>Title</Form.Label>
                    <Form.Control
                        type="text"
                        name="title"
                        placeholder="Title"
                        value={title || ""}
                        onChange={handleTitleChange}
                        isInvalid={!title}
                        />
                        <br/>
                         <Form.Control
                        type="text"
                        name="title"
                        placeholder="resource"
                        value={resource || ""}
                        onChange={handleResourceChange}
                        isInvalid={!title}
                        />
                    <Form.Control.Feedback type="invalid">{!title}</Form.Control.Feedback>
                </Form.Group>    
            </Form.Row>
            <Form.Row>
                <Form.Group as={Col} md="12" controlId="start">
                    <Form.Label>Start</Form.Label>
                    <br/>
                    <DatePicker
                        showTimeSelect
                        className="form-control"
                        selected={start || ""}
                        onChange={handleStartChange}
                        />
                </Form.Group>
            </Form.Row>
            <Form.Row>
                <Form.Group as={Col} md="12" controlId="end">
                    <Form.Label>End</Form.Label>
                    <br/>
                    <DatePicker
                        showTimeSelect
                        className="form-control"
                        selected={end || ""}
                        onChange={handleEndChange}
                        />
                </Form.Group>
            </Form.Row>
            <Button disabled={loading} type="submit" style={buttonStyle}>
                {loading ? <Loader type="TailSpin" color="#00BFFF" height={20} width={20}/> : "Save"}
            </Button>
            <Button disabled={loading} type="button" style={buttonStyle} onClick={deleteCalendarEvent}>
                Delete
            </Button>
            <Button disabled={loading} type="button" onClick={onCancel}>
                Cancel
            </Button>
            <Form.Group>
                
            </Form.Group>
        </Form>
    )
}

export default observer(CalendarForm)