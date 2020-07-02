import React, { useState, useEffect, forwardRef, useImperativeHandle } from "react"
import { Typeahead } from "react-bootstrap-typeahead"
import { getUsers, getEmployees, getCustomers, getProcedures, getRoles, getPositions, getResourcetypes, getResources, getRestrictions, getActiveUsers, getResourcesOfType, getEmployeesOfPosition, getWarnings } from "./requests"
import {getTranslatedWarning, kindOfWarningList} from "./Warnings"

// when using this Object you have to give 4 props:
// DbObject: defining what Object you want to pick (select out of predefined list below)
// setState: the setState method for the state in your Form
// initial: an Array with the values which have to be initally selected
// multiple: defining whether multiple values can be selected (as boolean)
// exclude: the element which is removed from the selection

const ObjectPicker = forwardRef((props, ref) => {
    let { DbObject, setState, initial, multiple, ident, selectedItem, filter,disabled } = props
    if (!selectedItem) selectedItem = { id: null }
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
        role: "Rolle",
        restriction: "Einschränkung",
        customerRole: "Rolle",
        employeeRole: "Rolle",
        activeUser: "Kunde",
        warning: "Konflikttyp"
    }

    useEffect(() => {
        switch (DbObject) {
            case 'user':
            case 'employee':
            case 'activeUser':
            case 'customer': getUserData(); break;
            case 'procedure': getProcedureData(); break;
            case 'resource': getResourceData(); break;
            case 'resourceType': getResourceTypeData(); break;
            case 'position': getPositionData(); break;
            case 'customerRole': getRoleData("customer"); break;
            case 'employeeRole': getRoleData("employee"); break;
            case 'restriction': getRestrictionData(); break;
            case 'warning': getWarningData();
        }
    }, [])

    useEffect(() => {
        buildInitialValues()
    }, [initial,init])


    useImperativeHandle(ref, () => ({
        resetSelected() {
            setSelected([])
        }
    }))

    function buildInitialValues() {
        if (initial) {
            let init = []
            initial.some(item => {
                for (let i = 0; i < options.length; i++) {
                    if(item.id != undefined) { //array contains objects with ids
                        if(item.id == options[i].id) {
                            init.push(options[i])
                        } 
                    }else { //array containts strings
                        if(item == options[i]){
                            init.push(options[i])
                        }
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
            case 'employee': {
                if (filter) {
                    res = await getEmployeesOfPosition(filter)
                } else {
                    res = await getEmployees()
                }; break;
            }
            case 'activeUser': res = await getActiveUsers()
        }
        const result = res.data.map(item => {
            return {
                ...item,
                labelKey: item.firstName + " " + item.lastName
            }
        })
        //filter for Anonymous and Admin user
        for (let i = 0; i < result.length; i++) {
            if ((result[i].username == "admin") || (result[i].username == "anonymousUser")) {
                result.splice(i, 1)
                i -= 1
            }
        }
        //reduce the selection
        result.map((item) => {
            if (item.id != selectedItem.id && item.systemStatus != "deleted") {
                finalResult.push(item)
            }
        })
        setOptions(finalResult)
        setLabelKey("labelKey")
        setInit(true)
    }


    async function getResourceData() {
        let res = {}
        if (filter) {
            res = await getResourcesOfType(filter)
        } else {
            res = await getResources()
        }
        const result = res.data.map(item => {
            return { ...item }
        })
        setOptions(result)
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

    async function getRoleData(userType) {
        let finalResult = []
        const res = await getRoles()
        res.data.map((item) => {
            //reduce the selection
            if (userType == "employee") {
                finalResult.push(item)
            } else { // customer
                if (item.name != "ADMIN_ROLE") {
                    finalResult.push(item)
                }
            }
        })
        setOptions(finalResult)
        setLabelKey("name")
        setInit(true)
    }

    async function getRestrictionData() {
        const res = await getRestrictions()
        const result = res.data.map(item => {
            return {
                ...item
            }
        })
        setOptions(result)
        setLabelKey("name")
        setInit(true)
    }

    async function getWarningData() {
        var allTranslatedWarnings = []
        kindOfWarningList.map(singleWarning =>{
            allTranslatedWarnings.push(getTranslatedWarning(singleWarning))
        })
        setOptions(allTranslatedWarnings)
        setInit(true)
    }

    const handleChange = event => {
        setSelected(event)
        if (!ident) {
            setState(event)
        } else {
            setState({ data: event, ident: ident, DbObject: DbObject })
        }
    }

    return (
        <React.Fragment>
            <Typeahead
                style={{ width: "100%" }}
                clearButton
                disabled={disabled}
                placeholder={labels[DbObject] + " wählen"}
                multiple={multiple || false}
                options={options}
                id="basic-typeahead"
                onChange={handleChange}
                selected={selected}
                labelKey={labelKey}
                selectHintOnEnter
            />
        </React.Fragment>

    )
})

export default ObjectPicker