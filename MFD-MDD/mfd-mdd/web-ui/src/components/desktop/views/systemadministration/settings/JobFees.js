import React from 'react';
import {connect} from 'react-redux';
import {reduxForm} from 'redux-form';
import Field from '../../../common/form/Field';
import TextField from '../../../common/form/TextField';
import saveData from '../../../../../actions/common/saveData';
import {
    EVENT_VIEW_SAVE_DATA,
    TEXTFIELD_NUMBERS_PATTERN_REGEX
} from '../../../../../utils/Constants';
import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {toastr} from 'react-redux-toastr';
import Settings from "../../common/Settings";


let GeneralForm = props => {
    const {handleSubmit, invalid} = props;
    return (
        <form onSubmit={handleSubmit}>


            <div class="board__header">
            </div>


            <div class="board__action">

            </div>


            <div class="content pro__setting sys__setting-job">

                <div class="view__header">
                    <h2>Job Fees Settings</h2>
                </div>

                <div class="sys__setting-job-item-wrapper">

                    <div class="sys__setting-job-item">
                        <h3>Temporary job fees:</h3>
                        <div class="item__box">
                            <div class="text">
                                <p>Compensation Fee for RDA/DA/RDAEF 1 and 2/Front Office($)<span class="star">*</span>:</p>
                            </div>
                            <Field name="job_fees__temporary__compensation_rda" setting__general autocomplete="off"
                                   component={TextField} type="number" regexPattern={TEXTFIELD_NUMBERS_PATTERN_REGEX} required={true}
                                   minLength={1} maxLength={3}/>
                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>Compensation Fee for RDH ($)<span class="star">*</span>:</p>
                            </div>
                            <Field name="job_fees__temporary__compensation_rdh" setting__general autocomplete="off"
                                   component={TextField} type="number" regexPattern={TEXTFIELD_NUMBERS_PATTERN_REGEX} required={true}
                                   minLength={1} maxLength={3}/>
                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>Compensation Fee for DDS/DMD ($)<span class="star">*</span>:</p>
                            </div>
                            <Field name="job_fees__temporary__compensation_dds" setting__general autocomplete="off"
                                   component={TextField} type="number" regexPattern={TEXTFIELD_NUMBERS_PATTERN_REGEX} required={true}
                                   minLength={1} maxLength={3}/>
                        </div>

                        <div class="item__box">
                            <div class="text">
                                <p>Compensation Fee for Specialist (endodontist, orthodontist, anesthesiologist,
                                    etc) ($)<span class="star">*</span>:</p>
                            </div>
                            <Field name="job_fees__temporary__compensation_specialist" setting__general
                                   autocomplete="off"
                                   component={TextField} type="number" regexPattern={TEXTFIELD_NUMBERS_PATTERN_REGEX} required={true}
                                   minLength={1} maxLength={3}/>
                        </div>

                        {false && <div class="item__box">
                            <div class="text">
                                <p>Compensation Fee (%)<span class="star">*</span>:</p>
                            </div>
                            <Field name="job_fees__temporary__compensation_dds" setting__general autocomplete="off"
                                   component={TextField} type="number" regexPattern={TEXTFIELD_NUMBERS_PATTERN_REGEX} required={true}
                                   minLength={1} maxLength={3}/>
                        </div>}
                    </div>


                    <div class="sys__setting-job-item">
                        <h3>Permanent Job:</h3>
                        <div class="item__box">
                            <div class="text">
                                <p>Weeks per year<span class="star">*</span>:</p>
                            </div>
                            <Field name="job_fees__permanent__weeks_per_year" setting__general autocomplete="off"
                                   component={TextField} type="number" required={true} readonly={'readonly'}
                                   minLength={1} maxLength={2} min={1} max={52}/>
                        </div>
                        <div class="item__box">
                            <div class="text">
                                <p>Percentage for hiring of Assistants, Front Desks, Hygienists (%)<span class="star">*</span>:</p>
                            </div>
                            <Field name="job_fees__permanent__percentage_for_hiring_others" setting__general autocomplete="off"
                                   component={TextField} type="number" regexPattern={TEXTFIELD_NUMBERS_PATTERN_REGEX} required={true}
                                   minLength={1} max={100}/>
                        </div>
                        <div className="item__box">
                            <div className="text">
                                <p>Percentage for hiring of Dentists/Specialists (%)<span className="star">*</span>:</p>
                            </div>
                            <Field name="job_fees__permanent__percentage_for_hiring_dentists" setting__general autocomplete="off"
                                   component={TextField} type="number" regexPattern={TEXTFIELD_NUMBERS_PATTERN_REGEX}
                                   required={true}
                                   minLength={1} max={100}/>
                        </div>
                    </div>


                </div>
                <div class="sys__setting-job-help">
                    <p class="mandatory">Fields marked* are mandatory</p>
                </div>
                <div class="footer__btn">
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Save</button>
                </div>

            </div>
        </form>)
}

const Form = reduxForm({
    form: 'jobfees'
})(GeneralForm);

class JobFees extends Settings {

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

const JobFeesConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseViewDispatcherMap(dispatch), {
            onSave: (managedObject) => {
                dispatch(saveData({queryName: 'updateSystemSettings'}, managedObject)).then(
                    function (result) {

                        dispatch({type: EVENT_VIEW_SAVE_DATA, result});
                        toastr.success("Change Job Fees Settings Data", "The data has been changed successfully.");

                    });
            }
        });
    })(JobFees);

export default JobFeesConnector;