import React from 'react';
import {reduxForm} from 'redux-form';
import Field from '../../../common/form/Field';
import TextArea from '../../../common/form/TextArea';
import CheckboxGroup from '../../../common/form/CheckboxGroup';
import SelectField from '../../../common/form/SelectField';
import Checkbox from '../../../common/form/Checkbox';

export const dialogInfo = {references: ['languages']}

let FrontOfficePersonnel = props => {
    const {handleSubmit, handleCancel, invalid, readOnly} = props;
    const years = [];
    for (var y = 1; y < 60; y++) {
        years.push({code: y, name: y});
    }
    return (
        <form onSubmit={handleSubmit}>
            <div class="quest__dds-main modal48">
                <div class="quest__main-item-wrapper">
                    <div class="quest__main-item">

                        <div class="item__box">
                            <div class="text">
                                <p>How many years did you work in dental field?<span class="star">*</span></p>
                            </div>
                            <div class="input board__input-wrapper">
                                <Field name="managedObject.yoeInDental" menuItems={years} component={SelectField}
                                       normalize={(value, previousValue, allValues) => parseInt(value, 10)}
                                       disabled={readOnly} required={true}/>
                            </div>
                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>How many years of dental front office or manager experience?</p>
                            </div>
                            <div class="input board__input-wrapper">
                                <Field name="managedObject.yoeBySpecialty" menuItems={years} component={SelectField}
                                       normalize={(value, previousValue, allValues) => parseInt(value, 10)}
                                       disabled={readOnly}/>
                            </div>
                        </div>

                    </div>


                    <div className="quest__main-item quest__main-item-textarea first">
                        <div className="item__box">
                            <div className="text">
                                <p>Are you proficient with digital radiography and which systems are you familiar
                                    with?</p>
                            </div>
                            <Field name="managedObject.digitalRadiographySystems" component={TextArea}
                                   required={false} readOnly={readOnly}/>
                        </div>
                    </div>

                    <div className="quest__main-item quest__main-item-gray quest__main-item-check-wrapper">
                        <div className="header">What specialties are you familiar with?</div>
                        <Field name="managedObject.specialtiesFamiliarity"
                               items={[{code: 'pedo', name: 'Pedo'},
                                   {code: 'prostho', name: 'Prostho'},
                                   {code: 'perio', name: 'Perio'},
                                   {code: 'endo', name: 'Endo'},
                                   {code: 'general', name: 'General'},
                                   {code: 'cosmetic', name: 'Cosmetics'},
                                   {code: 'implants', name: 'Implants'},
                                   {code: 'oralSurgery', name: 'Oral Surgery'}]} component={CheckboxGroup}
                               classWrapper="check__wrapper"
                               itemClassWrapper="item__box" required={false}
                               /*validate={oneShouldBeChecked}*/ disabled={readOnly}/>
                    </div>

                    <div className="quest__main-item quest__main-item-textarea">
                        <div className="item__box">
                            <div className="text">
                                <p>Which practice management software are you experienced at?</p>
                            </div>
                            <Field name="managedObject.managementSoftware" component={TextArea} required={false}
                                   readOnly={readOnly}/>
                        </div>
                    </div>

                    <div className="quest__main-item quest__main-item-gray quest__main-item-check-overlay">
                        <div className="header">What duties are you confident in?</div>

                        <Field name="managedObject.duties"
                               items={[{code: 'insuranceBilling', name: 'Insurance billing'},
                                   {code: 'eligibilityVerification', name: 'Eligibility verification'},
                                   {code: 'patientScheduling', name: 'Patient scheduling'},
                                   {code: 'hygieneRecall', name: 'Hygiene recall'},
                                   {code: 'acctReceivable', name: 'Acct Receivable'},
                                   {code: 'claimSubmission', name: 'Claim Submission'},
                                   {code: 'insurancePaymentCollection', name: 'Insurance payment collection'},
                                   {code: 'patientCoordination', name: 'Patient coordination'},
                                   {code: 'posting', name: 'Posting'},
                                   {code: 'acctPayable', name: 'Acct Payable'},
                                   {code: 'collections', name: 'Collections'},
                                   {code: 'treatmentPlanning', name: 'Treatment Planing'},
                                   {code: 'treatmentPresentation', name: 'Treatment Presentation'},
                                   {code: 'financialCoordination', name: 'Financial Coordination'},
                                   {code: 'payroll', name: 'Payroll'},
                                   {code: 'marketingSocialIntegration', name: 'Marketing/Social Media Integration'},
                                   {
                                       code: 'officeManagement',
                                       name: 'Office Management(Run morning huddles, Build profits and production graphs,\n' +
                                       '                                        supervise'
                                   }]} component={CheckboxGroup}
                               classWrapper="check__wrapper"
                               itemClassWrapper="item__box" required={false}
                              /* validate={oneShouldBeChecked}*/ disabled={readOnly}/>
                    </div>

                    <div class="quest__main-item">
                        <div class="radio">

                            <div class="item__radio">
                                <div class="item__radio-header">Are you familiar with intraoral cameras?</div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.xRaysAndCameraImagesToInsurance"
                                               component={Checkbox} disabled={readOnly}/>
                                    </div>
                                </div>
                            </div>

                            <div class="item__radio">
                                <div class="item__radio-header">Can you cross trained front to back?</div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.crossTrained" component={Checkbox}
                                               disabled={readOnly}/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="quest__main-item">
                        <div class="item__box-help">
                            <p class="mandatory">Fields marked* are mandatory</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Cancel</button>
                    {!readOnly &&
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>}
                </div>
            </div>

        </form>)
}

FrontOfficePersonnel = reduxForm({
    form: 'questionnaire'
})(FrontOfficePersonnel);

export default FrontOfficePersonnel;