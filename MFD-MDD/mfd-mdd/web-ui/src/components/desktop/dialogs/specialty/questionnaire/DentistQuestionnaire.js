import React from 'react';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../../common/form/Field';
import TextArea from '../../../common/form/TextArea';
import SelectField from '../../../common/form/SelectField';
import {stringToInt} from '../../../../../utils/Normalizers';
import Checkbox from '../../../common/form/Checkbox';
import {connect} from "react-redux";

export const dialogInfo = {
    references: ['languages']
    //fields: []
}

let DentistQuestionnaireForm = props => {
    const {handleSubmit, handleCancel, invalid, readOnly, managedObject} = props;
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
                                <p>How many as a dentist?</p>
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
                        <div className="header">Percentage of comfort level in following specialties:</div>
                        <div class="check__wrapper">
                            <div class="item__box"><Field name="managedObject.specialtiesComfort.pedo"
                                                          component="input" id="spComfort_pedo" type="number"
                                                          class="percent_input" min={0} max={100}
                                                          normalize={stringToInt} disabled={readOnly}/><label
                                for="spComfort_pedo">Pedo</label></div>
                            <div class="item__box"><Field name="managedObject.specialtiesComfort.prostho"
                                                          component="input" id="spComfort_pedo" type="number"
                                                          class="percent_input" min={0} max={100}
                                                          normalize={stringToInt} disabled={readOnly}/><label
                                for="spComfort_prostho">Prostho</label></div>
                            <div class="item__box"><Field name="managedObject.specialtiesComfort.perio"
                                                          component="input" id="spComfort_prostho" type="number"
                                                          class="percent_input" min={0} max={100}
                                                          normalize={stringToInt} disabled={readOnly}/><label
                                for="spComfort_perio">Perio</label></div>
                            <div class="item__box"><Field name="managedObject.specialtiesComfort.endo"
                                                          component="input" id="spComfort_endo" type="number"
                                                          class="percent_input" min={0} max={100}
                                                          normalize={stringToInt} disabled={readOnly}/><label
                                for="spComfort_endo">Endo</label></div>
                            <div class="item__box"><Field name="managedObject.specialtiesComfort.general"
                                                          component="input" id="spComfort_general" type="number"
                                                          class="percent_input" min={0} max={100}
                                                          normalize={stringToInt} disabled={readOnly}/><label
                                for="spComfort_general">General</label></div>
                            <div class="item__box"><Field name="managedObject.specialtiesComfort.cosmetic"
                                                          component="input" id="spComfort_cosmetic" type="number"
                                                          class="percent_input" min={0} max={100}
                                                          normalize={stringToInt} disabled={readOnly}/><label
                                for="spComfort_cosmetic">Cosmetics</label></div>
                            <div class="item__box"><Field name="managedObject.specialtiesComfort.implants"
                                                          component="input" id="spComfort_implants" type="number"
                                                          class="percent_input" min={0} max={100}
                                                          normalize={stringToInt} disabled={readOnly}/><label
                                for="spComfort_implants">Implants</label></div>
                            <div class="item__box"><Field name="managedObject.specialtiesComfort.oralSurgery"
                                                          component="input" id="spComfort_oralSurgery" type="number"
                                                          class="percent_input" min={0} max={100}
                                                          normalize={stringToInt} disabled={readOnly}/><label
                                for="spComfort_oralSurgery">Oral Surgery</label></div>
                        </div>
                    </div>

                    <div class="quest__main-item">
                        <div class="radio">

                            <div class="item__radio">
                                <div class="item__radio-header">Are you willing to work as RDH for temporary
                                    assignments?
                                </div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.temporaryAsRdh" component={Checkbox}
                                               disabled={readOnly}/>
                                    </div>
                                </div>
                            </div>
                            {(managedObject && managedObject.temporaryAsRdh) &&

                                <div class="info-div">
                                    <div className="state__attention">
                                        <div className="item__box">
                                            <div className="text">
                                                <p>Please add RDH as a specialty and upload your DDS license</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            }

                            <div class="item__radio">
                                <div class="item__radio-header">Are you familiar with Cad-Cam (E4D or Cerec)?</div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.cadCam" component={Checkbox} disabled={readOnly}/>
                                    </div>
                                </div>
                            </div>

                            <div class="item__radio">
                                <div class="item__radio-header">How comfortable are you with surgery?</div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.surgery" component={Checkbox} disabled={readOnly}/>
                                    </div>
                                </div>
                            </div>

                            <div class="item__radio">
                                <div class="item__radio-header">Can you stand 8 hrs on feet?</div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.hoursOnFeet" component={Checkbox}
                                               disabled={readOnly}/>
                                    </div>
                                </div>
                            </div>

                 {/*           <div class="item__radio">
                                <div class="item__radio-header">If you work as RDH, do you agree with $440-$480 (8 or 10
                                    patients per 8 hours)?
                                </div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.patientsPerDay" component={Checkbox}
                                               disabled={readOnly}/>
                                    </div>
                                </div>
                            </div>*/}

                            <div class="item__radio">
                                <div class="item__radio-header">Are you familiar with intraoral cameras?</div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.intraOralCam" component={Checkbox}
                                               disabled={readOnly}/>
                                    </div>
                                </div>
                            </div>

                            <div class="item__radio">
                                <div class="item__radio-header">Are you familiar with Pano?</div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.pano" component={Checkbox} disabled={readOnly}/>
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

DentistQuestionnaireForm = reduxForm({
    form: 'questionnaire'
})(DentistQuestionnaireForm);

const selector = formValueSelector('questionnaire');

DentistQuestionnaireForm = connect(state => {
    const managedObject = selector(state, 'managedObject');
    return {
        managedObject: managedObject
    }
})(DentistQuestionnaireForm)

export default DentistQuestionnaireForm;