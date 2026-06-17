import React, {Component} from 'react';
import {connect} from 'react-redux';
import Recaptcha from 'react-grecaptcha';
import {getSiteKey} from '../../../../actions/common/security/recaptcha';
import {baseDispatcherMap, baseStateMap} from '../BaseDialog';
import {EVENT_CHANGED_RECAPTCHA_HASH} from '../../../..//utils/Constants';

import {Logger} from 'react-logger-lib';

class BaseRecaptcha extends Component {

    state = {
        sitekey: undefined
    }

    async componentDidMount() {
        let me = this;

        const siteKey = await getSiteKey();

        me.setState({ sitekey: siteKey });

    }

    render() {
        const {sitekey} = this.state;
        const verifyCallback = response=> {
            Logger.of('App.BaseRecaptcha.verifyCallback').info('Captcha hash:', response);
            this.props.onChangedRecaptcha(response);
        }
        const expiredCallback = () => {
        Logger.of('App.BaseRecaptcha.verifyCallback').info('Expired');
            this.props.onChangedRecaptcha(null);    
        }
        return (<div>{sitekey &&<Recaptcha
            sitekey={sitekey}
            callback={verifyCallback}
            expiredCallback={expiredCallback}
            locale="us-US"
            className="customClassName"
            />}</div>);
    }
}
const BaseRecaptchaConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {
        });
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
            onChangedRecaptcha: (recaptchaHash) => {
                 dispatch({ type: EVENT_CHANGED_RECAPTCHA_HASH, recaptchaHash: recaptchaHash });
            }
        });
    })(BaseRecaptcha);

export default BaseRecaptchaConnector;
