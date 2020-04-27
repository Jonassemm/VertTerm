import React, {useEffect} from "react"
import {Jumbo} from "./Jumbo"

export const Home = () => {

    useEffect(() => {
        console.log("mounting Home")
        return () => {
            console.log("unmounting Home")
        }
    })

    return (
    <div>
       <Jumbo/>
    </div>
    )
}
