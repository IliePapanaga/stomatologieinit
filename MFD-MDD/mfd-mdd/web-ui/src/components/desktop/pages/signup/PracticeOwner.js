import React from 'react';
import {connect} from 'react-redux';
import {reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';
import TextArea from '../../common/form/TextArea';
import SelectField from '../../common/form/SelectField';
import ExSelectField from '../../common/form/ExSelectField';
import BaseRecaptcha from '../../common/security/BaseRecaptcha';
import {Field as QueryField} from '../../../../models/core/QueryInfo';
import Footer from '../../common/page/Footer';
import {phone} from '../../../../utils/Normalizers';
import Chooser from '../../dialogs/signup/Chooser';
import {
    EVENT_CHANGED_RECAPTCHA_HASH,
    PASSWORD_PATTERN_REGEX,
    REST_API_PREFIX_PUBLIC,
    TEXTFIELD_PATTERN_REGEX,
    TITLES,
    WEBSITE_PATTERN_REGEX
} from '../../../../utils/Constants';
import {STATES} from '../../../../data/States';
import {SPECIALTY} from '../../../../data/Specialty';
import UiView from '../../../../utils/UiView';
import ObjectHelper from '../../../../utils/Object';
import {matchPassword, validateEmail} from '../../../../utils/Validators';
import BasePage, {basePageDispatcherMap, basePageStateMap} from '../../common/BasePage';
import saveData from '../../../../actions/common/saveData';
import checkUserName from '../../../../actions/signup/checkUserName';
import {Logger} from 'react-logger-lib';
import {FieldsInfo} from "../../../../models/core/FieldsInfo";
import ca from '../../../../resources/docs/ca.pdf';
import PasswordField from "../../common/form/PasswordField";

let SignupForm = props => {
    const {handleSubmit, handleCancel, change, page, invalid} = props;
    let changedZipCode = UiView.getFindByZipCodeHandler(change);
    return (
        <form className="register__form" onSubmit={handleSubmit}>
            <div class="signup__practice">

                <div class="modal__header">
                    <h2>Create a Mayday Dental Staffing Account for employers</h2>
                </div>
                <div class="signup__practice-registration">
                <div class="signup__practice-cred">
                    <div class="signup__practice-cred-header">
                        <h3 class="cred">Credentials</h3>
                    </div>

                    <div class="signup__practice-cred-content">


                        <div class="signup__practice-cred-content-form">
                            <div class="item__box">
                                <div class="text">
                                    <p>Email Address<span class="star">*</span>:</p>
                                </div>
                                <Field name="username" type="email" autocomplete="off" component={TextField}
                                       validate={validateEmail} required={true} minLength={6} maxLength={254}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Password<span class="star">*</span>:</p>
                                </div>
                                <Field classField="for__pass-btn" name="password" type="password" autocomplete="off" component={PasswordField}
                                       required={true} minLength={10} maxLength={60}
                                       regexPattern={PASSWORD_PATTERN_REGEX} hasInputIcon={false} hasValidation={true} fieldWrapperClass="input"/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Re-type Password<span class="star">*</span>:</p>
                                </div>
                                <Field classField="for__pass-btn" name="repassword" type="password" autocomplete="off" component={PasswordField}
                                       validate={matchPassword} required={true} minLength={10} maxLength={60}
                                       regexPattern={PASSWORD_PATTERN_REGEX}  hasInputIcon={false} hasValidation={true} fieldWrapperClass="input"/>

                            </div>
                        </div>

                        <div class="signup__practice-cred-content-help">
                            <div class="signup__practice-cred-content-help-info">

                                <div class="help">
                                    <p>Password must be at least 10 characters long with characters from 3 of these 4
                                        categories:</p>
                                    <ul>
                                        <li>upper case letters</li>
                                        <li>lower case letters</li>
                                        <li>numbers</li>
                                        <li>special characters, i.e "#,!,$,%" etc.</li>
                                    </ul>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>


                <div class="signup__practice-personal">
                    <div class="signup__practice-personal-header">
                        <h3 class="personal">Personal Details</h3>
                    </div>
                    <div class="signup__practice-personal-form">
                        <div class="signup__practice-personal-form-left">
                            <div class="item__box">
                                <div class="text">
                                    <p>Title:</p>
                                </div>
                                <div class="input board__input-wrapper">
                                    <Field name="contact.name.title" component={SelectField} menuItems={TITLES}
                                           required={false}/>
                                </div>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>First Name<span class="star">*</span>:</p>
                                </div>
                                <Field name="contact.name.first" autocomplete="off" component={TextField}
                                       required={true} minLength={2} maxLength={60}
                                       regexPattern={TEXTFIELD_PATTERN_REGEX}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Last Name<span class="star">*</span>:</p>
                                </div>
                                <Field name="contact.name.last" autocomplete="off" component={TextField} required={true}
                                       minLength={2} maxLength={66} regexPattern={TEXTFIELD_PATTERN_REGEX}/>
                            </div>

                        </div>

                        <div class="signup__practice-personal-form-right">
                            <div class="item__box zip">
                                <div class="text">
                                    <p>ZIP<span class="star">*</span>:</p>
                                </div>
                                <Field name="contact.address.zipCode" autocomplete="off" component={TextField}
                                       required={true} mask="99999" maskChar="_" onChange={changedZipCode}/>
                            </div>
                            <div class="item__box">
                                <div class="text">
                                    <p>Street<span class="star">*</span>:</p>
                                </div>
                                <Field name="contact.address.street" autocomplete="off" component={TextField}
                                       required={true} minLength={2} maxLength={60}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>City<span class="star">*</span>:</p>
                                </div>
                                <Field name="contact.address.city" autocomplete="off" component={TextField}
                                       required={true} minLength={2} maxLength={60}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>State<span class="star">*</span>:</p>
                                </div>
                                <div class="input board__input-wrapper">
                                    <Field name="contact.address.state" component={SelectField} menuItems={STATES}
                                           required={true}/>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>

                <div class="signup__practice-billing">
                    <div class="signup__practice-billing-header">
                        <h3 class="billing">Billing Address Information</h3>
                    </div>
                    <div class="signup__practice-billing-form">
                        <div class="signup__practice-billing-form-left">
                            <div class="item__box">
                                <div class="text">
                                    <p>Billing ZIP<span class="star">*</span>:</p>
                                </div>
                                <Field name="registerPractice.billingAddress.zipCode" autocomplete="off"
                                       component={TextField} required={true} mask="99999" maskChar="_"
                                       onChange={UiView.getFindByZipCodeHandler(change, `registerPractice.billingAddress.`)}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Billing Street<span class="star">*</span>:</p>
                                </div>
                                <Field name="registerPractice.billingAddress.street" autocomplete="off"
                                       component={TextField} required={true} minLength={2} maxLength={60}/>
                            </div>

                        </div>
                        <div class="signup__practice-billing-form-right">
                            <div class="item__box">
                                <div class="text">
                                    <p>Billing City<span class="star">*</span>:</p>
                                </div>
                                <Field name="registerPractice.billingAddress.city" autocomplete="off"
                                       component={TextField} required={true} minLength={2} maxLength={60}/>
                            </div>
                            <div class="item__box">
                                <div class="text">
                                    <p>Billing State<span class="star">*</span>:</p>
                                </div>
                                <div class="input board__input-wrapper">
                                    <Field name="registerPractice.billingAddress.state" component={SelectField}
                                           menuItems={STATES} required={true}/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>




                <div class="signup__practice-company">
                    <div class="signup__practice-company-header">
                        <h3 class="company">Company Details</h3>
                    </div>
                    <div class="signup__practice-company-form">
                        <div class="signup__practice-company-form-left">

                            <div class="item__box">
                                <div class="text">
                                    <p>Company Name<span class="star">*</span>:</p>
                                </div>
                                <Field name="registerPractice.name" autocomplete="off" component={TextField}
                                       required={true} minLength={1} maxLength={60}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Contact Phone<span class="star">*</span>:</p>
                                </div>
                                <Field name="contact.phone" autocomplete="off" component={TextField} required={true}
                                       mask="(999) 999-9999" maskChar=" " normalize={phone}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Company Website:</p>
                                </div>
                                <Field name="registerPractice.webSite" autocomplete="off" component={TextField}
                                       required={false} minLength={2} maxLength={60}
                                       regexPattern={WEBSITE_PATTERN_REGEX}/>
                            </div>


                            <div class="item__box">
                                <div class="text">
                                    <p>Company Specialty<span class="star">*</span>:</p>
                                </div>
                                <Field name="registerPractice.specialities" component={ExSelectField} multiple={true}
                                       required={true} menuItems={SPECIALTY}/>
                            </div>


                            <div class="item__box">
                                <div class="text">
                                    <p>Office Manager Name:</p>
                                </div>
                                <Field name="registerPractice.officeManagerName" autocomplete="off"
                                       component={TextField} required={false} minLength={2} maxLength={60}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Second Email:</p>
                                </div>
                                <Field name="contact.email" autocomplete="off" component={TextField} required={false} minLength={6} maxLength={30}
                                       validate={validateEmail}/>
                            </div>

                        </div>


                        <div class="signup__practice-company-form-right">

                            <div class="item__box">
                                <div class="text">
                                    <p>Doctor Cell<span class="star">*</span>:</p>
                                </div>
                                <Field name="registerPractice.phone" autocomplete="off" component={TextField}
                                       required={true} mask="(999) 999-9999" maskChar=" " normalize={phone}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>Fax:</p>
                                </div>
                                <Field name="contact.fax" autocomplete="off" component={TextField} required={false}
                                       mask="(999) 999-9999" maskChar=" " normalize={phone}/>
                            </div>

                            <div class="item__box">
                                <div class="text">
                                    <p>After working hours phone number<span class="star">*</span>:</p>
                                </div>
                                <Field name="registerPractice.afterWorkPhone" autocomplete="off" component={TextField}
                                       required={true} mask="(999) 999-9999" maskChar=" " normalize={phone}/>
                            </div>

                            <div class="item__box textarea">
                                <div class="text">
                                    <p>Practice Management Software<span class="star">*</span>:</p>
                                </div>
                                <div class="registration__form-item-input">
                                    <Field name="registerPractice.softwares" component={TextArea} required={true}/>
                                </div>

                            </div>
                        </div>
                    </div>


                    <div class="signup__practice-help">
                        {/*   <p class="signup__practice-help-red">Fields marked * are mandatory</p>*/}
                        <p>By creating an Mayday Dental Staffing Account, you agree to Mayday Dental Staffing's <a onClick={function(){
                            UiView.showResource(ca, 'Terms And Conditions');
                        }}>Terms
                            And Conditions</a> and agree to be contacted by employers via Mayday Dental Staffing</p>

                        <p class="mandatory">Fields marked* are mandatory</p>
                    </div>


                    <div class="signup__practice-signature">
                        <div class="signup__practice-signature-content">
                            <BaseRecaptcha/></div>

                    </div>

                </div>
                </div>

                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Back</button>
                    <button disabled={invalid || !page.props.recaptchaHash} class="blue">register</button>
                </div>
            </div>
        </form>)
}

SignupForm = reduxForm({
    form: 'signup',
    asyncValidate: checkUserName,
    asyncBlurFields: ['username']
})(SignupForm);

/**
 * Main application component. Containts base navigation panels and base view
 */
class PracticeOwner extends BasePage {
    submit(data) {
        let me = this;
        let registrationData = ObjectHelper.copyObject(data);
        // let signature = this.sigCanvas.getTrimmedCanvas()
        //     .toDataURL('image/png');
        delete registrationData.agree;
        delete registrationData.repassword;
        // registrationData.signature = signature.substr(signature.indexOf(',') + 1);

        Logger.of('App.Signup.submit').info('Signup data:', registrationData);

        me.props.onSignup(registrationData, me.props.router, me.props.recaptchaHash);
    }

    cancel() {
        let router = this.props.router;
        router.navigateToUrl("/");

        UiView.showDialog(<Chooser store={UiView.createDialogStore()} managedObject={{}} actions={{
            goTo: function (url) {
                router.navigateToUrl(url);
            }
        }}/>);
    }

    clearSignatureCanvas() {
        this.sigCanvas.clear();
    }

    render() {
        return ([
            <SignupForm onSubmit={this.submit.bind(this)} handleCancel={this.cancel.bind(this)} page={this}
                        clearSignatureCanvas={this.clearSignatureCanvas.bind(this)} key="mainForm"/>,
            <div class="logo__absolute" key="logo">
                <img src="img/logo-abs.png" alt="logo"/>
            </div>,
            <Footer key="footer"/>
        ]);
    }
}


const PracticeOwnerConnector = connect(function (state, ownProps) {
        return Object.assign(basePageStateMap(state, ownProps), {
            recaptchaHash: state.context.recaptchaHash
        });
    },
    function (dispatch) {
        return Object.assign(basePageDispatcherMap(dispatch), {
            onSignup: (signupData, router, recaptchaHash) => {
                dispatch(saveData({
                    queryName: 'registerPracticeOwner',
                    uri: REST_API_PREFIX_PUBLIC,
                    fields: [new FieldsInfo({select:[new QueryField('id')]})]
                }, {practiceOwner: signupData}, recaptchaHash)).then(function (result) {
                    if (result && result.id) {
                        dispatch({type: EVENT_CHANGED_RECAPTCHA_HASH, recaptchaHash: undefined});
                        localStorage.setItem('email', signupData.username);
                        localStorage.setItem('id', result.id);
                        router.navigateToUrl("/signup/complete");
                    }
                });
            }
        });
    })(PracticeOwner);

export default PracticeOwnerConnector;






























