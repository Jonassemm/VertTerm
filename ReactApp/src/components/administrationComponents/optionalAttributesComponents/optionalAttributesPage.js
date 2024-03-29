//author: Patrick Venturini
import React, { useState, useEffect } from "react"
import {getOptionalAttributes} from  "../../requests"
import OptionalAttributesForm from "./optionalAttributesForm"
import OverviewPage from "../../OverviewPage"


function optionalAttributesPage({userStore}) {
    const [optionalAttributeLists, setOptionalAttributeLists] = useState([])


    useEffect(() => {
        loadOptionalAttributes()
    }, [])


    //----------------------------------------LOAD-----------------------------------
    const loadOptionalAttributes = async () => {
        try{ 
          const {data} = await getOptionalAttributes();
          setOptionalAttributeLists(data)
        }catch (error) {
          console.log(Object.keys(error), error.message)
        }
      
    };


    //-----------------------------OverviewPage-Components---------------------------
    const tableBody =
        optionalAttributeLists.map((item,index) => {
            var attributeClass //translated boolean
            switch(item.classOfOptionalAttribut) {
                case "User":
                    attributeClass = "Benutzer"
                break;
                case "Customer":
                    attributeClass = "Kunde"
                break;
                case "Employee":
                    attributeClass = "Mitarbeiter"
                break;
                default: attributeClass = "UNDIFINED"
            }
            return ([
                index + 1,
                attributeClass]
            )
        })

        
    const modal = (onCancel,edit,selectedItem) => {
        return (
            <OptionalAttributesForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
                userStore={userStore}
            />
        )
    }


    return (
        <React.Fragment>
            <OverviewPage
                pageTitle="Optionale Attributlisten"
                newItemText="Neues Attribut"
                tableHeader={["#", "Attributklasse"]}
                tableBody={tableBody}
                modal={modal}
                modalSize="lg"
                data={optionalAttributeLists}
                refreshData={loadOptionalAttributes}
                withoutCreate={true}
            />
        </React.Fragment>
    )
}
export default optionalAttributesPage