import { observable, action, decorate } from "mobx"
import { getUserData } from "./components/navigationComponents/ActiveUserRequests"

class UserStore {
    user = null
    setUser(user) {
        this.user = user
    }

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
        const res = await getUserData(this.userID)
        console.log(res)
        this.setRoles(res.data.roles.map(item => {
                    return (item.name)

                }))
        /* const tempRights = []
        res.data.roles.forEach((item,index,array) => {
            item.rights.forEach((item, index) => {
                tempRights.push(item)
            })
        }) */
        var allRights = []
        res.data.roles.map((role) => {
            role.rights.map((right)=> {
                if(!allRights.some(allRights => allRights.id == right.id)){
                    allRights.push(right) //only push rights which are not in allRights
                }
            })
        })
        this.setUser(res.data)
        this.setRights(allRights) //without multiple rights
        this.setUsername(res.data.username)
        this.setUserID(res.data.id)
    }

    deleteCurrentUser() {
        this.setUsername(null)
        this.setUserID(null)
        this.setRights([])
        this.setRoles([])
    }
}

UserStore = decorate(UserStore, {
    user: observable,
    username: observable,
    userID: observable,
    rights: observable,
    roles: observable,
    loggedIn: observable,
    setUser: action,
    setRights: action,
    setRoles: action,
    setUsername: action,
    setUserID: action,
    setLoggedIn: action
})

export { UserStore }