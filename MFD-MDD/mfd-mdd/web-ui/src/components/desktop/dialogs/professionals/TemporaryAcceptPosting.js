import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {reduxForm} from 'redux-form';
import {Logger} from 'react-logger-lib';
import DateHelper, {serverShortDateFormat} from "../../../../utils/DateHelper";
import moment from "moment/moment";
import DateChooser from "../../common/form/DateChooser";

export const dialogInfo = {
    references: ['categories', 'languages']
}

let Form = props => {
    const {handleSubmit, handleCancel, invalid, dialog, initialValues: {managedObject, allDays}, references, viewDates, viewDates1, onViewDates, onViewDates1, dates, days, onCancel, readOnly, zonedJobDays} = props;
    let name = managedObject.name;
    let selectedCategories = references.categories.filter((item) => item.subCategories.filter(subCategories => subCategories.id === managedObject.requiredSubcategories[0]).length > 0);
    let location = name.substring(name.lastIndexOf("\\") + 1);
    let specialty = name.substring(0, name.lastIndexOf("\\"));

    return (
        <form onSubmit={handleSubmit}>

            <div class="apptemp__main-posting modal26">
                <div class="posting__item">
                    <h3>Information</h3>
                    <div class="item__box">
                        <div class="title">
                            <p>Posting Name:</p>
                        </div>
                        <div class="data">
                            <p>{name}</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Location:</p>
                        </div>
                        <div class="data">
                            <p>{location}</p>
                        </div>
                    </div>
                </div>
                <div class="posting__item">
                    <h3>Speciality</h3>
                    <div class="item__box">
                        <div class="title">
                            <p>Category:</p>
                        </div>
                        <div class="data">
                            <p>{selectedCategories[0].name}</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Specialty:</p>
                        </div>
                        <div class="data">
                            <p>{specialty}</p>
                        </div>
                    </div>
                </div>
                <div class="posting__item">
                    <h3>Dates required for the job</h3>

                    <div class="item__box">
                        <div class="title">
                            <p>Starts:</p>
                        </div>
                        <div class="data">
                            <p>{DateHelper.joinDateAndTimeString(managedObject.startDate, managedObject.startTime, "dddd, MMMM Do, YYYY")}</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Ends:</p>
                        </div>
                        <div class="data">
                            <p>
                                <p>{DateHelper.joinDateAndTimeString(managedObject.endDate, managedObject.endTime, "dddd, MMMM Do, YYYY")}</p>
                            </p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Total days required for the job:</p>
                        </div>
                        <div class="data">
                            <p>{managedObject.zonedJobDays.length}</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <button class="blue" type="button" onClick={onViewDates}>Show in Calendar
                        </button>
                        <div className="info-div">
                            <div className="state__attention">
                                <div className="item__box">
                                    <div className="text">
                                        <p>Important: Please view calendar to see all work days</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <br/>
                        {viewDates &&
                        <div class="date__wrapper">
                            <DateChooser inline="true"
                                         zonedJobDays={zonedJobDays}
                                         dropdownMode="select" showMonthDropdown={true}
                                         minDate={DateHelper.getServerDate(managedObject.startDate)}
                                         maxDate={DateHelper.getServerDate(managedObject.endDate)}/>
                            <div class="footer__btn-wrapper">
                                <div class="footer__btn">
                                    <button class="blue white" type="button" onClick={function () {
                                        dialog.setState({viewDates: false})
                                    }}>Close
                                    </button>
                                    {false && <button class="blue" type="button">Ok</button>}
                                </div>
                            </div>
                        </div>
                        }
                    </div>

                </div>
                <div class="posting__item">
                    <h3>Selection dates</h3>

                    <div class="item__box item__box-check">
                        <input type="checkbox" name="allDays" checked={allDays} id="allDays" readonly="readonly"/>
                        <label for="allDays"></label>

                        <div class="text">
                            <p>You are available during the whole required period</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="title">
                            <p>Total days you are available:</p>
                        </div>
                        <div class="data">
                            <p>{days} </p>
                        </div>
                    </div>

                    <div class="item__box">
                        <button class="blue" type="button" onClick={onViewDates1}>Show in Calendar
                        </button>
                        <br/>
                        {viewDates1 &&
                        <div class="date__wrapper">
                            <DateChooser inline="true"
                                         highlightDates={dates}
                                         zonedJobDays={zonedJobDays}
                                         dropdownMode="select" showMonthDropdown={true}
                                         minDate={DateHelper.getServerDate(managedObject.startDate)}
                                         maxDate={DateHelper.getServerDate(managedObject.endDate)}/>

                            <div class="footer__btn-wrapper footer__btn-wrapper-legend">
                                <div class="footer__legend">

                                    <div class="footer__legend-item">
                                        <div class="color"></div>
                                        <p>Available dates for work</p>
                                    </div>
                                    <div className="footer__legend-item">
                                        <div className="color"></div>
                                        <p>Days you applying for work</p>
                                    </div>
                                </div>
                                <div class="footer__btn">
                                    <button class="blue white" type="button" onClick={onCancel}>Close</button>
                                </div>
                            </div>
                        </div>
                        }
                    </div>

                </div>
                <div class="posting__item">
                    <div class="item__box">
                        <div class="title">
                            <h3>Comments:</h3>
                        </div>
                        <div class="data">
                            <p>{managedObject.comment}</p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button class="blue white" type="button" onClick={handleCancel}>Close</button>
                    {!readOnly &&
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Accept</button>}
                </div>
            </div>
        </form>)
}

Form = reduxForm({
    form: 'temporaryacceptposting'
})(Form);


class TemporaryAcceptPosting extends BaseDialog {

    state = {
        viewDates: false,
        viewDates1: false,
        dates: [],
        days: 0,
        allDays: true
    }


    constructor(props) {
        super(props);

        let jobDays = props.managedObject.application ? props.managedObject.application.jobDays : props.managedObject.zonedJobDays.filter(d => {
            return !d.excluded;
        });
        let workingDays = [];
        jobDays.forEach(function (jobDay) {
            workingDays.push(moment(jobDay.date, serverShortDateFormat));
        });

        this.state['dates'] = workingDays;
        this.state['days'] = workingDays.length;
        this.state['allDays'] = true;
        this.state['zonedJobDays'] = props.managedObject.zonedJobDays.filter(d => {
            return !d.excluded;
        });
    }

    dialogProps() {
        return {
            width: 700,
            height: 700,
            className: "modal__gray",
            title: "Posting"
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

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        let managedObject = managedData.managedObject;

        managedData.managedObject = {
            applicationId: managedObject.applicationId
        }
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject, allDays: this.state.allDays}}
                      references={this.props.references} viewDates={this.state.viewDates}
                      viewDates1={this.state.viewDates1}
                      zonedJobDays={this.state.zonedJobDays}
                      days={this.state.days}
                      dates={this.state.dates}
                      onViewDates={this.viewDates.bind(this)} onViewDates1={this.viewDates1.bind(this)}
                      onCancel={this.onCancel.bind(this)}
                      readOnly={this.props.readOnly}
        />);
    }

    viewDates() {
        this.setState({viewDates: !this.state.viewDates});
    }

    viewDates1() {
        this.setState({viewDates1: !this.state.viewDates1});
    }

    onCancel() {
        let me = this;

        me.setState({
            viewDates1: false
        });
    }
}

const TemporaryAcceptPostingDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(TemporaryAcceptPosting);

export default TemporaryAcceptPostingDialogConnector;
