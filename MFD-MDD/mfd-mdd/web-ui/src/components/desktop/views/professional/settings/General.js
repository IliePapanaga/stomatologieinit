import React, {Component} from 'react';
import {connect, Provider} from 'react-redux';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../../common/form/Field';
import TextField from '../../../common/form/TextField';
import SelectField from '../../../common/form/SelectField';
import Checkbox from '../../../common/form/Checkbox';
import CheckboxGroup from '../../../common/form/CheckboxGroup';
import YesNoAnswer from '../../../common/form/YesNoAnswer';
import {oneShouldBeChecked, validateEmail} from '../../../../../utils/Validators';
import {getDataPromise} from '../../../../../actions/common/getData';
import saveData from '../../../../../actions/common/saveData';
import ReactUploadFile from 'react-upload-file';
import {
    EVENT_VIEW_SAVE_DATA,
    REST_API_PREFIX_GET_USER_PHOTO,
    REST_API_PREFIX_UPLOAD_PHOTO,
    TEXTFIELD_PATTERN_REGEX,
    TITLES
} from '../../../../../utils/Constants';
import {STATES} from '../../../../../data/States';
import {COMMUTING_RADIUS} from '../../../../../data/CommutingRadius';

import ObjectHelper from '../../../../../utils/Object';
import Remote from '../../../../../utils/Remote';
import UiView from '../../../../../utils/UiView';
import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import ChangePassword from '../../../dialogs/account/ChangePassword';
import ChangeUserName from '../../../dialogs/account/ChangeUserName';

import {changePassword} from '../../../../../actions/common/security/password';
import requestChangeUserName from '../../../../../actions/common/security/username';
import {toastr} from 'react-redux-toastr';
import Cookies from 'universal-cookie';
import {Logger} from 'react-logger-lib';
import {phone} from '../../../../../utils/Normalizers';


import BrowseDialogButton from '../../../common/BrowseDialogButton';
import FindLocation from '../../../dialogs/filters/FindLocation';
import findLocationByCoordinates from '../../../../../actions/signup/findLocationByCoordinates';


let GeneralForm = props => {
    const {handleSubmit, onChangeUserName, onChangePassword, change, page, photoUrl, invalid, showAddressWarning} = props;
    let changedZipCode = UiView.getFindByZipCodeHandler(change, 'professional.contact.address.', null, true);
    const cookies = new Cookies();

    const uploadOptions = {
        baseUrl: REST_API_PREFIX_UPLOAD_PHOTO,
        // query: {
        // },
        // // query: {
        //     //   category: '1',
        //     //   _: Date().getTime()
        //     // },
        //     query: (files) => {
        //         const l = files.length;
        //         const queryObj = {};
        //         for (let i = l - 1; i >= 0; --i) {
        //             queryObj[i] = files[i].name;
        //         }
        //         return queryObj;
        //     },
        //     body: {
        //       purpose: 'save'
        //     },
        //     // body: (files) => {
        //     //     const l = files.length;
        //     //     const queryObj = {};
        //     //     for (let i = l - 1; i >= 0; --i) {
        //     //         queryObj[i] = files[i].name;
        //     //     }
        //     //     return queryObj;
        //     // },
        //     // dataType: 'json',
        //multiple: true,
        //     numberLimit: 9,
        accept: 'image/png,image/bmp,image/jpeg',
        fileFieldName: 'file',
        withCredentials: false,
        requestHeaders: {
            //'Content-Type': 'multipart/form-data',
            'X-XSRF-TOKEN': cookies.get('XSRF-TOKEN')

        },
        beforeChoose: () => {
            return true;
        },
        didChoose: (files) => {
            console.log('you choose', typeof files === 'string' ? files : files[0].name);
        },
        beforeUpload: (files) => {
            let allowed = files && files.length > 0 && files[0].size && files[0].size < 500 * 1024;
            if (!allowed) {
                toastr.error("Upload Photo", `The Photo size exceeds 500 KB`);
            }
            return allowed;
        },
        didUpload: (files) => {
            console.log('you just uploaded', typeof files === 'string' ? files : files[0].name);
        },
        uploading: (progress) => {
            console.log('loading...', progress.loaded / progress.total + '%');
        },
        uploadSuccess: (resp) => {
            if (page) {
                page.refreshPhoto();
            }

        },
        uploadError: (err) => {
        },
        page: page
    }
    return (
        <form onSubmit={handleSubmit}>

            <div class="board">
                <div class="board__header"></div>
                <div class="board__action"></div>
                <div class="content pro__setting pro__setting-general">
                    <div class="view__header">
                        <h2>General Settings</h2>
                    </div>
                    <div class="pro__setting-wrapper">
                        <div class="pro__setting-left">
                            <div class="pro__setting-left-account">
                                <h3>Account Details</h3>
                                <div class="pro__setting-left-account-item-wrapper">
                                    <div class="pro__setting-left-account-item">
                                        <div class="text">
                                            <p>Email Address<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="username" readonly="true" setting__general autocomplete="off"
                                               component={TextField} validate={validateEmail} required={true}
                                               minLength={6} maxLength={254}/>
                                        <div class="eye__wrapper hidden">
                                            <div class="eye-show"></div>
                                        </div>
                                        <div class="btn">
                                            <button class="blue" type="button" onClick={onChangeUserName}>Change
                                            </button>
                                        </div>
                                    </div>


                                    <div class="pro__setting-left-account-item">
                                        <div class="text">
                                            <p>Password<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="password" type="password" readonly="true" setting__general
                                               autocomplete="off" component={TextField} required={true} minLength={6}
                                               maxLength={254}/>

                                        <div class="eye__wrapper hidden">
                                            <div class="eye-show"></div>
                                        </div>

                                        <div class="btn">
                                            <button class="blue" type="button" onClick={onChangePassword}>Change
                                            </button>
                                        </div>
                                    </div>


                                    {false && <div class="pro__setting-left-account-item check">
                                        <div class="text">
                                            <p>Notifications:</p>
                                        </div>
                                        {/*<Field name="professional.notificationsEnabled" component={Switcher} />*/}
                                        <Field name="professional.notificationsEnabled" component={Checkbox}/>
                                    </div>}
                                </div>
                            </div>


                            <div class="pro__setting-left-contact">

                                <h3>Contact Details</h3>

                                <div class="pro__setting-left-contact-wrapper">
                                    <div class="pro__setting-left-contact-left">

                                        <div class="item__box">
                                            <div class="text">
                                                <p>Title:</p>
                                            </div>
                                            <div class="input board__input-wrapper">
                                                <Field name="professional.contact.name.title" component={SelectField}
                                                       menuItems={TITLES.filter(t=>t.value!=='Miss')} required={false}/>
                                            </div>
                                        </div>


                                        <div class="item__box">
                                            <div class="text">
                                                <p>First Name<span class="star">*</span>:</p>
                                            </div>
                                            <Field name="professional.contact.name.first" autocomplete="off"
                                                   component={TextField} required={true} minLength={2} maxLength={60}
                                                   regexPattern={TEXTFIELD_PATTERN_REGEX} readonly="true"/>
                                        </div>


                                        <div class="item__box">
                                            <div class="text">
                                                <p>Last Name<span class="star">*</span>:</p>
                                            </div>
                                            <Field name="professional.contact.name.last" autocomplete="off"
                                                   component={TextField} required={true} minLength={2} maxLength={66}
                                                   regexPattern={TEXTFIELD_PATTERN_REGEX} readonly="true"/>

                                        </div>

                                        <div class="item__box photo">
                                            <div class="text">
                                                <p>Photo<span class="star">*</span>:</p>
                                            </div>
                                            <div class="img">
                                                <img class="avatar" width="118px" height="118px" src={`${photoUrl}`}
                                                     alt=""/>
                                            </div>
                                            <div class="btn">
                                                <ReactUploadFile options={uploadOptions}
                                                                 chooseFileButton={<button type="button" class="blue"
                                                                                           value="Browse"/>}/>
                                            </div>
                                        </div>

                                    </div>
                                    <div class="pro__setting-left-contact-right">

                                        <div class="item__box">
                                            <div class="text">
                                                <p>Contact Phone<span class="star">*</span>:</p>
                                            </div>
                                            <Field name="professional.contact.phone" autocomplete="off"
                                                   component={TextField} required={true} mask="(999) 999-9999"
                                                   maskChar=" " normalize={phone}/>

                                        </div>


                                        <div class="item__box zip">
                                            <div class="text">
                                                <p>ZIP<span class="star">*</span>:</p>
                                            </div>
                                            {showAddressWarning && [<p class="zip__text">or</p>,
                                                <BrowseDialogButton title="Find My Location" dialog={FindLocation}
                                                                    browseProps={{showDistance: false}}
                                                                    actions={{
                                                                        save: function (editor, updatedManagedObject, successfulCallBack) {
                                                                            const {lat, lng} = updatedManagedObject;
                                                                            findLocationByCoordinates({
                                                                                lat: lat,
                                                                                lng: lng
                                                                            }).then(function (location) {
                                                                                Logger.of('App.findLocationByCoordinates').info('Location:', location);
                                                                                if (location) {
                                                                                    change(`professional.contact.address.zipCode`, location.zipCode);
                                                                                    change(`professional.contact.address.city`, location.city);
                                                                                    if (location.route && location.streetNumber) {
                                                                                        change(`professional.contact.address.street`, `${location.streetNumber} ${location.route}`);
                                                                                    }if (STATES.findIndex(function (item) {
                                                                                            return item.code === location.state
                                                                                        }) >= 0) {
                                                                                        change(`professional.contact.address.state`, location.state);
                                                                                    } else {
                                                                                        change(`professional.contact.address.state`, '');
                                                                                    }
                                                                                }

                                                                                change(`professional.contact.address.longitude`, lat);
                                                                                change(`professional.contact.address.latitude`, lng);
                                                                                editor.close();
                                                                            });
                                                                        }
                                                                    }}/>]}
                                            <Field name="professional.contact.address.zipCode" autocomplete="off"
                                                   component={TextField} required={true} mask="99999" maskChar="_"
                                                   onChange={changedZipCode}/>
                                        </div>

                                        <div class="item__box">
                                            <div class="text">
                                                <p>Street<span class="star">*</span>:</p>
                                            </div>
                                            <Field name="professional.contact.address.street" autocomplete="off"
                                                   component={TextField} required={true} minLength={2} maxLength={60}/>
                                        </div>

                                        <div class="item__box">
                                            <div class="text">
                                                <p>City<span class="star">*</span>:</p>
                                            </div>
                                            <Field name="professional.contact.address.city" autocomplete="off"
                                                   component={TextField} required={true} minLength={2} maxLength={60}/>
                                        </div>

                                        <div class="item__box">
                                            <div class="text">
                                                <p>State<span class="star">*</span>:</p>
                                            </div>
                                            <div class="input board__input-wrapper">
                                                <Field name="professional.contact.address.state" component={SelectField}
                                                       menuItems={STATES} required={true}/>
                                            </div>
                                        </div>



                                    </div>
                                </div>
                            </div>
                        </div>

                        {/*-------------------------------------------*/}

                        {showAddressWarning && <div class="location__box">
                            <div class="location__box-text">
                                <p>Unfortunately, we cannot find your address on the map. Please, make sure the address
                                    is correct, and if it is, please, <a>Find your location</a> on the map for
                                    us.</p>
                            </div>
                        </div>}

                        {/*--------------------------------------------*/}


                        <div class="pro__setting-right">

                            <div class="pro__setting-right-preferences">
                                <h3>Preferences & Availability</h3>
                                <div class="pro__setting-right-preferences-wrapper">
                                    <div class="pro__setting-right-preferences-left">


                                        <div class="item__radio">
                                            <div class="item__radio-header">Looking for permanent position:
                                            </div>
                                            <Field name="professional.jobPreference.lookingForPermanentJob"
                                                   component={YesNoAnswer}/>

                                        </div>


                                        <div class="item__radio">
                                            <div class="item__radio-header">Looking for temporary position:
                                            </div>
                                            <Field name="professional.jobPreference.lookingForTemporaryJob"
                                                   component={YesNoAnswer}/>
                                        </div>

                                        <div class="item__radio">
                                            <div class="item__radio-header">Full Time:</div>
                                            <Field name="professional.jobPreference.lookingForFullTimeJob"
                                                   component={YesNoAnswer}/>
                                        </div>

                                        <div class="item__radio">
                                            <div class="item__radio-header">Part Time:</div>
                                            <Field name="professional.jobPreference.lookingForPartTimeJob"
                                                   component={YesNoAnswer}/>
                                        </div>

                                        <div class="item__radio">
                                            <div class="item__radio-header">Are evenings OK?
                                            </div>
                                            <Field name="professional.jobPreference.eveningWorkingHoursOk"
                                                   component={YesNoAnswer}/>
                                        </div>

                                    </div>

                                    <div class="pro__setting-right-preferences-right">


                                        <div class="pro__setting-right-input">

                                            <div class="pro__setting-right-input-item">
                                                <div class="item__box item__box-salary">
                                                    <div class="text">
                                                        <p>Desired Rate Per Hour ($)<span class="star">*</span>:</p>
                                                    </div>
                                                    <div class="input">
                                                        <Field
                                                            name="professional.jobPreference.desiredRatePerHour"
                                                            component="input"
                                                            type="number"
                                                            class="board__input"
                                                        />
                                                    </div>
                                                </div>
                                            </div>

                                            {false && <div class="pro__setting-right-input-item">
                                                <div class="item__box item__box-salary">
                                                    <div class="text">
                                                        <p>Your Salary Range ($):<br/><span class="description">(For Permanent Jobs)</span></p>
                                                    </div>
                                                    <div class="item__box-time-wrapper">
                                                        <div class="item__box-time-top-from">
                                                            <Field
                                                                name="professional.jobPreference.salaryFrom"
                                                                component="input"
                                                                type="float"
                                                                class="board__input"
                                                            />
                                                        </div>
                                                        <div class="item__box-time-top-to">
                                                            <Field
                                                                name="professional.jobPreference.salaryTo"
                                                                component="input"
                                                                type="float"
                                                                class="board__input"
                                                            />
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>}


                                            <div class="pro__setting-right-input-item">
                                                <div class="item__box">
                                                    <div class="text">
                                                        <p>Radius willing to travel in miles from home location<span
                                                            class="star">*</span>:</p>
                                                    </div>
                                                    <div class="board__input-wrapper input">
                                                        <Field name="professional.jobPreference.commutingRadius"
                                                               component={SelectField} menuItems={COMMUTING_RADIUS}
                                                               required={false}/>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="pro__setting-right-check">
                                                <div class="item__box">
                                                    <div class="text">
                                                        <p>Willing to relocate:</p>
                                                    </div>
                                                    <Field name="professional.jobPreference.willingToRelocate"
                                                           component={Checkbox}/>

                                                </div>
                                                <p>(You will be able to observe all US permanent postings)</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="pro__setting-right-availability">
                                <div class="item__check days">

                                    <div class="item__check-header">Checkmark days of the week that you are available to temp<span class="star">*</span>:</div>
                                    <Field name="professional.jobPreference.availabilityDays"
                                           items={[{code: 'MONDAY', name: 'Monday'},
                                               {code: 'TUESDAY', name: 'Tuesday'},
                                               {code: 'WEDNESDAY', name: 'Wednesday'},
                                               {code: 'THURSDAY', name: 'Thursday'},
                                               {code: 'FRIDAY', name: 'Friday'},
                                               {code: 'SATURDAY', name: 'Saturday'},
                                               {code: 'SUNDAY', name: 'Sunday'}]} component={CheckboxGroup}
                                           classWrapper="item__check-content-wrapper"
                                           itemClassWrapper="item__check-content" required={true}
                                           validate={oneShouldBeChecked}/>
                                </div>
                            </div>


                            {false && <div class="pro__setting-right-locations">
                                <div class="item__check">

                                    <div class="item__check-header">Which of the following Bay Area locations
                                        can you work in?
                                    </div>
                                    <Field name="professional.jobPreference.bayAreas" items={[{code: 'NB', name: 'NB'},
                                        {code: 'EB', name: 'EB'},
                                        {code: 'SF', name: 'SF'},
                                        {code: 'SB', name: 'SB'},
                                        {code: 'SACR', name: 'SACR'},
                                        {code: 'PENNIN', name: 'PENNIN'},
                                        {code: 'MONTER_SANCRUZ', name: 'MONTER/SAN CRUZ'}]} component={CheckboxGroup}
                                           classWrapper="item__check-content-wrapper"
                                           itemClassWrapper="item__check-content"/>

                                </div>
                            </div>}


                        </div>

                        <div class="pro__setting-right description">
                            <p>Please note: Your availability selection may limit the amount of temporary and permanent jobs you see.</p>
                        </div>

                        <div class="item__box-help">
                            <p class="mandatory">Fields marked* are mandatory</p>
                        </div>
                    </div>

                    <div class="footer__btn">
                        <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Save</button>
                    </div>

                </div>


            </div>
        </form>)
}

GeneralForm = reduxForm({
    form: 'general'
})(GeneralForm);

const selector = formValueSelector('general');

 GeneralForm = connect(state => {
    const longitude = selector(state, 'professional.contact.address.longitude');
    const latitude = selector(state, 'professional.contact.address.latitude');
    const zipCode = selector(state, 'professional.contact.address.zipCode');
    return {
        showAddressWarning: (!longitude && !latitude) && zipCode && zipCode.indexOf('_') < 0
    }
})(GeneralForm)

class General extends Component {

    state = {username: undefined, practice: undefined, photoUrl: undefined}

    async componentDidMount() {
        let me = this,
            id = me.props.userId,
            username = me.props.username,
            fields = Remote.getFieldsByModel(me.props.metaInfo, "ProfessionalModel");
        me.refreshPhoto();
        let data = await getDataPromise('professional', id, fields);

        if (!data.jobPreference) {
            ObjectHelper.setValue(data, "jobPreference.salaryFrom", 1, true);
            ObjectHelper.setValue(data, "jobPreference.salaryTo", 1000, true);
            ObjectHelper.setValue(data, "jobPreference.lookingForPartTimeJob", false, true);
            ObjectHelper.setValue(data, "jobPreference.lookingForFullTimeJob", false, true);
            ObjectHelper.setValue(data, "jobPreference.lookingForPermanentJob", false, true);
            ObjectHelper.setValue(data, "jobPreference.eveningWorkingHoursOk", false, true);
            ObjectHelper.setValue(data, "jobPreference.lookingForTemporaryJob", false, true);
            ObjectHelper.setValue(data, "jobPreference.willingToRelocate", false, true);
        }


        me.setState({
            managedObject: {professional: data, username: username, password: "123456789"}
        });

    }

    refreshPhoto() {
        let me = this,
            id = me.props.userId,
            t = Date.now(),
            photoUrl = REST_API_PREFIX_GET_USER_PHOTO.replace('{0}', id) + `?${t}`;

        me.setState({photoUrl: photoUrl});
    }

    submit(data) {
        let me = this,
            resultData = ObjectHelper.copyObject(data),
            saveData = {};
        saveData.jobPreference = resultData.professional.jobPreference;
        delete resultData.professional['jobPreference'];
        delete resultData.professional['profile'];
        saveData.professional = resultData.professional;

        Logger.of('App.General.submit').info('General data:', saveData);

        me.props.onSave(saveData);
    }

    changeUserName() {
        UiView.showDialog(<Provider store={UiView.createDialogStore()}><ChangeUserName managedObject={{}} actions={{
            reset: function (data, dialog) {
                requestChangeUserName(data.oldpassword, data.username).then(function (result) {
                    if (result) {
                        toastr.success("Change User Name", "Request for changing User Name was sent successfuly.");
                        dialog.close();
                    } else {
                        toastr.error("Change User Name", "Request for changing User Name wasn't sent successfuly.");
                    }
                });
            }
        }}/></Provider>);
    }

    changePassword() {
        UiView.showDialog(<Provider store={UiView.createDialogStore()}><ChangePassword managedObject={{}} actions={{
            reset: function (data, dialog) {
                changePassword(data.oldpassword, data.password).then(function (result) {
                    if (result) {
                        toastr.success("Change Password", "Password was changed successfuly.");
                        dialog.close();
                    } else {
                        toastr.error("Change Password", "Password wasn't changed successfuly.");
                    }
                });
            }
        }}/></Provider>);

    }

    render() {
        if (!this.state.managedObject) {
            return false;
        }
        const photoUrl = this.state.photoUrl;
        return ([
            <div class="board__header"></div>,
            <div class="board__action"></div>,
            <GeneralForm onSubmit={this.submit.bind(this)} onChangeUserName={this.changeUserName.bind(this)}
                  onChangePassword={this.changePassword.bind(this)} page={this} photoUrl={photoUrl}
                  initialValues={this.state.managedObject}/>
        ]);
    }
}

const GeneralConnector = connect(
    function (state, ownProps) {
        let userId = state.context.currentUser ? state.context.currentUser.id : undefined;
        return Object.assign(baseViewStateMap(state, ownProps), {
            userId: userId,
            username: state.context.currentUser ? state.context.currentUser.username : undefined
        });
    },
    function (dispatch) {
        return Object.assign(baseViewDispatcherMap(dispatch), {
            onSave: (managedObject) => {
                dispatch(saveData({queryName: 'updateProfessionalGeneral'}, managedObject)).then(
                    function (result) {
                        //if (result) {
                        dispatch({type: EVENT_VIEW_SAVE_DATA, result});
                        toastr.success("Change General Data", "The data has been changed successfully.");
                        //}
                    });
            }
        });
    })(General);

export default GeneralConnector;




