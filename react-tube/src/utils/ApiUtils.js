import {API_BASE_URL, ACCESS_TOKEN} from '../constants/holder'

const request = (options) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    })
    
    if(localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options)
    .then(response => {

        if (response.status === 204){
            return Promise.resolve()
        }

        return response.json().then(json => {

                if(!response.ok) {
                    return Promise.reject(json);
                }
                return json;
            })
    }

    );
}


export function getCurrentUser() {
    if(!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/users/me",
        method: 'GET'
    });
}

export function login(loginRequest) {
    return request({
        url: API_BASE_URL + "/auth/login",
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function signup(signupRequest) {
    return request({
        url: API_BASE_URL + "/auth/signup",
        method: 'POST',
        body: JSON.stringify(signupRequest)
    });
}

export function post_request(url, body){
    return request({
        url: url,
        method: 'POST',
        body: JSON.stringify(body)
    })
}

export function get_request(url){
    return request({
        url: url,
        method: 'GET'
    })
}


const addParams = (url, params = {}) => {
    if (params) {
        let query = Object.keys(params)
            .map(param => encodeURIComponent(param) + '=' + encodeURIComponent(params[param]))
            .join('&');
        url += query;
    }
    return url;
}

export const router = {
    video: {
        getBySlug: (slug) => `${API_BASE_URL}/videos/${slug}`,
        post: () => `${API_BASE_URL}/videos`,
        recommendations: (params = {}) => addParams(`${API_BASE_URL}/videos/recs?`, params),
        like: (slug, action) => addParams(`${API_BASE_URL}/videos/${slug}/like?`, {action: action}),
        dislike: (slug, action) => addParams(`${API_BASE_URL}/videos/${slug}/dislike?`, {action: action})
    },
    comment:{
        ofVideoBySlug: (slug, params = {}) => addParams(`${API_BASE_URL}/comments?`, {slug: slug, ...params}),
        postBySlug: (slug) => addParams(`${API_BASE_URL}/comments?`, {slug: slug}),
        reply: (id) => `${API_BASE_URL}/comments/${id}/reply`
    },
    user : {
        subscribe: (id) => `${API_BASE_URL}/users/${id}/subscribe`,
        unsubscribe: (id) => `${API_BASE_URL}/users/${id}/unsubscribe`
    }
}