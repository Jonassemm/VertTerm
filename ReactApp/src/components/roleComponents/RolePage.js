import React, { useState, useEffect } from "react"
import { Container, Row, Col, Button, Table, Modal } from "react-bootstrap"
import styled from "styled-components"
import { getRoles } from "./RoleRequests"
import RoleForm from "./RolesForm"

const Style = styled.div`

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

function RolePage(userStore) {
    const [roles, setRoles] = useState([])
    const [showEditModal, setShowEditModal] = useState(false)
    const [selectedRole, setSelectedRole] = useState({})
    const [showNewModal, setShowNewModal] = useState(false)

    async function prepareRoles() {
        const res = await getRoles()
        const result = res.data.map(item => {
            const rightStrings = item.rights.map(item => {
                return item['name']
            })
            return {
                ...item,
                rights: rightStrings
            }
        }
        )
        setRoles(result)
    }

    useEffect(() => {
        prepareRoles()
    }, [])

    const handleClick = event => {
        let x = (event.target.parentElement.firstChild.textContent) - 1
        setSelectedRole(roles[x])
        setShowEditModal(true)
    }

    const hideModals = () => {
        prepareRoles()
        setShowEditModal(false)
        setShowNewModal(false)
    }

    const handleNewRole = () => {
        setShowNewModal(true)
    }

    return (
        <Style>
            <Container>
                <Modal centered show={showEditModal} onHide={hideModals}>
                    <Modal.Header>
                        <Modal.Title>{selectedRole.name}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <RoleForm
                            onCancel={hideModals}
                            edit={true}
                            selected={selectedRole}
                        />
                    </Modal.Body>
                </Modal>
                <Modal centered show={showNewModal} onHide={hideModals}>
                    <Modal.Header>
                        <Modal.Title>Neue Rolle</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <RoleForm
                            onCancel={hideModals}
                            edit={false}
                            selected={selectedRole}
                        />
                    </Modal.Body>
                </Modal>
                <Row>
                    <Col ><h1>Rollen</h1></Col>
                    <Col className="colR"><Button onClick={handleNewRole}>Neue Rolle</Button></Col>
                </Row>
                <Row >
                    <Table striped bordered hover>
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Bezeichnung</th>
                                <th>Beschreibung</th>
                                <th>Rechte</th>
                            </tr>
                        </thead>
                        <tbody>
                            {roles.map((item, index) => {
                                return (
                                    <tr key={item.id} onClick={handleClick}>
                                        <td>{index + 1}</td>
                                        <td>{item.name}</td>
                                        <td>{item.description}</td>
                                        <td>{item.rights.join(", ")}</td>
                                    </tr>
                                )
                            })}
                        </tbody>
                    </Table>
                </Row>
            </Container>
        </Style>
    )
}

export default RolePage