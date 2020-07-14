//author: Patrick Venturini
import React, { useState, useEffect } from "react"
import {getRestrictions } from "../../requests"
import RestrictionForm from "./RestrictionForm"
import OverviewPage from "../../OverviewPage"


function RestrictionPage({userStore}) {
    const [restrictionsa, setRestrictions] = useState([])


    useEffect(() => {
        loadRestrictions()
    }, [])


    //--------------------------------LOAD----------------------------------
    const loadRestrictions = async () => {
        try{ 
          const {data} = await getRestrictions();
          setRestrictions(data);
        }catch (error) {
          console.log(Object.keys(error), error.message)
        }
    };


    //----------------------OverviewPage-Components-------------------------
    const tableBody =
        restrictionsa.map((item, index) => {
            return ([
                index + 1,
                item.name]
            )
        })


    const modal = (onCancel,edit,selectedItem) => {
        return (
            <RestrictionForm
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
                pageTitle="Einschränkungen"
                newItemText="Neue Einschränkung"
                tableHeader={["#", "Bezeichnung"]}
                tableBody={tableBody}
                modal={modal}
                data={restrictionsa}
                refreshData={loadRestrictions}
            />
        </React.Fragment>
    )
}
export default RestrictionPage