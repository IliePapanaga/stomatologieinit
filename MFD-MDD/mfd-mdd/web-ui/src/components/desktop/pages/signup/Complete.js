import React from 'react';
import {connect} from 'react-redux';
import {toastr} from 'react-redux-toastr';
import BasePage, {basePageDispatcherMap, basePageStateMap} from '../../common/BasePage';
import sendWelcomeMailAgain from '../../../../actions/signup/sendWelcomeMailAgain';
import BaseRecaptcha from '../../common/security/BaseRecaptcha';
import Footer from '../../common/page/Footer';

/**
 * Main application component. Containts base navigation panels and base view
 */
class Complete extends BasePage {
    sendWelcomeMailAgain(recaptchaHash) {
        this.props.sendWelcomeMailAgain(localStorage.getItem('id'), recaptchaHash);
    }
    render() {
        let {router} = this.props,
            professioanl = localStorage.getItem('professional'),
            title = `Create a Mayday Dental Staffing Account`;
        if (!professioanl) {
            title += ` for employers`;
        }
        return ([
            <div class="signup__finish" key="content">

                <div class="modal__header ">
                    <h2>{title}</h2>
                </div>

                <div class="signup__finish-img">
                    <img src="img/sentMail.png" alt="sentMail" />
                </div>

                <div class="signup__finish-text">
                    <p>Confirm your email address. We have just sent you a confirmation email to <a>{localStorage.getItem('email')}.</a>Click in
                the cofirmation link in the email to complete your sign up</p>
                </div>

                <div class="signup__finish-link">
                    <p>{this.props.recaptchaHash && <a onClick={this.sendWelcomeMailAgain.bind(this, this.props.recaptchaHash)}>Didn't get the email?</a>}</p>
                    <div class="signup__practice-btn">
                        <BaseRecaptcha />
                    </div>
                </div>

                <div class="signup__practice-btn">
                    <button class="blue" onClick={function () { router.navigateToUrl("/"); } }>Ok</button>
                </div>

            </div>,
            <div class="logo__absolute" key="logo">
                <img src="img/logo-abs.png" alt="logo" />
            </div>,
            <Footer key="footer" />
        ]);
    }
}


const CompleteConnector = connect(function (state, ownProps) {
    return Object.assign(basePageStateMap(state, ownProps), {
        recaptchaHash: state.context.recaptchaHash
    });
},
    function (dispatch) {
        return Object.assign(basePageDispatcherMap(dispatch), {
            sendWelcomeMailAgain: (id, recaptchaHash) => {
                dispatch(sendWelcomeMailAgain(id, recaptchaHash)).then(function (result) {
                    if (result) {
                        toastr.success("Send Welcome Mail Again", "Welcome Mail was sent again.");
                    }
                });
            }
        });
    })(Complete);

export default CompleteConnector;