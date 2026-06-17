import React from 'react';
import {connect} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {reduxForm} from 'redux-form';
import {REST_API_PREFIX_GET_USER_PHOTO} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import UiView from '../../../../utils/UiView';

export const dialogModify = {
    references: ['languages']
    //fields: []
}

let Form = props => {
    const { handleSubmit, change, handleCancel, invalid } = props;
    const managedObject = props.initialValues.managedObject;
    const photoUrl = REST_API_PREFIX_GET_USER_PHOTO.replace('{0}', managedObject.id);
    const { languages } = props.references;
    //
    // <Field name="managedObject.comments" component={TextArea} required={false} />
    let changedZipCode = UiView.getFindByZipCodeHandler(change, 'managedObject.contact.address.');
    return (
        <form onSubmit={handleSubmit}>

                <div class="attention__main modal23">
                    <div class="attention__main-item">
                        <p>Are you sure to modify No Show?</p>
                        <div class="item__box">
                            <div class="text">
                                <p>Comment:</p>
                            </div>
                            <div class="input">
                                <textarea></textarea>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="footer__btn">
                    <button class="blue white">Cancel</button>
                    <button class="blue">Ok</button>
                </div>
        </form >)
}

Form = reduxForm({
    form: 'location'
})(Form);


class ModifyNoShowForProfessional extends BaseDialog {
    componentDidMount() {
        try {
            let extendedJs = require('../../../../resources/js/main');
            extendedJs.modalInitialization();
        } catch (ex) { }

    }
    dialogProps() {
        return {
            width: 470,
            height: 300,
            className: "modal__gray",
            title: "Modify No Show"
        }
    }
    beforeSave(dialog, managedData) {
        let jobPreference = managedData.managedObject.jobPreference,
            professional = managedData.managedObject;

        delete professional['jobPreference'];
        delete professional['profile'];

        managedData.managedObject = { jobPreference: jobPreference, professional: professional };

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this} initialValues={{ managedObject: this.props.managedObject }} references={this.props.references} />);
    }
}

const ModifyNoShowForProfessionalDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {
        });
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
    })(ModifyNoShowForProfessional);

export default ModifyNoShowForProfessionalDialogConnector;