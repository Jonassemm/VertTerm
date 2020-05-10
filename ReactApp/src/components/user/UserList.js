import React, {useState, useEffect, Fragment} from 'react'
import {Card, Table, Button, Modal} from 'react-bootstrap';
import {useHistory} from 'react-router-dom';
import Layout from "./Layout"


import {observer} from "mobx-react"
import {
    getEmployeeList,
    removeEmployee
  } from "./UserRequests";
import { remove } from 'mobx';

export default function UserList(props) {
  
    const [showRemoveConfirmModal, setShowRemoveConfirmModal] = useState(false)
    const [removeIndex, setRemoveIndex] = useState(null)

    const history = useHistory();
    const [userList, setUserList] = useState([])

    const handleRemoveIndexChange = data => {setRemoveIndex(data.target.value); showModal();}
    

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
            userList.splice((index),1)
            setUserList([...userList])
          } catch (error) {
            console.log(Object.keys(error), error.message)
            alert("An error occoured while removing a user")
          }
      }

    const hideModal = () => {
        setShowRemoveConfirmModal(false)
    }

    const showModal = () => {
        console.log("MODAL")
        setShowRemoveConfirmModal(true)
    }

    const handleRemoveUser = () => {
        removeUser(removeIndex)
        setShowRemoveConfirmModal(false)
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
                        <td>{systemStatus == 0 ? "Deaktiviert":"Aktiviert"}</td>
                        <td style={{width: "300px"}}>
                            <Button  onClick={() => history.push('/employee/edit/' + id)} style={{marginRight:"5px"}}>Ansicht</Button>
                            {//<Button onClick={() => {if(window.confirm('Wollen Sie diesen Benutzer wirklich entfernen?')){removeUser(index)};}}  id={id}>Entfernen</Button>
                            }<Button onClick={handleRemoveIndexChange} value={index} id={id}>Entfernen</Button>
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

    return (
    <Layout>
        <div className="page">
            <Modal show={showRemoveConfirmModal} onHide={hideModal}>
                    <Modal.Header closeButton>
                        <Modal.Title>Benutzer entfernen</Modal.Title>
                    </Modal.Header>

                    <Modal.Body>
                        <p>Soll der Benutzer wirklich gelöscht werden? </p>
                    </Modal.Body>

                    <Modal.Footer>
                        <Button variant="secondary"onClick={hideModal}>Nein</Button>
                        <Button variant="primary" onClick={handleRemoveUser}>Ja</Button>
                    </Modal.Footer>
            </Modal>
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
        </div>
    </Layout>
   )
}