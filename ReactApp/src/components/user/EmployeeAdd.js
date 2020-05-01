import React ,{useState, useEffect} from 'react'
import {Form, Table, Card, Col, Container, Button, InputGroup} from 'react-bootstrap'
import {observer} from "mobx-react"
import Layout from "./Layout"

import Availability from "./../availability/AvailabilityForm"
import {
  addEmployee,
  getRolesOfUser,
  getRoles,
  getUser
} from "./requests";

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


function EmployeeAdd(props) {
  
  const [forename, setForename] = useState("")
  const [surname, setSurname] = useState("")
  const [username, setUsername] = useState("") 
  const [password, setPassword] = useState("")
  const [position, setPosition] = useState("")
  const [status, setStatus] = useState(null)

  let firstSelectedRole = null // to add the first role wich is listed when click on "Hinzufügen"
  const [selectedRole, setSelectedRole] = useState(null)
  const [userRoles, setUserRoles] = useState([])
  const [choosableRoles, setChoosableRoles] = useState([{roleName: "Admin"},{roleName: "Gast"},{roleName: "Benutzerdefinierte Rolle"}])

  const [currentUser, setCurrentUser] = useState(null)
  const [validated, setValidated] = useState(false)




  const handleSubmit = async event => {
    const form = event.currentTarget;
    event.preventDefault();//reload the page after clicking "Enter"

    const usertype = "employee"
    const employeeData = {forename, surname, username, password, position, status, roles: userRoles, usertype}
    try {
      await addEmployee(employeeData);
      console.log("AXIOS: addEmployee()")
    } catch (error) {
      console.log(Object.keys(error), error.message)
      alert("An error occoured while adding a employee")
    }
  };

  //---------------------------------ROLES---------------------------------
  //LOAD
  const loadUserRoles = async () => {
    const response = await getRolesOfUser();
    const data = response.data.map(role => {
        return {
            ...role,
        }
    })
    console.log("load Role: " + data + " / " + [data] +  " / " + {data})
    setUserRoles(data)
  }

  const loadChoosableRoles = async () => {
    const response = await getRoles();
    const data = response.data.map(role => {
        return {
            ...role,
        }
    })
    console.log("load Role: " + data + " / " + [data] +  " / " + {data})
    setChoosableRoles(data)
  }

  //ADD
  const addRole = () => {
    console.log("add Role:" + selectedRole)
    console.log("firstRole: " + firstSelectedRole)

    if(userRoles.some(roles => roles.roleName === selectedRole) || (userRoles.some(roles => roles.roleName === firstSelectedRole) && (selectedRole==null))) {
      alert("Rolle bereits vorhanden!")  
      } else if (selectedRole == null) {
        setUserRoles([...userRoles, {roleName: firstSelectedRole}]) 
        console.log("first role added and reset: " + firstSelectedRole)
      } else {
      setUserRoles([...userRoles, {roleName: selectedRole}])
      }
  };

  const updateChoosableRoles = (index) => {
    console.table(userRoles);
    userRoles.splice((index),1);
    setUserRoles([...userRoles]);
    console.table(userRoles);
  }
  //REMOVE
  const removeRole = (index) => {
    console.table(userRoles);
    userRoles.splice((index),1);
    setUserRoles([...userRoles]);
    console.table(userRoles);
  }

  //---------------------------------CURRENT_USER---------------------------------
  const loadUser = async () => {
    const response = await getUser();
    const data = response.data.map(properties => {
        return {
            ...properties,
        }
    })
    console.log("load User: " + data + " / " + [data] +  " / " + {data});
    setCurrentUser(data);
  }


  //---------------------------------RENDERING---------------------------------
  function renderDropdown() {
    if (choosableRoles != null) {
        return choosableRoles.map((role, index) => {
            const {roleName} = role
            if(index == 0) {
              firstSelectedRole = roleName
            }
            return (
                <option key={index} value={roleName}>{roleName}</option>
            )
        })
    } else {
        return (
            <tr align="center">
                <td colSpan="7">Kein Benutzer vorhanden</td>
            </tr>
        )
    }
    
}

  useEffect(() => {
    switch(props.type) {
      case "edit":
        loadUser();
        console.log("useEffect-Call: loadUser()");
        break;
      case "add":
        break;
    }
  })

  const handleForenameChange = data => setForename(data.target.value)
  const handleSurnameChange = data => setSurname(data.target.value)
  const handleUsernameChange = data => setUsername(data.target.value)
  const handlePasswordChange = data => setPassword(data.target.value)
  const handlePositionChange = data => setPosition(data.target.value)
  const handleRoleInputChange = data => setSelectedRole(data.target.value)
  const handleStatusChange = data => setStatus(data.target.value)


   return (
    <Layout>
      <Card style={{marginBottom: "50px"}} className={"border border-dark bg-dark text-white"}>
        <Form id="employeeAdd" onSubmit={(e) => handleSubmit(e)}>
          <Card.Header><h3>Mitarbeiter hinzufügen</h3></Card.Header>
          <Card.Body>
            <Form.Row>
              <Form.Group as={Col} md="5" >
                <Form.Label>Vorname:</Form.Label>
                <Form.Control
                  required
                  name="forename"
                  type="text"
                  placeholder="Vorname"
                  value={forename || ""}
                  onChange={handleForenameChange}
                />
                <Form.Control.Feedback>Passt!</Form.Control.Feedback>
              </Form.Group>
              <Form.Group as={Col} md="5">
                <Form.Label>Nachname:</Form.Label>
                <Form.Control
                  required
                  name="surename"
                  type="text"
                  placeholder="Nachname"
                  value={surname || ""}
                  onChange={handleSurnameChange}
                />
                <Form.Control.Feedback>Passt!</Form.Control.Feedback>
              </Form.Group>
              
              <Form.Group as={Col} md="2">
                  <Form.Label>Benutzerkonto:</Form.Label>
                  <Form.Check
                      required
                      type="radio"
                      label="Aktiviert"
                      name="status"
                      value="aktive"
                      id="SystemStatusAktive"
                      onClick={handleStatusChange}
                  />
                  <Form.Check
                      required
                      type="radio"
                      label="Deaktiviert"
                      name="status"
                      id="SystemStatusInaktive"
                      value="inaktive"
                      onClick={handleStatusChange}
                  />
              </Form.Group>
            </Form.Row>
            <Form.Row>
              <Form.Group as={Col} md="5" >
                  <Form.Label>Benutzername:</Form.Label>
                  <InputGroup>
                      <Form.Control
                      required
                      name="username"
                      type="text"
                      placeholder="Benutzername"
                      value={username || ""}
                      onChange={handleUsernameChange}
                      />
                      <Form.Control.Feedback type="invalid">
                      Bitte geben Sie ein Benutzernamen ein.
                      </Form.Control.Feedback>
                  </InputGroup>
              </Form.Group>
              <Form.Group as={Col} md="5">
                  <Form.Label>Passwort:</Form.Label>
                  <InputGroup>
                      <Form.Control
                      required
                      name="password"
                      type="password"
                      placeholder="Passwort"
                      value={password || ""}
                      onChange={handlePasswordChange}
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
                  <Container style={{display: "flex", flexWrap: "nowrap"}}>
                    {/*<Form.Control 
                      type="text"
                      placeholder="Rolle" 
                      onChange={handleRoleInputChange}
                    />*/}
                      <select onChange={handleRoleInputChange} className="custom-select">
                          {renderDropdown()}
                      </select>
                    <Button onClick={addRole} style={{marginLeft: "20px"}}>Hinzufügen</Button>
                  </Container> 
              </Form.Group>
              <Form.Group as={Col} md="3">
                <Form.Label>Position:</Form.Label>
                  <Form.Control 
                      required
                      name="position"
                      type="text"
                      placeholder="Position" 
                      value={position || ""}
                      onChange={handlePositionChange}
                      />  
              </Form.Group>
            </Form.Row>
            <Form.Row>
                <Form.Group as={Col} md="5">
                    <Table striped hover variant="light">
                      <thead>
                          <tr>
                              <th>Zugewiesene Rollen</th>
                          </tr>
                      </thead>
                      <tbody>
                        {
                          userRoles.map((role, index) =>(
                              <tr key={index}>
                                <td><Form.Control readOnly type="text" name={"Rolle"+ index} value={role.roleName}/></td>
                                <td><Button onClick={()=>removeRole(index)} id={role.roleName}>Entfernen</Button></td>
                              </tr>
                            )
                          )
                        }
                      </tbody>
                    </Table> 
                </Form.Group>
            </Form.Row>
            {/*<Availability/> */}
        </Card.Body>
        <Card.Footer style={{textAlign: "right"}}>
          <Button size="md" variant="success" type="submit">
            Mitarbeiter anlegen
          </Button>
        </Card.Footer>
      </Form>
    </Card>
  </Layout>
  )
}

export default observer(EmployeeAdd)


    

