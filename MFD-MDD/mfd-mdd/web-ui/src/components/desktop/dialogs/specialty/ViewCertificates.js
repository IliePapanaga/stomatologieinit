import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import TextArea from '../../common/form/TextArea';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import saveData from '../../../../actions/common/saveData';
import fileDownload from 'react-file-download';
import {
    CERTIFICATE_TYPE_DA, CERTIFICATE_TYPE_TITLES,
    REST_API_PREFIX_DOWNLOAD_CERTIFICATE,
    REST_API_PREFIX_VIEW_CERTIFICATE,
    STATUS_APPROVED,
    STATUS_REJECTED,
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
    SUB_CATEGORY_ID_XRAY
} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import {toastr} from 'react-redux-toastr';
import axios from 'axios';
import {findStatusName} from '../../../../data/Statuses';

export const dialogInfo = {
    //fields: []
}


let Form = props => {
    return (
        <form>
            <Field name="comment" component={TextArea} readOnly={props.readOnly ? true : false}/>
        </form>)
}

Form = reduxForm({
    form: 'comment'
})(Form);


class ViewCertificates extends BaseDialog {

    constructor(props) {
        super(props);
        this.state = {
            managedObject: props.managedObject
        }
    }

    dialogProps() {
        let me = this;
        const titles = CERTIFICATE_TYPE_TITLES.find(t => t.code === me.props.managedObject.certificateType.id);
        let title = titles ? (titles.title.toLowerCase().indexOf('certificate')>=0 ? titles.title : `${titles.title} certificate`) : `${me.props.managedObject.certificateType.id} CERTIFICATE`;
        title = title.replace(new RegExp("_", "g"), ' ');
        return {
            title: title,
            width: 960,
            height: 400,
            className: "modal__gray"
        }
    }

    certificateFormElement(managedObject) {
        let title = undefined,
            licenseInfo = undefined,
            expirationInfo = undefined,
            certificateInfo = undefined;

        switch (managedObject.certificateType.id) {
            case SUB_CATEGORY_ID_CPR:
                title = 'CPR Info';
                expirationInfo = 'Cardio Pulmonary Resuscitation (CPR) Expiration';
                certificateInfo = 'Cardio Pulmonary Resuscitation (CPR) Certificate';
                break;
            case CERTIFICATE_TYPE_DA:
                title = 'DA Certificate';
                certificateInfo = 'Dental Assistant Certificate';
                break;
            case SUB_CATEGORY_ID_XRAY:
                title = 'X-Ray License';
                certificateInfo = 'X-Ray License';
                break;
            case SUB_CATEGORY_ID_RDA:
                title = 'RDA/RDAEF/RDAEF 1/RDAEF 2 Certificates';
                licenseInfo = 'RDA/RDAEF/RDAEF 1/RDAEF 2 Number';
                expirationInfo = 'RDA/RDAEF/RDAEF 1/RDAEF 2 Expiration';
                certificateInfo = 'RDA/RDAEF/RDAEF 1/RDAEF 2';
                break;
            case SUB_CATEGORY_ID_CDA:
                title = 'CDA (Certified Dental Assistant)';
                expirationInfo = 'CDA (Certified Dental Assistant) Expiration';
                certificateInfo = 'CDA (Certified Dental Assistant)';
                break;
            case SUB_CATEGORY_ID_DDS_OR_DMD:
                title = 'DDS or DMD License';
                licenseInfo = 'DDS or DMD License Number';
                expirationInfo = 'DDS or DMD License Expiration';
                certificateInfo = 'DDS or DMD License';
                break;
            case SUB_CATEGORY_ID_DEA:
                title = 'DEA License';
                licenseInfo = 'DEA Number Entry';
                expirationInfo = 'DEA License Expiration';
                certificateInfo = 'DEA License';
                break;
            case SUB_CATEGORY_ID_RDH:
            case SUB_CATEGORY_ID_RDHAP:
                title = `${managedObject.certificateType.id} License`;
                licenseInfo = `${managedObject.certificateType.id} License Number`;
                expirationInfo = `${managedObject.certificateType.id} License Expiration`;
                certificateInfo = `${managedObject.certificateType.id} License`;
                break;
            case SUB_CATEGORY_ID_LIABILITY:
                title = `Liability insurance`;
                expirationInfo = `Liability insurance expiration`;
                certificateInfo = `Liability insurance`;
                break;
            case SUB_CATEGORY_ID_DIODE_LASER:
                title = `Diode laser certification`;
                licenseInfo = `Diode laser certification number`;
                expirationInfo = `Diode laser certification expiration`;
                certificateInfo = `Diode laser certification`;
                break;
            case SUB_CATEGORY_ID_NPI:
                title = `National Identification Number`;
                licenseInfo = `National Identification Number (NPI)* (10 digits)`;
                certificateInfo = `National Identification Number`;
                break;

            case SUB_CATEGORY_ID_ENDODONTIC_ASSISTANT:
                title = `Endodontic assistant certification`;
                licenseInfo = `Endodontic assistant certification number`;
                expirationInfo = `Endodontic assistant certification expiration`;
                break;
            case SUB_CATEGORY_ID_ORAL_SURGERY_ASSISTANT:
                title = `Oral surgery assistant certification`;
                licenseInfo = `Oral surgery assistant certification number`;
                expirationInfo = `Oral surgery assistant certification expiration`;
                break;
            case SUB_CATEGORY_ID_ORTHODONTIC_ASSISTANT:
                title = `Orthodontic assistant certification`;
                licenseInfo = `Orthodontic assistant certification number`;
                expirationInfo = `Orthodontic assistant certification expiration`;
                break;
            case SUB_CATEGORY_ID_PEDODONTIC_ASSISTANT:
                title = `Pedodontic assistant certification`;
                licenseInfo = `Pedodontic assistant certification number`;
                expirationInfo = `Pedodontic assistant certification expiration`;
                break;
            case SUB_CATEGORY_ID_PERIODONTAL_ASSISTANT:
                title = `Periodontal assistant certification`;
                licenseInfo = `Periodontal assistant certification number`;
                expirationInfo = `Periodontal assistant certification expiration`;
                break;
            default:
                break;
        }
        return (<div class="certificates__main">
            <div class="certificates__main-item">
                <h3>{title}</h3>
                <div class="item__box">
                    <div class="title">
                        <p>Status:</p>
                    </div>
                    <div class="data">
                        <p>{findStatusName(managedObject.status)}</p>
                    </div>
                </div>
                {licenseInfo && managedObject.licenseNumber && <div class="item__box">
                    <div class="title">
                        <p>{licenseInfo}:</p>
                    </div>
                    <div class="data">
                        <input value={managedObject.licenseNumber}/>
                    </div>
                </div>}

                {expirationInfo && managedObject.expirationDate && <div class="item__box">
                    <div class="title">
                        <p>{expirationInfo}:</p>
                    </div>
                    <div class="data">
                        <input value={managedObject.expirationDate}/>
                    </div>
                </div>}
                <div class="item__box">
                    <div class="title">
                        <p><span>{certificateInfo}</span><span>{certificateInfo && <span>:</span>}</span></p>
                    </div>
                    <div class="data">
                        {managedObject.certificate &&
                        <div class="data__item"><p>{managedObject.certificate.name}</p></div>}
                        {this.generateActions(managedObject)}
                    </div>
                </div>
                <div class="item__box">
                    <div class="title">
                        <p>Comments:</p>
                    </div>
                    <div class="data">
                        <Form initialValues={{comment: managedObject.comment}}
                              readOnly={managedObject.status === STATUS_REJECTED}/>
                    </div>
                </div>
            </div>
        </div>);
    }

    generateActions(managedObject) {
        let actions = [];
        if (managedObject.certificate) {
            actions.push(<div class="data__item">
                <button class="white" onClick={this.onView.bind(this, managedObject.certificate)}>View</button>
            </div>);
            actions.push(<div class="data__item">
                <button class="white" onClick={this.onDownload.bind(this, managedObject.certificate)}>Download</button>
            </div>);
        }

        actions.push(<div class="data__item">
            <button class="blue" disabled={managedObject.status === STATUS_APPROVED}
                    onClick={this.onVerify.bind(this, managedObject, managedObject.certificate)}>Approve
            </button>
        </div>);
        actions.push(<div class="data__item">
            <button class="yellow" disabled={managedObject.status === STATUS_REJECTED}
                    onClick={this.onReject.bind(this, managedObject, managedObject.certificate)}>Reject
            </button>
        </div>);

        return (actions);
    }

    onVerify(managedObject, certificate, event) {
        let me = this;
        me.onExecuteOperation({id: managedObject.id},
            <div class="modal57">
                <div class="header activate"><h2>Approve</h2></div>
                <div class="body">
                    Are you sure you want to approve the certificate?
                </div>
            </div>, "approveCertificateDetails", function (result) {
                managedObject.status = STATUS_APPROVED;
                me.props.parent.state.nodes.find(node => node.certificateId === managedObject.id).status = STATUS_APPROVED;
                let allIdentical = me.props.parent.state.nodes.find(node => node.status !== STATUS_APPROVED) == null;
                if (allIdentical) {
                    me.props.updateStatus(STATUS_APPROVED);
                }
                me.props.parent.setState({nodes: me.props.parent.state.nodes});
                me.setState({managedObject: {...managedObject}});
            });
    }

    onReject(managedObject, certificate, event) {
        let me = this;
        me.onExecuteOperation({
                reject: {
                    id: managedObject.id,
                    comment: document.querySelector('form #comment').value
                }
            },
            <div class="modal57">
                <div class="header deactivate"><h2>Rejection</h2></div>
                <div class="body">Are you sure you want to reject the certificate?</div>
            </div>, "rejectCertificateDetails", function (result) {
                me.props.parent.state.nodes.find(node => node.certificateId === managedObject.id).status = STATUS_REJECTED;
                managedObject.status = STATUS_REJECTED;
                me.props.updateStatus(STATUS_REJECTED);
                me.setState({managedObject: {...managedObject}});
                me.props.parent.setState({nodes: me.props.parent.state.nodes});
            });
    }

    onView(certificate, event) {
        var windowWidth = window.innerWidth;
        var windowHeight = window.innerHeight;
        var width = 900,
            height = 800,
            left = (windowWidth / 2) - (width / 2),
            top = (windowHeight / 2) - (height / 2);
        let url = `${REST_API_PREFIX_VIEW_CERTIFICATE}/${certificate.id}`;

        window.open(url, `View certificate ${certificate.name}`,
            "centerscreen=yes,width=" + 960 + ",height=" + 800 + ",top=" + Math.round(top) + ",left=" + Math.round(left) + ",titlebar=no,menubar=no,toolbar=no,personalbar=no,directories=no,location=no,resizable=yes,scrollbars=yes,status=no");
    }

    onDownload(certificate, event) {
        let path = `${REST_API_PREFIX_DOWNLOAD_CERTIFICATE}/${certificate.id}`;
        axios({
            method: 'get',
            url: path,
            responseType: 'blob'
        }).then(function (response) {
            fileDownload(response.data, certificate.name, certificate.contentType);
        });
    }

    onExecuteOperation(managedObject, confirmationMessage, queryName, callBack) {
        let me = this;
        const toastrConfirmOptions = {
            onOk: function () {
                try {
                    me.props.onExecuteOperation(queryName, managedObject, callBack);
                } catch (ex) {
                    Error.showErrors(ex)
                }

            },
            onCancel: () => console.log('CANCEL: clicked')
        };
        toastr.confirm(confirmationMessage, toastrConfirmOptions);


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
        const FormElements = this.certificateFormElement(this.state.managedObject);
        return ([FormElements,


            <div class="footer__btn">
                <button class="blue white" type="button" onClick={this.close.bind(this)}>Close</button>
            </div>]);
    }
}

const ViewCertificatesDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
            onExecuteOperation: (queryName, managedObject, callBackFn) => {
                let me = this;

                dispatch(saveData({queryName: queryName}, managedObject)).then(
                    function (result) {
                        callBackFn.call(me, result);
                    });
            }
        });
    })(ViewCertificates);

export default ViewCertificatesDialogConnector;
