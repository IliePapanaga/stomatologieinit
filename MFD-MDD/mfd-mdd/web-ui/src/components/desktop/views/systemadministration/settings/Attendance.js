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


let AttendanceForm = props => {
    const {handleSubmit, invalid} = props;
    return (
        <form onSubmit={handleSubmit}>


            <div class="board__header">
            </div>


            <div class="board__action">

            </div>


            <div class="content pro__setting sys__setting-att">

                <div class="view__header">
                    <h2>Attendance Settings</h2>
                </div>

                <div class="sys__setting-att-item-wrapper">
                    <div class="sys__setting-att-item">
                        <div class="item__box">
                            <div class="text">
                                <p>Allowed number of No Show<span class="star">*</span>:</p>
                            </div>
                            <Field name="postings__default__allowed_no_show" setting__general autocomplete="off"
                                   component={TextField} required={true}
                                   minLength={1} maxLength={3} readonly="readonly" />
                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>Allowed number of rejections (denials)<span class="star">*</span>:</p>
                            </div>
                            <Field name="postings__default__allowed_rejections" setting__general autocomplete="off"
                                   component={TextField} required={true}
                                   minLength={1} maxLength={3} readonly="readonly"/>
                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>Automatic payment starts after starting job (min)<span class="star">*</span>:</p>
                            </div>
                            <Field name="postings__default__payment_start_after_starting_job" setting__general
                                   autocomplete="off"
                                   component={TextField} required={true}
                                   minLength={1} maxLength={3} readonly="readonly"/>
                        </div>
                    </div>
                    <div class="sys__setting-att-help">
                        <p class="mandatory">Fields marked* are mandatory</p>
                    </div>
                    <div class="footer__btn">
                        <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Save</button>
                    </div>
                </div>
            </div>
        </form>)
}

const Form = reduxForm({
    form: 'attendance'
})(AttendanceForm);

class Attendance extends Settings {


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

const AttendanceConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseViewDispatcherMap(dispatch), {
            onSave: (managedObject) => {
                dispatch(saveData({queryName: 'updateSystemSettings'}, managedObject)).then(
                    function (result) {

                        dispatch({type: EVENT_VIEW_SAVE_DATA, result});
                        toastr.success("Change Attendance Settings Data", "The data has been changed successfully.");

                    });
            }
        });
    })(Attendance);

export default AttendanceConnector;