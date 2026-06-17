import React from 'react';
import {connect} from 'react-redux';
import {toastr} from 'react-redux-toastr';
import {ROOT_PATH} from '../../../../utils/Constants';
import BasePage, {basePageDispatcherMap, basePageStateMap} from '../../common/BasePage';
import confirmChangeUserName from '../../../../actions/common/security/confirmChangeUserName';
import {Logger} from 'react-logger-lib';
import Footer from '../../common/page/Footer';


/**
 * Main application component. Containts base navigation panels and base view
 */
class ChangeUserName extends BasePage {

  state = {
    activated: undefined
  }

  async componentDidMount() {
    let me = this;

    const activated = await confirmChangeUserName(this.props.token);
    
    Logger.of('App.ChangeUserName.componentDidMount').info('is activated:', activated);

    this.setState({ activated: activated });

    if (activated) {
      toastr.success("Change User Name", "User Name was changed successfuly!");

      setTimeout(function () {
        me.props.router.navigateToUrl(ROOT_PATH);
      }, 1000);

    } else {
      toastr.error("Change User Name", "You couldn't confirm changing your User Name. Please contact with administrator.");
    }
  }

  render() {
    return ([
      <div class="logo__absolute" key="logo">
        <img src="img/logo-abs.png" alt="logo"/>
      </div>,
      <Footer key="footer" />
    ]);
  }
}


const ChangeUserNameConnector = connect(function (state, ownProps) {
  return Object.assign(basePageStateMap(state, ownProps), {
  });
},
  function (dispatch) {
    return Object.assign(basePageDispatcherMap(dispatch), {
    });
  })(ChangeUserName);

export default ChangeUserNameConnector;