import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';
import SelectField from '../../common/form/SelectField';
import Checkbox from '../../common/form/Checkbox';
import {validateCardExpDate} from '../../../../utils/Validators';
import {
    PAYMENT_TYPE_BANK_ACCOUNT,
    PAYMENT_TYPE_CREDIT_CARD,
    CARD_NUMBER_PATTERN_REGEX
} from '../../../../utils/Constants';
import {PAYMENT_TYPES} from '../../../../data/PaymentTypes';
import {Logger} from 'react-logger-lib';
import {BankAccountInput} from "../../../../models/payment/BankAccountInput";
import {CreditCardInput} from "../../../../models/payment/CreditCardInput";
import {getDataPromise} from "../../../../actions/common/getData";
import {FieldsInfo} from "../../../../models/core/FieldsInfo";
import {toastr} from 'react-redux-toastr';

export const dialogInfo = {
    //fields: []
}

let Form = props => {
    const {handleSubmit, change, handleCancel, invalid} = props;
    let {type} = props;
    const {managedObject} = props.initialValues;
    if (!type && managedObject['_type_']) {
        switch (managedObject['_type_']) {
            case 'BankAccount':
                type = PAYMENT_TYPE_BANK_ACCOUNT;
                break;
            case 'CreditCard':
                type = PAYMENT_TYPE_CREDIT_CARD;
                break;
            default:
                break;
        }
        change('type',type);
    }
    return (
        <form onSubmit={handleSubmit}>

            <div class="payment__main modal47">

                <h3>General</h3>

                <div class="item__box">
                    <div class="text">
                        <p>Type<span class="star">*</span>:</p>
                    </div>
                    <div class="input board__input-wrapper">
                        <Field name="type" component={SelectField}
                               menuItems={PAYMENT_TYPES} required={true} disabled={managedObject.id}/>
                    </div>
                </div>

                <div class="item__box">
                    <div class="text">
                        <p>Payment Name<span class="star">*</span>:</p>
                    </div>
                    <Field name="managedObject.label" component={TextField} required={true} minLength={6}
                           maxLength={254}/>
                </div>

                <div class="item__box check">
                    <div class="text">
                        <p>Preferred:</p>
                    </div>
                    <Field name="managedObject.preferred" component={Checkbox}/>
                </div>

                {type === PAYMENT_TYPE_BANK_ACCOUNT && !managedObject.id &&

                [
                    <h3>Bank Account Information</h3>,

                    <div class="item__box">
                        <div class="text">
                            <p>The name on the customer's ACH Account<span class="star">*</span>:</p>
                        </div>
                        <Field name="managedObject.bankAccount.name" component={TextField} required={true} minLength={2}
                               maxLength={60}/>
                    </div>,
                    <div class="item__box">
                        <div class="text">
                            <p>Account Number<span class="star">*</span>:</p>
                        </div>
                        <Field name="managedObject.bankAccount.account" component={TextField} required={true}
                               minLength={2}
                               maxLength={60}/>
                    </div>,
                    <div class="item__box">
                        <div class="text">
                            <p>Routing Number<span class="star">*</span>:</p>
                        </div>
                        <Field name="managedObject.bankAccount.routing" component={TextField} required={true}
                               minLength={2}
                               maxLength={60}/>
                    </div>
                ]
                }
                {type === PAYMENT_TYPE_CREDIT_CARD && !managedObject.id &&
                [

                    <h3>Credit Card Information</h3>,
                    <div class="item__box">
                        <div class="text">
                            <p>Credit Card Number<span class="star">*</span>:</p>
                        </div>
                        <Field name="managedObject.creditCard.number" component={TextField} required={true} minLength={15} maxLength={16} regexPattern={CARD_NUMBER_PATTERN_REGEX}/>
                    </div>,
                    <div class="item__box">
                        <div class="text">
                            <p>Credit Card Expiration Date (MMYY)<span class="star">*</span>:</p>
                        </div>
                        <Field name="managedObject.creditCard.expiration" component={TextField} required={true}
                               mask="9999"
                               maskChar="_"
                               validate={validateCardExpDate}
                        />
                    </div>
                ]

                }
                <div class="item__box">
                    <div class="text">
                        <p>Please be informed that the selected payment method covers all of your office locations.</p>
                    </div>
                </div>
                <div class="item__box">
                    <div class="payment__help">
                        <p class="mandatory">Fields marked* are mandatory</p>
                    </div>
                </div>
            </div>
            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Cancel</button>
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>
                </div>
            </div>
        </form>)
}

Form = reduxForm({
    form: 'paymentmethod'
})(Form);

const selector = formValueSelector('paymentmethod');

Form = connect(state => {
    const type = selector(state, 'type');
    return {
        type: type
    }
})(Form)


class PaymentMethod extends BaseDialog {
    dialogProps() {
        return {
            width: 500,
            height: 500,
            className: "modal__gray",
            title: "Payment Method"
        }
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
        let me = this;
        Logger.of('App.AddPosting.beforeSave').info('ManagedObject data:', managedData);

        if (!managedData.managedObject.id) {
            getDataPromise('paymentForm', undefined, [new FieldsInfo({select: [{name: 'submitUrl'}]})]).then(function (data) {

                let inputs = undefined;

                switch (managedData.type) {
                    case PAYMENT_TYPE_CREDIT_CARD:
                        inputs = [
                            `<input type='hidden' name='billing-cc-number' value='${managedData.managedObject.creditCard.number}'/>`,
                            `<input type='hidden' name='billing-cc-exp' value='${managedData.managedObject.creditCard.expiration}'/>`
                        ];

                        break;
                    case PAYMENT_TYPE_BANK_ACCOUNT:
                        inputs = [
                            `<input type='hidden' name='billing-account-name' value='${managedData.managedObject.bankAccount.name}'/>`,
                            `<input type='hidden' name='billing-account-number' value='${managedData.managedObject.bankAccount.account}'/>`,
                            `<input type='hidden' name='billing-routing-number' value='${managedData.managedObject.bankAccount.routing}'/>`
                        ];
                        break;
                    default:
                        break;
                }

                var iframeBody =
                    `<form method='post' action='${data.submitUrl}' id='mainFrm'>` +
                    inputs.join('') +
                    `</form>`;


                var iframe = document.createElement('iframe');

                var frameId = "iframe" + (new Date()).getTime();
                iframe.id = frameId;
                iframe.width = 0;
                iframe.height = 0;
                document.body.appendChild(iframe);

                iframe.contentWindow.document.write(iframeBody);
                iframe.contentWindow.document.close();

                document.getElementById(frameId).onload = function () {
                    document.body.removeChild(iframe);
                }

                let listener = function (event) {

                    if (window.removeEventListener) {
                        window.removeEventListener("message", listener, false);
                    } else {
                        window.detachEvent("onmessage", listener);
                    }

                    if (event.origin !== window.location.protocol + "//" + window.location.host)
                        return;
                    switch (event.data.result) {
                        case 'success':
                            let pData = {
                                id: event.data.id,
                                label: managedData.managedObject.label,
                                preferred: managedData.managedObject.preferred
                            };
                            let saveData = undefined;

                            switch (managedData.type) {
                                case PAYMENT_TYPE_CREDIT_CARD:
                                    saveData = {
                                        card: new CreditCardInput(pData)
                                    };
                                    break;
                                case PAYMENT_TYPE_BANK_ACCOUNT:
                                    saveData = {
                                        ach: new BankAccountInput(pData)
                                    };
                                    break;
                                default:
                                    break;
                            }
                            me.executeSave(me, saveData, function (dialog) {
                                if (dialog.afterSave(dialog, saveData)) {
                                    dialog.onClose();
                                }
                            });
                            break;
                        case 'error':
                            if(event.data.message){
                                toastr.error('Error', event.data.message);
                            }
                            break;
                        default:
                            break;
                    }

                };

                if (window.addEventListener) {
                    window.addEventListener("message", listener, false);
                } else {
                    window.attachEvent("onmessage", listener);
                }

                iframe.contentWindow.document.getElementById('mainFrm').submit();

            });

            return false;
        } else {

            let pData = {
                id: managedData.managedObject.id,
                label: managedData.managedObject.label,
                preferred: managedData.managedObject.preferred
            };
            switch (managedData.type) {
                case PAYMENT_TYPE_CREDIT_CARD:
                    managedData.managedObject = {
                        card: new CreditCardInput(pData)
                    };
                    break;
                case PAYMENT_TYPE_BANK_ACCOUNT:
                    managedData.managedObject = {
                        ach: new CreditCardInput(pData)
                    };
                    break;
                default:
                    break;
            }

            return true;
        }
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}/>);
    }
}

const PaymentMethodDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(PaymentMethod);

export default PaymentMethodDialogConnector;
