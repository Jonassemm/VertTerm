//author: Patrick Venturini
import React from 'react'
import {Form, Col, Button, Modal} from "react-bootstrap"
import {getErrorMessage} from "./calendarComponents/bookingErrors"
import {Link} from 'react-router-dom';

/*================================USAGE===========================*/
/*  Make sure you have something like this:
    - const [MYshowExceptionModal, MYsetShowExceptionModal] = useState(false)
    - const Mywarning = "WARNING_TEXT"
    
    ====================Modal example usage=======================
        import {ExceptionModal} from "../../ExceptionModal"

        return (
            ...
            <ExceptionModal 
                showExceptionModal={MYshowExceptionModal}
                setShowExceptionModal={MYsetShowExceptionModal} 
                exception={MYwarning}
                overrideSubmit={handleOverrideDelete}
                overrideText="This text is shown in the submit-button"
            />
            ...
        )
    */

export function ExceptionModal({
    showExceptionModal, 
    setShowExceptionModal,  
    exception, 
    overrideSubmit = null,          //optional (only in combination with overrideText)
    overrideText = null,            //optional (only in combination with overrideSubmit)
    warning = null                  //optional, links to the conflict page
    }) {

    return (
        <Modal size="xl" show={showExceptionModal} onHide={() => setShowExceptionModal(false)}>
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
                                value={getErrorMessage((exception == "Availability") ? "removedAvailability" : exception)}
                            />
                    </Form.Group>
                </Form.Row>
            </Modal.Body>
            <Modal.Footer>
                <div style={{ textAlign: "right" }}>
                    {overrideSubmit != null && overrideText != null &&
                        <Button variant="danger" style={{marginLeft:"10px"}} onClick={overrideSubmit}>{overrideText}</Button>
                    }
                    {warning != null &&
                        <Link to={`/warning/${warning}`}>
                            <Button variant="success" style={{ marginLeft: "10px" }}>Konflikt beheben</Button>
                        </Link>
                    }
                    <Button style={{marginLeft:"10px"}} onClick={() => setShowExceptionModal(false)} variant="secondary">OK</Button>
                </div>
            </Modal.Footer>
        </Modal>
    )
}
