import React, { useState, useEffect } from 'react'
import { Form, Container, Table, Card, Col, Row, Button, InputGroup, FormControl } from 'react-bootstrap';
import Layout from "./Layout"

const styles = {
  radioFullBox: {

  },
  radioBox: {
    color: 'black',
    display: 'flex',
    height: '80px',
    width: '120px',
    border: '5px',
    background: '#dadada',
    flexDirection: 'column'
  },
  radioLabel: {
    background: 'blue'
  },
  radioButton: {
    background: 'green',
    flexDirection: 'row',
    flexBasis: '70%',
    margin: '0px 0px 0px 5px'
  }
}


export default function CustomerAdd() {
  const [userName, setUserName] = useState("")
  const [validated, setValidated] = useState(false)


  const handleSubmit = (event) => {
    const form = event.currentTarget;
    if (form.checkValidity() === false) {
      event.preventDefault();
      event.stopPropagation();
    }

    setValidated(true);
  };

  return (
    <Layout>
      <Card className={"border border-dark bg-dark text-white"}>
        <Card.Header>Mitarbeiter hinzufügen</Card.Header>
        <Card.Body>
          <Form noValidate validated={validated} onSubmit={handleSubmit}>
            <Form.Row>
              <Form.Group as={Col} md="5" controlId="validationFirstname">
                <Form.Label>Vorname:</Form.Label>
                <Form.Control
                  required
                  type="text"
                  placeholder="Vorname"
                />
                <Form.Control.Feedback>Passt!</Form.Control.Feedback>
              </Form.Group>
              <Form.Group as={Col} md="5" controlId="validationSurname">
                <Form.Label>Nachname:</Form.Label>
                <Form.Control
                  required
                  type="text"
                  placeholder="Nachname"
                />
                <Form.Control.Feedback>Passt!</Form.Control.Feedback>
              </Form.Group>

              <Form.Group as={Col} md="2">
                <Form.Label>Systemstatus:</Form.Label>
                <Form.Check
                  type="radio"
                  label="Aktiviert"
                  name="SystemStatus"
                  id="SystemStatusAktive"
                />
                <Form.Check
                  type="radio"
                  label="Deaktiviert"
                  name="SystemStatus"
                  id="SystemStatusInaktive"
                />
              </Form.Group>
            </Form.Row>
            <Form.Row>
              <Form.Group as={Col} md="5" controlId="validationUsername">
                <Form.Label>Benutzername:</Form.Label>
                <InputGroup>
                  <Form.Control
                    type="text"
                    placeholder="Benutzername"
                    required
                  />
                  <Form.Control.Feedback type="invalid">
                    Bitte geben Sie ein Benutzernamen ein.
                </Form.Control.Feedback>
                </InputGroup>
              </Form.Group>
              <Form.Group as={Col} md="5" controlId="validationPassword">
                <Form.Label>Passwort:</Form.Label>
                <InputGroup>
                  <Form.Control
                    type="password"
                    placeholder="Passwort"
                    required
                  />
                  <Form.Control.Feedback type="invalid">
                    Bitte gebben Sie ein Passwort ein.
                </Form.Control.Feedback>
                </InputGroup>
              </Form.Group>
            </Form.Row>
            <Form.Row>
              <Form.Group as={Col} md="5">
                <Form.Label>Rolle hinzufügen:</Form.Label>
                <Container style={{ display: "flex", flexWrap: "nowrap" }}>
                  <Form.Control
                    type="text"
                    placeholder="Rolle"
                  />
                  <Button style={{ marginLeft: "20px" }}>Hinzufügen</Button>
                </Container>
              </Form.Group>
            </Form.Row>
            <Form.Row>
              <Form.Group as={Col} md="5">
                <Table striped hover variant="light">
                  <thead>
                    <tr>
                      <th>Rollen</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td >Keine Rolle vorhanden</td>
                    </tr>
                  </tbody>
                </Table>
              </Form.Group>
            </Form.Row>
            <Button type="submit">Kunde anlegen</Button>
          </Form>
        </Card.Body>
      </Card>
    </Layout>
  )
}




