import React, {useEffect, useState} from 'react'
import {Container} from "react-bootstrap";
import {get_request, post_request, router} from "../utils/ApiUtils";
import VideoComponent from "../components/video/Video";
import CommentSectionComponent from "../components/comment/CommentSection";

import './Video.css'

const Video = (props) => {

    const {authenticated, currentUser} = props

    const [video, setVideo] = useState(null)
    const [comments, setComments] = useState([])
    const [loading, setLoading] = useState(false)

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
       get_request(router.comment.ofVideoBySlug(props.match.params.slug, {depth: -1, size: 100}))
           .then(data => {
               setComments(data.reverse())
           })
           .catch(error => {
               console.log(error)
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

    const handleComment = (payload) => {
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
        <Container>
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
            />
        </Container>
    )
}

export default Video

