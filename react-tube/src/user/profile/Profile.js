import React from 'react'

const Profile = (props) => {

    const {currentUser} = props

    console.log(currentUser)

    return <div>
        <h1>Profile</h1>

        <div>{currentUser.username}</div>
        <div>{currentUser.email}</div>
        <div>{currentUser.id}</div>
        <img src = {currentUser.imageUrl} alt = 'avatar' />
    </div> 
}

export default Profile