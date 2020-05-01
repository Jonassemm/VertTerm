import React, {useState, useEffect} from 'react'
import {Card, Table,  Button,} from 'react-bootstrap';
import {useHistory} from 'react-router-dom';
import Layout from "./Layout"


import {observer} from "mobx-react"
import {
    getEmployeeList
  } from "./requests";

export default function UserList(props) {
  
    const history = useHistory();
    const [userList, setUserList] = useState([  {id: 1, username: "albeeins", forename: "Albert", surename: "Einstein", systemstatus: "aktiv"},
                                                {id: 2, username: "maricuri", forename: "Marie", surename: "Curie", systemstatus: "aktiv"},
                                                {id: 3, username: "julicaes", forename: "Julius", surename: "Caesar", systemstatus: "deativiert"}])

    const handleUserListChange = data => setUserList(data)

    useEffect( () => {
        getUserList()
    })

    function renderTableBody() {
        if (userList != null) {
            return userList.map((user, index) => {
                const {id, username, surename, forename, systemstatus} = user
                return (
                    <tr key={index}>
                        <td>{id}</td>
                        <td>{username}</td>
                        <td>{forename}</td>
                        <td>{surename}</td>
                        <td>{systemstatus}</td>
                        <td style={{width: "300px"}}>
                            <Button  onClick={() => history.push('/employee/edit/' + id)} style={{marginRight:"5px"}}>Ansicht</Button>
                            <Button onClick={()=>removeUser(index)} id={id}>Entfernen</Button>
                        </td>
                    </tr>
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
    
    const getUserList = async () => {
        const response = await getEmployeeList();
        const users = response.data.map(user => {
            return {
                ...user,
            }
        })
    }

    

    const removeUser = (index) => {
        console.table(userList)
        console.log("remove nr.:" + index)
        console.log("role:" + userList[index])
        userList.splice((index),1)
        setUserList([...userList])
        console.table(userList)
      }
   

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
                        {renderTableBody()}
                    </tbody>
                </Table>
            </Card.Body>
        </Card>
    </Layout>
   )
}