import React from 'react';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';
import DateField from '../../common/form/DateField';
import ReactUploadFile from 'react-upload-file';
import {findStatusName} from "../../../../data/Statuses";
import CertificateInfoPage from "./CertificateInfoPage";
import Renderer from "../../../../utils/Renderer";
import DateHelper from "../../../../utils/DateHelper";

class CPRInfoPage extends CertificateInfoPage {


    statusClassPostfix(certificateDetails) {
        if (certificateDetails && certificateDetails.status) {
            return " row__btn row__btn-" + Renderer.getRowStatusClassPrefix(certificateDetails.status) + ' pressed';
        }
        return '';
    }

    initTemplate(handleSubmit, handleCancel, uploadOptions, managedObject, dialog, allowUpload, readOnly, scope) {
        let me = this;
        let classPostfix = me.statusClassPostfix(managedObject.certificateDetails);
        return <form onSubmit={handleSubmit} class="modal__form modal54 modal53">
            <ReactUploadFile ref={(el) => {
                scope.uploadFile = el
            }} options={uploadOptions}/>

            <div class="specialties__second-main">
                <h3>CPR Info</h3>

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


                <div class="item__box">
                    <div class="text">
                        <p>Cardio Pulmonary Resuscitation (CPR) Expiration<span class="star">*</span>:</p>
                    </div>
                    <div class="input input__date">
                        <Field name="managedObject.certificateDetails.expirationDate" class="date"
                               placeholder="Select date" minDate={DateHelper.getCurrentDate()}
                               component={DateField} dateFormat="MM/DD/YYYY" readOnly={readOnly}/>
                    </div>
                </div>

                <div class="item__box">
                    <div class="text">
                        <p>Cardio Pulmonary Resuscitation (CPR) Certificate<span class="star">*</span>:</p>
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
                        }} disabled={!allowUpload||readOnly}>Upload/Edit
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

}


export default CPRInfoPage