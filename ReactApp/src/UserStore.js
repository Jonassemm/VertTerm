import { observable, action, decorate } from "mobx"
import { getUserData } from "./ActiveUserRequests"

class UserStore {
    username = null
    setUsername(username) {
        this.username = username;
    }

    userID = null
    setUserID(id) {
        this.userID = id
    }

    rights = []
    setRights(rights) {
        this.rights = rights
    }

    roles = []
    setRoles(roles) {
        this.roles = roles
    }

    loggedIn = false
    setLoggedIn(loggedIn) {
        if (!loggedIn) {
            this.deleteCurrentUser()
        } else {
            this.getData()
        }
        this.loggedIn = loggedIn
    }

    async getData() {
        try {
            await getUserData(this.userID)
                .then(res => {
                    this.setRoles(res.data.roles.map(item => {
                        return (item.name)

                    }))
                })
        } catch (error) {

        }
    }

    deleteCurrentUser() {
        this.setUsername(null)
        this.setUserID(null)
        this.setRights([])
        this.setRoles([])
    }
}

UserStore = decorate(UserStore, {
    username: observable,
    userID: observable,
    rights: observable,
    roles: observable,
    loggedIn: observable,
    setRights: action,
    setRoles: action,
    setUsername: action,
    setUserID: action,
    setLoggedIn: action
})

export { UserStore }