import React from 'react';
import {connect} from 'react-redux';

import BaseView, {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {
    DEFAULT_CELL_STYLE,
    FILTER_TYPE_DATE,
    FILTER_TYPE_SELECT,
    NUMBER_CELL_STYLE,
    SELECTION_MODE_SINGLE
} from '../../../../../utils/Constants';

import Renderer from '../../../../../utils/Renderer';
import {INTERVIEW} from '../../../../../data/Statuses';
import {serverDateFormat, uiDateFormat} from "../../../../../utils/DateHelper";
import moment from "moment/moment";

class Interview extends BaseView {
    initView(props) {
        let configuration = {
            requestInfo: {
                fetchQueryName: 'interviews',
                getQueryName: "professional",
                //addQueryName: "addPracticeLocation",
                updateQueryName: "updateProfessionalGeneral",
                //deleteQueryName: "deletePracticeLocation",
                getResponseModel: "ProfessionalModel",
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined
            },

            additionalFields: ['professionalLastName', 'time'],
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'jobPostingName',
                    name: 'jobPostingName',
                    title: 'Name',
                    orderName: 'JOB_POSTING_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'practiceOwnerLastName',
                    name: 'practiceOwnerLastName',
                    title: 'Client Last Name',
                    orderName: 'PRACTICE_OWNER_LAST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'practiceOwnerFirstName',
                    name: 'practiceOwnerFirstName',
                    title: 'Client First Name',
                    orderName: 'PRACTICE_OWNER_FIRST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'practiceName',
                    name: 'practiceName',
                    title: 'Office',
                    orderName: 'PRACTICE_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'practiceLocationName',
                    name: 'practiceLocationName',
                    title: 'Location',
                    orderName: 'PRACTICE_LOCATION_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'professionalFirstName',
                    name: 'professionalFirstName',
                    orderName: 'PROFESSIONAL_FIRST_NAME',
                    title: 'Candidate Name',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                        value = value || '';
                        let lastName = row['professionalLastName'] || '';
                        return <div><span class={cellClass}>{`${value} ${lastName}`}</span></div>;
                    }
                },
                {
                    dataIndex: 'status',
                    name: 'status',
                    title: 'Interview Status',
                    orderName: 'STATUS',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer(INTERVIEW)
                },
                {
                    dataIndex: 'date',
                    name: 'date',
                    title: 'Interview Date and Time',
                    orderName: 'DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: function (value, cellClass, row, columnIndex, rowIndex, dateIndex) {
                        if (value) {

                            let date = moment(value, serverDateFormat);
                            if (date) {
                                let times: Array<string> = undefined;
                                if (row['time']) {
                                    times = row['time'].split(':');
                                }
                                if (times) {
                                    Renderer.setTime(date, ...times);
                                }
                                value = date.format(uiDateFormat);
                            }
                        }
                        return <div><span class={cellClass}>{value}</span></div>;
                    }
                },
                {
                    dataIndex: 'type', name: 'type', title: 'Type', orderName: 'TYPE', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'numberOfInterview',
                    name: 'numberOfInterview',
                    title: 'Number of Interview',
                    orderName: 'NUMBER_OF_INTERVIEW',
                    cellClass: NUMBER_CELL_STYLE
                }
            ],
            actions: [],
            filters: [
                {
                    type: FILTER_TYPE_SELECT,
                    name: "status",
                    menuItems: INTERVIEW,
                    multiple: false,
                    required: false,
                    title: 'Status'
                },
                {type: FILTER_TYPE_DATE, placeholder: "Interview Date", name: "date", onlyDate: true}
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    onDismissSOS() {
        alert("Dismiss SOS");
    }

    /*    renderViewHeader(handleSubmit, handleReset) {
            return (<div class="board__header">
                                <div class="board__header-activity">

                                    <div class="board__header-activity-item">
                                        <div class="item__box">
                                            <div class="text">
                                                <p>Office:</p>
                                            </div>
                                            <div class="input board__input-wrapper">
                                                <select required class="board__input">
                                                    <option value="" disabled selected hidden>Select</option>
                                                    <option>1</option>
                                                    <option>2</option>
                                                </select>
                                            </div>
                                        </div>
                                    </div>


                                    <div class="board__header-activity-item">
                                        <div class="item__box">
                                            <div class="text">
                                                <p>Interview Status:</p>
                                            </div>
                                            <div class="input board__input-wrapper">
                                                 <Field name="status" component={SelectField} menuItems={STATUSES} required={false} />
                                            </div>
                                        </div>
                                    </div>
                                </div>





                                <div class="board__header-custom">

                                    <div class="board__header-custom-item">
                                        <div class="board__header-custom-item-text">
                                            <p>Interview Day:</p>
                                        </div>
                                        <div class="board__header-custom-item-data-wrapper">
                                            <div class="board__header-custom-item-data">
                                                <div class="board__date">
                                                <Field name="startDate" class="date" placeholder="Select date" component={DateField} dateFormat="DD.MM.YYYY" showYearDropdown="{true}" />

                                                </div>
                                            </div>
                                        </div>

                                    </div>

                                    <div class="board__header-custom-item">
                                        <button class="reset" type="button" onClick={handleReset}>Reset Filters</button>
                                    </div>
                                </div>
                            </div>);
        }*/
    /*  render() {
          return(<div style={{textAlignment:"center"}}>In progress...</div>);
      }*/
}

const InterviewConnector = connect(
    baseViewStateMap,
    baseViewDispatcherMap)(Interview);

export default InterviewConnector;