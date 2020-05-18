import React, { useState, useEffect } from "react"
import { Typeahead } from "react-bootstrap-typeahead"
import { getUsers, getEmployees, getCustomers, getProcedures, getRoles } from "./requests"
import { set } from "mobx"

function ObjectPicker({ DbObject, setState, state, initial }) {
    const [options, setOptions] = useState([])
    const [labelKey, setLabelKey] = useState("")
    const labels = {
        user: "Benutzer",
        employee: "Mitarbeiter",
        customer: "Kunde",
        procedure: "Prozedur",
        resource: "Ressource",
        resourceType: "Ressourcentyp",
        position: "Position"
    }

    useEffect(() => {
        console.log(DbObject)
        switch (DbObject) {
            case 'user':
            case 'employee': 
            case 'customer': getUserData(); break;
            case 'procedure': getProcedureData(); break;
            case 'resource': getResourceData(); break;
            case 'resourceType': getResourceTypeData(); break;
            case 'position': getPositionData(); break;
            case 'role': getRoleData();
        }
    }, [])

    async function getUserData() {
        let res = []
        switch (DbObject) {
            case 'user': res = await getUsers(); break;
            case 'customer': res = await getCustomers(); break;
            case 'employee': res = await getEmployees()
        }

        const result = res.data.map(item => {
            return {
                ...item,
                labelKey: item.firstName + " " + item.lastName
            }
        })
        setOptions(result)
        setLabelKey("labelKey")
    }

    async function getResourceData() {   //API missing

    }

    async function getResourceTypeData() { //API missing

    }

    async function getPositionData() {   //API missing
        data = [{id:"1", name:"Position1", description:"Erste Position"},{id:"2", name:"Position2", description:"Zweite Position"}]
        const result = data.map((item) => {
            return {
                ...item
            }
        })
    }

    async function getProcedureData() {
        const res = await getProcedures()
        const result = res.data.map(() => {
            return {
                ...item,
                labelKey: item.name
            }
        })
    }

    async function getRoleData() {
        const res = await getRoles()
        const result = res.data.map((item) => {
            return {
                ...item
            }   
        })
        setOptions(result)
        setLabelKey("name")
    }

    return (
        <React.Fragment>
            <Typeahead
                clearButton
                placeholder= {labels[DbObject] + " wählen"}
                //multiple
                options={options}
                id="basic-typeahead"
                onChange={setState}
                selected={state} //new
                labelKey={labelKey}
                selectHintOnEnter
            />
        </React.Fragment>
        
    )
}

export default ObjectPicker