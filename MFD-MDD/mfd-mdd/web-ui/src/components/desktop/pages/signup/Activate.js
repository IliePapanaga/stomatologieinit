import React from 'react';
import {connect} from 'react-redux';
import {toastr} from 'react-redux-toastr';
import {ROOT_PATH} from '../../../../utils/Constants';
import BasePage, {basePageDispatcherMap, basePageStateMap} from '../../common/BasePage';
import completeRegistration from '../../../../actions/signup/completeRegistration';
import {Logger} from 'react-logger-lib';
import Footer from '../../common/page/Footer';

/**
 * Main application component. Containts base navigation panels and base view
 */
class Activate extends BasePage {

  state = {
    activated: undefined
  }

  async componentDidMount() {
    let me = this;

    const activated = await completeRegistration(this.props.token);

    Logger.of('App.Activate.componentDidMount').info('is activated:', activated);

    this.setState({ activated: activated });

    if (activated) {
      toastr.success("Account Activation Successful", "Thanks for signing up! Your new account is set up and ready to go.");
    } else {
      toastr.error("Account Activation Unsuccessful", "You couldn't activate the account. Please contact with administrator.");
    }

    setTimeout(function () {
      me.props.router.navigateToUrl(ROOT_PATH);
    }, 500);
  }

  render() {
    return ([
      <div class="logo__absolute" key="logo">
        <img src="img/logo-abs.png" alt="logo" />
      </div>,
      <Footer key="footer" />
    ]);
  }
}


const ActivateConnector = connect(function (state, ownProps) {
  return Object.assign(basePageStateMap(state, ownProps), {
  });
},
  function (dispatch) {
    return Object.assign(basePageDispatcherMap(dispatch), {
    });
  })(Activate);

export default ActivateConnector;