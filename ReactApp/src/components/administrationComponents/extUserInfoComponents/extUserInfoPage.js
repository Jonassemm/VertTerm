import React, { useState, useEffect } from "react"
//import { getAllExtUserInfo } from "./extUserInfoRequests"
import ExtUserInfoForm from "./ExtUserInfoForm"
import OverviewPage from "../../OverviewPage"

function ExtUserInfoPage() {
    const [extUserInfo, setExtUserInfo] = useState([])

    useEffect(() => {
        loadExtUserInfo()
    }, [])

    const loadExtUserInfo = async () => {
        var data = [];
        try{ 
          //const response = await getAllExtUserInfo();
          data = response.data;
        }catch (error) {
          console.log(Object.keys(error), error.message)
          //alert("An error occoured while loading all extUserInfo")
          data = [{id:"1", name:"E-Mail", isRequired: true},{id:"2", name:"Telefon-Nr.", isRequired: false}]
        }
        setExtUserInfo(data);
    };

    const tableBody =
        extUserInfo.map((item, index) => {
            var isRequired //translated boolean
            switch(item.isRequired) {
                case true:
                    isRequired = "Ja"
                break;
                case false:
                    isRequired = "Nein"
                break;
                default: isRequired = "UNDIFINED"
            }
            return ([
                index + 1,
                item.name,
                isRequired]
            )
        })

    const modal = (onCancel,edit,selectedItem) => {
        return (
            <ExtUserInfoForm
                onCancel={onCancel}
                edit={edit}
                selected={selectedItem}
            />
        )
    }

    return (
        <React.Fragment>
            <OverviewPage
                pageTitle="Erweiterte Benutzerinformationen"
                newItemText="Neues Attribut"
                tableHeader={["#", "Bezeichnung", "Pflichtfeld"]}
                tableBody={tableBody}
                modal={modal}
                data={extUserInfo}
                refreshData={loadExtUserInfo}
            />
        </React.Fragment>
    )
}

export default ExtUserInfoPage