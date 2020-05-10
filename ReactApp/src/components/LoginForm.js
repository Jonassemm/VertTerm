import React, { useState, useEffect, useRef } from "react"
import Form from "react-bootstrap/Form"
import Button from "react-bootstrap/Button"
import Overlay from "react-bootstrap/Overlay"
import Popover from "react-bootstrap/Popover"
import { observer } from "mobx-react"
import { getLogin, getLogout } from "../ActiveUserRequests"


function LoginForm({ userStore }) {
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [inputError, setInputError] = useState(false)
    const target = useRef()

    const handleUsernameChange = ev => {
        setUsername(ev.target.value)
    }

    const handlePasswordChange = ev => {
        setPassword(ev.target.value)
    }

    const handleSubmit = async event => {
        setInputError(false)
        event.preventDefault()
        if (!userStore.loggedIn) {
            await getLogin(username, password)
                .then(res => {
                    if (res.status = "200") {
                        userStore.setUserID(res.data)
                        userStore.setLoggedIn(true)
                    }
                })
                .catch(() => {
                    setInputError(true)
                    clearInputFields()
                }
                )
        } else {
            await getLogout()
                .then(res => {
                    if (res.status == "200") {
                        userStore.setLoggedIn(false)
                    }
                })
                .catch(() => {
                    console.log(error)
                })
        }
    }

function clearInputFields() {
    setUsername("")
    setPassword("")
}

useEffect(() => {
    clearInputFields()
}, [userStore.loggedIn])

const popover = (
    <Popover id="popover-basic">
        <Popover.Title as="h3">Falscher Login</Popover.Title>
        <Popover.Content style={{ color: "#FF0000" }}>
            Falscher Benutzername oder falsches Passwort!
            </Popover.Content>
    </Popover>
)

return (
    <div>

        <div id="form">
            <Form inline onSubmit={handleSubmit}>
                {!userStore.loggedIn ?
                    <Form.Group>
                        <Form.Control
                            required
                            className="mr-sm-2"
                            type="text"
                            placeholder="Benutzername"
                            value={username || ''}
                            onChange={handleUsernameChange}
                        />
                        <Form.Control
                            required
                            className="mr-sm-2"
                            type="password"
                            placeholder="Passwort"
                            value={password || ''}
                            onChange={handlePasswordChange}
                        />
                    </Form.Group>
                    : null}
                <Button ref={target} type="submit">{!userStore.loggedIn ? "Login" : "Logout"}</Button>
                <Overlay target={target} show={inputError} placement="bottom">
                    <Popover id="popover-basic">
                        <Popover.Title as="h3">Falscher Login!</Popover.Title>
                        <Popover.Content style={{ color: "#FF0000" }}>
                            Falscher Benutzername oder falsches Passwort!
                            </Popover.Content>
                    </Popover>
                </Overlay>
            </Form>
        </div>
    </div>
)
}

export default observer(LoginForm)