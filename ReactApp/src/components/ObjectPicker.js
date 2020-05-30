import React, { useState, useEffect } from "react"
import { Typeahead } from "react-bootstrap-typeahead"
import { getUsers, getEmployees, getCustomers, getProcedures, getRoles, getPositions, getResourcetypes, getResources} from "./requests"

// when using this Object you have to give 4 props:
// DbObject: defining what Object you want to pick (select out of predefined list below)
// setState: the setState method for the state in your Form
// initial: an Array with the values which have to be initally selected
// multiple: defining whether multiple values can be selected (as boolean)
// exclude: the element which is removed from the selection

function ObjectPicker({ DbObject, setState, initial, multiple, exclude = {id: -1} }) {
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
        position: "Position",
        restriction: "Einschränkung"
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
            case 'role': getRoleData(); break;
            case 'restriction': getRestrictionData();
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
        let finalResult = []
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
        //reduce the selection
        result.map((item) => {
            if(item.id != exclude.id && item.systemStatus != "deleted"){
                finalResult.push(item)
            }
        })
        setOptions(result)
        setLabelKey("labelKey")
        setInit(true)
    }

    async function getResourceData() {  
        let finalResult = []
        const res = await getResources()
        res.data.map((item) => {
            //reduce the selection
            if(item.id != exclude.id && item.status != "deleted"){
                finalResult.push(item)
            }
        }) 
        setOptions(finalResult)
        setLabelKey("name")
        setInit(true)
    }

    async function getResourceTypeData() { 
        const res = await getResourcetypes()
        const result = res.data.map((item) => {
            return {
                ...item
            }
        })
        setOptions(result)
        setLabelKey("name")
        setInit(true)
    }

    async function getPositionData() { 
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


    async function getRestrictionData() { //API missing
       /*  const res = await getRescriction()
        const result = res.data.map(item => {
            return {
                ...item
            }   
        })
        setOptions(result)
        setLabelKey("name") */
        setLabelKey("")
        setInit(true)
    }

    const handleChange = event => {
        setSelected(event)
        setState(event)
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