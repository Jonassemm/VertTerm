import React, {useState, useEffect} from 'react'
import {Card, Table,  Button,} from 'react-bootstrap';
import {useHistory} from 'react-router-dom';
import Layout from "./Layout"


import {observer} from "mobx-react"
import {
    getEmployeeList,
    removeEmployee
  } from "./UserRequests";
import { remove } from 'mobx';

export default function UserList(props) {
  
    const history = useHistory();
    const [userList, setUserList] = useState([])

    const handleUserListChange = data => setUserList(data)

    useEffect( () => {
        loadUserList()
    },[])
    
    //---------------------------------USER---------------------------------
    //LOAD USERLIST
    const loadUserList = async () => {
        var data = []
        try {
            const response = await getEmployeeList();
            data = response.data.map(user => {
                return {
                    ...user,
                }
            })
        }catch (error) {
            console.log(Object.keys(error), error.message)
            alert("An error occoured while loading userlist")
        }
        setUserList(data)
    }
    //REMOVE USER
    const removeUser = async  (index) => {
        try {
            console.log("AXIOS: removeUser()")
            console.log(userList[index])
            console.log(userList[index].id)
            await removeEmployee(userList[index].id);
          } catch (error) {
            console.log(Object.keys(error), error.message)
            alert("An error occoured while adding a user")
          }

        userList.splice((index),1)
        setUserList([...userList])
 
      }

    //---------------------------------RENDERING---------------------------------
    //DYNAMIC TABLE
    function renderTableBody() {
        if (userList != null) {
            return userList.map((user, index) => {
                const {id, username, firstName, lastName, systemStatus} = user
                return (
                    <tr key={index}>
                        <td>{username}</td>
                        <td>{firstName}</td>
                        <td>{lastName}</td>
                        <td>{systemStatus}</td>
                        <td style={{width: "300px"}}>
                            <Button  onClick={() => history.push('/employee/edit/' + id)} style={{marginRight:"5px"}}>Ansicht</Button>
                            <Button onClick={() => {if(window.confirm('Wollen Sie diesen Benutzer wirklich entfernen?')){removeUser(index)};}}  id={id}>Entfernen</Button>
                        </td>
                    </tr>
                )
            })
        } else {
            return (
                <tr align="center">
                    <td colSpan="5">Kein Benutzer vorhanden</td>
                </tr>
            )
        }
    }

    /*
    function confirmDelet(index) {
        const handleClose = () => setShow(false);
        const handleDelete = () => removeUser(index);
        return (
            <>
              <Button variant="primary" onClick={handleShow}>
                Launch demo modal
              </Button>
        
              <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                  <Modal.Title>Benutzer entfernen</Modal.Title>
                </Modal.Header>
                <Modal.Body>Wollen Sie diesen Benutzer wirklich entfernen?</Modal.Body>
                <Modal.Footer>
                  <Button variant="secondary" onClick={handleClose}>
                    Close
                  </Button>
                  <Button variant="primary" onClick={handleDelete}>
                    Save Changes
                  </Button>
                </Modal.Footer>
              </Modal>
            </>
          );
    }*/

    return (
    <Layout>
        <Card className={"border border-dark bg-dark text-white"}>
            <Card.Header>{props.heading}</Card.Header>
            <Card.Body>
                <Table striped hover variant="dark">
                    <thead>
                        <tr>
                            <th>Benutzername</th>
                            <th>Nachname</th>
                            <th>Vorname</th>
                            <th>Status</th>
                            <th>AKTION</th>
                        </tr>
                    </thead>
                    <tbody>
                        {renderTableBody()}
                    </tbody>
                </Table>
            </Card.Body>
        </Card>
    </Layout>
   )
}