import React from 'react'
import './Home.css'

const Home = (props) => {


    return (
        <div className="home-container">
            <div className="container">
                {props.authenticated && <div> Hello, {props.currentUser.username}</div>}
                <div className="graf-bg-container">
                    <div className="graf-layout">
                        <div className="graf-circle"></div>
                        <div className="graf-circle"></div>
                        <div className="graf-circle"></div>
                        <div className="graf-circle"></div>
                        <div className="graf-circle"></div>
                        <div className="graf-circle"></div>
                        <div className="graf-circle"></div>
                        <div className="graf-circle"></div>
                        <div className="graf-circle"></div>
                        <div className="graf-circle"></div>
                        <div className="graf-circle"></div>
                    </div>
                </div>
                <h1 className="home-title">Soical Demo</h1>
            </div>
        </div>
    )
}

export default Home