import React from 'react'
import './Home.css'
import Container from 'react-bootstrap/Container'
import { Col, Row } from 'react-bootstrap'

const Home = (props) => {


    return (
        <Container fluid>
            <Row>
                <Col>1 of 2</Col>
                <Col>2 of 2</Col>
            </Row>
            <Row>
                <Col>1 of 3</Col>
                <Col xs = {6}>2 of 3</Col>
                <Col>3 of 3</Col>
            </Row>
        </Container>
    )
}

export default Home