import React from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {reduxForm} from 'redux-form';
import {Logger} from 'react-logger-lib';
import DateHelper, {serverShortDateFormat} from "../../../../utils/DateHelper";
import DateChooser from "../../common/form/DateChooser";
import moment from "moment/moment";

export const dialogInfo = {
    references: ['categories', 'languages']
}

let Form = props => {
    const {handleSubmit, dialog, handleCancel, invalid, initialValues: {managedObject, allDays}, references, viewDates, viewDates1, onViewDates, onViewDates1, onSelectDates, setAllDays, dates, days, onCancel, onApply, zonedJobDays} = props;
    let name = managedObject.name;
    let selectedCategories = references.categories.filter((item) => item.subCategories.filter(subCategories => subCategories.id === managedObject.requiredSubcategories[0]).length > 0);
    let location = name.substring(name.lastIndexOf("\\") + 1);
    let specialty = name.substring(0, name.lastIndexOf("\\"));


    return (
        <form onSubmit={handleSubmit}>

            <div class="apptemp__main-posting modal26 modal59">
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
                            <p>{DateHelper.joinDateAndTimeString(managedObject.endDate, managedObject.endTime, "dddd, MMMM Do, YYYY")}</p>
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
                                         dropdownMode="select" showMonthDropdown={true}
                                         minDate={DateHelper.getServerDate(managedObject.startDate)}
                                         maxDate={DateHelper.getServerDate(managedObject.endDate)}
                                         zonedJobDays={zonedJobDays}
                            />
                            <div class="footer__btn-wrapper">
                                <div class="footer__btn">
                                    <button class="blue white" type="button" onClick={function () {
                                        dialog.setState({viewDates: false})
                                    }}>Cancel
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

                    <div class="item__box">
                        <div class="input">
                            <input type="checkbox" name="allDays" checked={allDays} id="allDays" onChange={setAllDays}/>
                            <label for="allDays"></label>
                        </div>
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
                                         maxDate={DateHelper.getServerDate(managedObject.endDate)}
                                         onChange={onSelectDates}/>

                            <div class="footer__btn-wrapper footer__btn-wrapper-legend">
                                <div class="footer__warning">Important: Please select dates when you are available
                                    for a job
                                </div>
                                <div class="footer__legend">
                                    <div className="footer__legend-item">
                                        <div className="color"></div>
                                        <p>Available dates for work</p>
                                    </div>
                                    <div className="footer__legend-item">
                                        <div className="color"></div>
                                        <p>Days you are applying to work</p>
                                    </div>
                                </div>
                                <div class="footer__btn">
                                    <button class="blue white" type="button" onClick={onCancel}>Cancel
                                    </button>
                                    <button class="blue" type="button" onClick={onApply}>Ok</button>
                                </div>
                            </div>
                        </div>}
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
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Apply</button>
                </div>
            </div>
        </form>)
}

Form = reduxForm({
    form: 'temporaryaplyposting'
})(Form);

class TemporaryApplyPosting extends BaseDialog {
    state = {
        viewDates: false,
        viewDates1: false,
        tmpDates: [],
        dates: [],
        zonedJobDays: [],
        days: 0,
        allDays: true
    }


    constructor(props) {
        super(props);
        let workingDays = [];//DateHelper.getDays(props.managedObject.startDate, props.managedObject.endDate, true);
        this.state['tmpDates'] = workingDays;
        this.state['dates'] = workingDays;
        this.state['days'] = workingDays.length;
        this.state['allDays'] = false;
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
        let workingDays = [];

        this.state.dates.forEach(function (m) {
            workingDays.push(m.format(serverShortDateFormat));
        });

        managedData.managedObject = {
            temporaryJobApplication: {
                jobPostingId: managedObject.id,
                workingDays: workingDays
            }
        }
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject, allDays: this.state.allDays}}
                      references={this.props.references}
                      viewDates={this.state.viewDates}
                      viewDates1={this.state.viewDates1}
                      days={this.state.days}
                      zonedJobDays={this.state.zonedJobDays}
                      dates={this.state.tmpDates}
                      onViewDates={this.viewDates.bind(this)} onViewDates1={this.viewDates1.bind(this)}
                      onSelectDates={this.onSelectDates.bind(this)} setAllDays={this.setAllDays.bind(this)}
                      onCancel={this.onCancel.bind(this)}
                      onApply={this.onApply.bind(this)}/>);
    }

    viewDates() {
        this.setState({viewDates: !this.state.viewDates});
    }

    viewDates1() {
        if (!this.state.viewDates1) {
            this.setState({startDate: undefined});
        }
        this.setState({viewDates1: !this.state.viewDates1});
    }


    setAllDays(e, newValue, oldValue) {
        let me = this;
        let workingDays = e.target.checked ? me.props.managedObject.zonedJobDays.filter(d => {
            return !d.excluded;
        }).map(d => moment(d.date, serverShortDateFormat)) : [];

        this.setState({
            tmpDates: workingDays,
            dates: workingDays,
            days: workingDays.length,
            allDays: e.target.checked
        });
    }

    onSelectDates(value) {
        let dates = [];
        let has = false;
        this.state.tmpDates.forEach(function (m) {
            if (!m.isSame(value)) {
                dates.push(m);
            } else {
                has = true;
            }
        });
        if (!has) {
            dates.push(value);
        }

        this.setState({tmpDates: dates});
    }

    onCancel() {
        let me = this;
        let allDays = DateHelper.getDaysBetween(me.props.managedObject.startDate, me.props.managedObject.endDate);
        let days = me.state.dates.length;

        me.setState({
            tmpDates: this.state.dates,
            viewDates1: false,
            days: days,
            allDays: allDays === days
        });
    }

    onApply() {
        let me = this;
        let allDays = DateHelper.getDaysBetween(me.props.managedObject.startDate, me.props.managedObject.endDate);
        let days = me.state.tmpDates.length;
        this.setState({
            dates: this.state.tmpDates,
            viewDates1: false,
            days: days,
            allDays: allDays === days
        });
    }
}

const TemporaryApplyPostingDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(TemporaryApplyPosting);

export default TemporaryApplyPostingDialogConnector;
