import React from 'react';
import {RouterView} from 'react-spa-router';


export class BaseRouterView extends RouterView {
    render() {
        return (
            [
                this.state.current ? <div key="base_router_view_current" className={this.state.currentClassName} style={{ position: 'static', width: '100%' }}>{this.state.current}</div> : null,
                this.state.previous ? <div key="base_router_view_previous" className={this.state.previousClassName}>{this.state.previous}</div> : null
            ]
        );
    }
}
