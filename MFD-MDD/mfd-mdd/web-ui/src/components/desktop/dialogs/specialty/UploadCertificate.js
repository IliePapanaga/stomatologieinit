import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';

import AdditionalAssistantInfoPage from './AdditionalAssistantInfoPage';
import CPRInfoPage from './CPRInfoPage';
import NPIInfoPage from './NPIInfoPage'
import {simpleSave} from '../../../../actions/common/saveData';
import {Logger} from 'react-logger-lib';
import Cookies from 'universal-cookie';


import {
    CERTIFICATE_TYPE_DA,
    REST_API_PREFIX_UPLOAD_CERTIFICATE,
    REST_API_PREFIX_VIEW_CERTIFICATE,
    SUB_CATEGORY_ID_CDA,
    SUB_CATEGORY_ID_CPR,
    SUB_CATEGORY_ID_DDS_OR_DMD,
    SUB_CATEGORY_ID_DEA,
    SUB_CATEGORY_ID_DIODE_LASER,
    SUB_CATEGORY_ID_ENDODONTIC_ASSISTANT,
    SUB_CATEGORY_ID_LIABILITY,
    SUB_CATEGORY_ID_NPI,
    SUB_CATEGORY_ID_ORAL_SURGERY_ASSISTANT,
    SUB_CATEGORY_ID_ORTHODONTIC_ASSISTANT,
    SUB_CATEGORY_ID_PEDODONTIC_ASSISTANT,
    SUB_CATEGORY_ID_PERIODONTAL_ASSISTANT,
    SUB_CATEGORY_ID_RDA,
    SUB_CATEGORY_ID_RDH,
    SUB_CATEGORY_ID_RDHAP,
    SUB_CATEGORY_ID_XRAY,
    CERTIFICATE_TYPE_TITLES
} from '../../../../utils/Constants';
import {toastr} from "react-redux-toastr";
import CertificateInfoPage from "./CertificateInfoPage";
import DateHelper from "../../../../utils/DateHelper";
import Error from "../../../../utils/Error";
import {getDataPromise} from "../../../../actions/common/getData";
import Remote from "../../../../utils/Remote";

export const dialogInfo = {}

const cookies = new Cookies();

export class UploadCertificate extends BaseDialog {

    dialogProps() {
        let action = this.props.readOnly ? 'View' : 'Upload/Edit';
        const titles = CERTIFICATE_TYPE_TITLES.find(t => t.code === this.props.managedObject.certificate.type);
        const type = titles ? (titles.title.toLowerCase().indexOf('certificate')>=0 ? titles.title : `${titles.title} certificate`) : `${this.props.managedObject.certificate.type} certificate`;
        let title = `${action} ${type}`;
        title = title.replace(new RegExp("_", "g"), ' ');

        return {
            width: 640,
            height: 630,
            className: "modal__gray",
            title: title
        }
    }

    beforeSave(dialog, managedData) {
        delete managedData.managedObject['category'];
        delete managedData.managedObject['addedSubCategories'];

        Logger.of('App.AddEditSpecialty.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    getUploadOptions(dialog, managedObject, bodyFn, uploadSuccessFn) {

        return {
            baseUrl: REST_API_PREFIX_UPLOAD_CERTIFICATE,
            body: bodyFn,
            multiple: true,
            accept: 'image/png,image/bmp,image/jpeg,image/gif, image/tiff, application/pdf,application/vnd.ms-excel,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document',
            fileFieldName: 'file',
            withCredentials: false,
            requestHeaders: {
                'X-XSRF-TOKEN': cookies.get('XSRF-TOKEN')

            },
            beforeChoose: () => {
                return true;
            },
            didChoose: (files) => {
                console.log('you choose', typeof files === 'string' ? files : files[0].name);
            },
            beforeUpload: (files) => {
                let allowed = files && files.length > 0 && files[0].size && files[0].size < 5 * Math.pow(1024, 2);
                if (!allowed) {
                    toastr.info("Upload certificate", `The certificate size exceeds 5 MB`);
                }
                return allowed;
            },
            didUpload: (files) => {
                console.log('you just uploaded', typeof files === 'string' ? files : files[0].name);
            },
            uploading: (progress) => {
                console.log('loading...', progress.loaded / progress.total + '%');
            },
            uploadSuccess: uploadSuccessFn,
            uploadError: (err) => {
            },
            dialog: dialog,
            certificateDetails: managedObject.certificateDetails,
            managedObject: managedObject,
            type: undefined,
            chooseFileButton: < button type="button"/>,
            render: function () {
                const inputProps = {
                    accept: this.props.options.accept,
                    multiple: this.props.options.multiple
                };
                const chooseFileButton = React.cloneElement(this.props.chooseFileButton, {
                    type: "button",
                    onClick: this.commonChooseFile
                }, [(< input type="file" name="ajax-upload-file-input" style={{display: 'none'}}
                             onChange={this.commonChangeFile.bind(this)} {...inputProps} key={"file-button"}/>)]);
                return (
                    <div style={{display: 'none'}}>
                        {chooseFileButton}
                    </div>
                )
            }
        }
    }

    getCertDetails(listCertificateDetails, certificateType, subCategories) {
        if (listCertificateDetails) {
            let certificateDetails = listCertificateDetails.filter(function (item) {
                return subCategories.includes(item.subcategory.id);
            });
            if (certificateDetails) {
                certificateDetails = certificateDetails.filter(function (item) {
                    return item.certificateDetails.find(function (item) {
                        return item.certificateType.id === certificateType;
                    });
                });
                if (certificateDetails.length > 0) {
                    return certificateDetails[0].certificateDetails.find(function (item) {
                        return item.certificateType.id === certificateType;
                    });
                }
            }
        }
        return null;
    }


    isAllowedCertificateTypeForSubCategory(certificateType, category, subCategory) {
        let categories = this.props.references.categories;
        let categoryObject = categories.find(c => c.id === category);
        if (categoryObject && categoryObject.subCategories && categoryObject.subCategories.length > 0) {
            let subCategoryObject = categoryObject.subCategories.find(subc => subc.id === subCategory);
            if (subCategoryObject && subCategoryObject.certificateTypes && subCategoryObject.certificateTypes.length > 0) {
                let certificateTypeObject = subCategoryObject.certificateTypes.find(cType => cType.id === certificateType);
                return certificateTypeObject != null;
            }
        }
        return false;
    }

    onView(certificate, event) {
        var windowWidth = window.innerWidth;
        var windowHeight = window.innerHeight;
        var width = 900,
            height = 800,
            left = (windowWidth / 2) - (width / 2),
            top = (windowHeight / 2) - (height / 2);

        window.open(`${REST_API_PREFIX_VIEW_CERTIFICATE}/${certificate.certificate.id}`,
            `View certificate ${certificate.certificate.name}`,
            "centerscreen=yes,width=" + 960 + ",height=" + 800 + ",top=" + Math.round(top) + ",left=" + Math.round(left) + ",titlebar=no,menubar=no,toolbar=no,personalbar=no,directories=no,location=no,resizable=yes,scrollbars=yes,status=no");

        window.open();
    }

    onSaveCertificateDetails(certificateDetails, change, event) {
        let me = this,
            data = new FormData(),
            expirationDate = certificateDetails.expirationDate ? DateHelper.convertServerDateStringToString(certificateDetails.expirationDate, 'YYYY-MM-DD') : undefined,
            licenseNumber = certificateDetails.licenseNumber;

        let certificateType = certificateDetails.certificateType.id;
        let index = 0;
        data.append(`certificates[${index}].type`, certificateType);
        if (expirationDate) {
            data.append(`certificates[${index}].expirationDate`, expirationDate);
        }
        if (licenseNumber) {
            data.append(`certificates[${index}].licenseNumber`, licenseNumber);
        }
        let url = `${REST_API_PREFIX_UPLOAD_CERTIFICATE}`;

        simpleSave(url, data).then(function (response) {

            let fields = Remote.getFieldsByModel(me.props.metaInfo,
                "RequiredCertificateConnection", []);

            getDataPromise('professionalRequiredCertificates', {}, fields).then(function (result) {
                fields = Remote.getFieldsByModel(me.props.metaInfo,
                    "CertificateDetailsModel", []);

                getDataPromise('certificateDetails', result.nodes.find(n => n.type === certificateDetails.certificateType.id).certificateId, fields).then(function (certificateDetails) {
                    change("managedObject.certificateDetails", certificateDetails);
                });
            });
            me.close();
        })
            .catch(error => {
                Error.showRemoteErrors(error, url);
                return false
            })
    }

    certificateForm = undefined;

    initForm() {
        if (!this.certificateForm) {
            switch (this.props.managedObject.certificate.type) {
                case SUB_CATEGORY_ID_CPR:
                    this.certificateForm = new CPRInfoPage({
                        expirationInfo: 'Cardio Pulmonary Resuscitation (CPR) Expiration'
                    }).certificateForm;
                    break;
                case CERTIFICATE_TYPE_DA:
                    this.certificateForm = new CertificateInfoPage({
                        title: 'DA Certificate',
                        //expirationInfo: 'Dental Assistant Certificate Expiration',
                        certificateInfo: 'Dental Assistant Certificate'
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_XRAY:
                    this.certificateForm = new CertificateInfoPage({
                        title: 'X-Ray License',
                        //expirationInfo: 'X-Ray License Expiration',
                        certificateInfo: 'X-Ray License'
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_RDA:
                    this.certificateForm = new CertificateInfoPage({
                        title: 'RDA/RDAEF/RDAEF 1/RDAEF 2 Certificates',
                        licenseInfo: 'RDA/RDAEF/RDAEF 1/RDAEF 2 Number',
                        expirationInfo: 'RDA/RDAEF/RDAEF 1/RDAEF 2 Expiration',
                        certificateInfo: 'RDA/RDAEF/RDAEF 1/RDAEF 2'
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_CDA:
                    this.certificateForm = new CertificateInfoPage({
                        title: 'CDA (Certified Dental Assistant)',
                        expirationInfo: 'CDA (Certified Dental Assistant) Expiration',
                        certificateInfo: 'CDA (Certified Dental Assistant)'
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_DDS_OR_DMD:
                    this.certificateForm = new CertificateInfoPage({
                        title: 'DDS or DMD License',
                        licenseInfo: 'DDS or DMD license Number',
                        expirationInfo: 'DDS or DMD License Expiration',
                        certificateInfo: 'DDS or DMD License'
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_DEA:
                    this.certificateForm = new CertificateInfoPage({
                        title: 'DEA License',
                        licenseInfo: 'DEA Number Entry',
                        expirationInfo: 'DEA License Expiration',
                        certificateInfo: 'DEA License'
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_RDH:
                case SUB_CATEGORY_ID_RDHAP:
                    this.certificateForm = new CertificateInfoPage({
                        title: `${this.props.managedObject.certificate.type} License`,
                        licenseInfo: `${this.props.managedObject.certificate.type} License Number`,
                        expirationInfo: `${this.props.managedObject.certificate.type} License Expiration`,
                        certificateInfo: `${this.props.managedObject.certificate.type} License`
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_LIABILITY:
                    this.certificateForm = new CertificateInfoPage({
                        title: `Liability insurance`,
                        expirationInfo: `Liability insurance expiration`,
                        certificateInfo: `Liability insurance`
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_DIODE_LASER:
                    this.certificateForm = new CertificateInfoPage({
                        title: `Diode laser certification`,
                        licenseInfo: `Diode laser certification number`,
                        expirationInfo: `Diode laser certification expiration`,
                        certificateInfo: `Diode laser certification`
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_NPI:
                    this.certificateForm = new NPIInfoPage({
                        title: `National Identification Number`,
                        licenseInfo: `National Identification Number (NPI)* (10 digits)`,
                        certificateInfo: `National Identification Number`
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_ENDODONTIC_ASSISTANT:
                    this.certificateForm = new AdditionalAssistantInfoPage({
                        title: `Endodontic assistant certification`,
                        licenseInfo: `Endodontic assistant certification number`,
                        expirationInfo: `Endodontic assistant certification expiration`
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_ORAL_SURGERY_ASSISTANT:
                    this.certificateForm = new AdditionalAssistantInfoPage({
                        title: `Oral surgery assistant certification`,
                        licenseInfo: `Oral surgery assistant certification number`,
                        expirationInfo: `Oral surgery assistant certification expiration`
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_ORTHODONTIC_ASSISTANT:
                    this.certificateForm = new AdditionalAssistantInfoPage({
                        title: `Orthodontic assistant certification`,
                        licenseInfo: `Orthodontic assistant certification number`,
                        expirationInfo: `Orthodontic assistant certification expiration`,
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_PEDODONTIC_ASSISTANT:
                    this.certificateForm = new AdditionalAssistantInfoPage({
                        title: `Pedodontic assistant certification`,
                        licenseInfo: `Pedodontic assistant certification number`,
                        expirationInfo: `Pedodontic assistant certification expiration`
                    }).certificateForm;
                    break;
                case SUB_CATEGORY_ID_PERIODONTAL_ASSISTANT:
                    this.certificateForm = new AdditionalAssistantInfoPage({
                        title: `Periodontal assistant certification`,
                        licenseInfo: `Periodontal assistant certification number`,
                        expirationInfo: `Periodontal assistant certification expiration`
                    }).certificateForm;
                    break;
                default:
                    break;
            }
        }
        return this.certificateForm;
    }

    renderDialogContent() {

        let Form = this.initForm();

        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}}
                      certificateDetails={this.props.certificateDetails}
                      references={this.props.references}
                      readOnly={this.props.readOnly}/>);
    }
}

const UploadCertificateDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(UploadCertificate);

export default UploadCertificateDialogConnector;
