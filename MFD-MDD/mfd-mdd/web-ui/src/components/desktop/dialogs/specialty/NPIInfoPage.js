import React from 'react';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';
import DateField from '../../common/form/DateField';
import ReactUploadFile from 'react-upload-file';
import {findStatusName} from "../../../../data/Statuses";
import CertificateInfoPage from "./CertificateInfoPage";
import {TEXTFIELD_DIGITS_PATTERN_REGEX, SUB_CATEGORY_ID_NPI} from "../../../../utils/Constants";
import DateHelper from "../../../../utils/DateHelper";

class NPIInfoPage extends CertificateInfoPage {

    initTemplate(handleSubmit, handleCancel, uploadOptions, managedObject, dialog, allowUpload, readOnly, scope, invalid, change) {
        let me = this;
        let title = me.title;
        let expirationInfo = me.expirationInfo;
   //     let certificateInfo = me.certificateInfo;
        let classPostfix = me.statusClassPostfix(managedObject.certificateDetails);
        managedObject.certificateDetails.certificateType = {id: SUB_CATEGORY_ID_NPI};

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
                        <Field name="managedObject.certificateDetails.licenseNumber" component={TextField} maxLength={10} minLength={10} regexPattern={TEXTFIELD_DIGITS_PATTERN_REGEX}
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
                    <button class="blue white" type="button" onClick={handleCancel}>Close</button>
                    {!readOnly &&
                    <button class="blue" type="button" disabled={invalid}
                            onClick={dialog.onSaveCertificateDetails.bind(dialog, managedObject.certificateDetails, change)}>Save
                    </button>
                    }
                </div>
            </div>
        </form>;
    }

}


export default NPIInfoPage