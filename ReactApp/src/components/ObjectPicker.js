//author: Jonas Semmler
import React, { useState, useEffect, forwardRef, useImperativeHandle } from "react"
import { Typeahead } from "react-bootstrap-typeahead"
import { getUsers, getEmployees, getCustomers, getProcedures, getRoles, getPositions, getResourceTypes, getResources, getRestrictions, getPublicProcedures } from "./requests"
import { getTranslatedWarning, kindOfWarningList } from "./Warnings"

// when using this Object you have to give props:
// DbObject: defining what Object you want to pick (select out of predefined list below)
// setState: the setState method for the state in your Form
// initial: an Array with the values which have to be initally selected
// multiple: defining whether multiple values can be selected (as boolean)
// ident: a Object which just gets passed back with every change
// filter: In some cases you can filter the queried results by a type (e.g. resources of a certain type)
// status: specifies the desired system status of the selectable inputs
// disabled: Boolean whether you can edit the Input


const ObjectPicker = forwardRef((props, ref) => {
    let { DbObject, setState, initial, multiple, ident, filter, status, disabled } = props
    if(status) status = status.toUpperCase()
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
    }, [initial, init])


    useImperativeHandle(ref, () => ({
        resetSelected() {
            setSelected([])
        }
    }))

    function buildInitialValues() {
        if (initial) {
            let init = []
            initial.some(item => {
                if (item) {
                    for (let i = 0; i < options.length; i++) {
                        if (item.id != undefined) { //array contains objects with ids
                            if (item.id == options[i].id) {
                                init.push(options[i])
                            }
                        } else { //array containts strings
                            if (item == options[i]) {
                                init.push(options[i])
                            }
                        }

                    }
                }
            })
            setSelected(init)
        }
    }

    async function getUserData() {
        let res = []
        switch (DbObject) {
            case 'user': res = await getUsers(status); break;
            case 'customer': res = await getCustomers(status); break;
            case 'employee': res = await getEmployees(status,filter); break;
        }
        //attach labelKey
        const result = res.data.map(item => {
            return {
                ...item,
                labelKey: item.firstName + " " + item.lastName
            }
        })
        //filter for Anonymous and Admin users
        for (let i = 0; i < result.length; i++) {
            if (result[i].firstName === null) {
                result.splice(i, 1)
                i -= 1
            }
        }
        setOptions(result)
        setLabelKey("labelKey")
        setInit(true)
    }


    async function getResourceData() {
        const {data} = await getResources(status,filter)
        setOptions(data)
        setLabelKey("name")
        setInit(true)
    }

    async function getResourceTypeData() {
        const {data} = await getResourceTypes(status)
        setOptions(data)
        setLabelKey("name")
        setInit(true)
    }

    async function getPositionData() {
        const {data} = await getPositions(status)
        setOptions(data)
        setLabelKey("name")
        setInit(true)
    }

    async function getProcedureData() {
        let result = []
        if(filter == "public"){
            const {data} = await getPublicProcedures(status)
            result = data
        }else{
            const {data} = await getProcedures(status)
            result = data
        }
        setOptions(result)
        setLabelKey("name")
        setInit(true)
    }

    async function getRoleData(userType) {
        console.log("role")
        let finalResult = []
        const res = await getRoles(status)
        res.data.map((item) => {
            //reduce the selection
            if (userType == "employee") {
                finalResult.push(item)
            } else { // customer
                console.log(item)
                if (item.name != "Admin_role") {
                    finalResult.push(item)
                }
            }
        })
        setOptions(finalResult)
        setLabelKey("name")
        setInit(true)
    }

    async function getRestrictionData() {
        const {data} = await getRestrictions()
        setOptions(data)
        setLabelKey("name")
        setInit(true)
    }

    async function getWarningData() {
        var allTranslatedWarnings = []
        kindOfWarningList.map(singleWarning => {
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