import React from 'react';
import {connect} from 'react-redux';
import {toastr} from 'react-redux-toastr';
import {reduxForm} from 'redux-form';
import Field from '../../common/form/Field';

import {PASSWORD_PATTERN_REGEX} from '../../../../utils/Constants';
import {matchPassword} from '../../../../utils/Validators';

import BasePage, {basePageDispatcherMap, basePageStateMap} from '../../common/BasePage';
import resetPassword from '../../../../actions/common/security/password';
import {Logger} from 'react-logger-lib';
import Footer from '../../common/page/Footer';

const renderField = (field) => (
  <div class="reset__pass-main-input">
    <input {...field.input} {...field} />
    <div>
      <svg class="input__icon">
        <use xlinkHref="#Password"></use>
      </svg>
    </div>
  </div>
)


let Form = props => {
  const { handleSubmit, handleCancel, invalid } = props;

  return (
    <form onSubmit={handleSubmit}>


      <div class="reset__pass">

        <div class="modal__header">
          <h2>Please reset your password</h2>
        </div>


        <div class="reset__pass-main">

          <div class="reset__pass-main-header">
            <h3>Create New Password</h3>
          </div>

          <div class="reset__pass-main-img">
            <img src="img/reset-password.png" alt="reset" />
          </div>
          <div class="reset__pass-main-text">
            <div class="help">
              <p>Password must be at least 10 characters long with characters from 3 of these 4 categories:</p>
              <ul>
                <li>upper case letters</li>
                <li>lower case letters</li>
                <li>numbers</li>
                <li>special characters, i.e "#,!,$,%" etc.</li>
              </ul>
            </div>
          </div>

          <div class="reset__pass-main-input-wrapper">
            <div class="for__pass-btn">
            <Field name="password" type="password" placeholder="New Password*" autocomplete="off" component={renderField} required={true} minLength={10} maxLength={60} regexPattern={PASSWORD_PATTERN_REGEX} />
              <span class="pass__hide"></span>
              <span class="pass__show hide"></span>
            </div>
            <div class="for__pass-btn">
            <Field name="repassword" type="password" placeholder="Confirm Password*" autocomplete="off" component={renderField} validate={matchPassword} required={true} minLength={10} maxLength={60} regexPattern={PASSWORD_PATTERN_REGEX} />
            <span class="pass__hide"></span>
            <span class="pass__show hide"></span>
          </div>
          </div>

          <div class="reset__pass-help">
            <p class="mandatory">Fields marked* are mandatory</p>
          </div>

        </div>


        <div class="footer__btn">
          <button class="blue white" onClick={handleCancel}>Cancel</button>
          <button class="blue" disabled={invalid}>Ok</button>
        </div>


      </div>
    </form >)
}

Form = reduxForm({
  form: 'resetPassword'
})(Form);

/**
 * Main application component. Containts base navigation panels and base view
 */
class Reset extends BasePage {
  submit(data) {
    let me = this;
    Logger.of('App.Reset.submit').info('Reset data:', data);

    me.props.onResetPassword(me.props.token, data.password, me.props.router);
  }
  cancel() {
    let me = this;
    me.props.router.navigateToUrl("/");
  }
  render() {
    return ([
      <Form onSubmit={this.submit.bind(this)} handleCancel={this.cancel.bind(this)} page={this} key="mainForm" />,
      <div class="logo__absolute">
        <img src="img/logo-abs.png" alt="logo" />
      </div>,
      <Footer key="footer" />
    ]);
  }
}


const ResetConnector = connect(function (state, ownProps) {
  return Object.assign(basePageStateMap(state, ownProps), {
  });
},
  function (dispatch) {
    return Object.assign(basePageDispatcherMap(dispatch), {
      onResetPassword: (token, password, router) => {
        dispatch(resetPassword(token, password)).then(function (result) {
          if (result) {
            toastr.success("Resset Password", "Password was changed successful");
            router.navigateToUrl("/");
          }
        });
      }
    });
  })(Reset);

export default ResetConnector;