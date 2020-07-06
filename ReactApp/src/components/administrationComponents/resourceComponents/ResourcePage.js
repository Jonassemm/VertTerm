//author: Patrick Venturini
import React, { useState, useEffect } from "react"
import OverviewPage, {modalTypes} from "../../OverviewPage"
import ResourceForm from "./ResourceForm"
import {getAllResources} from "./ResourceRequests"
import {ExceptionModal} from "../../ExceptionModal"


function ResourcePage({userStore}) {
    const [resources, setResources] = useState([])
    const [exception, setException] = useState(null)
    const [showExceptionModal, setShowExceptionModal] = useState(false)


    useEffect(() => {
        loadResources()
    }, [])


    //-----------------------------ExceptionModal-----------------------------
    const handleExceptionChange = (newException) => {
        setException(newException)
        setShowExceptionModal(true)
    }


    //---------------------------------LOAD-----------------------------------
    const loadResources= async () => {
        var data = [];
        try{ 
            const response = await getAllResources();
            data = response.data;
        }catch (error) {
            console.log(Object.keys(error), error.message)
        }
        
        //don't save object with status="deleted"
        var reducedData = []
        data.map((singleResource) => {
            if(singleResource.status != "deleted") {
                reducedData.push(singleResource)
            }
          })
        setResources(reducedData);
    }


    //------------------------OverviewPage-Components--------------------------
    const tableBody =
        resources.map((item, index) => {
            var status //translated status
            switch(item.status) {
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
            var price 
            if(item.pricePerUnit >= 0) {
                price = (item.pricePerUnit/100) + " €"
            } 
            var type = ""
            item.resourceTypes.map((singleType, index) => {
                if(index == 0) {
                    type += singleType.name
                } else {
                    type += ", " + singleType.name
                }
            })
            return (
                [index + 1,
                item.name,
                item.description,
                type,
                item.amountInStock,
                price,
                status]
            )
        })


    const modal = (onCancel, edit, selectedItem) => {
        return (
            <ResourceForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
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
            <OverviewPage 
                pageTitle="Ressourcen"
                newItemText="Neue Ressource"
                tableHeader={["#", "Bezeichnung", "Beschreibung", "Ressourcentyp", "Bestandsmenge", "Einzelpreis", "Status"]}
                tableBody={tableBody}
                modal={modal}
                data={resources}
                refreshData={loadResources} 
                userStore={userStore}
                modalType={modalTypes.resource}
                modalSize="xl"
            />
        </React.Fragment>
    )
}
export default ResourcePage