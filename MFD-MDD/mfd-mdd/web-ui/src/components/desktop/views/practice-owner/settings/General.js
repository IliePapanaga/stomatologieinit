import React, {Component} from 'react';
import {connect, Provider} from 'react-redux';
import {reduxForm} from 'redux-form';
import Field from '../../../common/form/Field';
import TextField from '../../../common/form/TextField';
import TextArea from '../../../common/form/TextArea';
import SelectField from '../../../common/form/SelectField';
import ExSelectField from '../../../common/form/ExSelectField';
import {validateEmail} from '../../../../../utils/Validators';
import {getDataPromise} from '../../../../../actions/common/getData';
import saveData from '../../../../../actions/common/saveData';
import {
    EVENT_VIEW_SAVE_DATA,
    TEXTFIELD_PATTERN_REGEX,
    TITLES,
    WEBSITE_PATTERN_REGEX
} from '../../../../../utils/Constants';
import {SPECIALTY} from '../../../../../data/Specialty';
import ObjectHelper from '../../../../../utils/Object';
import {phone} from '../../../../../utils/Normalizers';
import Remote from '../../../../../utils/Remote';
import UiView from '../../../../../utils/UiView';
import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import ChangePassword from '../../../dialogs/account/ChangePassword';
import ChangeUserName from '../../../dialogs/account/ChangeUserName';
import {changePassword} from '../../../../../actions/common/security/password';
import requestChangeUserName from '../../../../../actions/common/security/username';
import {toastr} from 'react-redux-toastr';
import {Logger} from 'react-logger-lib';
import {STATES} from '../../../../../data/States';


let GeneralForm = props => {
    const {handleSubmit, onChangeUserName, onChangePassword, change, invalid} = props;
    let changedZipCode = UiView.getFindByZipCodeHandler(change, 'practice.practiceOwner.contact.address.');
    return (
        <form onSubmit={handleSubmit}>


            <div class="board__header">
            </div>


            <div class="board__action">

            </div>


            <div class="content pro__setting practice__setting-general">
                <div class="view__header">
                    <h2>General Settings</h2>
                </div>
                <div class="pro__setting-wrapper">


                    <div class="pro__setting-left">
                        <div class="pro__setting-item">

                            <h3>Account Details</h3>


                            <div class="pro__setting-left-account-item-top-wrapper">


                                <div class="pro__setting-left-account-item">
                                    <div class="text">
                                        <p>Email Address<span class="star">*</span>:</p>
                                    </div>
                                    <Field name="username" readonly="true" setting__general autocomplete="off"
                                           component={TextField} validate={validateEmail} required={true} minLength={6}
                                           maxLength={254}/>
                                    <div class="eye__wrapper hidden">
                                        <div class="eye-show"></div>
                                    </div>
                                    <div class="btn">
                                        <button class="blue" type="button" onClick={onChangeUserName}>Change</button>
                                    </div>
                                </div>


                                <div class="pro__setting-left-account-item">
                                    <div class="text">
                                        <p>Password<span class="star">*</span>:</p>
                                    </div>
                                    <Field name="password" type="password" readonly="true" setting__general
                                           autocomplete="off" component={TextField} required={true}/>


                                    <div class="eye__wrapper hidden">
                                        <div class="eye-show"></div>
                                    </div>

                                    <div class="btn">
                                        <button class="blue" type="button" onClick={onChangePassword}>Change</button>
                                    </div>
                                </div>


                            </div>

                        </div>


                        <div class="pro__setting-item">

                            <h3>Personal Details</h3>


                            <div class="pro__setting-left-personal-item-top-wrapper">


                                <div class="pro__setting-left-personal-item">
                                    <div class="item__box">
                                        <div class="text">
                                            <p>Title:</p>
                                        </div>
                                        <div class="input board__input-wrapper">
                                            <Field name="practice.practiceOwner.contact.name.title"
                                                   component={SelectField} menuItems={TITLES} required={false}/>
                                        </div>
                                    </div>


                                    <div class="item__box">
                                        <div class="text">
                                            <p>First Name<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.practiceOwner.contact.name.first" autocomplete="off"
                                               component={TextField} required={true} minLength={2} maxLength={60}
                                               regexPattern={TEXTFIELD_PATTERN_REGEX} readonly="true"/>
                                    </div>


                                    <div class="item__box">
                                        <div class="text">
                                            <p>Last Name<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.practiceOwner.contact.name.last" autocomplete="off"
                                               component={TextField} required={true} minLength={2} maxLength={66}
                                               regexPattern={TEXTFIELD_PATTERN_REGEX} readonly="true"/>
                                    </div>
                                </div>


                                <div class="pro__setting-left-personal-item">

                                    <div class="item__box zip">
                                        <div class="text">
                                            <p>ZIP<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.practiceOwner.contact.address.zipCode" autocomplete="off"
                                               component={TextField} required={true} mask="99999" maskChar="_"
                                               onChange={changedZipCode}/>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>Street<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.practiceOwner.contact.address.street" autocomplete="off"
                                               component={TextField} required={true} minLength={2} maxLength={60}/>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>City<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.practiceOwner.contact.address.city" autocomplete="off"
                                               component={TextField} required={true} minLength={2} maxLength={60}/>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>State<span class="star">*</span>:</p>
                                        </div>
                                        <div class="input board__input-wrapper">
                                            <Field name="practice.practiceOwner.contact.address.state"
                                                   component={SelectField} menuItems={STATES} required={true}/>
                                        </div>
                                    </div>


                                </div>


                            </div>

                        </div>


                        <div class="pro__setting-item">

                            <h3>Billing Address Information</h3>
                            <p class="pro__setting-item-description">This may be different than office location. Please make sure to click on "My Account" then "Locations" to add a location. You will not be able to create a job posting until you have created at least one office location.</p>

                            <div class="pro__setting-left-billing-item-top-wrapper">


                                <div class="pro__setting-left-billing-item">
                                    <div class="item__box">
                                        <div class="text">
                                            <p>Billing ZIP<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.billingAddress.zipCode" autocomplete="off"
                                               component={TextField} required={true} mask="99999" maskChar="_"
                                               onChange={UiView.getFindByZipCodeHandler(change, `practice.billingAddress.`)}/>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>Billing Street<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.billingAddress.street" autocomplete="off"
                                               component={TextField} required={true} minLength={2} maxLength={60}/>
                                    </div>
                                </div>


                                <div class="pro__setting-left-billing-item">
                                    <div class="item__box">
                                        <div class="text">
                                            <p>Billing City<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.billingAddress.city" autocomplete="off"
                                               component={TextField} required={true} minLength={2} maxLength={60}/>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>Billing State<span class="star">*</span>:</p>
                                        </div>
                                        <div class="input board__input-wrapper">
                                            <Field name="practice.billingAddress.state" component={SelectField}
                                                   menuItems={STATES} required={true}/>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>


                        <div class="pro__setting-item">

                            <h3>Сompany Details</h3>


                            <div class="pro__setting-left-company-item-top-wrapper">


                                <div class="pro__setting-left-company-item">
                                    <div class="item__box">
                                        <div class="text">
                                            <p>Company Name<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.name" autocomplete="off" component={TextField}
                                               required={true} minLength={1} maxLength={60}/>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>Contact Phone<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.practiceOwner.contact.phone" autocomplete="off"
                                               component={TextField} required={true} mask="(999) 999-9999" maskChar=" "
                                               normalize={phone}/>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>Company Website:</p>
                                        </div>
                                        <Field name="practice.webSite" autocomplete="off" component={TextField}
                                               required={false} minLength={2} maxLength={60}
                                               regexPattern={WEBSITE_PATTERN_REGEX}/>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>Company Specialty<span class="star">*</span>:</p>
                                        </div>
                                        <div class="input board__input-wrapper">
                                            <Field name="practice.specialities" component={ExSelectField}
                                                   multiple={true} required={true} menuItems={SPECIALTY}/>
                                        </div>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>Office Manager Name:</p>
                                        </div>
                                        <Field name="practice.officeManagerName" autocomplete="off"
                                               component={TextField} required={false} minLength={1} maxLength={60}/>
                                    </div>

                                </div>


                                <div class="pro__setting-left-company-item">
                                    <div class="item__box">
                                        <div class="text">
                                            <p>Second Email:</p>
                                        </div>
                                        <Field name="practice.secondEmail" autocomplete="off"
                                               component={TextField} validate={validateEmail} required={false}
                                               minLength={6} maxLength={30}/>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>Doctor Cell<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.phone" autocomplete="off" component={TextField}
                                               required={true} mask="(999) 999-9999" maskChar=" " normalize={phone}/>
                                    </div>
                                    <div class="item__box">
                                        <div class="text">
                                            <p>Fax:</p>
                                        </div>
                                        <Field name="practice.practiceOwner.contact.fax" autocomplete="off"
                                               component={TextField} required={false} mask="(999) 999-9999" maskChar=" "
                                               normalize={phone}/>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>After working hours phone number<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.afterWorkPhone" autocomplete="off" component={TextField}
                                               required={true} mask="(999) 999-9999" maskChar=" " normalize={phone}/>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>Practice Management Software<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="practice.softwares" component={TextArea} required={true}/>
                                    </div>

                                </div>
                            </div>

                        </div>
                        <div class="item__box-help">
                            <p class="mandatory">Fields marked* are mandatory</p>
                        </div>

                    </div>

                </div>

                <div class="footer__btn">
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Save</button>
                </div>

            </div>
        </form>)
}

const Form = reduxForm({
    form: 'general'
})(GeneralForm);

class General extends Component {

    state = {username: undefined, practice: undefined}


    async componentDidMount() {
        let me = this,
            id = me.props.userId,
            username = me.props.username,
            fields = Remote.getFieldsByModel(me.props.metaInfo, "PracticeModelInput", [], true);
        let data = await getDataPromise('practice', id, fields);

        me.setState({managedObject: {practice: data, username: username, password: "123456789"}});

    }

    submit(data) {
        let me = this,
            resultData = ObjectHelper.copyObject(data),
            saveData = {};
        saveData.practiceOwner = resultData.practice.practiceOwner
        delete resultData.practice['practiceOwner'];
        saveData.practice = resultData.practice;

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
        return ([
            <div class="board__header"></div>,
            <div class="board__action"></div>,
            <Form onSubmit={this.submit.bind(this)} onChangeUserName={this.changeUserName.bind(this)}
                  onChangePassword={this.changePassword.bind(this)} page={this}
                  initialValues={this.state.managedObject}/>
        ]);
    }
}

const GeneralConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {
            userId: state.context.currentUser ? state.context.currentUser.id : undefined,
            username: state.context.currentUser ? state.context.currentUser.username : undefined
        });
    },
    function (dispatch) {
        return Object.assign(baseViewDispatcherMap(dispatch), {
            onSave: (managedObject) => {
                dispatch(saveData({queryName: 'updatePracticeOwnerGeneral'}, managedObject)).then(
                    function (result) {

                        dispatch({type: EVENT_VIEW_SAVE_DATA, result});
                        toastr.success("Change General Data", "The data has been changed successfully.");

                    });
            }
        });
    })(General);

export default GeneralConnector;