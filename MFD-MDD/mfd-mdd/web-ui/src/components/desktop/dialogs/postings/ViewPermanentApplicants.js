import React from 'react';
import {connect, Provider} from 'react-redux';
import {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import BaseGrid from '../../common/BaseGrid';
import {formValueSelector, reduxForm} from 'redux-form';
import {
    DEFAULT_CELL_STYLE,
    EVENT_VIEW_SAVE_DATA,
    REST_API_PREFIX,
    REST_API_PREFIX_GET_USER_PHOTO,
    SELECTION_MODE_NONE, STATUS_BOOKING_APPLIED, STATUS_BOOKING_FILLED, STATUS_BOOKING_OFFER_SENT,
    STATUS_CANCELLED,
    STATUS_COMPLETED,
    STATUS_FILLED,
    STATUS_INVITED,
    STATUS_REJECTED,
    STATUS_SCHEDULED,
} from '../../../../utils/Constants';
import Renderer from '../../../../utils/Renderer';
import {uiDateFormat} from '../../../../utils/DateHelper';
import UiView from '../../../../utils/UiView';
import {ViewApplicantsDialog} from "./ViewApplicants";
import PermanentSelectInterviewDate from "../practice/PermanentSelectInterviewDate";
import saveData, {saveDataPromise} from "../../../../actions/common/saveData";
import {BOOKING, INTERVIEW} from "../../../../data/Statuses";
import Remote from "../../../../utils/Remote";
import {getDataPromise} from "../../../../actions/common/getData";
import DateField from "../../common/form/DateField";
import Field from "../../common/form/Field";


export const dialogInfo = {
    references: ['categories', 'languages', 'educations', 'academicDegrees']
}

let Form = props => {
    const {handleSubmit, handleCancel, change, onSchedule, onCancelSchedule, onBook, onCancel, onView, dialog, readOnly, interviewDate} = props;
    const managedObject = props.initialValues.managedObject;
    const postingSpecialties = managedObject.name ? managedObject.name.toLowerCase().split('\\') : null;
    if (postingSpecialties) {
        postingSpecialties.forEach(s => postingSpecialties.push(s.replace('_', ' ')))
    }
    const columns = [
        {
            dataIndex: 'professionalId',
            name: 'professionalId',
            title: 'ProfessionalId',
            hidden: true,
            cellClass: DEFAULT_CELL_STYLE
        }, {
            dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
        },
        {
            dataIndex: 'lastName',
            name: 'lastName:',
            title: 'Last Name',
            orderName: 'LAST_NAME',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                let src = REST_API_PREFIX_GET_USER_PHOTO.replace('{0}', row.professionalId);
                return (<div>
                    <img src={src} onClick={onView.bind(dialog, row)} alt=""/><p
                    onClick={onView.bind(dialog, row)}>{value}</p>
                </div>);
            }
        },
        {
            dataIndex: 'firstName',
            name: 'firstName',
            title: 'First Name',
            orderName: 'FIRST_NAME',
            cellClass: DEFAULT_CELL_STYLE
        },
        {
            dataIndex: 'specialty',
            name: 'specialty',
            title: 'Specialty',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                const applicantSpecialties = value.split(',');
                const commonSpecialties = applicantSpecialties.filter(function (value) {
                    return value && postingSpecialties ? postingSpecialties.indexOf(value.toLowerCase()) > -1 : false;
                });
                return <div class={cellClass}>{commonSpecialties.join(', ')}</div>;
            }

        },
        {
            dataIndex: 'rph',
            name: 'rph',
            title: 'Rate($)',
            orderName: 'RPH',
            cellClass: DEFAULT_CELL_STYLE
        },
        {
            dataIndex: 'rating',
            name: 'rating',
            title: 'Rating',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: Renderer.getRatingRenderer
        },
        {
            dataIndex: 'interviewStatus',
            name: 'interviewStatus',
            title: 'Interview Status',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: Renderer.getStatusCenterRenderer(INTERVIEW)
        }];


    if (!readOnly) {
        columns.push({
            dataIndex: 'id',
            name: 'id',
            title: 'Interview',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                return (
                    <div>
                        <button class="white blue" type="button"
                                disabled={[STATUS_FILLED].includes(managedObject.status) || (row.interviewStatus && (![STATUS_REJECTED, STATUS_CANCELLED, STATUS_COMPLETED].includes(row.interviewStatus)))}
                                onClick={onSchedule.bind(dialog, row)}>Schedule
                        </button>
                        &nbsp;
                        <button class="white blue" type="button"
                                disabled={!row.interviewId || ![STATUS_INVITED, STATUS_SCHEDULED].includes(row.interviewStatus)}
                                onClick={onCancelSchedule.bind(dialog, row)}>Cancel
                        </button>
                    </div>)
            }
        });

        columns.push({
            dataIndex: 'bookingStatus',
            name: 'bookingStatus',
            title: 'Booking Status',
            renderer: Renderer.getStatusCenterRenderer(BOOKING),
        });

        columns.push({
            dataIndex: 'id',
            name: 'id',
            title: 'Booking',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                return (
                    <div>
                        <button class="blue short" type="button"
                                disabled={[STATUS_BOOKING_FILLED, STATUS_BOOKING_OFFER_SENT].includes(row.bookingStatus)}
                                onClick={onBook.bind(dialog, row)}>Hire
                        </button>
                        {<button class="white blue" type="button"
                                 disabled={![STATUS_BOOKING_FILLED].includes(row.bookingStatus)}
                                 onClick={onCancel.bind(dialog, row)}>Cancel
                        </button>}
                    </div>)
            }
        });
    }
    return (
        <form onSubmit={handleSubmit}>
            {interviewDate != null && <div class="view__btn">
                <Field name="interviewDate" component={DateField} readOnly={true} onlyDate={false}
                       dateFormat={uiDateFormat}/>
            </div>}
            <div class="modal__gray-main modal15 modal49">
                <div class="modal__table">
                    <BaseGrid
                        columns={columns}
                        selectionMode={SELECTION_MODE_NONE}
                        url={REST_API_PREFIX} queryName="permanentPostingApplicants"
                        additionalFields={['interviewId', 'interviewStatus', 'bookingStatus']}
                        params={{postingId: managedObject.id}}
                        onRef={ref => (dialog.applicantsGrid = ref)}
                        hasPagination={true}
                        localOrdering={false}
                        key="base_view_base_grid"
                        changeSelection={dialog.onSelectRecord.bind(dialog, change)}/>
                </div>
            </div>


            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Close</button>
                </div>
            </div>

        </form>)
}

Form = reduxForm({
    form: 'viewpermanentapplicants'
})(Form);

const selector = formValueSelector('viewpermanentapplicants');

Form = connect(state => {
    const interviewDate = selector(state, 'interviewDate')
    return {
        interviewDate: interviewDate
    }
})(Form);


class ViewPermanentApplicants extends ViewApplicantsDialog {
    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}
                      onBook={this.onBook} onCancel={this.onCancel} onView={this.onView}
                      onSchedule={this.onSchedule} onCancelSchedule={this.onCancelSchedule}
                      readOnly={this.props.readOnly}/>);
    }

    onSelectRecord(change, selection) {
        let me = this;
        if (selection.length > 0 && selection[0].interviewId) {
            let requestFields = Remote.getFieldsByModel(me.props.metaInfo, "ScheduledJobInterview", [], false);
            getDataPromise("interview", {id: selection[0].interviewId}, requestFields).then(function (interview) {
                if (interview.acceptedOption) {
                    change('interviewDate', interview.acceptedOption.dateTime);
                }
            });
        }
    }

    onSchedule(applicant, ev) {
        let me = this;

        let actions = {
            save: function (editor, newManagedObject, successfulCallBack) {
                saveDataPromise({
                    queryName: 'scheduleInterview',
                    showLoader: true
                }, newManagedObject).then(function (result) {
                    applicant.interviewStatus = STATUS_INVITED;
                    me.applicantsGrid.setState({selection: []});
                    editor.close();
                });
            }
        }

        UiView.showDialog(<Provider store={UiView.createDialogStore()}><PermanentSelectInterviewDate
            managedObject={{applicant: applicant, posting: this.props.managedObject}}
            references={me.props.references}
            metaInfo={me.props.metaInfo} actions={actions}/></Provider>);
    }

    onBook(row, ev) {
        let me = this;
        let selection = [row];
        if (selection.length > 0) {
            let requestFields = Remote.getFieldsByModel(me.props.metaInfo, "PaymentPermanentInfo", [], false);
            getDataPromise("permanentPaymentDetailsPreview", {applicationId: selection[0].id}, requestFields).then(function (details) {
                const days = details.workingDaysPerWeek.split(',');
                me.onExecuteOperation(ev,
                    {applicationId: selection[0].id},
                    <div class="modal57">
                        <div class="header important"><h2>Hire this candidate</h2></div>
                        <div class="body">
                            <div class="message-info">
                                Please confirm you are
                                hiring <span class="message-info-value">{details.professionalFirstName} {details.professionalLastName}</span> as <span class="message-info-value">{me.getPostingSpeciality()}</span>.
                            </div>
                            <div class="message-info">
                                Working days per week: <span class="message-info-value">{days.length}</span> ({details.workingDaysPerWeek.split(',').join(', ')})
                            </div>
                            <div class="message-info">
                                Working hours per a day: <span class="message-info-value">{details.hoursPerDay}</span>
                            </div>
                            <div class="message-info">
                                Number of weeks in a year: <span class="message-info-value">{details.weeksPerYear}</span>
                            </div>
                            <div class="message-info">
                                Hourly rate: <span class="message-info-value">${details.hourlyRate}</span>
                            </div>
                            <div class="message-info">
                                Total permanent placement fee: <span class="message-info-value">${details.totalFee}</span>.
                            </div>
                            <div class="message-info">
                                Your job will marked as filled for these dates.
                            </div>
                            <div class="message-info">
                                Please be informed that a desired rate for Bill Parter is <span class="message-info-value">${details.hourlyRate}</span> per hour.
                                Information about hourly rate will be sent to the candidate and Mayday Dental Staffing
                                office.
                            </div>
                        </div>
                    </div>,
                    'bookApplication',
                    function (returnObject) {
                        console.log(returnObject);
                        let updatedObject = Object.assign({}, selection[0]);
                        updatedObject.bookingStatus = STATUS_BOOKING_OFFER_SENT;
                        me.applicantsGrid.updateRow(selection[0], updatedObject);
                    });
            });
        }
    }

    onCancel(row, ev) {
        let me = this;
        let selection = [row];
        if (selection.length > 0) {
            me.onExecuteOperation(ev,
                {applicationId: selection[0].id},
                <div class="modal57">
                    <div class="header deactivate"><h2>Cancel</h2></div>
                    <div class="body">
                        Are you sure to cancel booking?
                    </div>
                </div>,
                'cancelApplication',
                function (returnObject) {
                    row.bookingStatus = STATUS_BOOKING_APPLIED;
                    me.applicantsGrid.setState({selection: []});
                });
        }
    }

    onCancelSchedule(row, ev) {
        let me = this;
        let selection = [row];
        if (selection.length > 0) {
            me.onExecuteOperation(ev,
                {id: selection[0].interviewId},
                <div class="modal57">
                    <div class="header deactivate"><h2>Cancel Interview</h2></div>
                    <div class="body">
                        Are you sure you want to cancel the scheduled interview?
                    </div>
                </div>,
                'cancelInterview',
                function (returnObject) {
                    row.interviewStatus = STATUS_CANCELLED;
                    me.applicantsGrid.setState({selection: []});
                });
        }
    }

}

const ViewPermanentApplicantsDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
            onExecuteOperation: (queryName, managedObject, callBackFn) => {
                let me = this;
                dispatch(saveData({queryName: queryName}, managedObject)).then(
                    function (result) {
                        dispatch({type: EVENT_VIEW_SAVE_DATA, result});
                        callBackFn.call(me, result);
                    });
            }
        });
    })(ViewPermanentApplicants);

export default ViewPermanentApplicantsDialogConnector;