import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';

import {matchPassword, validateEmail} from '../../../../utils/Validators';
import {PASSWORD_PATTERN_REGEX, TEXTFIELD_PATTERN_REGEX} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import checkUserName from "../../../../actions/signup/checkUserName";
import {phone} from "../../../../utils/Normalizers";

export const dialogInfo = {}

let Form = props => {
    const {handleSubmit, handleCancel, invalid} = props;
    return (
        <form onSubmit={handleSubmit}>

            <div class="modal__gray-main modal27">
                <div class="item__box">
                    <div class="text">
                        <p>First Name<span class="star">*</span>:</p>
                    </div>
                    <Field name="managedObject.contact.name.first" autocomplete="off" component={TextField}
                           required={true} minLength={2} maxLength={60}
                           regexPattern={TEXTFIELD_PATTERN_REGEX}/>
                </div>
                <div class="item__box">
                    <div class="text">
                        <p>Last Name<span class="star">*</span>:</p>
                    </div>
                    <Field name="managedObject.contact.name.last" autocomplete="off" component={TextField}
                           required={true} minLength={2} maxLength={60}
                           regexPattern={TEXTFIELD_PATTERN_REGEX}/>
                </div>
                <div class="item__box">
                    <div class="text">
                        <p>Email<span class="star">*</span>:</p>
                    </div>
                    <Field name="username" autocomplete="off" component={TextField}
                           validate={validateEmail}
                           required={true} minLength={6} maxLength={254}/>
                </div>
                <div class="item__box">
                    <div class="text">
                        <p>Phone<span class="star">*</span>:</p>
                    </div>
                    <Field name="managedObject.contact.phone" autocomplete="off" component={TextField} required={true}
                           mask="(999) 999-9999" maskChar=" "
                           normalize={phone}/>
                </div>
                <div className="item__box">
                    <div className="text">
                        <p>Password<span className="star">*</span>:</p>
                    </div>
                    <Field name="password" type="password" autocomplete="off" component={TextField}
                           required={true} minLength={10} maxLength={60}
                           regexPattern={PASSWORD_PATTERN_REGEX}/>
                </div>

                <div className="item__box">
                    <div className="text">
                        <p>Re-type Password<span className="star">*</span>:</p>
                    </div>
                    <Field name="repassword" type="password" autocomplete="off" component={TextField}
                           validate={matchPassword} required={true} minLength={10} maxLength={60}
                           regexPattern={PASSWORD_PATTERN_REGEX}/>
                </div>

                <div class="item__box">
                    <p class="mandatory">Fields marked* are mandatory</p>
                </div>
            </div>

            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button className="blue white" type="button" onClick={handleCancel}>Cancel</button>
                    <button className="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>
                </div>
            </div>

        </form>)
}

Form = reduxForm({
    form: 'adduser',
    asyncValidate: checkUserName,
    asyncBlurFields: ['managedObject.username']
})(Form);

class AddEditUser extends BaseDialog {
    dialogProps() {
        return {
            width: 470,
            height: 340,
            className: "modal__gray",
            title: "Add User"
        }
    }

    /**
     * TODO change component for skiping this logic
     */
    convertData(managedObject) {
        return managedObject;
    }

    /**
     * TODO change component for skiping this logic
     */
    beforeSave(dialog, managedData) {

        managedData.managedObject.username = managedData.username;
        managedData.managedObject.password = managedData.password;
        managedData.managedObject = {systemUser: managedData.managedObject};

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}/>);
    }
}

const AddEditUserDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(AddEditUser);

export default AddEditUserDialogConnector;
