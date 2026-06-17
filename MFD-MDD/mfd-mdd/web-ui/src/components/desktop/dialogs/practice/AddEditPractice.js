import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import TextArea from '../../common/form/TextArea';
import ExSelectField from '../../common/form/ExSelectField';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';
import SelectField from '../../common/form/SelectField';
import {validateEmail} from '../../../../utils/Validators';
import {TEXTFIELD_PATTERN_REGEX, TITLES, WEBSITE_PATTERN_REGEX} from '../../../../utils/Constants';
import {STATES} from '../../../../data/States';
import {SPECIALTY} from '../../../../data/Specialty';
import {Logger} from 'react-logger-lib';
import {phone} from '../../../../utils/Normalizers';
import UiView from '../../../../utils/UiView';

export const dialogInfo = {
    //fields: []
}


let Form = props => {
    const {handleSubmit, change, handleCancel, invalid} = props;
    const {fullName, email} = props.initialValues;
    let changedZipCode = UiView.getFindByZipCodeHandler(change, 'managedObject.practiceOwner.contact.address.');
    return (
        <form onSubmit={handleSubmit}>

            <div class="profile__main modal29">

                <div class="profile__main-item">
                    <h3>Account Details</h3>
                    <div class="text__box-wrapper">
                    <div class="text__box">
                        <div class="title">Name:</div>
                        <div class="data">{fullName}</div>
                    </div>

                    <div class="text__box">
                        <div class="title">Email:</div>
                        <div class="data">{email}</div>
                    </div>
                    </div>
                    {false && <button class="blue" disabled={invalid}>Ok</button>}
                </div>

                <div class="profile__main-item">
                    <h3>Personal Details</h3>
                    <div class="profile__main-item-content-wrapper">
                        <div class="profile__main-item-left">
                            <div class="item__box">
                                <div class="text">
                                    <p>Title:</p>
                                </div>
                                <div class="input board__input-wrapper">
                                    <Field name="managedObject.practiceOwner.contact.name.title" component={SelectField}
                                           menuItems={TITLES} required={false}/>
                                </div>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>First Name<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practiceOwner.contact.name.first" autocomplete="off"
                                       component={TextField} required={true} minLength={2} maxLength={60}
                                       regexPattern={TEXTFIELD_PATTERN_REGEX}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Last Name<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practiceOwner.contact.name.last" autocomplete="off"
                                       component={TextField} required={true} minLength={2} maxLength={66}
                                       regexPattern={TEXTFIELD_PATTERN_REGEX}/>
                            </div>
                        </div>
                        <div class="profile__main-item-right">
                            <div class="item__box zip">
                                <div class="text">
                                    <p>ZIP<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practiceOwner.contact.address.zipCode" autocomplete="off"
                                       component={TextField} required={true} mask="99999" maskChar="_"
                                       onChange={changedZipCode}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Street<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practiceOwner.contact.address.street" autocomplete="off"
                                       component={TextField} required={true} minLength={2} maxLength={60}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>City<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practiceOwner.contact.address.city" autocomplete="off"
                                       component={TextField} required={true} minLength={2} maxLength={60}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>State<span class="star">*</span>:</p>
                                </div>
                                <div class="input board__input-wrapper">
                                    <Field name="managedObject.practiceOwner.contact.address.state"
                                           component={SelectField} menuItems={STATES} required={true}/>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>

                <div class="profile__main-item">
                    <h3>Billing Address Information</h3>
                    <div class="profile__main-item-content-wrapper">
                        <div class="profile__main-item-left">
                            <div class="item__box">
                                <div class="text">
                                    <p>Billing ZIP<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practice.billingAddress.zipCode" autocomplete="off"
                                       component={TextField} required={true} mask="99999" maskChar="_"
                                       onChange={UiView.getFindByZipCodeHandler(change, 'managedObject.practice.billingAddress.')}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Billing Street<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practice.billingAddress.street" autocomplete="off"
                                       component={TextField} required={true} minLength={2} maxLength={60}/>
                            </div>

                        </div>
                        <div class="profile__main-item-right">
                            <div class="item__box">
                                <div class="text">
                                    <p>Billing City<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practice.billingAddress.city" autocomplete="off"
                                       component={TextField} required={true} minLength={2} maxLength={60}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Billing State<span class="star">*</span>:</p>
                                </div>
                                <div class="input board__input-wrapper">
                                <Field name="managedObject.practice.billingAddress.state" autocomplete="off"
                                       component={SelectField} menuItems={STATES} required={true}/>
                            </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="profile__main-item">
                    <h3>Company Details</h3>
                    <div class="profile__main-item-content-wrapper">
                        <div class="profile__main-item-left">

                            <div class="item__box">
                                <div class="text">
                                    <p>Company Name<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practice.name" autocomplete="off" component={TextField}
                                       required={true} minLength={1} maxLength={60}/>
                            </div>
                            <div class="item__box">
                                <div class="text">
                                    <p>Company Phone<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practiceOwner.contact.phone" autocomplete="off"
                                       component={TextField} required={true} mask="(999) 999-9999" maskChar=" "
                                       normalize={phone}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Company Website:</p>
                                </div>
                                <Field name="managedObject.practice.webSite" autocomplete="off" component={TextField}
                                       required={false} minLength={2} maxLength={60}
                                       regexPattern={WEBSITE_PATTERN_REGEX}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Company Specialty<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practice.specialities" component={ExSelectField}
                                       multiple={true} required={false} menuItems={SPECIALTY}/>
                            </div>
                            <div class="item__box">
                                <div class="text">
                                    <p>Office Manager Name:</p>
                                </div>
                                <Field name="managedObject.practice.officeManagerName" autocomplete="off"
                                       component={TextField} required={false} minLength={1} maxLength={60}/>
                            </div>
                        </div>
                        <div class="profile__main-item-right">

                            <div class="item__box">
                                <div class="text">
                                    <p>Second Email:</p>
                                </div>
                                <Field name="managedObject.practiceOwner.contact.email" autocomplete="off"
                                       component={TextField} validate={validateEmail} required={true} minLength={6}
                                       maxLength={30}/>
                            </div>
                            <div class="item__box">
                                <div class="text">
                                    <p>Doctor Cell<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practice.phone" autocomplete="off" component={TextField}
                                       required={true} mask="(999) 999-9999" maskChar=" " normalize={phone}/>
                            </div>
                            <div class="item__box">
                                <div class="text">
                                    <p>Fax:</p>
                                </div>
                                <Field name="managedObject.practiceOwner.contact.fax" autocomplete="off"
                                       component={TextField} required={false} mask="(999) 999-9999" maskChar=" "
                                       normalize={phone}/>
                            </div>
                            <div class="item__box">
                                <div class="text">
                                    <p>After working hours phone number<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practice.afterWorkPhone" autocomplete="off"
                                       component={TextField} required={true} mask="(999) 999-9999" maskChar=" "
                                       normalize={phone}/>
                            </div>
                            <div class="item__box">
                                <div class="text">
                                    <p>Practice Management Software<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practice.softwares" component={TextArea} required={false}/>
                            </div>
                            {false && <div class="item__box">
                                <div class="text">
                                    <p>MDD Account Manager<span class="star">*</span>:</p>
                                </div>
                                <Field name="managedObject.practice.officeManagerName" autocomplete="off"
                                       component={TextField} required={true} minLength={1} maxLength={60}/>

                            </div>}

                        </div>
                    </div>
                </div>

                <div class="profile__main-item">
                    <h3>Comments:</h3>
                    <div class="profile__main-item-comments">
                        <Field name="managedObject.practiceOwner.comments" component={TextArea} required={false}/>
                    </div>
                </div>

                <div class="profile__main-item">
                    <p class="mandatory">Fields marked* are mandatory</p>
                </div>

            </div>

            <div class="footer__btn">
                <button class="blue white" type="button" onClick={handleCancel}>Cancel</button>
                <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>
            </div>


        </form>)
}

Form = reduxForm({
    form: 'office'
})(Form);


class AddEditPractice extends BaseDialog {
    dialogProps() {
        return {
            width: 960,
            height: 700,
            className: "modal__gray",
            title: "Client Profile"
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

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this} initialValues={{
            managedObject: this.convertData(this.props.managedObject),
            fullName: `${this.props.managedObject.practiceOwner.contact.name.first} ${this.props.managedObject.practiceOwner.contact.name.last}`,
            email: this.props.managedObject.practiceOwner.contact.email
        }}/>);
    }
}

const AddEditPracticeDialogConnector = connect(
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
    })(AddEditPractice);

export default AddEditPracticeDialogConnector;
