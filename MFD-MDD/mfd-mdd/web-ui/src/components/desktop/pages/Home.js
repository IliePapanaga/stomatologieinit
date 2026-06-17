import React from 'react';
import {connect, Provider} from 'react-redux';
import BasePage, {basePageDispatcherMap, basePageStateMap} from '../common/BasePage';
import UiView from '../../../utils/UiView';
import {loadCurrentUser, login} from '../../../actions/common/authorization';
import {EVENT_AUTH_USER_LOADED, ROOT_PATH} from '../../../utils/Constants';
import Chooser from '../dialogs/signup/Chooser';
import Footer from '../common/page/Footer';
import ForgotPassword from '../dialogs/signup/ForgotPassword';
import {reduxForm} from 'redux-form';
import Field from '../common/form/Field';
import Checkbox from '../common/form/Checkbox';
import PasswordField from "../common/form/PasswordField";

let LoginForm = props => {
  const { handleSubmit, forgotPassword, signup, invalid } = props;

  return (
    <form onSubmit={handleSubmit}>
      <div class="login" key="login">
        <div class="login__header"><img src="img/logo-abs2.png" alt="logo"></img></div>
        <div class="login__main">
          <div class="login__main-item">
            <Field name="username" autocomplete="off" component="input" required={true} placeholder="Email" />
            <div><svg class="input__icon"><use xlinkHref="#email"></use></svg></div>
          </div>

            <Field classField="login__main-item pass" buttonsWrapperClass="for__pass-btn for__pass-btn-login" hasValidation={false} name="password" autocomplete="off" /*component="input"*/ required={true} type="password" placeholder="Password" component={PasswordField}/>


            {false&&<div class="login__main-item pass">
            <Field name="password" autocomplete="off" component="input" required={true} type="password" placeholder="Password" />
            <div><svg class="input__icon"><use xlinkHref="#Password"></use></svg></div>

            <div class="for__pass-btn">
              <span class="pass__hide"></span>
              <span class="pass__show hide"></span>
            </div>

          </div>}
          <div class="login__main-text">

            <div class="login__main-text-item">
              <Field name="rememberMe" component={Checkbox} title="Remember me" />
            </div>
            <div class="login__main-text-item">
              <a onClick={forgotPassword}>Forgot password?</a>
            </div>
          </div>
        </div>
        <div class="login__footer">
          <div class="login__footer-btn">
            <button disabled={invalid}>Log in</button>
          </div>
          <div class="login__footer-link">
            <p>Don’t have an account?<a class="login__signup-link modal__window" onClick={signup}>Sign Up</a></p>
          </div>
        </div>
      </div>
    </form >)
}

LoginForm = reduxForm({
  form: 'login'
})(LoginForm);


class Home extends BasePage {

  login(data) {
    this.props.onLogin(data.username, data.password, data.rememberMe, this);
  }

  signup() {
    let router = this.props.router;
    UiView.showDialog(<Chooser store={UiView.createDialogStore()} managedObject={{}} actions={{ goTo: function (url) { router.navigateToUrl(url); } }} />);

  }

  forgotPassword() {
    UiView.showDialog(<Provider store={UiView.createDialogStore()}><ForgotPassword managedObject={{}} actions={{}} /></Provider>);
  }

  render() {
    return (
      [<LoginForm onSubmit={this.login.bind(this)} forgotPassword={this.forgotPassword.bind(this)} signup={this.signup.bind(this)} page={this} key="loginForm" />,
      <div class="logo__absolute" key="logo">
        <img src="img/logo-abs.png" alt="logo" />
      </div>,
      <Footer key="footer" />
      ]);
  }
}


const HomeConnector = connect(
  function (state, ownProps) {
    return Object.assign(basePageStateMap(state, ownProps), {
      context: state.context
    });
  },
  function (dispatch) {
    return Object.assign(basePageDispatcherMap(dispatch), {
      onLogin: (username, password, rememberMe, dialog) => {
        dialog.props.router.navigateToUrl(ROOT_PATH);
        dispatch(login(username, password, rememberMe)).then(function (result) {
          loadCurrentUser().then(currentUser => {
            dispatch({ type: EVENT_AUTH_USER_LOADED, currentUser: currentUser });
          });
        });

      }
    });
  })(Home);

export default HomeConnector;