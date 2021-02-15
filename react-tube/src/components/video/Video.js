import React from 'react'
import {Image,Container, Col, Row, Button} from "react-bootstrap";

import './Video.css'

const VideoComponent = ({authenticated, currentUser, channel, video, onLikeClick, onDislikeClick, onSubscribeClick}) => {

    debugger

    const likeClass = video.liked ? 'video-rate-set' : ''
    const dislikeClass = video.disliked ? 'video-rate-set' : ''

    return (
        <Container>
            <Row>
                <video className = 'video-player' src = {video.url} controls/>
            </Row>
            <Row>
                <h2>{video.title}</h2>
            </Row>
            <Row>
                <Col>
                    <p>
                        <span className = 'video-views' >{video.views + ' '} views</span>
                        <span className = 'video-upload-date' >{video.uploadDate}</span>
                    </p>
                </Col>
                <Col md='auto' className='align-middle'>
                    <span className = {'video-rate like ' + likeClass} onClick = {onLikeClick}>Likes: {video.likeCount}</span>
                    <span className = {'video-rate dislike ' + dislikeClass} onClick = {onDislikeClick}>Dislikes: {video.dislikeCount}</span>
                </Col>
            </Row>
            <Row md = 'auto'>
                <div className = 'splitter'/>
            </Row>
            <Row>
                <Col md='auto'>
                    <Image className = 'video-channel-avatar' src = {channel.imageUrl} roundedCircle/>
                </Col>
                <Col>
                    <Row className = 'video-channel-username'>
                        {channel.username}
                    </Row>
                    <Row className = 'video-channel-subs-count'>
                        {channel.subscriberCount} subs
                    </Row>
                </Col>
                <Col md='auto'>
                    <Button variant= {channel.subscribed ?'danger' :'outline-danger'} onClick = {() => onSubscribeClick(channel.id, channel.subscribed)}>{channel.subscribed ? 'Subscribed' : 'Subscribe'}</Button>
                </Col>
            </Row>
            <Row>
                <Col>
                    <p>{video.description}</p>
                </Col>
            </Row>
            <Row md = 'auto'>
                <div className = 'splitter'/>
            </Row>
        </Container>
    )
}

export default VideoComponent