import React, {useState, useEffect} from 'react'
import {Link} from 'react-router-dom'
import {login} from '../../utils/ApiUtils'
import { Redirect } from 'react-router-dom'
import { ACCESS_TOKEN , GOOGLE_AUTH_URL, GITHUB_AUTH_URL} from '../../constants/holder'
import googleLogo from '../../img/google-logo.png'

import './Login.css'

const SocialLogin = (props) => {
    return (
            <div className="social-login">
                <a className="btn btn-block social-btn google" href={GOOGLE_AUTH_URL}>
                    <img src={googleLogo} alt="Google" /> Log in with Google</a>
            </div>
    )
}

const LoginForm = (props) => {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')

    const handleEmailChange = (e) => {
        setEmail(e.target.value)
    }

    const handlePasswordChange = (e) => {
        setPassword(e.target.value)
    }

    const handleSubmit = (e) => {
        e.preventDefault()

        const loginRequest = {
            email : email,
            password: password
        }

        login(loginRequest).then(response => {
            localStorage.setItem(ACCESS_TOKEN, response.accessToken)
            props.history.push('/')
        }).catch(error => 
            console.log("something went wrong")
        )
    }


    return (
        <form onSubmit = {handleSubmit}>
            <div className = 'form-item'>
                <input type = "email" name = 'email' className = 'form-control' placeholder = 'Email'
                    value = {email} onChange = {handleEmailChange} required/>
            </div>
            <div className = 'form-item'>
                <input type = 'password' name = 'password' className = 'form-control' placeholder = 'Password'
                    value = {password} onChange = {handlePasswordChange} required/>
            </div>
            <div className = 'form-item'>
                <button type = 'submit' className = 'btn btn-block btn-primary'>Login</button>
            </div>
        </form>
    )
}

const Login = (props) => {

    const {authenticated} = props

    useEffect(() => {
        if (props.location.state && props.location.state.error){
            setTimeout(() => {
                props.history.replace({
                    pathname: props.location.pathname,
                    state: {}
                })
            }, 100)
        }
    }, [])

    if (authenticated){
        return <Redirect 
            to = {{
                pathname: "/",
                state: {from: props.location}
            }} 
            />
    }

    return (
        <div className = 'login-container'>
            <div className = 'login-content'>
                <h1 className = 'login-title'>Login to Tube</h1>
                <SocialLogin />
                <div className="or-separator">
                    <span className="or-text">OR</span>
                </div>
                <LoginForm {...props}/>
                <span className="signup-link">New user? <Link to="/signup">Sign up!</Link></span>
            </div>
        </div>
    )
}

export default Login
