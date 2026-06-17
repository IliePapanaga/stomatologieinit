import React from 'react';
import {connect, Provider} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';
import TextArea from '../../common/form/TextArea';
import CheckboxGroup from '../../common/form/CheckboxGroup';
import YesNoAnswer from '../../common/form/YesNoAnswer';
import SelectField from '../../common/form/SelectField';
import GridField from '../../common/form/GridField';
import {phone} from '../../../../utils/Normalizers';
import {oneShouldBeChecked} from '../../../../utils/Validators';
import {
    DEFAULT_CELL_STYLE,
    REST_API_PREFIX_GET_USER_PHOTO,
    STATUS_REJECTED,
    TEXTFIELD_PATTERN_REGEX,
    TITLES
} from '../../../../utils/Constants';
import {STATES} from '../../../../data/States';
import {COMMUTING_RADIUS} from '../../../../data/CommutingRadius';
import {Logger} from 'react-logger-lib';
import {findStatus} from '../../../../data/Statuses';
import UiView from '../../../../utils/UiView';
import Remote from '../../../../utils/Remote';
import {getDataPromise} from '../../../../actions/common/getData';
import Renderer from '../../../../utils/Renderer';
import ViewCertificates, {dialogInfo as dialogInfoViewCertificates} from '../specialty/ViewCertificates';
import Questionnaire from '../specialty/Questionnaire';
import Error from "../../../../utils/Error";
import {saveDataPromise} from "../../../../actions/common/saveData";
import FindLocation from "../filters/FindLocation";
import findLocationByCoordinates from "../../../../actions/signup/findLocationByCoordinates";
import BrowseDialogButton from '../../common/BrowseDialogButton';
import ObjectHelper from "../../../../utils/Object";
import Execute from '../operations/Execute';
import {toastr} from "react-redux-toastr";
import DateHelper, {uiDateFormatMonthDayYear, serverShortDateFormat} from "../../../../utils/DateHelper";
import moment from "moment/moment";

export const dialogInfo = {
    references: ['languages', 'categories']
}

let Form = props => {
    const {handleSubmit, change, handleCancel, invalid, showAddressWarning, dialog, updateStatus, anchor = 'profile__main-content-general'} = props;
    const managedObject = props.initialValues.managedObject;
    const photoUrl = REST_API_PREFIX_GET_USER_PHOTO.replace('{0}', managedObject.id);
    const {languages} = props.references;
    let changedZipCode = UiView.getFindByZipCodeHandler(change, 'managedObject.contact.address.', null, true);
    return (
        <form onSubmit={handleSubmit}>
            <div class="profile__main sys__pro-info modal44">
                <div class="profile__main-content-wrapper">
                    <div class="profile__main-content-general">

                        <div
                            class={UiView.checkClass(anchor, 'profile__main-content-general', 'profile__main-slide', 'open active')}>
                            <h2>General</h2>
                        </div>

                        <div
                            class={UiView.checkClass(anchor, 'profile__main-content-general', 'profile__main-slide-content', 'show')}>
                            <div class="info">

                                <div class="profile__main-content-general-basic">
                                    <h3>Basic Information</h3>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>Title:</p>
                                        </div>
                                        <div class="input board__input-wrapper">
                                            <Field name="managedObject.contact.name.title" component={SelectField}
                                                   menuItems={TITLES.filter(t => t.value !== 'Miss')} required={false}/>
                                        </div>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>First Name<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="managedObject.contact.name.first" autocomplete="off"
                                               component={TextField} required={true} minLength={2} maxLength={60}
                                               regexPattern={TEXTFIELD_PATTERN_REGEX}/>
                                    </div>

                                    <div class="item__box">
                                        <div class="text">
                                            <p>Last Name<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="managedObject.contact.name.last" autocomplete="off"
                                               component={TextField} required={true} minLength={2} maxLength={60}
                                               regexPattern={TEXTFIELD_PATTERN_REGEX}/>
                                    </div>


                                    <div class="item__box">
                                        <div class="text">
                                            <p>Contact Phone<span class="star">*</span>:</p>
                                        </div>
                                        <Field name="managedObject.contact.phone" autocomplete="off"
                                               component={TextField} required={true} mask="(999) 999-9999" maskChar=" "
                                               normalize={phone}/>
                                    </div>


                                    <div class="item__box item__box-salary">
                                        <div class="text">
                                            <p>Desired hourly salary ($/H)<span
                                                class="star">*</span>:</p>
                                        </div>
                                        <div class="input">
                                            <Field required={true}
                                                   name="managedObject.jobPreference.desiredRatePerHour"
                                                   component="input"
                                                   type="number"
                                                   class="board__input"
                                            />
                                        </div>
                                    </div>

                                    {false && <div class="item__box item__box-salary">
                                        <div class="text">
                                            <p>Desired salary range ($/H)<span
                                                class="description">(For Permanent Job)</span>:</p>
                                        </div>
                                        <div class="input">
                                            <Field required={true}
                                                   name="managedObject.jobPreference.salaryFrom"
                                                   component="input"
                                                   type="number"
                                                   class="board__input"
                                            />
                                            <Field required={true}
                                                   name="managedObject.jobPreference.salaryTo"
                                                   component="input"
                                                   type="number"
                                                   class="board__input"
                                            />
                                        </div>
                                    </div>}


                                    <div class="profile__main-content-general-basic-down">
                                        <h3>Address</h3>
                                        <div class="item__box zip">
                                            <div class="text">
                                                <p>ZIP<span class="star">*</span>: </p>
                                            </div>
                                            {showAddressWarning && [<p class="zip__text">or</p>,
                                                <BrowseDialogButton title="Find My Location" dialog={FindLocation}
                                                                    actions={{
                                                                        save: function (editor, updatedManagedObject, successfulCallBack) {
                                                                            const {lat, lng} = updatedManagedObject;
                                                                            findLocationByCoordinates({
                                                                                lat: lat,
                                                                                lng: lng
                                                                            }).then(function (location) {
                                                                                Logger.of('App.findLocationByCoordinates').info('Location:', location);
                                                                                if (location) {
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
                                                                                }

                                                                                change(`managedObject.contact.address.longitude`, lat);
                                                                                change(`managedObject.contact.address.latitude`, lng);
                                                                                editor.close();
                                                                            });
                                                                        }
                                                                    }} browseProps={{showDistance: false}}/>]}
                                            <Field name="managedObject.contact.address.zipCode" autocomplete="off"
                                                   component={TextField} required={true} mask="99999" maskChar="_"
                                                   onChange={changedZipCode}/>
                                        </div>

                                        <div class="item__box">
                                            <div class="text">
                                                <p>Street<span class="star">*</span>:</p>
                                            </div>
                                            <Field name="managedObject.contact.address.street" autocomplete="off"
                                                   component={TextField} required={true} minLength={2} maxLength={60}/>
                                        </div>

                                        <div class="item__box">
                                            <div class="text">
                                                <p>City<span class="star">*</span>:</p>
                                            </div>
                                            <Field name="managedObject.contact.address.city" autocomplete="off"
                                                   component={TextField} required={true} minLength={2} maxLength={60}/>
                                        </div>

                                        <div class="item__box">
                                            <div class="text">
                                                <p>State<span class="star">*</span>:</p>
                                            </div>
                                            <div class="input board__input-wrapper">
                                                <Field name="managedObject.contact.address.state"
                                                       component={SelectField} menuItems={STATES} required={true}/>
                                            </div>
                                        </div>
                                    </div>

                                </div>
                                {/*-------------------------------------------*/}

                                {false && showAddressWarning && <div class="location__box">
                                    <div class="location__box-text">
                                        <p>Unfortunately, we cannot find your address on the map. Please, make sure the
                                            address is
                                            correct, and if it is, please, <a>Find your location</a> on the map for
                                            us.</p>
                                    </div>
                                </div>}

                                {/*--------------------------------------------*/}
                                <div class="profile__main-content-general-account">
                                    <h3>Account Details</h3>


                                    <div class="profile__main-content-general-account-basic-wrapper">
                                        <div class="photo">
                                            <img src={photoUrl} alt=""/>
                                        </div>


                                        <div class="">
                                            <div class="item__box">
                                                <div class="title">
                                                    <p>Email Address:</p>
                                                </div>
                                                <div class="data">
                                                    <p>{managedObject.contact.email}</p>
                                                </div>
                                            </div>

                                            <div class="item__box">
                                                <div class="title">
                                                    <p>Status:</p>
                                                </div>
                                                <div class="data">
                                                    <p>{findStatus(managedObject.status).name}</p>
                                                </div>
                                            </div>

                                            <div class="item__box">
                                                <div class="title">
                                                    <p>Date Started:</p>
                                                </div>
                                                <div class="data">
                                                    <p>{DateHelper.convertServerDateStringToString(managedObject.dateStarted, uiDateFormatMonthDayYear, true)}</p>
                                                </div>
                                            </div>

                                        </div>

                                    </div>

                                    <div class="info">
                                        <div class="info__item">
                                            <h3>Availability</h3>

                                            <div class="item__check">
                                                <div class="item__check-header">Days of the week<span
                                                    class="star">*</span>:
                                                </div>
                                                <Field name="managedObject.jobPreference.availabilityDays"
                                                       items={[{code: 'MONDAY', name: 'Mon'},
                                                           {code: 'TUESDAY', name: 'Tue'},
                                                           {code: 'WEDNESDAY', name: 'Wed'},
                                                           {code: 'THURSDAY', name: 'Thu'},
                                                           {code: 'FRIDAY', name: 'Fri'},
                                                           {code: 'SATURDAY', name: 'Sat'},
                                                           {code: 'SUNDAY', name: 'Sun'}]} component={CheckboxGroup}
                                                       classWrapper="item__check-content-wrapper"
                                                       itemClassWrapper="item__check-content" required={true}
                                                       validate={oneShouldBeChecked}/>

                                            </div>

                                            <div class="item__box">
                                                <div class="text">
                                                    <p>Radius willing to travel in miles from home location<span
                                                        class="star">*</span>:</p>
                                                </div>
                                                <div class="input board__input-wrapper">
                                                    <Field name="managedObject.jobPreference.commutingRadius"
                                                           component={SelectField} menuItems={COMMUTING_RADIUS}
                                                           required={false}/>
                                                </div>
                                            </div>


                                            <div class="item__radio-next">
                                                <h3>Search Job Options</h3>
                                                <div class="info__item">

                                                    <div class="item__radio">
                                                        <div class="item__radio-header">Looking for permament
                                                            position:
                                                        </div>
                                                        <Field name="managedObject.jobPreference.lookingForPermanentJob"
                                                               component={YesNoAnswer}/>
                                                    </div>


                                                    <div class="item__radio">
                                                        <div class="item__radio-header">Looking for temporary
                                                            position:
                                                        </div>
                                                        <Field name="managedObject.jobPreference.lookingForTemporaryJob"
                                                               component={YesNoAnswer}/>
                                                    </div>

                                                    <div class="item__radio">
                                                        <div class="item__radio-header">Full Time:</div>
                                                        <Field name="managedObject.jobPreference.lookingForFullTimeJob"
                                                               component={YesNoAnswer}/>
                                                    </div>

                                                    <div class="item__radio">
                                                        <div class="item__radio-header">Part Time:</div>
                                                        <Field name="managedObject.jobPreference.lookingForPartTimeJob"
                                                               component={YesNoAnswer}/>
                                                    </div>

                                                    <div class="item__radio">
                                                        <div class="item__radio-header">Availability in the
                                                            evenings:
                                                        </div>
                                                        <Field name="managedObject.jobPreference.eveningWorkingHoursOk"
                                                               component={YesNoAnswer}/>
                                                    </div>


                                                </div>
                                            </div>

                                        </div>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="profile__main-content-spec">

                        <div
                            class={UiView.checkClass(anchor, 'profile__main-content-spec', 'profile__main-slide', 'open active')}>
                            <h2>Specialties</h2>
                        </div>

                        <div
                            class={UiView.checkClass(anchor, 'profile__main-content-spec', 'profile__main-slide-content', 'show')}>

                            <Field
                                name="managedObject.subcategories"
                                columns={[
                                    {
                                        dataIndex: 'id',
                                        name: 'id',
                                        title: 'Id',
                                        hidden: true,
                                        cellClass: DEFAULT_CELL_STYLE
                                    },
                                    {
                                        dataIndex: 'categoryName',
                                        name: 'categoryName',
                                        title: 'Category',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'subCategoryName',
                                        name: 'subCategoryName',
                                        title: 'Sub-category',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'status',
                                        name: 'status',
                                        title: 'Document Status',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        renderer: Renderer.getStatusRenderer(),
                                        sortable: true
                                    }
                                ]}
                                component={GridField}
                                actions={[
                                    {
                                        label: "Questionnaire",
                                        hasSelectedRows: 1,
                                        onClick: function (Dialog, dialogInfo, button) {
                                            let me = this;
                                            const {selection} = me.state;
                                            if (selection.length > 0) {
                                                try {
                                                    if (ObjectHelper.isObject(selection[0])) {

                                                        let selectedObject = selection[0];

                                                        let categoryId = props.references.categories.find(c => c.name === selectedObject.categoryName).id;

                                                        let fields = Remote.getFieldsByModel(dialog.props.metaInfo,
                                                            "Questionnaire", []);

                                                        getDataPromise("getQuestionnaire", {
                                                            category: categoryId,
                                                            professional: managedObject.id
                                                        }, fields).then(function (mObject) {
                                                            mObject.categoryName = selectedObject.categoryName;


                                                            let convertToArraysFn = function (parent, objectName) {
                                                                if (parent[objectName]) {
                                                                    let arr = [];
                                                                    for (var key in parent[objectName]) {
                                                                        if (parent[objectName][key])
                                                                            arr.push(key);
                                                                    }
                                                                    parent[objectName] = arr;
                                                                }
                                                            }

                                                            convertToArraysFn(mObject, 'specialtiesFamiliarity');
                                                            convertToArraysFn(mObject, 'duties');


                                                            UiView.showDialog(<Provider
                                                                store={UiView.createDialogStore()}><Questionnaire
                                                                managedObject={mObject} parent={me}
                                                                references={props.references}
                                                                readOnly={true}/></Provider>);
                                                        });

                                                    }
                                                } catch (ex) {
                                                    Error.showErrors(ex)
                                                }
                                            }
                                        }
                                    }
                                ]}/>

                        </div>
                    </div>
                    <div className="profile__main-content-cert">

                        <div
                            className={UiView.checkClass(anchor, 'profile__main-content-cert', 'profile__main-slide', 'open active')}>
                            <h2>Certificates</h2>
                        </div>

                        <div
                            className={UiView.checkClass(anchor, 'profile__main-content-cert', 'profile__main-slide-content', 'show')}>

                            <Field
                                name="managedObject.certificates"
                                columns={[
                                    {
                                        dataIndex: 'certificateId',
                                        name: 'certificateId',
                                        title: 'Id',
                                        hidden: true,
                                        cellClass: DEFAULT_CELL_STYLE
                                    },
                                    {
                                        dataIndex: 'type',
                                        name: 'type',
                                        title: 'Type',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        renderer: Renderer.getCertificateTypeTitle(),
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'status',
                                        name: 'status',
                                        title: 'Status',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true,
                                        renderer: Renderer.getStatusRenderer()
                                    },
                                    {
                                        dataIndex: 'optional',
                                        name: 'optional',
                                        title: 'Required',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true,
                                        renderer: Renderer.getRequiredRenderer(true)
                                    }
                                ]}
                                component={GridField}
                                actions={[
                                    {
                                        label: "view",
                                        hasSelectedRows: 1,
                                        dialog: ViewCertificates,
                                        dialogInfo: dialogInfoViewCertificates,
                                        isEnabled: function (sel) {
                                            return sel[0].certificateId
                                        },
                                        onClick: function (Dialog, dialogInfo, button) {
                                            let me = this;
                                            const {selection} = me.state;
                                            if (selection.length > 0) {
                                                try {
                                                    if (ObjectHelper.isObject(selection[0])) {

                                                        let fields = Remote.getFieldsByModel(dialog.props.metaInfo,
                                                            "CertificateDetailsModel", []);

                                                        getDataPromise('certificateDetails', selection[0].certificateId, fields).then(function (certificateDetails) {

                                                            if (certificateDetails.expirationDate) {
                                                                certificateDetails.expirationDate = moment(certificateDetails.expirationDate, serverShortDateFormat).format(uiDateFormatMonthDayYear);
                                                            }
                                                            let actions = {
                                                                save: me.saveObject(me.requestInfo).bind(me)
                                                            }
                                                            UiView.showDialog(<Provider
                                                                store={UiView.createDialogStore()}><ViewCertificates
                                                                managedObject={certificateDetails} parent={me}
                                                                actions={actions}
                                                                updateStatus={updateStatus}/></Provider>);


                                                        });

                                                    }
                                                } catch (ex) {
                                                    Error.showErrors(ex)
                                                }
                                            }
                                        }
                                    }
                                ]}/>

                        </div>
                    </div>
                    <div class="profile__main-content-profile">
                        <div
                            class={UiView.checkClass(anchor, 'profile__main-content-profile', 'profile__main-slide', 'open active')}>
                            <h2>Profile</h2>
                        </div>

                        <div
                            class={UiView.checkClass(anchor, 'profile__main-content-profile', 'profile__main-slide-content', 'show')}>
                            <div class="info">
                                <div class="info__item">
                                    <div class="profile__item">

                                        <div class="item__box-wrapper">
                                            <div class="item__box">
                                                <div class="title">
                                                    <p>Skills Summary:</p>
                                                </div>
                                                <div class="data">
                                                    <p>{managedObject.profile.skillSummary} </p>
                                                </div>
                                            </div>

                                            <div class="item__box">
                                                <div class="title">
                                                    <p>Highest degree earned:</p>
                                                </div>
                                                <div class="data">
                                                    <p>{managedObject.profile.highestDegree}</p>
                                                </div>
                                            </div>

                                        </div>

                                        <div class="item__box-wrapper">

                                            <div class="item__box">
                                                <div class="title">
                                                    <p>Education:</p>
                                                </div>
                                                <div class="data">
                                                    <p>{managedObject.profile.education}</p>
                                                </div>
                                            </div>

                                            <div class="item__box">
                                                <div class="title">
                                                    <p>Language:</p>
                                                </div>
                                                <Field name="managedObject.profile.languages" component={TextField}
                                                       menuItems={languages} readonly="true"/>
                                            </div>
                                        </div>
                                    </div>


                                </div>

                                <h3>Work experience (last three jobs):</h3>

                                <Field
                                    name="managedObject.profile.workExperiences"
                                    columns={[
                                        {
                                            dataIndex: 'id',
                                            name: 'id',
                                            title: 'Id',
                                            hidden: true,
                                            cellClass: DEFAULT_CELL_STYLE
                                        },
                                        {
                                            dataIndex: 'hireDate',
                                            name: 'fromTo',
                                            title: 'From-To',
                                            cellClass: DEFAULT_CELL_STYLE,
                                            sortable: true,
                                            renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                                                value = DateHelper.convertServerDateStringToString(value, uiDateFormatMonthDayYear);
                                                let leaveDate = row['leaveDate'];
                                                leaveDate = DateHelper.convertServerDateStringToString(leaveDate, uiDateFormatMonthDayYear);
                                                return <div class={cellClass}>{value} - {leaveDate}</div>;
                                            }
                                        },
                                        {
                                            dataIndex: 'companyName',
                                            name: 'companyName',
                                            title: 'Company Name',
                                            cellClass: DEFAULT_CELL_STYLE,
                                            sortable: true
                                        },
                                        {
                                            dataIndex: 'responsibilities',
                                            name: 'responsibilities',
                                            title: 'Functions performed there',
                                            cellClass: DEFAULT_CELL_STYLE,
                                            sortable: true
                                        }
                                    ]}
                                    component={GridField}/>


                                <h3>Please provide contact information of your last employers:</h3>
                                <Field
                                    name="managedObject.profile.workReferences"
                                    columns={[
                                        {
                                            dataIndex: 'id',
                                            name: 'id',
                                            title: 'Id',
                                            hidden: true,
                                            cellClass: DEFAULT_CELL_STYLE
                                        },
                                        {
                                            dataIndex: 'name',
                                            name: 'name',
                                            title: 'Name',
                                            cellClass: DEFAULT_CELL_STYLE,
                                            sortable: true
                                        },
                                        {
                                            dataIndex: 'phone',
                                            name: 'phone',
                                            title: 'Phone',
                                            cellClass: DEFAULT_CELL_STYLE,
                                            sortable: true
                                        },
                                        {
                                            dataIndex: 'email',
                                            name: 'email',
                                            title: 'Email',
                                            cellClass: DEFAULT_CELL_STYLE,
                                            sortable: true
                                        }
                                    ]}
                                    component={GridField}/>


                                <div class="info__item">
                                    {/*  <div class="profile__item">
                                        <h3>Professional reference</h3>
                                        {managedObject.profile.workReferences.length > 0 && managedObject.profile.workReferences.map((workReference, index) => (

<div>
                                            <div class="item__box">
                                                <div class="title">
                                                    <p>Name:</p>
                                                </div>
                                                <div class="data">
                                             <p>{workReference.name}</p>
                                              </div>
                                            </div>

                                            <div class="item__box">
                                            <div class="title">
                                            <p>E-mail:</p>
                                            </div>
                                            <div class="data">
                                            <p>{workReference.email}</p>
                                            </div>
                                            </div>

                                            <div class="item__box">
                                            <div class="title">
                                            <p>Phone:</p>
                                            </div>
                                            <div class="data">
                                            <p>{workReference.phone}</p>
                                            </div>
                                            </div>
</div>
                                        ))}

                                    </div>*/}
                                    {/*         <div class="profile__item">
                                        <h3>Work experience (last three jobs)</h3>
                                        {managedObject.profile.workExperiences.length > 0 && managedObject.profile.workExperiences.map((workExperience, index) => (
                                            <div class="item__box">
                                                <div class="title">
                                                    <p>{DateHelper.convertServerDateStringToString(workExperience.hireDate, 'MM/YYYY')}– {DateHelper.convertServerDateStringToString(workExperience.leaveDate, 'MM/YYYY')}</p>
                                                </div>
                                                <div class="data">
                                                    <p>{workExperience.companyName}</p>
                                                    <p>{workExperience.responsibilities}</p>
                                                </div>
                                            </div>
                                        ))}
                                    </div>*/}
                                </div>
                            </div>
                        </div>

                    </div>
                    <div class="profile__main-content-general-blacklists">
                        <div
                            class={UiView.checkClass(anchor, 'profile__main-content-general-blacklists', 'profile__main-slide', 'open active')}>
                            <h2>Blacklists</h2>
                        </div>
                        <div
                            class={UiView.checkClass(anchor, 'profile__main-content-general-blacklists', 'profile__main-slide-content', 'show')}>

                            <h3>Professional blacklisted by Practices:</h3>
                            <Field
                                name="managedObject.blackListedProfessionalDetails"
                                columns={[
                                    {
                                        dataIndex: 'practiceId',
                                        name: 'id',
                                        title: 'Id',
                                        hidden: true,
                                        cellClass: DEFAULT_CELL_STYLE
                                    },
                                    {
                                        dataIndex: 'practiceOwnerFirstName',
                                        name: 'practiceOwnerFirstName',
                                        title: 'Client',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true,
                                        renderer(value, cellClass, row, columnIndex, rowIndex) {
                                            return <div class={cellClass}>
                                                {`${value} ${row.practiceOwnerLastName}`}
                                            </div>
                                        }
                                    },
                                    {
                                        dataIndex: 'practiceName',
                                        name: 'practiceName',
                                        title: 'Company Name',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    // {
                                    //     dataIndex: 'practiceLocationName',
                                    //     name: 'practiceLocationName',
                                    //     title: 'Practice Name',
                                    //     cellClass: DEFAULT_CELL_STYLE,
                                    //     sortable: true
                                    // },
                                    {
                                        dataIndex: 'blackListDate',
                                        name: 'blackListDate',
                                        title: 'BlackList Date',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true,
                                        renderer: Renderer.getDateRenderer
                                    },
                                    {
                                        dataIndex: 'unblackListDate',
                                        name: 'unblackListDate',
                                        title: 'Unblacklist Date',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true,
                                        renderer: Renderer.getDateRenderer
                                    }
                                ]}
                                component={GridField}/>
                            <h3>Practices blacklisted by Professional:</h3>
                            <Field
                                name="managedObject.blackListedLocationDetails"
                                columns={[
                                    {
                                        dataIndex: 'practiceId',
                                        name: 'id',
                                        title: 'Id',
                                        hidden: true,
                                        cellClass: DEFAULT_CELL_STYLE
                                    },
                                    {
                                        dataIndex: 'practiceOwnerFirstName',
                                        name: 'practiceOwnerFirstName',
                                        title: 'Client',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true,
                                        renderer(value, cellClass, row, columnIndex, rowIndex) {
                                            return <div class={cellClass}>
                                                {`${value} ${row.practiceOwnerLastName}`}
                                            </div>
                                        }
                                    },
                                    {
                                        dataIndex: 'practiceName',
                                        name: 'practiceName',
                                        title: 'Company Name',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'practiceLocationName',
                                        name: 'practiceLocationName',
                                        title: 'Practice Name',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'blackListDate',
                                        name: 'blackListDate',
                                        title: 'BlackList Date',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true,
                                        renderer: Renderer.getDateRenderer
                                    },
                                    {
                                        dataIndex: 'unblackListDate',
                                        name: 'unblackListDate',
                                        title: 'Unblacklist Date',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true,
                                        renderer: Renderer.getDateRenderer
                                    }
                                ]}
                                component={GridField}/>
                        </div>
                    </div>
                    <div class="profile__main-content-general-problem">
                        <div
                            class={UiView.checkClass(anchor, 'profile__main-content-general-problem', 'profile__main-slide', 'open active')}>
                            <h2>Problems</h2>
                        </div>

                        <div
                            class={UiView.checkClass(anchor, 'profile__main-content-general-problem', 'profile__main-slide-content', 'show')}>
                            <h3>No Show And Rejections by Dental Offices:</h3>

                            <Field
                                name="managedObject.professionalNoShows"
                                columns={[
                                    {
                                        dataIndex: 'id',
                                        name: 'id',
                                        title: 'Id',
                                        hidden: true,
                                        cellClass: DEFAULT_CELL_STYLE
                                    },
                                    {
                                        dataIndex: 'type',
                                        name: 'type',
                                        title: 'Type',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'lastName',
                                        name: 'lastName',
                                        title: 'Client Last Name',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'firstName',
                                        name: 'firstName',
                                        title: 'Client First Name',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'office',
                                        name: 'office',
                                        title: 'Office',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'posting',
                                        name: 'posting',
                                        title: 'Posting',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'date',
                                        name: 'date',
                                        title: 'Date',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true,
                                        renderer: Renderer.getDateRenderer
                                    },
                                    {
                                        dataIndex: 'status',
                                        name: 'status',
                                        title: 'Status',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        renderer: Renderer.getStatusRenderer(),
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'comments',
                                        name: 'comments',
                                        title: 'Comments',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    }
                                ]}
                                actions={[{
                                    label: "Edit",
                                    disallowedValues: [STATUS_REJECTED],
                                    disallowedByFieldName: "type",
                                    hasSelectedRows: 1,
                                    onClick: function (Dialog, dialogInfo, button) {
                                        let me = this;
                                        const {selection} = me.state;
                                        if (selection.length > 0) {
                                            try {
                                                if (ObjectHelper.isObject(selection[0])) {

                                                    let actions = {
                                                        save: function (editor, updatedManagedObject, successfulCallBack) {
                                                            saveDataPromise({
                                                                queryName: 'updateNoShow',
                                                                showLoader: true
                                                            }, {
                                                                updateNoShow: {
                                                                    id: selection[0].id,
                                                                    comments: updatedManagedObject.comment
                                                                }
                                                            }).then(function (result) {
                                                                selection[0].comments = updatedManagedObject.comment;
                                                                me.setState({selection: []});
                                                                editor.close();
                                                            });
                                                        }
                                                    }
                                                    UiView.showDialog(<Provider
                                                        store={UiView.createDialogStore()}><Execute
                                                        managedObject={{comment: selection[0].comments}} parent={me}
                                                        actions={actions}
                                                        title='Modify No Show'
                                                        width={470}
                                                        height={410}
                                                        body={<div class="attention__main modal9">
                                                            <div class="attention__main-item">
                                                                <div class="text">
                                                                    <br/>
                                                                    <p>Are you sure to modify No Show?</p>
                                                                </div>
                                                                <div class="item__box">
                                                                    <div class="text">
                                                                        <p>Comments:</p>
                                                                    </div>
                                                                    <div class="input">
                                                                        <Field name="managedObject.comment"
                                                                               component={TextArea}
                                                                               placeholder="Your text..."
                                                                               required={true}/>
                                                                    </div>
                                                                </div>
                                                                <div class="text">
                                                                    <p class="mandatory">Fields marked* are
                                                                        mandatory</p>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        }
                                                    /></Provider>);

                                                }
                                            } catch (ex) {
                                                Error.showErrors(ex)
                                            }
                                        }
                                    }
                                },
                                    {
                                        label: "Delete",
                                        disallowedValues: [STATUS_REJECTED],
                                        disallowedByFieldName: "type",
                                        hasSelectedRows: 1,
                                        onClick: function (Dialog, dialogInfo, button) {
                                            let me = this;
                                            const {selection} = me.state;
                                            if (selection.length > 0) {
                                                try {

                                                    const toastrConfirmOptions = {
                                                        onOk: function () {
                                                            try {
                                                                saveDataPromise({
                                                                    queryName: 'dismissNoShow',
                                                                    showLoader: true
                                                                }, {
                                                                    dismissNoShow: {
                                                                        id: selection[0].id,
                                                                        comments: ''
                                                                    }
                                                                }).then(function (result) {
                                                                    me.setState({nodes: me.state.nodes.filter(n => n.id !== selection[0].id)});
                                                                });
                                                            } catch (ex) {
                                                                Error.showErrors(ex)
                                                            }

                                                        },
                                                        onCancel: () => console.log('CANCEL: clicked')
                                                    };
                                                    toastr.confirm(<div className="modal57">
                                                        <div className="header activate"><h2>Delete</h2></div>
                                                        <div className="body">Are you sure you want to delete the
                                                            selection?
                                                        </div>
                                                    </div>, toastrConfirmOptions);
                                                } catch (ex) {
                                                    Error.showErrors(ex)
                                                }
                                            }
                                        }
                                    }]}
                                component={GridField}/>
                            <h3>Denials by Professional:</h3>
                            <Field
                                name="managedObject.professionalRejections"
                                columns={[
                                    {
                                        dataIndex: 'id',
                                        name: 'id',
                                        title: 'Id',
                                        hidden: true,
                                        cellClass: DEFAULT_CELL_STYLE
                                    },
                                    {
                                        dataIndex: 'lastName',
                                        name: 'lastName',
                                        title: 'Client Last Name',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'firstName',
                                        name: 'firstName',
                                        title: 'Client First Name',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'office',
                                        name: 'office',
                                        title: 'Office',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'posting',
                                        name: 'posting',
                                        title: 'Posting',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'date',
                                        name: 'date',
                                        title: 'Date',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true,
                                        renderer: Renderer.getDateRenderer
                                    },
                                    {
                                        dataIndex: 'status',
                                        name: 'status',
                                        title: 'Status',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        renderer: Renderer.getStatusRenderer(),
                                        sortable: true
                                    },
                                    {
                                        dataIndex: 'comments',
                                        name: 'comments',
                                        title: 'Comments',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    }
                                ]}
                                actions={[{
                                    label: "Edit",
                                    disallowedValues: [STATUS_REJECTED],
                                    disallowedByFieldName: "type",
                                    hasSelectedRows: 1,
                                    onClick: function (Dialog, dialogInfo, button) {
                                        let me = this;
                                        const {selection} = me.state;
                                        if (selection.length > 0) {
                                            try {
                                                if (ObjectHelper.isObject(selection[0])) {

                                                    let actions = {
                                                        save: function (editor, updatedManagedObject, successfulCallBack) {
                                                            saveDataPromise({
                                                                queryName: 'updateRejection',
                                                                showLoader: true
                                                            }, {
                                                                rejection: {
                                                                    id: selection[0].id,
                                                                    comments: updatedManagedObject.comment
                                                                }
                                                            }).then(function (result) {
                                                                selection[0].comments = updatedManagedObject.comment;
                                                                me.setState({selection: []});
                                                                editor.close();
                                                            });
                                                        }
                                                    }
                                                    UiView.showDialog(<Provider
                                                        store={UiView.createDialogStore()}><Execute
                                                        managedObject={{comment: selection[0].comments}} parent={me}
                                                        actions={actions}
                                                        title='Modify Denials'
                                                        width={470}
                                                        height={410}
                                                        body={<div class="attention__main modal9">
                                                            <div class="attention__main-item">
                                                                <div class="text">
                                                                    <br/>
                                                                    <p>Are you sure to modify Denials ?</p>
                                                                </div>
                                                                <div class="item__box">
                                                                    <div class="text">
                                                                        <p>Comments:</p>
                                                                    </div>
                                                                    <div class="input">
                                                                        <Field name="managedObject.comment"
                                                                               component={TextArea}
                                                                               placeholder="Your text..."
                                                                               required={true}/>
                                                                    </div>
                                                                </div>
                                                                <div class="text">
                                                                    <p class="mandatory">Fields marked* are
                                                                        mandatory</p>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        }
                                                    /></Provider>);

                                                }
                                            } catch (ex) {
                                                Error.showErrors(ex)
                                            }
                                        }
                                    }
                                },
                                    {
                                        label: "Delete",
                                        disallowedValues: [STATUS_REJECTED],
                                        disallowedByFieldName: "type",
                                        hasSelectedRows: 1,
                                        onClick: function (Dialog, dialogInfo, button) {
                                            let me = this;
                                            const {selection} = me.state;
                                            if (selection.length > 0) {
                                                try {

                                                    const toastrConfirmOptions = {
                                                        onOk: function () {
                                                            try {
                                                                saveDataPromise({
                                                                    queryName: 'dismissRejection',
                                                                    showLoader: true
                                                                }, {
                                                                    rejection: {
                                                                        id: selection[0].id,
                                                                        comments: ''
                                                                    }
                                                                }).then(function (result) {
                                                                    me.setState({nodes: me.state.nodes.filter(n => n.id !== selection[0].id)});
                                                                });
                                                            } catch (ex) {
                                                                Error.showErrors(ex)
                                                            }

                                                        },
                                                        onCancel: () => console.log('CANCEL: clicked')
                                                    };
                                                    toastr.confirm(<div className="modal57">
                                                        <div className="header activate"><h2>Delete</h2></div>
                                                        <div className="body">Are you sure you want to delete the
                                                            selection?
                                                        </div>
                                                    </div>, toastrConfirmOptions);
                                                } catch (ex) {
                                                    Error.showErrors(ex)
                                                }
                                            }
                                        }
                                    }]}
                                component={GridField}/>
                        </div>
                    </div>
                </div>

                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Cancel</button>
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>
                </div>
            </div>
        </form>)
}

Form = reduxForm({
    form: 'addeditpofessional'
})(Form);

const selector = formValueSelector('addeditpofessional');

Form = connect(state => {
    const longitude = selector(state, 'managedObject.contact.address.longitude');
    const latitude = selector(state, 'managedObject.contact.address.latitude');
    const zipCode = selector(state, 'managedObject.contact.address.zipCode');
    return {
        showAddressWarning: (!longitude && !latitude) && zipCode && zipCode.indexOf('_') < 0
    }
})(Form)


class AddEditProfessional extends BaseDialog {

    async componentDidMount() {
        try {
            let extendedJs = require('../../../../resources/js/main');
            extendedJs.modalInitialization();
        } catch (ex) {
        }
    }

    dialogProps() {
        return {
            width: 990,
            height: 700,
            className: "modal__gray",
            title: "Professional Info"
        }
    }

    beforeSave(dialog, managedData) {
        let jobPreference = managedData.managedObject.jobPreference,
            professional = managedData.managedObject;

        delete professional['jobPreference'];
        delete professional['subcategories'];
        delete professional['certificates'];
        delete professional['blackListedLocationDetails'];
        delete professional['blackListedProfessionalDetails'];
        delete professional['professionalNoShows'];
        delete professional['professionalRejections'];

        //saveData({queryName: 'updateProfessionalProfile'}, {profile: professional['profile']})(null);
        delete professional['profile'];

        managedData.managedObject = {jobPreference: jobPreference, professional: professional};

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}
                      anchor={this.props.anchor} updateStatus={this.props.updateStatus}/>);
    }
}

const AddEditProfessionalDialogConnector = connect(
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
    })(AddEditProfessional);

export default AddEditProfessionalDialogConnector;
