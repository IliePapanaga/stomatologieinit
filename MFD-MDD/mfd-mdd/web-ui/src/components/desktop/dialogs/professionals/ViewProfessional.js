import React from 'react';
import {connect} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import GridField from '../../common/form/GridField';
import {DEFAULT_CELL_STYLE, REST_API_PREFIX_GET_USER_PHOTO, STATUS_APPROVED, CATEGORY_ID_FRONT_OFFICE_PERSONNEL, CATEGORY_ID_DENTISTS, CATEGORY_ID_ASSISTANTS, CATEGORY_ID_HYGIENISTS} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import Renderer from '../../../../utils/Renderer';
import saveData from "../../../../actions/common/saveData";
import UiView from "../../../../utils/UiView";

export const dialogInfo = {
    references: ['languages', 'educations']
};

let Form = props => {
    const {handleSubmit, handleCancel} = props;
    const managedObject = props.initialValues.managedObject;
    const photoUrl = REST_API_PREFIX_GET_USER_PHOTO.replace('{0}', managedObject.id);
    let languages = [];
    let certificates = [];
    let specialities = [];

    if (managedObject.profile.languages) {
        managedObject.profile.languages.forEach(function (language) {
            let lang = props.references.languages.find(l => l.id === language);
            languages.push(lang.name);
        });
    }

    managedObject.certificates.forEach(function (certificate) {
        if (certificate.status === STATUS_APPROVED) {
            certificates.push(certificate.type);
        }
    });

    if (managedObject.professionalSubcategories) {
        managedObject.professionalSubcategories.forEach(function (subCategory) {
            let subCategoryName = subCategory.subCategoryName;
            specialities.push(subCategoryName);
        });
    }

    return (
        <form onSubmit={handleSubmit}>
            <div class="profile__main sys__pro-info modal16">
                <div class="profile__main-content-wrapper">

                    <div class="profile__main-content">
                        <div class="profile__main-slide open active">
                            <h2>General</h2>
                        </div>

                        <div class="profile__main-slide-content show">
                            <div class="profile__main-slide-item-wrapper">
                                <div class="profile__main-slide-item">
                                    <img src={photoUrl} alt="" width="80px"/>
                                </div>
                                <div class="profile__main-slide-item">
                                    <div class="item__box">
                                        <div class="title">
                                            <p>Title:</p>
                                        </div>
                                        <div class="data">
                                            <p>{managedObject.contact.name.title}</p>
                                        </div>
                                    </div>

                                    <div class="item__box">
                                        <div class="title">
                                            <p>Last Name:</p>
                                        </div>
                                        <div class="data">
                                            <p>{managedObject.contact.name.last}</p>
                                        </div>
                                    </div>

                                    <div class="item__box">
                                        <div class="title">
                                            <p>First Name:</p>
                                        </div>
                                        <div class="data">
                                            <p>{managedObject.contact.name.first}</p>
                                        </div>
                                    </div>

                                    <div class="item__box">
                                        <div class="title">
                                            <p>Specialty:</p>
                                        </div>
                                        <div class="data">
                                            <p>{specialities.join(', ')}</p>
                                        </div>
                                    </div>

                                    <div class="item__box item__box-salary">
                                        <div class="title">
                                            <p>Desired hourly salary ($/H):<br/><span class="description">(For Temporary Jobs)</span>
                                            </p>
                                        </div>
                                        <div class="data">
                                            <p>{managedObject.jobPreference.desiredRatePerHour}</p>
                                        </div>
                                    </div>

                                    <div class="item__box">
                                        <div class="title">
                                            <p>Language:</p>
                                        </div>
                                        <div class="data">
                                            <p>{languages.join(', ')}</p>
                                        </div>
                                    </div>

                                    <div class="item__box">
                                        <div class="title">
                                            <p>City:</p>
                                        </div>
                                        <div class="data">
                                            <p>{managedObject.contact.address.city}</p>
                                        </div>
                                    </div>

                                    <div class="item__box">
                                        <div class="title">
                                            <p>Certificates:</p>
                                        </div>
                                        <div class="data">
                                            <p>{certificates.join(', ')}</p>
                                        </div>
                                    </div>

                                    <div class="item__box">
                                        <div class="title">
                                            <p>Skills Summary:</p>
                                        </div>
                                        <div class="data">
                                            <p>{managedObject.profile.skillSummary}</p>
                                        </div>
                                    </div>

                                    <div class="item__box">
                                        <div class="title">
                                            <p>Education:</p>
                                        </div>
                                        <div class="data">
                                            <p>{managedObject.profile.education && props.references.educations.find(e => e.id === managedObject.profile.education).name}</p>
                                        </div>
                                    </div>

                                    <div class="item__box">
                                        <div class="title">
                                            <p>Highest degree earned:</p>
                                        </div>
                                        <div class="data">
                                            <p>{managedObject.profile.highestDegree && props.references.academicDegrees.find(e => e.id === managedObject.profile.highestDegree).name}</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>


                    <div class="profile__main-content">

                        <div class="profile__main-slide">
                            <h2>Work experience</h2>
                        </div>

                        <div class="profile__main-slide-content">
                            <div class="profile__main-slide-item-wrapper">

                                <div class="profile__main-slide-item">
                                    <div class="item__box">
                                        <div class="title">
                                            <p>Total experience in dental field:</p>
                                        </div>
                                        <div class="data">
                                            <p>{managedObject.getQuestionnaire.yoeInDental} years</p>
                                        </div>
                                    </div>
                                    <div class="item__box">
                                        <div class="title">
                                            {managedObject.category === CATEGORY_ID_FRONT_OFFICE_PERSONNEL &&
                                            <p>Working experience as a Front Office Manager:</p>}
                                            {managedObject.category === CATEGORY_ID_ASSISTANTS &&
                                            <p>Working experience as a RDA/DA:</p>}
                                            {managedObject.category === CATEGORY_ID_HYGIENISTS &&
                                            <p>Working experience as a RDH:</p>}
                                            {managedObject.category === CATEGORY_ID_DENTISTS &&
                                            <p>Working experience as a RDH:</p>}
                                        </div>
                                        <div class="data">
                                            <p>{managedObject.getQuestionnaire.yoeBySpecialty} years</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <h3>Work experience (latest three jobs):</h3>

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
                                        renderer: Renderer.getDateRendererMonthDayYear
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
                                        name: 'functionThatPerformedThere',
                                        title: 'Function That Performed There',
                                        cellClass: DEFAULT_CELL_STYLE,
                                        sortable: true
                                    }
                                ]}
                                component={GridField}/>
                        </div>
                    </div>
                    <div class="profile__main-content">
                        <div class="profile__main-slide">
                            <h2>Additional Information</h2>
                        </div>

                        <div class="profile__main-slide-content">
                            <div class="profile__main-slide-item-block">
                                <div class="item__box">
                                    <div class="title">
                                        <p>Proficient with next digital radiography and familiar with systems:</p>
                                    </div>
                                    <div class="data">
                                        <p>{managedObject.getQuestionnaire.digitalRadiographySystems}</p>
                                    </div>
                                </div>
                            </div>
                            <div class="profile__main-slide-item-block">
                                {managedObject.getQuestionnaire.specialtiesComfort&&<div class="item__box">
                                    <div class="title">
                                        <p>Percentage of comfort level in following specialties:</p>
                                    </div>
                                    <div class="data">
                                        <div class="item__box">
                                            <div className="item__box-wrapper">
                                                <div className="item__box">
                                                    <div className="title">
                                                        <p>Pedo:</p>
                                                    </div>
                                                    <div className="data">
                                                        <p>{UiView.getAsPercent(managedObject.getQuestionnaire, "specialtiesComfort.pedo")}</p>
                                                    </div>
                                                </div>

                                                <div className="item__box">
                                                    <div className="title">
                                                        <p>Prostho:</p>
                                                    </div>
                                                    <div className="data">
                                                        <p>{UiView.getAsPercent(managedObject.getQuestionnaire, "specialtiesComfort.prostho")}</p>
                                                    </div>
                                                </div>

                                                <div className="item__box">
                                                    <div className="title">
                                                        <p>Perio:</p>
                                                    </div>
                                                    <div className="data">
                                                        <p>{UiView.getAsPercent(managedObject.getQuestionnaire, "specialtiesComfort.perio")}</p>
                                                    </div>
                                                </div>

                                                <div className="item__box">
                                                    <div className="title">
                                                        <p>Endo: </p>
                                                    </div>
                                                    <div className="data">
                                                        <p>{UiView.getAsPercent(managedObject.getQuestionnaire, "specialtiesComfort.endo")}</p>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="item__box-wrapper">
                                                <div className="item__box">
                                                    <div className="title">
                                                        <p>General:</p>
                                                    </div>
                                                    <div className="data">
                                                        <p>{UiView.getAsPercent(managedObject.getQuestionnaire, "specialtiesComfort.general")}</p>
                                                    </div>
                                                </div>

                                                <div className="item__box">
                                                    <div className="title">
                                                        <p>Cosmetic:</p>
                                                    </div>
                                                    <div className="data">
                                                        <p>{UiView.getAsPercent(managedObject.getQuestionnaire, "specialtiesComfort.cosmetic")}</p>
                                                    </div>
                                                </div>

                                                <div className="item__box">
                                                    <div className="title">
                                                        <p>Implants:</p>
                                                    </div>
                                                    <div className="data">
                                                        <p>{UiView.getAsPercent(managedObject.getQuestionnaire, "specialtiesComfort.implants")}</p>
                                                    </div>
                                                </div>

                                                <div className="item__box">
                                                    <div className="title">
                                                        <p>Oral Surgery:</p>
                                                    </div>
                                                    <div className="data">
                                                        <p>{UiView.getAsPercent(managedObject.getQuestionnaire, "specialtiesComfort.oralSurgery")}</p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>}

                                {managedObject.getQuestionnaire.specialtiesFamiliarity&&<div class="item__box">
                                    <div class="title">
                                        <p>Familiar with following specialties:</p>
                                    </div>
                                    <div class="data">
                                        <div class="item__box">
                                            <div class="item__box-wrapper">
                                                <div class="item__box">
                                                    <div class="title">
                                                        <p>Pedo:</p>
                                                    </div>
                                                    <div class="data">
                                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire,"specialtiesFamiliarity.pedo")}</p>
                                                    </div>
                                                </div>

                                                <div class="item__box">
                                                    <div class="title">
                                                        <p>Prostho:</p>
                                                    </div>
                                                    <div class="data">
                                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire,"specialtiesFamiliarity.prostho")}</p>
                                                    </div>
                                                </div>

                                                <div class="item__box">
                                                    <div class="title">
                                                        <p>Perio:</p>
                                                    </div>
                                                    <div class="data">
                                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire,"specialtiesFamiliarity.perio")}</p>
                                                    </div>
                                                </div>

                                                <div class="item__box">
                                                    <div class="title">
                                                        <p>Endo: </p>
                                                    </div>
                                                    <div class="data">
                                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire,"specialtiesFamiliarity.endo")}</p>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="item__box-wrapper">
                                                <div class="item__box">
                                                    <div class="title">
                                                        <p>General:</p>
                                                    </div>
                                                    <div class="data">
                                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire,"specialtiesFamiliarity.general")}</p>
                                                    </div>
                                                </div>

                                                <div class="item__box">
                                                    <div class="title">
                                                        <p>Cosmetic:</p>
                                                    </div>
                                                    <div class="data">
                                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire,"specialtiesFamiliarity.cosmetic")}</p>
                                                    </div>
                                                </div>

                                                <div class="item__box">
                                                    <div class="title">
                                                        <p>Implants:</p>
                                                    </div>
                                                    <div class="data">
                                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire,"specialtiesFamiliarity.implants")}</p>
                                                    </div>
                                                </div>

                                                <div class="item__box">
                                                    <div class="title">
                                                        <p>Oral Surgery:</p>
                                                    </div>
                                                    <div class="data">
                                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire,"specialtiesFamiliarity.oralSurgery")}</p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>}
                            </div>


                            {managedObject.category===CATEGORY_ID_FRONT_OFFICE_PERSONNEL && <div className="profile__main-slide-item-block">
                                {managedObject.getQuestionnaire.managementSoftware &&<div className="item__box">
                                    <div className="title">
                                        <p>Experienced at practice management software:</p>
                                    </div>
                                    <div className="data">
                                        <p>{managedObject.getQuestionnaire.managementSoftware}</p>
                                    </div>
                                </div>}

                                <div className="item__box">
                                    <div className="title">
                                        <p>Familiar with intraoral cameras:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "xRaysAndCameraImagesToInsurance")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Can cross trained front to back:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "crossTrained")}</p>
                                    </div>
                                </div>
                            </div>}

                            {managedObject.category===CATEGORY_ID_DENTISTS && <div className="profile__main-slide-item-block">

                                <div className="item__box">
                                    <div className="title">
                                        <p>Willing to work as RDH for temporary assignments:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "temporaryAsRdh")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Familiar with Cad-Cam (E4D or Cerec):</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "cadCam")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Comfortable with surgery:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "surgery")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Ability to stand 8 hrs on feet:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "hoursOnFeet")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Familiar with intraoral cameras:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "intraOralCam")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Familiar with Pano:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "pano")}</p>
                                    </div>
                                </div>
                            </div>}

                            {managedObject.category===CATEGORY_ID_ASSISTANTS && <div className="profile__main-slide-item-block">
                                {managedObject.getQuestionnaire.managementSoftware &&<div className="item__box">
                                    <div className="title">
                                        <p>Experienced at practice management software:</p>
                                    </div>
                                    <div className="data">
                                        <p>{managedObject.getQuestionnaire.managementSoftware}</p>
                                    </div>
                                </div>}

                                <div className="item__box">
                                    <div className="title">
                                        <p>Familiar with Cad-Cam (E4D or Cerec):</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "cadCam")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Familiar with intraoral cameras:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "intraOralCam")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Familiar with Pano:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "pano")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Can cross trained front to back:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "crossTrained")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Familiar with 3d imaging:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "imaging3D")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Familiar with Cephelometric XRay mashines:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "xray")}</p>
                                    </div>
                                </div>


                                <div className="item__box">
                                    <div className="title">
                                        <p>Familiar with Nomad:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "nomad")}</p>
                                    </div>
                                </div>
                            </div>}

                            {managedObject.category===CATEGORY_ID_HYGIENISTS && <div className="profile__main-slide-item-block">
                                {managedObject.getQuestionnaire.managementSoftware &&<div className="item__box">
                                    <div className="title">
                                        <p>Experienced at practice management software:</p>
                                    </div>
                                    <div className="data">
                                        <p>{managedObject.getQuestionnaire.managementSoftware}</p>
                                    </div>
                                </div>}

                                <div className="item__box">
                                    <div className="title">
                                        <p>Familiar with intraoral cameras:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "intraOralCam")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Familiar with Pano:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "pano")}</p>
                                    </div>
                                </div>

                                <div className="item__box">
                                    <div className="title">
                                        <p>Can call own patients and schedule recare appt:</p>
                                    </div>
                                    <div className="data">
                                        <p>{UiView.getYesNo(managedObject.getQuestionnaire, "recareAppt")}</p>
                                    </div>
                                </div>
                            </div>}
                        </div>

                    </div>
                </div>
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Close</button>
                </div>
            </div>
        </form>)
}

Form = reduxForm({
    form: 'viewpofessional'
})(Form);

const selector = formValueSelector('viewpofessional');

Form = connect(state => {
    const longitude = selector(state, 'managedObject.contact.address.longitude');
    const latitude = selector(state, 'managedObject.contact.address.latitude');
    const zipCode = selector(state, 'managedObject.contact.address.zipCode');
    return {
        showAddressWarning: (!longitude && !latitude) && zipCode && zipCode.indexOf('_') < 0
    }
})(Form)


class ViewProfessional extends BaseDialog {

    async componentDidMount() {
        try {
            let extendedJs = require('../../../../resources/js/main');
            extendedJs.modalInitialization();
        } catch (ex) {
        }
    }

    dialogProps() {
        return {
            width: 960,
            height: 760,
            className: "modal__gray",
            title: "Candidate Profile"
        }
    }

    beforeSave(dialog, managedData) {
        let jobPreference = managedData.managedObject.jobPreference,
            professional = managedData.managedObject;

        delete professional['jobPreference'];
        delete professional['subcategories'];
        saveData({queryName: 'updateProfessionalProfile         '}, {profile: professional['profile']})(null);
        delete professional['profile'];

        managedData.managedObject = {jobPreference: jobPreference, professional: professional};

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}
                      anchor={this.props.anchor}/>);
    }
}

const ViewProfessionalDialogConnector = connect(
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
    })(ViewProfessional);

export default ViewProfessionalDialogConnector;
