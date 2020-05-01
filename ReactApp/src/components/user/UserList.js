import React, { useState, useEffect } from 'react'
import { Card, Table, Image, ButtonGroup, Button, InputGroup, FormControl } from 'react-bootstrap';
import Layout from "./Layout"

export default function UserList(props) {
    return (
        <Layout>
            <Card className={"border border-dark bg-dark text-white"}>
                <Card.Header>{props.heading}</Card.Header>
                <Card.Body>
                    <Table striped hover variant="dark">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Benutzername</th>
                                <th>Nachname</th>
                                <th>Vorname</th>
                                <th>Status</th>
                                <th>AKTION</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>1</td>
                                <td>Beispiel</td>
                                <td>Musterman</td>
                                <td>Maximilian</td>
                                <td>aktiv</td>
                                <td style={{ width: "300px" }}>
                                    <Button style={{ marginRight: "5px" }}>
                                        Bearbeiten
                            </Button>
                                    <Button style={{ marginRight: "5px" }}>
                                        Details
                            </Button>
                                    <Button>
                                        Löschen
                            </Button>
                                </td>
                            </tr>
                            <tr align="center">
                                <td colSpan="7">Kein Benutzer vorhanden</td>
                            </tr>
                        </tbody>
                    </Table>
                </Card.Body>
            </Card>
        </Layout>
    )
}