//author: Patrick Venturini
import React, {useState, useEffect} from 'react'
import OverviewPage, {modalTypes} from "../../OverviewPage"
import UserForm from "./UserForm"
import {ExceptionModal} from "../../ExceptionModal"

import {
    getEmployeeList,
    getCustomerList
  } from "./UserRequests";


export default function UserPage({userStore, userType}) {
    const [userList, setUserList] = useState([])
    const [exception, setException] = useState(null)
    const [showExceptionModal, setShowExceptionModal] = useState(false)


    useEffect( () => {
        loadUserList()
    },[])


    const handleExceptionChange = (newException) => {
        setException(newException)
        setShowExceptionModal(true)
    }


    //---------------------------------LOAD---------------------------------
    const loadUserList = async () => {
        var data = []
        switch(userType) {
            case "employee":
                try {
                    const response = await getEmployeeList();
                    data = response.data.map(user => {
                        return {
                            ...user,
                        }
                    })
                }catch (error) {
                    console.log(Object.keys(error), error.message)
                }
            break;
            case "customer":
                try {
                    const response = await getCustomerList();
                    data = response.data.map(user => {
                        return {
                            ...user,
                        }
                    })
                }catch (error) {
                    console.log(Object.keys(error), error.message)
                }
            break;
          }

        //don't save object with status="deleted"
        var reducedData = []
        data.map((singleUser) => {
            if(singleUser.systemStatus != "deleted") {
                reducedData.push(singleUser)
            }
        })
        setUserList(reducedData)
    }


    //------------------------OverviewPage-Components-----------------------
    const tableBody = 
        userList.map((item, index) => { 
            var status //translated status
            switch(item.systemStatus) {
                case "active":
                    status = "Aktiviert"
                break;
                case "inactive":
                    status = "Deaktiviert"
                break;
                case "deleted":
                    status = "Gelöscht"
                break;
                default: status = "UNDIFINED"
            }
            return ([
                index + 1,
                item.username,
                item.lastName,
                item.firstName,
                status]
            )
        })


    const modalEmployee = (onCancel,edit,selectedItem) => {
        return (
            <UserForm //EmployeeForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
                type={"employee"}
                setException={handleExceptionChange}
                userStore={userStore}
            />
        )
    }


    const modalCustomer = (onCancel,edit,selectedItem) => {
        return (
            <UserForm //CustomerForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
                type={"customer"}
                setException={handleExceptionChange}
                userStore={userStore}
            />
        )
    }


    return (
        <React.Fragment>
            {exception != null && 
                <ExceptionModal
                    showExceptionModal={showExceptionModal} 
                    setShowExceptionModal={setShowExceptionModal} 
                    exception={exception}
                    warning={"AvailabilityWarning"}
                />
            }
            {userType == "employee" ? 
                <OverviewPage
                    pageTitle="Mitarbeiter"
                    newItemText="Neuer Mitarbeiter"
                    tableHeader={["#", "Benutzername", "Nachname", "Vorname", "Status"]}
                    tableBody={tableBody}
                    modal={modalEmployee}
                    data={userList}
                    modalSize="xl"
                    refreshData={loadUserList}
                    userStore={userStore}
                    modalType={modalTypes.user}
                /> :
                <OverviewPage
                    pageTitle="Kunden"
                    newItemText="Neuer Kunde"
                    tableHeader={["#", "Benutzername", "Nachname", "Vorname", "Status"]}
                    tableBody={tableBody}
                    modal={modalCustomer}
                    data={userList}
                    modalSize="xl"
                    refreshData={loadUserList}
                    userStore={userStore}
                    modalType={modalTypes.user}
                />
            }
        </React.Fragment>
   )
}