import React, { useState, useEffect } from "react"
import { Typeahead } from "react-bootstrap-typeahead"
import { getUsers, getEmployees, getCustomers, getProcedures, getRoles, getPositions} from "./requests"

// when using this Object you have to give 4 props:
// DbObject: defining what Object you want to pick (select out of predefined list below)
// setState: the setState method for the state in your Form
// initial: an Array with the values which have to be initally selected
// multiple: defining whether multiple values can be selected

function ObjectPicker({ DbObject, setState, initial, multiple, ident }) {
    const [options, setOptions] = useState([])
    const [labelKey, setLabelKey] = useState("")
    const [selected, setSelected] = useState([])
    const [init, setInit] = useState(false)
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

    useEffect(() => {
        buildInitialValues()
    }, [init])

    function buildInitialValues() {
        if(initial){
            let init = []
            initial.some(item => {
                for(let i = 0; i < options.length; i++){
                    if(item.id == options[i].id){
                        init.push(options[i])
                    }
                }
            })
            setSelected(init)
        }
    }

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
        setInit(true)
    }

    async function getResourceData() {   //API missing
        setLabelKey("")
        setInit(true)
    }

    async function getResourceTypeData() { //API missing
        setLabelKey("")
        setInit(true)
    }

    async function getPositionData() {   //API missing
        const res = await getPositions()
        const result = res.data.map((item) => {
            return {
                ...item
            }
        })
        setOptions(result)
        setLabelKey("name")
        setInit(true)
    }

    async function getProcedureData() {
        const res = await getProcedures()
        const result = res.data.map(item => {
            return {
                ...item,
                labelKey: item.name
            }
        })
        setOptions(result)
        setLabelKey("name")
        setInit(true)
    }

    async function getRoleData() {
        const res = await getRoles()
        const result = res.data.map(item => {
            return {
                ...item
            }   
        })
        setOptions(result)
        setLabelKey("name")
        setInit(true)
    }

    const handleChange = event => {
        setSelected(event)
        if(!ident){
            setState(event)
        }else{
            setState({data: event, ident: ident, DbObject: DbObject})
        }
    }

    return (
        <React.Fragment>
            <Typeahead
                clearButton
                placeholder= {labels[DbObject] + " wählen"}
                multiple = {multiple || false}
                options={options}
                id="basic-typeahead"
                onChange={handleChange}
                selected={selected}
                labelKey={labelKey}
                selectHintOnEnter
            />
        </React.Fragment>
        
    )
}

export default ObjectPicker