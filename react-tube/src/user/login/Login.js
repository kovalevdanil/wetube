import React, {useState, useEffect} from 'react'
import {Link} from 'react-router-dom'
import {login} from '../../utils/ApiUtils'
import { Redirect } from 'react-router-dom'
import {Alert, Container, Form, Button} from 'react-bootstrap'

import { ACCESS_TOKEN , GOOGLE_AUTH_URL} from '../../constants/holder'
import googleLogo from '../../img/google-logo.png'

import {GoogleLoginButton} from "react-social-login-buttons";

import './Login.css'

const SocialLogin = (props) => {
    return (
            <div className="social-login">
                <a href = {GOOGLE_AUTH_URL}>
                    <GoogleLoginButton />
                </a>
            </div>
    )
}

const LoginForm = (props) => {

    const [state, setState] = useState({
        email: '',
        password: ''
    })


    const [alertState, setAlertState] = useState({
        show: false,
        message: ''
    })

    const setShowAlert = (visible) =>
        setAlertState(prev => {
            return {
                ...prev,
                show: visible
            }
        })

    const handleInputChange = (e) => {
        const {name, value} = e.target

        setState(prev => ({
            ...prev,
            [name] : value
        }))
    }

    const handleSubmit = (e) => {
        e.preventDefault()

        const loginRequest = {
            email : state.email,
            password: state.password
        }

        login(loginRequest).then(response => {
            localStorage.setItem(ACCESS_TOKEN, response.accessToken)
            props.history.push('/')
        }).catch(error => {
            setAlertState({
                show: true,
                message: error.message
            })
        })
    }

    return (
        <Form className = "login-form" onSubmit = {handleSubmit}>
            <Form.Group controlId='email'>
                <Form.Control
                    type = 'email'
                    placeholder = 'Email'
                    name = 'email'
                    onChange={handleInputChange}
                    required
                />
            </Form.Group>

            <Form.Group controlId='password'>
                <Form.Control
                    type = 'password'
                    placeholder = 'Password'
                    name = 'password'
                    onChange = {handleInputChange}
                    required
                />
            </Form.Group>
            {alertState.show &&
            <Alert variant = 'danger' dismissible onClose = {() => setShowAlert(false)}>
                <Alert.Heading>Error</Alert.Heading>
                <p>Message from the server: {alertState.message}</p>
            </Alert>}
            <Button variant = 'primary' type = 'submit' block>
                Login
            </Button>
        </Form>
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
        <Container className = 'login-container'>
            <Container className = 'login-content'>
                <h2 className='login-title'>Login to Tube</h2>
                <SocialLogin />
                <hr/>
                <LoginForm {...props}/>
                <span className = 'signup-link'>Don't have an account? <Link to = '/signup'>Sign Up!</Link></span>
            </Container>
        </Container>
    )
}

export default Login
