import React, { useState, useEffect } from "react"
import { Typeahead } from "react-bootstrap-typeahead"
import { getUsers, getEmployees, getCustomers, getProcedures, getRoles, getPositions, getResourcetypes, getResources, getRestrictions} from "./requests"

// when using this Object you have to give 4 props:
// DbObject: defining what Object you want to pick (select out of predefined list below)
// setState: the setState method for the state in your Form
// initial: an Array with the values which have to be initally selected
// multiple: defining whether multiple values can be selected (as boolean)
// exclude: the element which is removed from the selection

function ObjectPicker({ DbObject, setState, initial, multiple, ident, selectedItem = {id: null} }) {
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
        restriction: "Einschränkung"
    }

    useEffect(() => {
        switch (DbObject) {
            case 'user':
            case 'employee': 
            case 'customer': getUserData(); break;
            case 'procedure': getProcedureData(); break;
            case 'resource': getResourceData(); break;
            case 'childResource': getResourceData("childResource"); break;
            case 'resourceType': getResourceTypeData(); break;
            case 'position': getPositionData(); break;
            case 'customerRole': getRoleData("customer"); break;
            case 'employeeRole': getRoleData("employee"); break;
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
            if(item.id != selectedItem.id && item.systemStatus != "deleted"){
                finalResult.push(item)
            }
        })
        setOptions(result)
        setLabelKey("labelKey")
        setInit(true)
    }

    //REKURSIVE function to prevent setting a parent-resource "A" as a child-resource of his child-resource "B" (-> ChildOf(A) = B, ChildOf(B) = A) 
    function checkChildResources(resource, reference) {
        var feedback = true
        var results = [] // for each child

        if(resource.childResources.length > 0) { 
            resource.childResources.map(singleChild => {
                if(singleChild.id == reference.id) {
                    results.push(false) //save result 
                }else {
                    results.push(checkChildResources(singleChild, reference)) //save result and start recursion
                }
            })
            if(results.map(singleResult => {

                if(!singleResult){
                    feedback = false //overwrite feedback if resource has reference as a child resource
                }
            }))
            return feedback

        } else {
            return feedback // resource cannot contain the reference as a child resource
        }
    }

    async function getResourceData(call = "normal") {  
        let finalResult = []
        const res = await getResources()
        res.data.map((item) => {
            //reduce the selection
            if(item.id != selectedItem.id && item.status != "deleted"){
                if(call == "childResource" && selectedItem.id != null) { //reduce selection of all parent resources
                    if(checkChildResources(item, selectedItem)){
                        finalResult.push(item)
                    }
                } else { //normal selection
                    finalResult.push(item)
                }
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

    async function getRoleData(userType) {
        let finalResult = []
        const res = await getRoles()
        res.data.map((item) => {
            //reduce the selection
            if(userType == "employee") {
                finalResult.push(item)
            }else { // customer
                if(item.name != "ADMIN_ROLE"){
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