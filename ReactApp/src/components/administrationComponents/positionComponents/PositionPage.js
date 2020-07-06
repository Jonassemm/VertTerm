//author: Patrick Venturini
import React, { useState, useEffect } from "react"
import { getAllPositions } from "./PositionRequests"
import PositionForm from "./PositionForm"
import OverviewPage, {modalTypes} from "../../OverviewPage"


function PositionPage({userStore}) {
    const [positions, setPositions] = useState([])


    useEffect(() => {
        loadPositions()
    }, [])


    //----------------------------------LOAD-------------------------------
    const loadPositions = async () => {
        var data = [];
        try{ 
          const response = await getAllPositions();
          data = response.data;
        }catch (error) {
          console.log(Object.keys(error), error.message)
        }

        //don't save object with status="deleted"
        var reducedData = []
        data.map((singlePosition) => {
            if(singlePosition.status != "deleted") {
                reducedData.push(singlePosition)
            }
        })
        setPositions(reducedData);
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