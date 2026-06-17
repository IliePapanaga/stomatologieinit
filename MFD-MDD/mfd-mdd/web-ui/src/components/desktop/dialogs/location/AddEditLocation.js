import React from 'react';
import {connect} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';
import TimeField from '../../common/form/TimeField';
import SelectField from '../../common/form/SelectField';

import ObjectHelper from '../../../../utils/Object';
import {validateEmail} from '../../../../utils/Validators';
import {TEXTFIELD_PATTERN_REGEX} from '../../../../utils/Constants';
import {STATES} from '../../../../data/States';
import {TIME_ZONES} from '../../../../data/TimeZones';
import {Logger} from 'react-logger-lib';
import UiView from '../../../../utils/UiView';
import {phone} from '../../../../utils/Normalizers';
import FindLocation from '../../dialogs/filters/FindLocation';
import findLocationByCoordinates from '../../../../actions/signup/findLocationByCoordinates';
import findTimeZoneByLocation from '../../../../actions/location/findTimeZoneByLocation';

import BrowseDialogButton from '../../common/BrowseDialogButton';

export const dialogInfo = {
    //fields: []
}

let Form = props => {
    const { handleSubmit, change, handleCancel, invalid, showAddressWarning } = props;
    let changedZipCode = UiView.getFindByZipCodeHandler(change, 'managedObject.contact.address.','managedObject.timeZone', true);
    return (
        <form onSubmit={handleSubmit}>
            <div class="location__main modal__gray-main modal30">
                <div className="location__main-item">
                <div className="item__box">
                    <div className="description__text">
                        <p>Please note: If you want to set up a specific payment type (ie: bank account or Credit Card)
                            for payments on the new added location, you will need to create a new Mayday Dental account.
                            If you wish to use the same credit card or ACH for all locations, then please proceed with
                            adding office locations. Please contact Mayday Dental at 888-899-4386 with any
                            questions.</p>
                    </div>
                </div>
                </div>

                <div class="location__main-item">
                    <div class="item__box">
                        <div class="text">
                            <p>Practice Name<span class="star">*</span>:</p>
                        </div>
                        <Field name="managedObject.name" autocomplete="off" component={TextField} required={true} minLength={2} maxLength={30} regexPattern={/^[a-zA-Z0-9'\s]*$/} />
                    </div>
                    {false &&
                        <div class="item__box">
                            <div class="text">
                                <p>Operating Hours<span class="star">*</span>:</p>
                            </div>
                            <div class="input input__time-wrapper">
                                <Field name="managedObject.workingHoursFrom" component={TimeField} step={15} required={true} />
                                <Field name="managedObject.workingHoursTo" component={TimeField} step={15} required={true} />
                            </div>
                        </div>}
                </div>
                <div class="location__main-item">
                    <h3>Location Contact Info</h3>
                    <div class="item__box">
                        <div class="text">
                            <p>First Name<span class="star">*</span>:</p>
                        </div>
                        <Field name="managedObject.contact.name.first" autocomplete="off" component={TextField} required={true} minLength={2} maxLength={60} regexPattern={TEXTFIELD_PATTERN_REGEX} />
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>Last Name<span class="star">*</span>:</p>
                        </div>
                        <Field name="managedObject.contact.name.last" autocomplete="off" component={TextField} required={true} minLength={2} maxLength={60} regexPattern={TEXTFIELD_PATTERN_REGEX} />
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>Location Email<span class="star">*</span>:</p>
                        </div>
                        <Field name="managedObject.contact.email" component={TextField} validate={validateEmail} required={true} minLength={6} maxLength={254} />
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>Location Phone<span class="star">*</span>:</p>
                        </div>
                        <Field name="managedObject.contact.phone" autocomplete="off" component={TextField} required={true} mask="(999) 999-9999" maskChar=" " normalize={phone} />
                    </div>
                </div>


                <div class="location__main-item">
                    <h3>Address Details</h3>
                    <div class="item__box zip">
                        <div class="text">
                            <p>ZIP<span class="star">*</span>:</p>
                        </div>
                        {showAddressWarning && [<p class="zip__text">or</p>,
                            <BrowseDialogButton title="Find My Location" dialog={FindLocation} actions={{
                                save: function (editor, updatedManagedObject, successfulCallBack) {
                                    const {lat, lng} = updatedManagedObject;
                                    findLocationByCoordinates({lat: lat, lng: lng}).then(function (location) {
                                        Logger.of('App.findLocationByCoordinates').info('Location:', location);
                                        if (location) {

                                            findTimeZoneByLocation(location.location || {
                                                lat: lat,
                                                lng: lng
                                            }).then(function (timeZone) {
                                                Logger.of('App.findTimeZoneByLocation').info('Time Zone:', timeZone);

                                                if (timeZone) {
                                                    change(`managedObject.timeZone`, timeZone);
                                                }
                                                change(`managedObject.contact.address.zipCode`, location.zipCode);
                                                change(`managedObject.contact.address.city`, location.city);
                                                if (location.route && location.streetNumber) {
                                                    change(`managedObject.contact.address.street`, `${location.streetNumber} ${location.route}`);
                                                }
                                                if (STATES.findIndex(function (item) {
                                                        return item.code === location.state
                                                    }) >= 0) {
                                                    change(`managedObject.contact.address.state`, location.state);
                                                } else {
                                                    change(`managedObject.contact.address.state`, '');
                                                }
                                            });

                                        }

                                        change(`managedObject.contact.address.longitude`, lat);
                                        change(`managedObject.contact.address.latitude`, lng);
                                        editor.close();
                                    });
                                }
                            }} browseProps={{showDistance: false}}/>]}
                        <Field name="managedObject.contact.address.zipCode" autocomplete="off" component={TextField} required={true} mask="99999" maskChar="_" onChange={changedZipCode} />
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>Street<span class="star">*</span>:</p>
                        </div>
                        <Field name="managedObject.contact.address.street" autocomplete="off" component={TextField} required={true} minLength={2} maxLength={60} />
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>City<span class="star">*</span>:</p>
                        </div>
                        <Field name="managedObject.contact.address.city" autocomplete="off" component={TextField} required={true} minLength={2} maxLength={60} />
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>State<span class="star">*</span>:</p>
                        </div>
                        <div class="input board__input-wrapper">
                            <Field name="managedObject.contact.address.state" component={SelectField} menuItems={STATES} required={true} />
                        </div>
                    </div>
                    <div class="item__box" style={{display:'none'}}>
                        <div class="text">
                            <p>Latitude <span class="star">*</span>:</p>
                        </div>
                        <div class="input board__input-wrapper">
                            <Field name="managedObject.contact.address.latitude" component={TextField} required={true} />
                        </div>
                    </div>
                    <div class="item__box" style={{display:'none'}}>
                        <div class="text">
                            <p>Longitude<span class="star">*</span>:</p>
                        </div>
                        <div class="input board__input-wrapper">
                            <Field name="managedObject.contact.address.longitude" component={TextField} required={true} />
                        </div>
                    </div>
                    <div class="item__box">
                        <div class="text">
                            <p>Time Zone<span class="star">*</span>:</p>
                        </div>
                        <div class="input board__input-wrapper">
                            <Field name="managedObject.timeZone" component={SelectField} menuItems={TIME_ZONES} required={true} />
                        </div>
                    </div>


                    <div class="location__description">
                        <p>Reminder: Click on "Payment Methods" under "My Account" to add a valid payment method in order to create a job posting.</p>
                    </div>

                    <div class="location__help">
                        <p class="mandatory">Fields marked* are mandatory</p>
                    </div>

                </div>
                {/*-------------------------------------------*/}

                {showAddressWarning && <div class="location__box">
                    <div class="location__box-text">
                        <p>Unfortunately, we cannot find your address on the map. Please, make sure the address is
                            correct, and if it is, please, use "FIND MY LOCATION" button and find your location on the map for
                            us.</p>
                    </div>
                </div>}

                {/*--------------------------------------------*/}
            </div>

            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Cancel</button>
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>
                </div>
            </div>
        </form >)
}

Form = reduxForm({
    form: 'location'
})(Form);

const selector = formValueSelector('location');

Form = connect(state => {
    const longitude = selector(state, 'managedObject.contact.address.longitude');
    const latitude = selector(state, 'managedObject.contact.address.latitude');
    const zipCode = selector(state, 'managedObject.contact.address.zipCode');
    return {
        showAddressWarning: (!longitude && !latitude) && zipCode && zipCode.indexOf('_') < 0
    }
})(Form)


class AddEditLocation extends BaseDialog {
    dialogProps() {
        return {
            width: 560,
            height: 740,
            className: "modal__gray",
            title: "Add Location"
        }
    }
    beforeSave(dialog, managedData) {
        let phoneFields = ['managedObject.contact.phone'];
        phoneFields.forEach(function (phoneField) {
            let phone = ObjectHelper.getValue(managedData, phoneField);
            phone = phone.replace(/[- ( )]*/g, '');
            ObjectHelper.setValue(managedData, phoneField, phone);
        }, this);

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this} initialValues={{ managedObject: this.props.managedObject }} />);
    }
}

const AddEditLocationDialogConnector = connect(
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
    })(AddEditLocation);

export default AddEditLocationDialogConnector;
