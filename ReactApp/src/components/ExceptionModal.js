import React from 'react'
import {Form, Col, Container, Tabs, Tab, Button, Modal} from "react-bootstrap"
import {getWarningsAsString} from "./Warnings"
import {getErrorMessage} from "./calendarComponents/bookingErrors"

/*================================USAGE===========================*/
/*  Make sure you have something like this:
    - const [MYshowExceptionModal, MYsetShowExceptionModal] = useState(false)
    - const Mywaring = ["waring1", ...]
    
    Modal example usage:
        import {conflictModal} from "../../conflictModal"

        return (
            ...
            <conflictModal 
                showExceptionModal={MYshowExceptionModal}
                setShowExceptionModal={MYsetShowExceptionModal} 
                warning={MYwaring}
            />
            ...
        )
    */


//setShowExceptionModal() (function)
//showExceptionModal (state)
//overrideSubmit() (function)
//overrideText (string)
//excepiton ([])


export function ExceptionModal({showExceptionModal, setShowExceptionModal, overrideSubmit, exception, overrideText}) {

    return (
        <Modal size="lg" show={showExceptionModal} onHide={() => setShowExceptionModal(false)}>
            <Modal.Header>
                <Modal.Title>
                    Dieser Vorgang verursachte einen Konflikt!
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
            <Form.Row>
                    <Form.Group as={Col} md="12">
                        <Form.Label>Konflikt:</Form.Label>
                            <Form.Control
                                readOnly
                                style={{background: "white", color: "red", fontWeight: "bold"}}
                                name="warnings"
                                type="text"
                                value={getErrorMessage(exception)} 
                            />
                    </Form.Group>
                </Form.Row>
            </Modal.Body>
            <Modal.Footer>
                <div style={{ textAlign: "right" }}>
                    <Button variant="danger" style={{marginLeft:"10px"}} onClick={overrideSubmit}>{overrideText}</Button>
                    <Button style={{marginLeft:"10px"}} onClick={() => setShowExceptionModal(false)} variant="secondary">OK</Button>
                </div>
            </Modal.Footer>
        </Modal>
    )
}