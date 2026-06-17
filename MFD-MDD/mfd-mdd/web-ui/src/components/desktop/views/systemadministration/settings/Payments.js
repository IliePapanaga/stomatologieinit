import React from 'react';
import {connect} from 'react-redux';
import {reduxForm} from 'redux-form';
import Field from '../../../common/form/Field';
import TextField from '../../../common/form/TextField';
import saveData from '../../../../../actions/common/saveData';
import {EVENT_VIEW_SAVE_DATA} from '../../../../../utils/Constants';
import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {toastr} from 'react-redux-toastr';
import Settings from "../../common/Settings";


let PaymentsForm = props => {
    const {handleSubmit, invalid} = props;
    return (
        <form onSubmit={handleSubmit}>


            <div class="board__header">
            </div>


            <div class="board__action">

            </div>


            <div class="content pro__setting sys__setting-payments">
                <div class="view__header">
                    <h2>Payments Settings</h2>
                </div>
                <div class="sys__setting-payments-item-wrapper">
                    <div class="sys__setting-payments-item">
                        <h3>Prime Rate Merchant’s Specifics</h3>
                        <div class="item__box">
                            <div class="text">
                                <p>Login<span class="star">*</span>:</p>
                            </div>

                            <Field name="payments__prime_rate_specifics__login" setting__general autocomplete="off"
                                   component={TextField} required={true}
                                   minLength={6} maxLength={60}/>

                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>Password<span class="star">*</span>:</p>
                            </div>

                            <Field type="password" name="payments__prime_rate_specifics__password" setting__general
                                   autocomplete="off"
                                   component={TextField} required={true}
                                   minLength={6} maxLength={60}/>

                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>API Keys<span class="star">*</span>:</p>
                            </div>
                            <Field name="payments__prime_rate_specifics__api_key" setting__general autocomplete="off"
                                   component={TextField} required={true}
                                   minLength={6} maxLength={60}/>
                        </div>
                    </div>


                    <div class="sys__setting-payments-item">
                        <h3>Payment Attempts:</h3>
                    {/*    <div class="item__box">
                            <div class="text">
                                <p>Payments attempts<span class="star">*</span>:</p>
                            </div>
                            <Field name="payments__attempts__number_of_attempts" setting__general autocomplete="off"
                                   component={TextField} required={true}
                                   minLength={1} maxLength={10}/>
                        </div>*/}

                        <div class="item__box">
                            <div class="text">
                                <p>Attempts Bank Account<span class="star">*</span>:</p>
                            </div>
                            <Field name="payments__attempts__number_of_attempts_ach" setting__general autocomplete="off"
                                   component={TextField} required={true}
                                   minLength={1} maxLength={10}/>
                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>Attempts Credit Card<span class="star">*</span>:</p>
                            </div>
                            <Field name="payments__attempts__number_of_attempts_card" setting__general autocomplete="off"
                                   component={TextField} required={true}
                                   minLength={1} maxLength={10}/>
                        </div>



                        <div class="item__box">
                            <div class="text">
                                <p>Payments interval (days)<span class="star">*</span>:</p>
                            </div>
                            <Field name="payments__attempts__interval_days" setting__general autocomplete="off"
                                   component={TextField} required={true}
                                   minLength={1} maxLength={10}/>
                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>Penalty fee for failed ACH payment($)<span class="star">*</span>:</p>
                            </div>
                            <Field name="payments__attempts__ach_penalty_fee" setting__general autocomplete="off"
                                   component={TextField} required={true}
                                   minLength={1} maxLength={10}/>
                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>Penalty fee for failed CC payments($)<span class="star">*</span>:</p>
                            </div>
                            <Field name="payments__attempts__cc_penalty_fee" setting__general autocomplete="off"
                                   component={TextField} required={true}
                                   minLength={1} maxLength={10}/>
                        </div>
                    </div>


                </div>
                <div class="sys__setting-payments-help">
                    <p class="mandatory">Fields marked* are mandatory</p>
                </div>
                <div class="footer__btn">
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Save</button>
                </div>

            </div>
        </form>)
}

const Form = reduxForm({
    form: 'payments'
})(PaymentsForm);

class Payments extends Settings {

    render() {
        if (!this.state.settings) {
            return false;
        }
        return ([
            <div class="board__header"></div>,
            <div class="board__action"></div>,
            <Form onSubmit={this.submit.bind(this)}
                  initialValues={this.state.settings}/>
        ]);
    }
}

const PaymentsConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseViewDispatcherMap(dispatch), {
            onSave: (managedObject) => {
                dispatch(saveData({queryName: 'updateSystemSettings'}, managedObject)).then(
                    function (result) {

                        dispatch({type: EVENT_VIEW_SAVE_DATA, result});
                        toastr.success("Change Payment Settings Data", "The data has been changed successfully.");

                    });
            }
        });
    })(Payments);

export default PaymentsConnector;