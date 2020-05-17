import React, { useState } from "react"
import styled from "styled-components"
import { Container, Row, Col, Button, Table, Modal } from "react-bootstrap"

const Style = styled.div`

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

function OverviewPage({
    pageTitle,        
    newItemText,        //text of the new Item button 
    tableHeader,
    tableBody,
    modal,              //input form
    data,               //table data of the desired object
    editModalText,      //optional prop for overwriting the title of the edit modal
    refreshData}) {
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

    return (
        <Style>
            <Modal centered show={showEditModal} onHide={hideModals}>
                <Modal.Header>
                    <Modal.Title>{editModalText || selectedItem.name}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {modal(hideModals,true,selectedItem)}
                </Modal.Body>
            </Modal>

            <Modal centered show={showNewModal} onHide={hideModals}>
                <Modal.Header>
                    <Modal.Title>{newItemText}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {modal(hideModals,false)}
                </Modal.Body>
            </Modal>

            <Container>
                <div className="topRow"></div>
                <Row>
                    <Col ><h1>{pageTitle}</h1></Col>
                    <Col className="colR"><Button onClick={handleNew}>{newItemText}</Button></Col>
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
                                return (
                                    <tr key={index} onClick={handleClick}>
                                        {rowItem.map((colItem, index) => {
                                            return (
                                                <td key={index}>{colItem}</td>
                                            )
                                        })}
                                    </tr>
                                )
                            })}
                        </tbody>
                    </Table>
                </Row>
            </Container>
        </Style >
    )
}

export default OverviewPage