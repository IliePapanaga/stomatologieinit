import React from 'react';
import {reduxForm} from 'redux-form';
import Field from '../../../common/form/Field';
import TextArea from '../../../common/form/TextArea';
import CheckboxGroup from '../../../common/form/CheckboxGroup';
import SelectField from '../../../common/form/SelectField';
import Checkbox from '../../../common/form/Checkbox';

export const dialogInfo = {references: ['languages']}

let HygienistQuestionnaire = props => {
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
                                <p>How many as a RDH?</p>
                            </div>
                            <div class="input board__input-wrapper">
                                <Field name="managedObject.yoeBySpecialty" menuItems={years} component={SelectField}
                                       normalize={(value, previousValue, allValues) => parseInt(value, 10)}
                                       disabled={readOnly}/>
                            </div>
                        </div>

                    </div>


                    <div class="quest__main-item">
                        <div class="radio">
                            <div class="item__radio">
                                <div class="item__radio-header">Do you feel comfortable working with Nitrous Oxide?
                                </div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.nitrousOxide" component={Checkbox}
                                               disabled={readOnly}/>
                                    </div>
                                </div>
                            </div>

                            <div class="item__radio">
                                <div class="item__radio-header">Do you feel comfortable applying Arestin and other
                                    anti-microbial?
                                </div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.antiMicrobial" component={Checkbox}
                                               disabled={readOnly}/>
                                    </div>
                                </div>
                            </div>

                            <div class="item__radio">
                                <div class="item__radio-header">Can you anesthetize by yourself?</div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.anesthetize" component={Checkbox}
                                               disabled={readOnly}/>
                                    </div>
                                </div>
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
                              /* validate={oneShouldBeChecked}*/ disabled={readOnly}/>
                    </div>

                    <div class="quest__main-item">
                        <div class="radio">

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

                            <div class="item__radio">
                                <div class="item__radio-header">Can you call your own patients and schedule recare
                                    appt?
                                </div>
                                <div class="item__radio-wrapper">
                                    <div class="item__radio-content">
                                        <Field name="managedObject.recareAppt" component={Checkbox}
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

HygienistQuestionnaire = reduxForm({
    form: 'questionnaire'
})(HygienistQuestionnaire);

export default HygienistQuestionnaire;