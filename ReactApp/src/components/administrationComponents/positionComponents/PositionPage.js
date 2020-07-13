//author: Patrick Venturini
import React, { useState, useEffect } from "react"
import {getPositions } from "../../requests"
import PositionForm from "./PositionForm"
import OverviewPage, {modalTypes} from "../../OverviewPage"

function PositionPage({userStore}) {
    const [positions, setPositions] = useState([])


    useEffect(() => {
        loadPositions()
    }, [])


    //----------------------------------LOAD-------------------------------
    const loadPositions = async () => {
        try{ 
          const {data} = await getPositions("notdeleted");
          setPositions(data);
        }catch (error) {
          console.log(Object.keys(error), error.message)
        }
    };

    //--------------------------OverviewPage-Components--------------------
    const tableBody =
        positions.map((item, index) => {
            return ([
                index + 1,
                item.name,
                item.description]
            )
        })


    const modal = (onCancel,edit,selectedItem) => {
        return (
            <PositionForm
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
                pageTitle="Positionen"
                newItemText="Neue Position"
                tableHeader={["#", "Bezeichnung", "Beschreibung"]}
                tableBody={tableBody}
                modal={modal}
                data={positions}
                refreshData={loadPositions}
                userStore={userStore}
                modalType={modalTypes.position}
            />
        </React.Fragment>
    )
}

export default PositionPage