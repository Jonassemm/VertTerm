import React, { useEffect } from "react"
import {getAnonymousLogin} from "./ActiveUserRequests"
import { useHistory } from "react-router-dom"
import { observer } from "mobx-react"

function AnonymousLogin({credString,userStore}) {
    credString = atob(credString)
    const seperatorIndex = credString.indexOf(',')
    const history = useHistory()
    const username = credString.substring(0, seperatorIndex)
    const password = credString.substring(seperatorIndex + 1, credString.length)
    
    useEffect(async () => {
        try{
            const res = await getAnonymousLogin(username,password)
            userStore.setUserID(res.data)
            userStore.setLoggedIn(true)
            history.push("/appointments")
        }catch(error){
            console.log(error)
        }
    })

    return (
        <React.Fragment>
            <span>Sie werden in kürze weitergeleitet...</span>
        </React.Fragment>
    )
}

export default observer(AnonymousLogin)