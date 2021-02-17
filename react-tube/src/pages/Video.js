import React, {useEffect, useState} from 'react'
import {Container, Col, Row} from "react-bootstrap";
import {get_request, post_request, router} from "../utils/ApiUtils";
import VideoComponent from "../components/video/Video";
import CommentSectionComponent from "../components/comment/CommentSection";

import './Video.css'
import {Link} from "react-router-dom";
import {strip} from "../utils/StringUtils";

const COMMENT_PAGE_SIZE = 5

const Video = (props) => {

    const {authenticated, currentUser} = props

    const [video, setVideo] = useState(null)
    const [videos, setVideos] = useState([])
    const [comments, setComments] = useState([])
    const [commentsLoaded, setCommentsLoaded] = useState(false)
    const [loading, setLoading] = useState(false)

    const [commentCurrentPage, setCommentCurrentPage] = useState(0)

    useEffect(() => {
        setLoading(true)
        get_request(router.video.getBySlug(props.match.params.slug))
            .then(data => {
                setVideo(data)
                setLoading(false)
            })
            .catch(error => {
                console.log(error)
                // props.history.push("/")
            })
    }, [])

    useEffect(() => {
       get_request(router.comment.ofVideoBySlug(props.match.params.slug, {depth: -1, size: COMMENT_PAGE_SIZE, page: commentCurrentPage}))
           .then(data => {
               setComments(data)
               setCommentCurrentPage(prev => prev + 1)
           })
           .catch(error => {
               console.log(error)
           })
    }, [])

    useEffect(() => {
        get_request(router.video.recommendations())
            .then(vs => {
                setVideos(vs)
            }).catch(error => {
                console.log(error) // TODO handle
            })
    }, [])

    if (loading || video === null){
        return <Container><h2>Wait for it...</h2></Container>
    }

    const handleLike = () => {

        const wasDisliked = video.disliked
        const wasLiked = video.liked

        const action = wasLiked ? 'unset' : 'set'

        post_request(router.video.like(props.match.params.slug, action ))
            .then(response => {
                setVideo(prevVideo => {
                    return {
                        ...prevVideo,
                        likeCount: wasLiked ? prevVideo.likeCount - 1 : prevVideo.likeCount + 1,
                        dislikeCount: wasDisliked ? prevVideo.dislikeCount - 1 : prevVideo.dislikeCount,

                        liked: !wasLiked,
                        disliked: false
                    }
                })
            })
            .catch(error => {
                console.log(error) // TODO handle
            })
    }

    const handleDislike = () => {

        const wasDisliked = video.disliked
        const wasLiked = video.liked

        const action = wasDisliked ? 'unset' : 'set'

        post_request(router.video.dislike(props.match.params.slug, action))
            .then(response => {
                setVideo(prevVideo => {
                    debugger
                    return {
                        ...prevVideo,
                        dislikeCount: wasDisliked ? prevVideo.dislikeCount - 1 : prevVideo.dislikeCount + 1,
                        likeCount: wasLiked ? prevVideo.likeCount - 1 : prevVideo.likeCount,

                        disliked: !wasDisliked,
                        liked: false
                    }
                })
            }).catch(error => {
                console.log(error) // TODO handle
        })
    }

    const handleCommentLike = (id, action) => {

    }

    const handleComment = (payload, replyTo = null) => {

        if (replyTo){
            post_request(router.comment.reply(replyTo.id), payload)
                .then(comment => {

                })

            return
        }

        post_request(router.comment.postBySlug(props.match.params.slug), payload)
            .then(comment => {
                debugger
                setComments(prev => {
                    return [comment].concat(prev)
                })
            }).catch(error => {
                console.log(error)
        })
    }

    const handleLoadComments = () => {
        get_request(router.comment.ofVideoBySlug(props.match.params.slug, {
            size: COMMENT_PAGE_SIZE,
            page: commentCurrentPage,
            depth: -1
        })).then(data => {
                setComments(prev => prev.concat(data))
                setCommentCurrentPage(prev => prev + 1)
                setCommentsLoaded(data.length < COMMENT_PAGE_SIZE)
        }).catch(console.log)
    }

    const handleSubscribe = (channelId, wasSubscribed) => {
        if (!channelId){
            console.log('channel id is no defined')
            return
        }

        post_request(router.user.subscribe(channelId))
            .then(result => {
                setVideo(prev => {
                    const prevChannel = {...prev.channel}
                    prevChannel.subscribed = !wasSubscribed
                    prevChannel.subscriberCount = prevChannel.subscriberCount + (wasSubscribed ? -1 : 1)
                    return {
                        ...prev,
                        channel: prevChannel
                    }
                })
            }).catch(console.log) // TODO handle

    }

    return (
        <Container className='video-page-container' fluid>
            <Row >
                <Col sm = {8}>
                    <VideoComponent
                        authenticated={authenticated}
                        currentUser={currentUser}
                        video = {video}
                        channel = {video.channel}
                        onLikeClick={handleLike}
                        onDislikeClick={handleDislike}
                        onSubscribeClick={handleSubscribe}
                    />
                    <CommentSectionComponent
                        comments = {comments}
                        currentUser = {currentUser}
                        authenticated = {authenticated}
                        leaveComment = {handleComment}
                        withLoadMore = {!commentsLoaded}
                        onLoadMore = {handleLoadComments}
                        commentsTotalCount = {video.commentCount}
                    />
                </Col>
                <Col sm = {4} className = 'recommendations' >
                    {videos.map(video => (
                        <Link to={`/video/${video.slug}`} className = 'align-self-center recommendations-video'>
                            <Row>
                                <Col md = 'auto'>
                                    <video width = '208' height = '103'>
                                        <source src = {video.url}/>
                                    </video>
                                </Col>
                                <Col className = 'recommendations-video-info'>
                                    <Row className = 'recommendations-video-title'>
                                        {strip(video.title, 20)}
                                    </Row>
                                    <Row className = 'recommendations-video-description'>
                                        {strip(video.description, 40)}
                                    </Row>
                                    <Row className = 'recommendations-video-date' md = 'auto'>
                                        {video.views} views • {video.uploadDate}
                                    </Row>
                                </Col>
                            </Row>
                        </Link>
                    ))}
                </Col>
            </Row>
        </Container>
    )
}

export default Video

