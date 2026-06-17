import React, {Component} from 'react';
import {connect, Provider} from 'react-redux';
import {reduxForm} from 'redux-form';
import Field from '../../../common/form/Field';
import TextField from '../../../common/form/TextField';
import TextArea from '../../../common/form/TextArea';
import DateField from '../../../common/form/DateField';
import SelectField from '../../../common/form/SelectField';
import ExSelectField from '../../../common/form/ExSelectField';
import {maxOrEqualsThanDate, validateEmail} from '../../../../../utils/Validators';
import {getDataPromise} from '../../../../../actions/common/getData';
import References from '../../../../../utils/References';
import {phone} from '../../../../../utils/Normalizers';
import saveData from '../../../../../actions/common/saveData';
import {
    EVENT_VIEW_SAVE_DATA,
    LANGUAGE_ENGLISH,
    TEXTFIELD_WITH_SPACE_PATTERN_REGEX
} from '../../../../../utils/Constants';
import ObjectHelper from '../../../../../utils/Object';
import Remote from '../../../../../utils/Remote';
import UiView from '../../../../../utils/UiView';
import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import ChangePassword from '../../../dialogs/account/ChangePassword';
import ChangeUserName from '../../../dialogs/account/ChangeUserName';

import {changePassword} from '../../../../../actions/common/security/password';
import requestChangeUserName from '../../../../../actions/common/security/username';
import {toastr} from 'react-redux-toastr';


import {Logger} from 'react-logger-lib';
import DateHelper from "../../../../../utils/DateHelper";

let ProfileForm = props => {
    const {handleSubmit, invalid, refs} = props;
    let educations = refs.educations || [];
    let languages = refs.languages || [];
    let academicDegrees = refs.academicDegrees || [];
    return (
        <form onSubmit={handleSubmit}>
            <div class="board__header">
            </div>


            <div class="board__action">
            </div>


            <div class="content pro__setting pro-setting-profile">

                <div class="view__header">
                    <h2>Skills and Experience</h2>
                </div>
                <div class="pro__setting-wrapper-overlay">

                <div class="pro__setting-top">
                    <div class="pro__setting-top-left">

                        <div class="item__box">
                            <div class="text">
                                <p>Skills Summary<span class="star">*</span>:</p>
                            </div>
                            <Field name="profile.skillSummary" component={TextArea} required={true}/>
                        </div>

                    </div>

                    <div class="pro__setting-top-right">
                        <div class="item__box">
                            <div class="text">
                                <p>Education<span class="star">*</span>:</p>
                            </div>
                            <div class="input board__input-wrapper">
                                <Field name="profile.education" component={SelectField} menuItems={educations}
                                       required={true}/>
                            </div>
                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>Highest Degree Earned<span class="star">*</span>:</p>
                            </div>
                            <div class="input board__input-wrapper">
                                <Field name="profile.highestDegree" component={SelectField} menuItems={academicDegrees}
                                       required={true}/>
                            </div>
                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>Languages<span class="star">*</span>:</p>
                            </div>
                            <div class="input board__input-wrapper">
                                <Field name="profile.languages" component={ExSelectField} menuItems={languages}
                                       required={true} multiple={true}/>
                            </div>
                        </div>
                    </div>
                </div>


                <div class="pro__setting-wrapper">

                    <div class="pro__setting-left">
                        <div class="pro__setting-left-work">

                            <h3>Work experience (latest three jobs):</h3>


                            <div class="item__box-time">
                                <div class="item__box-time-top">
                                    <div class="item__box-time-top-from">
                                        <Field name="workExperience0.hireDate" class="date" placeholder="Select date"
                                               component={DateField} dateFormat="MM/DD/YYYY" showYearDropdown="{true}" maxDate={DateHelper.getCurrentDate()} minDate={DateHelper.getServerDate(MIN_DATE)}/>
                                    </div>
                                    <div class="item__box-time-top-to">
                                        <Field name="workExperience0.leaveDate" class="date" placeholder="Select date"
                                               component={DateField} dateFormat="MM/DD/YYYY" showYearDropdown="{true}"
                                               validate={maxOrEqualsThanDate("workExperience0.hireDate", true)} minDate={DateHelper.getServerDate(MIN_DATE)}/>
                                    </div>
                                    <div class="item__box-time-top-name">
                                        <div class="text">
                                            <p>Company Name:</p>
                                        </div>
                                        <Field name="workExperience0.companyName" autocomplete="off"
                                               component={TextField} minLength={2} maxLength={60}
                                               regexPattern={TEXTFIELD_WITH_SPACE_PATTERN_REGEX}/>
                                    </div>
                                    <div class="item__box-time-bottom">
                                        <div class="text">
                                            <p>Functions that performed there:</p>
                                        </div>
                                        <Field name="workExperience0.responsibilities" component={TextArea}
                                               placeholder="Your text..."/>
                                    </div>
                                </div>
                            </div>

                            <div class="item__box-time">
                                <div class="item__box-time-top">
                                    <div class="item__box-time-top-from">
                                        <Field name="workExperience1.hireDate" class="date" placeholder="Select date"
                                               component={DateField} dateFormat="MM/DD/YYYY" showYearDropdown="{true}" maxDate={DateHelper.getCurrentDate()} minDate={DateHelper.getServerDate(MIN_DATE)}/>
                                    </div>
                                    <div class="item__box-time-top-to">
                                        <Field name="workExperience1.leaveDate" class="date" placeholder="Select date"
                                               component={DateField} dateFormat="MM/DD/YYYY" showYearDropdown="{true}"
                                               validate={maxOrEqualsThanDate("workExperience1.hireDate")} minDate={DateHelper.getServerDate(MIN_DATE)}/>
                                    </div>
                                    <div class="item__box-time-top-name">
                                        <div class="text">
                                            <p>Company Name:</p>
                                        </div>
                                        <Field name="workExperience1.companyName" autocomplete="off"
                                               component={TextField} minLength={2} maxLength={60}
                                               regexPattern={TEXTFIELD_WITH_SPACE_PATTERN_REGEX}/>
                                    </div>
                                    <div class="item__box-time-bottom">
                                        <div class="text">
                                            <p>Functions that performed there:</p>
                                        </div>
                                        <Field name="workExperience1.responsibilities" component={TextArea}
                                               required={false}
                                               placeholder="Your text..."/>
                                    </div>
                                </div>

                            </div>

                            <div class="item__box-time">
                                <div class="item__box-time-top">
                                    <div class="item__box-time-top-from">
                                        <Field name="workExperience2.hireDate" class="date" placeholder="Select date"
                                               component={DateField} dateFormat="MM/DD/YYYY" showYearDropdown="{true}" maxDate={DateHelper.getCurrentDate()} minDate={DateHelper.getServerDate(MIN_DATE)}/>
                                    </div>
                                    <div class="item__box-time-top-to">
                                        <Field name="workExperience2.leaveDate" class="date" placeholder="Select date"
                                               component={DateField} dateFormat="MM/DD/YYYY" showYearDropdown="{true}"
                                               validate={maxOrEqualsThanDate("workExperience2.hireDate")} minDate={DateHelper.getServerDate(MIN_DATE)}/>
                                    </div>
                                    <div class="item__box-time-top-name">
                                        <div class="text">
                                            <p>Company Name:</p>
                                        </div>
                                        <Field name="workExperience2.companyName" autocomplete="off"
                                               component={TextField} minLength={2} maxLength={60}
                                               regexPattern={TEXTFIELD_WITH_SPACE_PATTERN_REGEX}/>
                                    </div>

                                    <div class="item__box-time-bottom">
                                        <div class="text">
                                            <p>Functions that performed there:</p>
                                        </div>
                                        <Field name="workExperience2.responsibilities" component={TextArea}
                                               required={false}
                                               placeholder="Your text..."/>
                                    </div>
                                </div>

                            </div>

                        </div>
                    </div>


                    <div class="pro__setting-right">

                        <div class="pro__setting-right-references">
                            <h3>Please provide contact information of your last employers:</h3>

                            <div class="pro__setting-right-references-item">
                                <div class="item__box">
                                    <div class="text">
                                        <p>Name:</p>
                                    </div>
                                    <Field name="workReference0.name" autocomplete="off" component={TextField}
                                           minLength={2} maxLength={60}/>
                                </div>

                                <div class="item__box">
                                    <div class="text">
                                        <p>Email:</p>
                                    </div>
                                    <Field name="workReference0.email" autocomplete="off" component={TextField}
                                           validate={validateEmail} minLength={6} maxLength={254}/>
                                </div>

                                <div class="item__box">
                                    <div class="text">
                                        <p>Phone:</p>
                                    </div>
                                    <Field name="workReference0.phone" autocomplete="off" component={TextField}
                                           mask="(999) 999-9999" maskChar=" " normalize={phone}/>
                                </div>
                            </div>

                            <div class="pro__setting-right-references-item">
                                <div class="item__box">
                                    <div class="text">
                                        <p>Name:</p>
                                    </div>
                                    <Field name="workReference1.name" autocomplete="off" component={TextField}
                                           minLength={2} maxLength={60}/>
                                </div>

                                <div class="item__box">
                                    <div class="text">
                                        <p>Email:</p>
                                    </div>
                                    <Field name="workReference1.email" autocomplete="off" component={TextField}
                                           validate={validateEmail} minLength={6} maxLength={254}/>

                                </div>

                                <div class="item__box">
                                    <div class="text">
                                        <p>Phone:</p>
                                    </div>
                                    <Field name="workReference1.phone" autocomplete="off" component={TextField}
                                           mask="(999) 999-9999" maskChar=" " normalize={phone}/>
                                </div>
                            </div>

                            <div class="pro__setting-right-references-item">
                                <div class="item__box">
                                    <div class="text">
                                        <p>Name:</p>
                                    </div>
                                    <Field name="workReference2.name" autocomplete="off" component={TextField}
                                           minLength={2} maxLength={60}/>
                                </div>

                                <div class="item__box">
                                    <div class="text">
                                        <p>Email:</p>
                                    </div>
                                    <Field name="workReference2.email" autocomplete="off" component={TextField}
                                           validate={validateEmail} minLength={6} maxLength={254}/>
                                </div>

                                <div class="item__box">
                                    <div class="text">
                                        <p>Phone:</p>
                                    </div>
                                    <Field name="workReference2.phone" autocomplete="off" component={TextField}
                                           mask="(999) 999-9999" maskChar=" " normalize={phone}/>

                                </div>
                            </div>
                        </div>

                    </div>
                    </div>

                    <div class="pro__setting-right-mandatory">
                        <p class="mandatory">Fields marked* are mandatory</p>
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
})(ProfileForm);

const MIN_DATE = '1965-01-01';

class Profile extends Component {

    state = {username: undefined, practice: undefined}

    async componentDidMount() {
        let me = this,
            id = me.props.userId,
            fields = Remote.getFieldsByModel(me.props.metaInfo, "ProfessionalModel");


        me.props.onLoadRefs(me.props.references, me.props.metaInfo, function () {
            getDataPromise('professional', id, fields).then(function (data) {
                let managedObject = {profile: data.profile || {}};
                let getWorkExperiences = function (data, index) {
                        if (data.profile.workExperiences && data.profile.workExperiences.length > index) {
                            return data.profile.workExperiences[index];
                        }
                    },
                    getWorkReferences = function (data, index) {
                        if (data.profile.workReferences && data.profile.workReferences.length > index) {
                            return data.profile.workReferences[index];
                        }
                    };

                for (var i = 0; i < 3; i++) {
                    managedObject[`workExperience${i}`] = getWorkExperiences(managedObject, i);
                    managedObject[`workReference${i}`] = getWorkReferences(managedObject, i);
                }
                if (!managedObject.profile.languages || managedObject.profile.languages.length <= 0) {
                    managedObject.profile.languages = [LANGUAGE_ENGLISH];
                }
                me.setState({managedObject: managedObject});
            });
        });

    }

    submit(data) {
        let me = this,
            resultData = ObjectHelper.copyObject(data),
            saveData = {},
            updateArray = function (data, name, len) {
                let has = false;
                for (var i = 0; i < len; i++) {
                    if (data[`${name}${i}`]) {
                        has = true;
                        break;
                    }
                }
                if (has) {
                    data.profile[`${name}s`] = [];
                    for (i = 0; i < len; i++) {
                        if (data[`${name}${i}`]) {
                            data.profile[`${name}s`].push(data[`${name}${i}`]);
                        }
                    }
                }

            }

        updateArray(resultData, "workReference", 3);
        updateArray(resultData, "workExperience", 3);

        saveData['profile'] = resultData.profile;

        Logger.of('App.General.submit').info('General data:', saveData);

        me.props.onSave(saveData);
    }

    changeUserName() {
        UiView.showDialog(<Provider store={UiView.createDialogStore()}><ChangeUserName managedObject={{}} actions={{
            reset: function (data) {
                requestChangeUserName(data.oldpassword, data.username).then(function (result) {
                    if (result) {
                        toastr.success("Change User Name", "Request for changing User Name was sent successfuly.");
                    } else {
                        toastr.error("Change User Name", "Request for changing User Name wasn't sent successfuly.");
                    }
                });
            }
        }}/></Provider>);
    }

    changePassword() {
        UiView.showDialog(<Provider store={UiView.createDialogStore()}><ChangePassword managedObject={{}} actions={{
            reset: function (data) {
                changePassword(data.oldpassword, data.password).then(function (result) {
                    if (result) {
                        toastr.success("Change Password", "Password was changed successfuly.");
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
                  onChangePassword={this.changePassword.bind(this)} page={this} refs={this.props.references}
                  initialValues={this.state.managedObject}/>
        ]);
    }
}

const ProfileConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {
            userId: state.context.currentUser ? state.context.currentUser.id : undefined,
            username: state.context.currentUser ? state.context.currentUser.username : undefined,
            references: state.references,
            metaInfo: state.context.metaInfo
        });
    },
    function (dispatch) {
        return Object.assign(baseViewDispatcherMap(dispatch), {
            onLoadRefs: (references, metaInfo, callback) => {
                let me = this;
                References.updateRefs(["educations", "languages", "academicDegrees"], references, dispatch, metaInfo, me, callback);
            },
            onSave: (managedObject) => {
                dispatch(saveData({queryName: 'updateProfessionalProfile'}, managedObject)).then(
                    function (result) {

                        dispatch({type: EVENT_VIEW_SAVE_DATA, result});
                        toastr.success("Change General Data", "The data has been changed successfully.");

                    });
            }
        });
    })(Profile);

export default ProfileConnector;