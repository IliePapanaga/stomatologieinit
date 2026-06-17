import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';
import {TEXTFIELD_PATTERN_REGEX} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import {phone} from "../../../../utils/Normalizers";

export const dialogInfo = {
    //fields: []
}

let Form = props => {
    const {handleSubmit, handleCancel, invalid} = props;
    const {managedObject} = props.initialValues;
    return (
        <form onSubmit={handleSubmit}>

            <div class="modal__gray-main modal28">
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
                        <p>Phone<span class="star">*</span>:</p>
                    </div>
                    <Field name="managedObject.contact.phone" autocomplete="off" component={TextField} required={true}
                           mask="(999) 999-9999" maskChar=" "
                           normalize={phone}/>
                </div>
                <div class="item__box">
                    <div class="title">
                        <p>Email<span class="star">*</span>:</p>
                    </div>
                    <div class="data">
                        <p>{managedObject.contact.email}</p>
                    </div>
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
    form: 'location'
})(Form);


class EditUser extends BaseDialog {
    dialogProps() {
        return {
            width: 470,
            height: 340,
            className: "modal__gray",
            title: "Edit User"
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
        managedData.managedObject = {contact: managedData.managedObject.contact, id: managedData.managedObject.id};
        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}/>);
    }
}

const EditUserDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
            onSend: (sendData, d, form) => {
                // dispatch(requestResetPassword(sendData.email, form.recaptchaHash)).then(function (result) {
                //     if (result) {
                //            toastr.success("Resset Password", "Please, check your email, we sent you request for reset your password.");
                //         dispatch({ type: EVENT_CHANGED_RECAPTCHA_HASH, recaptchaHash: undefined });
                //         form.handleCancel();
                //     }
                // });
            }
        });
    })(EditUser);

export default EditUserDialogConnector;
