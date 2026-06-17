import React from 'react';
import {viewRender} from 'react-spa-router';

import Home from '../components/desktop/pages/Home';
import Main from '../components/desktop/pages/Main';
import PracticeOwner from '../components/desktop/pages/signup/PracticeOwner';
import Professioanl from '../components/desktop/pages/signup/Professioanl';
import Complete from '../components/desktop/pages/signup/Complete';
import Reset from '../components/desktop/pages/account/Reset';
import ChangeUserName from '../components/desktop/pages/account/ChangeUserName';
import Activate from '../components/desktop/pages/signup/Activate';

import AppGuard from '../security/AppGuard';
import {ROOT_PATH} from '../utils/Constants';
import UiView from '../utils/UiView';


export const routes = [
    { path: ROOT_PATH, action: ({ route, router }) => viewRender(<Home router={router} />) },
    {
        path: '/signup',
        redirectTo:'/signup/practice-owner',
        children: [
            {
                path: 'practice-owner', 
                actions: [({ route, router }) => {UiView.closeDialog()},({ route, router }) => viewRender(<PracticeOwner router={router} />)]
            },
            {
                path: 'professional', 
                actions: [({ route, router }) => {UiView.closeDialog()},({ route, router }) => viewRender(<Professioanl router={router} />)]
            },
            {
                path: 'complete',
                actions: [
                    ({ router, route }) => viewRender(<Complete router={router}/>)
                ]
            },
            {
                path: 'activate/:token([A-Za-z0-9]+)', action: ({ route, router }) => viewRender(<Activate token={route.params.token} router={router} />) 
            }]
    },
    {
        path: '/account',
        redirectTo:'/reset',
        children: [
            {
                path: 'reset/:token([A-Za-z0-9]+)', action: ({ route, router }) => viewRender(<Reset token={route.params.token} router={router} />) 
            },
            {
                path: 'username/:token([A-Za-z0-9]+)', action: ({ route, router }) => viewRender(<ChangeUserName token={route.params.token} router={router} />) 
            }]
    },{
        path: `/:functionalAreaKey([a-z-]+)/:viewSetKey([a-z-]+)/:viewKey([a-z-]+)`, action: ({ route, router }) => viewRender(
            <Main
                functionalAreaKey={route.params.functionalAreaKey}
                viewSetKey={route.params.viewSetKey}
                viewKey={route.params.viewKey} router={router} />), canActivate: [AppGuard]
    },
    {
        path: '/:functionalAreaKey([a-z-]+)/:viewSetKey([a-z-]+)', action: ({ route, router }) => viewRender(
            <Main
                functionalAreaKey={route.params.functionalAreaKey}
                viewSetKey={route.params.viewSetKey} router={router} />), canActivate: [AppGuard]
    }
];