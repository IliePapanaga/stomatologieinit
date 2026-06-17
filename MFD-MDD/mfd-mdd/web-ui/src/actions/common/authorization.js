import {RESPONSE_OK_STATUS} from '../../utils/Constants';
import Remote from '../../utils/Remote';
import ObjectHelper from '../../utils/Object';
import Error from '../../utils/Error';
import axios from 'axios';
import Cookies from "universal-cookie";

const cookies = new Cookies();

export function loadCurrentUser() {
    return new Promise((resolve) => {
        return Remote.executeQuery('currentAuthenticatedUserInfo{id roles status username, name{first last}, realUser{id roles status username, name{first last}}  }', function (status, userInfo) {
            switch (status) {
                case RESPONSE_OK_STATUS:
                    if (ObjectHelper.isObject(userInfo)) {
                        resolve(userInfo);
                    } else {
                        resolve(undefined);
                    }
                    break;
                default:
                    resolve(undefined);
            }
        }, function (response) {
            Error.showRemoteErrors(response);
            resolve(undefined);
        });
    });
}

export function login(login, password, rememberMe) {
    return function (dispatch) {
        let url = `login`;
        return axios({
            method: 'post',
            url: `${url}`,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-XSRF-TOKEN': cookies.get(' XSRF-TOKEN'),
                'X-Requested-With': 'XMLHttpRequest'
            },
            params: {
                username: login,
                password: password,
                'remember-me': rememberMe
            }
        }).then(function (response) {
            console.log(response);
            return true;
        })
            .catch(error => {
                Error.showRemoteErrors(error, url);
                return false
            })
    }
}

export function logout(login, password, client) {
    return function (dispatch) {
        return axios({
            method: 'post',
            url: `/logout`,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-XSRF-TOKEN': cookies.get(' XSRF-TOKEN'),
                'X-Requested-With': 'XMLHttpRequest'
            },
            params: {}
        }).then(function (response) {
            console.log(response);
            return true;
        })
            .catch(error => {
                Error.showRemoteErrors(error);
                return false
            })
    }
}

export function impersonate(username) {
    return function (dispatch) {
        let url = `impersonate`;
        return axios({
            method: 'post',
            url: `${url}`,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-XSRF-TOKEN': cookies.get(' XSRF-TOKEN'),
                'X-Requested-With': 'XMLHttpRequest'
            },
            params: {
                username: username
            }
        }).then(function (response) {
            console.log(response);
            return true;
        })
            .catch(error => {
                Error.showRemoteErrors(error, url);
                return false
            })
    }
}

export function exitFromImpersonate() {
    return function (dispatch) {
        return axios({
            method: 'post',
            url: `/impersonate/exit`,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-XSRF-TOKEN': cookies.get(' XSRF-TOKEN'),
                'X-Requested-With': 'XMLHttpRequest'
            },
            params: {}
        }).then(function (response) {
            if (response.status === RESPONSE_OK_STATUS) {
                return true;
            }
            return false;
        })
            .catch(error => {
                return false
            })
    }
}