import React, {useState} from 'react'
import {Redirect, Link} from 'react-router-dom'
import {GOOGLE_AUTH_URL, GITHUB_AUTH_URL} from '../../constants/holder'
import googleLogo from '../../img/google-logo.png'
import { signup } from '../../utils/ApiUtils'

import './Signup.css'

const SocialSignup = (props) => {
    return (
        <div className="social-signup">
            <a className="btn btn-block social-btn google" href={GOOGLE_AUTH_URL}>
                <img src={googleLogo} alt="Google" /> Sign up with Google</a>
        </div>
    )
}

const SignupForm = (props) => {
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')

    
    const handleEmailChange = (e) => {
        setEmail(e.target.value)
    }

    const handlePasswordChange = (e) => {
        setPassword(e.target.value)
    }

    const handleUsernameChange = (e) => {
        setUsername(e.target.value)
    }


    const handleSubmit = (e) => {
        e.preventDefault()

        const signupRequest = {
            email: email,
            password: password,
            username: username
        }

        signup(signupRequest).then(Response => {
            props.history.push("/login")
        }).catch(error => console.log("Something went wrong"))
    }


    return (
        <form onSubmit = {handleSubmit}>
            <div className = 'form-item'>
                <input type = 'text' name = 'username' placeholder = 'Username' className = 'form-control'
                    value = {username} onChange = {handleUsernameChange} required/>
            </div>
            <div className = 'form-item'>
                <input type = 'email' name = 'email' placeholder = 'Email' className = 'form-control'
                    value = {email} onChange = {handleEmailChange} required/>
            </div>
            <div className = 'form-item'>
                <input type = 'password' name = 'password' placeholder = 'Password' className = 'form-control'
                    value = {password} onChange = {handlePasswordChange} required/>
            </div>

            <button className = 'btn btn-block btn-primary' type ='submit'>Sign Up</button>
        </form>
    )
}

export const Signup = (props) => {
    const {authenticated} = props

    if (authenticated){
        return <Redirect 
            to = {{
                pathname: '/',
                state: {from: props.locaiton}
            }} 
        />
    }

    return (
        <div className = "signup-container">
            <div className = 'signup-content'>
                <h1 className = 'signup-title'> Signup to Tube</h1>
                <SocialSignup />
                <div className="or-separator">
                    <span className="or-text">OR</span>
                </div>
                <SignupForm {...props}/>
                <span className = 'login-link'>Already have an account? <Link to = '/login'>Login!</Link></span>
            </div>
        </div>
    )
}

export default Signup