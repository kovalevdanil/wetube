import React, {useEffect, useState} from 'react'
import './Home.css'
import Container from 'react-bootstrap/Container'
import { Col, Row } from 'react-bootstrap'
import {get_request, router} from "../utils/ApiUtils";
import {Link} from "react-router-dom";

const Home = (props) => {


    const [videos, setVideos] = useState([])
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        setLoading(true)
        get_request(router.video.recommendations())
           .then(vs => {
               setVideos(vs)
               setLoading(false)
           }).catch(error => {
                console.log(error) // TODO handle
                setLoading(false)
            })
    }, [])

    if (loading){
        return <Container>Loading...</Container>
    }

    return (
        <Container className='align-items-center'>
            <Row>
                {videos.map(video => (
                    <Link to={`/video/${video.slug}`} >
                        <video width = '320' height = '180'>
                            <source src = {video.url}/>
                        </video>
                        <div>{video.title.substring(0, 20)}</div>

                    </Link>
                ))}
            </Row>
        </Container>
    )
}

export default Home