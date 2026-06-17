import React from 'react';
import {connect} from 'react-redux';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';
import DateField from '../../common/form/DateField';
import {getDataPromise} from '../../../../actions/common/getData';

import DateHelper from '../../../../utils/DateHelper';
import Renderer from '../../../../utils/Renderer';
import Remote from '../../../../utils/Remote';
import ReactUploadFile from 'react-upload-file';
import {findStatusName} from "../../../../data/Statuses";


class CertificateInfoPage {

    certificateForm: any;
    title: string;
    licenseInfo: string;
    expirationInfo: string;
    certificateInfo: string;


    constructor(fields?: {
        id?: string,
        title?: string,
        licenseInfo?: string,
        expirationInfo?: string,
        certificateInfo?: string
    }) {
        let me = this;
        if (fields) {
            Object.assign(this, fields);
        }
        me.certificateForm = me.getForm();
    }

    statusClassPostfix(certificateDetails) {
        if (certificateDetails && certificateDetails.status) {
            return " row__btn row__btn-" + Renderer.getRowStatusClassPrefix(certificateDetails.status) + ' pressed';
        }
        return '';
    }

    initTemplate(handleSubmit, handleCancel, uploadOptions, managedObject, dialog, allowUpload, readOnly, scope, invalid, change) {
        let me = this;
        let title = me.title;
        let expirationInfo = me.expirationInfo;
        let certificateInfo = me.certificateInfo;
        let classPostfix = me.statusClassPostfix(managedObject.certificateDetails);

        return <form onSubmit={handleSubmit} class="modal__form modal53">
            <ReactUploadFile ref={(el) => {
                scope.uploadFile = el
            }} options={uploadOptions}/>

            <div class="specialties__second-main">
                <h3>{title}</h3>

                <div class="specialties__status">
                    <div class="item__box">
                        <div class="title">
                            <p>Status:</p>
                        </div>
                        <div class="content">
                            <p class={classPostfix}>{managedObject.certificateDetails && findStatusName(managedObject.certificateDetails.status)}</p>
                        </div>
                    </div>
                </div>

                {me.licenseInfo && <div class="item__box">
                    <div class="text">
                        <p>{me.licenseInfo}<span class="star">*</span>:</p>
                    </div>
                    <div class="input__long-spec">
                        <Field name="managedObject.certificateDetails.licenseNumber" component={TextField}
                               readonly={readOnly ? "true" : readOnly}/>
                    </div>
                </div>}
                {me.expirationInfo &&
                <div class="item__box">
                    <div class="text">
                        <p>{expirationInfo}<span class="star">*</span>:</p>
                    </div>
                    <div class="input input__date">
                        <Field name="managedObject.certificateDetails.expirationDate" class="date"
                               placeholder="Select date" minDate={DateHelper.getCurrentDate()}
                               component={DateField} dateFormat="MM/DD/YYYY" readOnly={readOnly}/>
                    </div>
                </div>}

                <div class="item__box">
                    <div class="text">
                        <p>{certificateInfo}<span class="star">*</span>:</p>
                    </div>
                    <Field name="managedObject.certificateDetails.certificate.name" component={TextField}
                           readonly="true"
                           placeholder="File Name"/>
                    <div class="input">
                        <button class="blue" type="button" onClick={function (e) {
                            if (scope.uploadFile) {
                                scope.uploadFile.type = managedObject.certificate.type
                                scope.uploadFile.input.click();
                            }
                        }} disabled={!allowUpload || readOnly}>Upload/Edit
                        </button>
                    </div>
                    <div class="input">
                        <button class="white" type="button"
                                disabled={!managedObject.certificateDetails || !managedObject.certificateDetails.certificate}
                                onClick={dialog.onView.bind(this, managedObject.certificateDetails)}>View
                        </button>
                    </div>
                </div>

                <div class="specialties__comments">
                    <div class="item__box">
                        <div class="title">
                            <p>Comments:</p>
                        </div>
                        <div class="content">
                            <p>{managedObject.certificateDetails && managedObject.certificateDetails.comment}</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="footer__btn-wrapper">
                <div class="specialties__help">
                    <p class="mandatory">Fields marked* are mandatory</p>
                </div>
                <div class="footer__btn">
                    {readOnly &&
                    <button className="blue white" type="button" onClick={handleCancel}>Close</button>
                    }
                    {!readOnly &&
                    <button className="blue white" type="button" onClick={handleSubmit}>Save</button>
                    }
                </div>
            </div>
        </form>;
    }

    getForm() {
        let page = this;

        let Form = props => {
            const {dialog, handleSubmit, change, handleCancel, initialValues, invalid, readOnly} = props;
            let managedObject = props.managedObject || initialValues.managedObject;
            let allowUpload = (!!managedObject.certificateDetails) && (!page.expirationInfo || managedObject.certificateDetails.expirationDate) && (!page.licenseInfo || managedObject.certificateDetails.licenseNumber);

            let uploadOptions = dialog.getUploadOptions(dialog, managedObject,
                function (files) {
                    let me = this,
                        data = {},
                        expirationDate = DateHelper.convertServerDateStringToString(me.props.options.certificateDetails.expirationDate, 'YYYY-MM-DD'),
                        licenseNumber = me.props.options.certificateDetails.licenseNumber,
                        file = me.files[0];

                    let certificateType = managedObject.certificate.type;
                    let index = 0;
                    data[`certificates[${index}].type`] = certificateType;
                    if (page.expirationInfo) {
                        data[`certificates[${index}].expirationDate`] = expirationDate;
                    }
                    if (page.licenseInfo) {
                        data[`certificates[${index}].licenseNumber`] = licenseNumber;
                    }
                    data[`certificates[${index}].file`] = file;
                    return data;
                },
                function (resp) {

                    let fields = Remote.getFieldsByModel(dialog.props.metaInfo,
                        "RequiredCertificateConnection", []);

                    getDataPromise('professionalRequiredCertificates', {}, fields).then(function (result) {
                        fields = Remote.getFieldsByModel(dialog.props.metaInfo,
                            "CertificateDetailsModel", []);

                        getDataPromise('certificateDetails', result.nodes.find(n => n.type === managedObject.certificate.type).certificateId, fields).then(function (certificateDetails) {
                            change("managedObject.certificateDetails", certificateDetails);
                        });
                    });
                });

            return (page.initTemplate(handleSubmit, handleCancel, uploadOptions, managedObject, dialog, allowUpload, readOnly, this, invalid, change));
        }

        Form = reduxForm({
            form: 'uploadcertificate',
            destroyOnUnmount: false,
            forceUnregisterOnUnmount: true
        })(Form);


        const selector = formValueSelector('uploadcertificate');

        Form = connect(state => {
            const managedObject = selector(state, 'managedObject');

            return {
                managedObject: managedObject
            }
        })(Form);

        return Form;
    }
}

export default CertificateInfoPage;