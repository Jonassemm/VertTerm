import React, { useState } from "react"
import styled from "styled-components"
import { Container, Row, Col, Button, Table, Modal } from "react-bootstrap"
import { hasRight } from "../auth"
//...Rights[0] = read right
//...Rights[1] = write right
import {
    ownUserRights, 
    userRights,
    positionRights,
    procedureRights,
    resourceRights,
    resourceTypeRights,
    ownAppointmentRights,
    appointmentRights,
    roleRights
 } from "./Rights"

export const Style = styled.div`

.topRow {
    margin: 65px 0px 0px 0px
}

.row {
    margin: 20px 0px 0px 0px
}

.col {
    padding: 0px
}

.colR {
    text-align: right
}

.btn {
    margin: 10px 0px 0px 0px
}

@media (min-width: 1600px) {
    .container{
        max-width: 70%;
    }
}

@media (min-width: 1200px) {
    .container{
        max-width: 90%;
    }
}

@media (max-width: 1200px) {
    .container {
        max-width: 100%
    }
}

`

// definition of the modal prop
/* 
const modal = (onCancel,edit,selectedItem) => {
    return (
        <CUSTOM_FORM                //your defined input form
            onCancel={onCancel}
            edit={edit}
            selected={selectedItem}
        />
    )
}
*/


export const modalTypes = {
    user: "user",
    position: "position",
    resource: "resource",
    resourceType: "resourceType",
    procedure: "procedure",
    appointment: "appointment",
    role: "role"
}


function OverviewPage({
    pageTitle,        
    newItemText,                //text of the new Item button 
    tableHeader,
    tableBody,
    modal,                      //input form
    modalType = null,           //optional prop to check a specific right to show the modal
    userStore = null,           //optional prop to check a specific right to show the modal
    data,                       //table data of the desired object
    editModalText,              //optional prop for overwriting the title of the edit modal
    modalSize,                  //optional prop for customize modal size -> ["sm", "lg", "xl"]
    withoutCreate = false,      //optional prop to remove the add button (boolean) 
    noTopMargin = false,        //optional prop to reduce te space above the OverviewPage
    refreshData,                //called after closing the modal   
    scrollable
    }) {
    const [showNewModal, setShowNewModal] = useState(false)
    const [showEditModal, setShowEditModal] = useState(false)
    const [selectedItem, setSelectedItem] = useState({})


    const handleClick = event => {
        if(event.target.type != "button"){
            let x = (event.target.parentElement.firstChild.textContent) - 1
            setSelectedItem(data[x])
            setShowEditModal(true)
        }
    }

    const hideModals = () => {
        refreshData()
        setShowEditModal(false)
        setShowNewModal(false)
    }

    const handleNew = () => {
        setShowNewModal(true)
    }


    //for showing the add-button
    const hasWriteRight = () =>{
        var rightExists = false
        switch(modalType) {
            case modalTypes.user:
                if(hasRight(userStore, [userRights[1]])){
                    rightExists = true
                }
                break;
            case modalTypes.position:
                if(hasRight(userStore, [positionRights[1]])){
                    rightExists = true
                }
                break;
            case modalTypes.procedure:
                if(hasRight(userStore, [procedureRights[1]])){
                    rightExists = true
                }
                break;
            case modalTypes.resource:
                if(hasRight(userStore, [resourceRights[1]])){
                    rightExists = true
                }
                break;
            case modalTypes.resourceType:
                if(hasRight(userStore, [resourceTypeRights[1]])){
                    rightExists = true
                }
                break;
            case modalTypes.role:
                if(hasRight(userStore, [roleRights[1]])){
                    rightExists = true
                }
                break;
            default: rightExists = true //no restrictions defined
        }
        return rightExists
    }


    const isOwnAppointment = () =>{
        var ownAppointment = false
        //check if the selected is an appointment of the logged in user 
        if(Object.keys(selectedItem).length != 0) {
            if(selectedItem.bookedCustomer.id == userStore.userID){
                ownAppointment = true
            }
            if(selectedItem.bookedEmployees.length > 0){
                selectedItem.bookedEmployees.map(employee => {
                    if(employee.id == userStore.userID){
                        ownAppointment = true
                    }
                })
            }
        }
        return ownAppointment
    }


    return (
        <Style>
            <Modal size={modalSize} scrollable={scrollable} centered show={showEditModal} onHide={hideModals} backdrop={"static"}>
                <Modal.Header>
                    <Modal.Title>{editModalText || selectedItem.name || selectedItem.username || selectedItem.title}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {modal(hideModals,true,selectedItem)}
                </Modal.Body>
            </Modal>

            <Modal size={modalSize} scrollable={scrollable} centered show={showNewModal} onHide={hideModals} backdrop={"static"}>
                <Modal.Header>
                    <Modal.Title>{newItemText}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {modal(hideModals,false)}
                </Modal.Body>
            </Modal>

            <Container>
                {!noTopMargin &&
                    <div className="topRow"></div>
                }
                <Row>
                    <Col ><h1>{pageTitle}</h1></Col>
                    {!withoutCreate && hasWriteRight() && <Col className="colR"><Button onClick={handleNew}>{newItemText}</Button></Col>}
                </Row>
                <Row >
                    <Table striped bordered hover>
                        <thead>
                            <tr>
                                {tableHeader.map((item, index) => {
                                    return (
                                        <th key={index}>{item}</th>
                                    )
                                })}
                            </tr>
                        </thead>
                        <tbody>
                            {tableBody.map((rowItem, index) => {
                                var showCurrentRow = false
                                if(modalType != null){
                                    if(userStore != null) {
                                        switch(modalType) {
                                            case modalTypes.user:
                                                if(hasRight(userStore, [userRights[0]])){
                                                    showCurrentRow = true
                                                }else if(hasRight(userStore, [ownUserRights[0]]) && rowItem[1] == userStore.username){
                                                    showCurrentRow = true
                                                }
                                                break;
                                            case modalTypes.position:
                                                if(hasRight(userStore, [positionRights[0]])){
                                                    showCurrentRow = true
                                                }
                                                break;
                                            case modalTypes.procedure:
                                                if(hasRight(userStore, [procedureRights[0]])){
                                                    showCurrentRow = true
                                                }
                                                break;
                                            case modalTypes.resource:
                                                if(hasRight(userStore, [resourceRights[0]])){
                                                    showCurrentRow = true
                                                }
                                                break;
                                            case modalTypes.resourceType:
                                                if(hasRight(userStore, [resourceTypeRights[0]])){
                                                    showCurrentRow = true
                                                }
                                                break;
                                            case modalTypes.role:
                                                if(hasRight(userStore, [roleRights[0]])){
                                                    showCurrentRow = true
                                                }
                                                break;
                                            case modalTypes.appointment:
                                                if(hasRight(userStore, [appointmentRights[0]])){
                                                    showCurrentRow = true
                                                }else if(hasRight(userStore, [ownAppointmentRights[0]]) && rowItem[4] == (userStore.firstName + ", " + userStore.lastName)){
                                                    showCurrentRow = true
                                                }
                                                break;
                                            default: showCurrentRow = false
                                        }
                                    }
                                    if(showCurrentRow){
                                        return (
                                            <tr key={index} onClick={handleClick}>
                                                {rowItem.map((colItem, index) => {
                                                    return (
                                                        <td key={index}>{colItem}</td>
                                                    )
                                                })}
                                            </tr>
                                        )
                                    }
                                }else {//no modalType -> no right check
                                    return (
                                        <tr key={index} onClick={handleClick}>
                                            {rowItem.map((colItem, index) => {
                                                return (
                                                    <td key={index}>{colItem}</td>
                                                )
                                            })}
                                        </tr>
                                    )
                                }
                            })}
                        </tbody>
                    </Table>
                </Row>
            </Container>
        </Style >
    )
}

export default OverviewPage