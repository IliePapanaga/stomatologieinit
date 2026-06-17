import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import TextArea from '../../common/form/TextArea';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';
import SelectField from '../../common/form/SelectField';

import {validateCardExpDate} from '../../../../utils/Validators';
import {
    PAYMENT_INSTANT_PAY_BY_NEW_CARD,
    PAYMENT_OPTION_CANCEL,
    PAYMENT_OPTION_COMPLETE,
    PAYMENT_OPTION_INSTANT_PAY,
    PAYMENT_OPTION_MODIFY,
    PAYMENT_OPTION_PARTIAL,
    PAYMENT_OPTION_RUN
} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import RadioGroup from "../../common/form/RadioGroup";
import Remote from "../../../../utils/Remote";
import {getDataPromise} from "../../../../actions/common/getData";
import {saveDataPromise} from "../../../../actions/common/saveData";
import {FieldsInfo} from "../../../../models/core/FieldsInfo";

export const dialogInfo = {}

let Form = props => {
    const {handleSubmit, handleCancel, invalid, operation, paymentMethods} = props;

    return <form onSubmit={handleSubmit}>

        <div class="payment__main modal__gray-main modal56">
            <div class="modal__gray-main-item">
                <p class="text">Select option:</p>
                <div class="check__wrapper">
                    <ul>
                        <Field name="managedObject.operation"
                               items={[
                                   {
                                       code: PAYMENT_OPTION_MODIFY,
                                       name: 'Modify this payment amount'
                                   },
                                   {
                                       code: PAYMENT_OPTION_COMPLETE,
                                       name: 'Mark payment as complete'
                                   },
                                   {
                                       code: PAYMENT_OPTION_PARTIAL,
                                       name: 'Mark payment as partial'
                                   },
                                   {
                                       code: PAYMENT_OPTION_RUN,
                                       name: 'Re-run this payment'
                                   },
                                   {
                                       code: PAYMENT_OPTION_CANCEL,
                                       name: 'Cancel this payment'
                                   },
                                   {
                                       code: PAYMENT_OPTION_INSTANT_PAY,
                                       name: 'Accept one-time Existing Credit Card payment'
                                   },
                                   {
                                       code: PAYMENT_INSTANT_PAY_BY_NEW_CARD,
                                       name: 'Accept one-time New Card payment'
                                   }]}
                               required={true}
                               component={RadioGroup}
                               itemRenderer={function (item, value, name, itemClassWrapper, scope) {
                                   let disabled = scope.props.disabled;
                                   return <li>
                                       <input disabled={disabled} type="radio" checked={value === item.code}
                                              id={`${item.code}`} value={name}
                                              onChange={(e) => scope.handleChange(e, item.code)}/>
                                       <label for={`${item.code}`}>{item.name}</label>
                                   </li>
                               }
                               }/>
                    </ul>
                </div>
            </div>
            <div class="modal__gray-main-item">
                {[PAYMENT_OPTION_RUN, PAYMENT_OPTION_INSTANT_PAY].includes(operation) && paymentMethods &&
                <div class="item__box">
                    <div class="text">
                        <p>Method<span class="star">*</span>:</p>
                    </div>
                    <div class="input board__input-wrapper">
                        <Field name="managedObject.paymentMethodId" component={SelectField}
                               menuItems={paymentMethods.filter(p => operation === PAYMENT_OPTION_RUN || p['_type_'] === 'CreditCard')}
                               required={true}/>
                    </div>
                </div>
                }

                {
                    operation === PAYMENT_INSTANT_PAY_BY_NEW_CARD &&
                    [
                        //<div class="item__box" key="1"><div class="text"><p>First Name<span class="star">*</span>:</p></div><Field name="managedObject.first" autocomplete="off" component={TextField} required={true} minLength={2} maxLength={60} regexPattern={TEXTFIELD_PATTERN_REGEX}/></div>,
                        //<div class="item__box" key="2"><div class="text"><p>Last Name<span class="star">*</span>:</p></div><Field name="managedObject.last" autocomplete="off" component={TextField} required={true} minLength={2} maxLength={66} regexPattern={TEXTFIELD_PATTERN_REGEX}/></div>,
                        <div class="item__box" key="3">
                            <div class="text"><p>Credit Card Number <span class="star"> *</span>:</p></div>
                            <Field name="managedObject.number" component={TextField} required={true}
                                   mask="9999999999999999" maskChar="_"/></div>,
                        <div class="item__box" key="4">
                            <div class="text"><p>Credit Card Expiration Date (MMYY)<span class="star">*</span>:</p>
                            </div>
                            <Field name="managedObject.expiration" component={TextField} required={true} mask="9999"
                                   maskChar="_" validate={validateCardExpDate}/></div>
                    ]
                }

                <div class="item__box">
                    <div class="text">
                        <p>Amount:</p>
                    </div>
                    <Field
                        name="managedObject.amount"
                        component="input"
                        type="number"
                        disabled={![PAYMENT_OPTION_MODIFY, PAYMENT_OPTION_PARTIAL, PAYMENT_OPTION_INSTANT_PAY, PAYMENT_INSTANT_PAY_BY_NEW_CARD].includes(operation)}/>

                </div>
                <div class="item__box">
                    <div class="text">
                        <p>Comment<span class="star">*</span>:</p>
                    </div>
                    <Field name="managedObject.comment" component={TextArea} required={true}/>
                </div>
            </div>
        </div>

        <div class="footer__btn-wrapper">
            <div class="footer__btn">
                <button class="blue white" type="button" onClick={handleCancel}>Cancel</button>
                <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>
            </div>
        </div>

    </form>
}

Form = reduxForm({
    form: 'paymentoption'
})(Form);

const selector = formValueSelector('paymentoption');

Form = connect(state => {
    const operation = selector(state, 'managedObject.operation');
    return {
        operation: operation
    }
})(Form)


class PaymentOptions extends BaseDialog {

    state = {paymentMethods: undefined}

    dialogProps() {
        return {
            width: 560,
            height: 600,
            className: "modal__gray",
            title: "Payment Options"
        }
    }

    async componentDidMount() {
        let me = this,
            practiceId = me.props.managedObject.practiceId,
            fields = Remote.getFieldsByModel(me.props.metaInfo, "PaymentInstrumentConnection");

        let result = await getDataPromise('practicePaymentMethods', {practiceId: practiceId}, fields);
        me.setState({paymentMethods: result.nodes});

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
        let paymentMethod = managedData.managedObject.paymentMethodId;
        let operation = managedData.managedObject.operation;
        let originManagedObject = managedData.managedObject;

        managedData.managedObject = {
            amount: managedData.managedObject.amount,
            paymentMethodId: [PAYMENT_OPTION_RUN, PAYMENT_OPTION_INSTANT_PAY].includes(operation) ? paymentMethod : undefined,
            paymentId: managedData.managedObject.id,
            comment: managedData.managedObject.comment,
            operation: operation === PAYMENT_INSTANT_PAY_BY_NEW_CARD ? PAYMENT_OPTION_INSTANT_PAY : operation
        };

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);

        if (operation === PAYMENT_INSTANT_PAY_BY_NEW_CARD) {

            saveDataPromise({
                queryName: 'paymentManualOperation',
                fields: [new FieldsInfo({select: [{name: 'url'}]})]
            }, managedData.managedObject).then(function (data) {

                let inputs = [
                    `<input type='hidden' name='billing-cc-number' value='${originManagedObject.number}'/>`,
                    `<input type='hidden' name='billing-cc-exp' value='${originManagedObject.expiration}'/>`
                    //`<input type='hidden' name='billing-account-name' value='${managedData.managedObject.first}'/> ${managedData.managedObject.last}'/>`
                ];

                var iframeBody =
                    `<form method='post' action='${data.url}' id='mainFrm'>` +
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
                            // let pData = {
                            //     id: event.data.id,
                            //     label: managedData.managedObject.label,
                            //     preferred: managedData.managedObject.preferred
                            // };
                            // let saveData = undefined;
                            //
                            // switch (managedData.type) {
                            //     case PAYMENT_TYPE_CREDIT_CARD:
                            //         saveData = {
                            //             card: new CreditCardInput(pData)
                            //         };
                            //         break;
                            //     case PAYMENT_TYPE_BANK_ACCOUNT:
                            //         saveData = {
                            //             ach: new BankAccountInput(pData)
                            //         };
                            //         break;
                            //     default:
                            //         break;
                            // }
                            // me.executeSave(me, saveData, function (dialog) {
                            //     if (dialog.afterSave(dialog, saveData)) {
                            dialog.onClose();
                            //     }
                            // });
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
        }

        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}
                      paymentMethods={this.state.paymentMethods}/>);
    }
}

const
    PaymentOptionsDialogConnector = connect(
        function (state, ownProps) {
            return Object.assign(baseStateMap(state, ownProps), {});
        },
        function (dispatch) {
            return Object.assign(baseDispatcherMap(dispatch), {})
        }
    )(PaymentOptions);

export default PaymentOptionsDialogConnector;
