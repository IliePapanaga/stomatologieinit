import React from 'react';
import {connect} from 'react-redux';

import BaseView, {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import {BUTTON_TYPE_EDIT, DEFAULT_CELL_STYLE, SELECTION_MODE_SINGLE} from '../../../../../utils/Constants';

import AddEditProfessional, {dialogInfo} from '../../../dialogs/professionals/AddEditProfessional';
import Renderer from '../../../../../utils/Renderer';

class Settings extends BaseView {
    initView(props) {
        let me = this;
        let configuration = {
            requestInfo: {
                fetchQueryName: 'systemUserProfessionals',
                getQueryName: "professional",
                //addQueryName: "addPracticeLocation",
                updateQueryName: "updateProfessionalGeneral",
                //deleteQueryName: "deletePracticeLocation",
                getResponseModel: "ProfessionalModel",
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined
            },

            additionalFields: [],
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'lastName', name: 'lastName', title: 'Last Name', orderName: 'LAST_NAME', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'firstName', name: 'firstName', title: 'First Name', orderName: 'FIRST_NAME', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'speciality', name: 'speciality', title: 'Specialty', orderName: 'SPECIALITY', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'status', name: 'status', title: 'Status', orderName: 'STATUS', cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer()
                },
                {
                    dataIndex: 'documentStatus', name: 'documentStatus', title: 'Document Status', orderName: 'STATUS', cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getStatusRenderer()
                },
                // {
                //     dataIndex: 'officeName', name: 'officeName', title: 'Region', orderName: 'OFFICE_NAME', cellClass: DEFAULT_CELL_STYLE
                // },
                {
                    dataIndex: 'phone', name: 'phone', title: 'Telephone #', orderName: 'PHONE', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'rph', name: 'rph', title: 'RPH ($/H)', orderName: 'RPH', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'rating', name: 'rating', title: 'Rating (Feedback)', orderName: 'RATING', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'lastEmploymentStartDate', name: 'lastEmploymentStartDate', orderName: 'LAST_EMPLOYMENT_START_DATE', title: 'Last Employment Start Date', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'lastActivity', name: 'lastActivity', title: 'Last Activity Date', orderName: 'LAST_ACTIVITY', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'noShow', name: 'noShow', title: 'No Show', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'cancellations', name: 'cancellations', title: '#Cancellations', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'approvedBy', name: 'approvedBy', title: 'Approved By', cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'modifiedDate', name: 'modifiedDate', title: 'Modified Date', cellClass: DEFAULT_CELL_STYLE
                }
            ],
            actions: [
                { label: "Edit", type: BUTTON_TYPE_EDIT, dialog: AddEditProfessional, dialogInfo: dialogInfo },
                { label: "Activate", onClick: me.onActivate },
                { label: "Deactivate", onClick: me.onDeactivate },
                { label: "On Behalf Of", onClick: me.onImpersonate },
                { label: "Review Docs", onClick: me.onReviewDocs },
                { label: "Modify No Snow", onClick: me.onModifyNoSnow }
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    onActivate() {
        alert("Activate");
    }

    onDeactivate() {
        alert("Deactivate");
    }

    onImpersonate() {
        alert("Impersonate");
    }

    onReviewDocs() {
        alert("Review Docs");
    }

    onModifyNoSnow() {
        alert("Modify No Snow");
    }

    renderViewHeader(handleSubmit, handleReset) {
        return (<div class="board__header">
            <div class="board__header-activity">
                <div class="board__header-activity-item">
                    <div class="item__box">
                        <div class="text">
                            <p>Specialty:</p>
                        </div>
                        <div class="input board__input-wrapper">
                            <select required class="board__input">
                                <option value="" disabled selected hidden>Select</option>
                                <option>1</option>
                                <option>2</option>
                            </select>
                        </div>
                    </div>
                    <div class="item__box">
                        <div class="text">
                            <p>Problematic:</p>
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
                            <p>New Comers:</p>
                        </div>
                        <div class="input board__input-wrapper">
                            <select required class="board__input">
                                <option value="" disabled selected hidden>Select</option>
                                <option>1</option>
                                <option>2</option>
                            </select>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>Status:</p>
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


            </div>

            <div class="board__header-custom">


                <div class="board__header-custom-item">
                    <div class="board__header-custom-item-text">
                        <p>Last Activity:</p>
                    </div>
                    <div class="board__header-custom-item-data-wrapper">
                        <div class="board__header-custom-item-data">
                            <div class="board__date board__date-from">
                                <input class="date" placeholder="Select date" />
                            </div>
                        </div>
                        <div class="board__header-custom-item-data">
                            <div class="board__date board__date-to">
                                <input class="date" placeholder="Select date" />
                            </div>
                        </div>
                    </div>
                </div>


                <div class="board__header-custom-item">

                    <div class="btn__box btn__box-selected">
                        <div class="text">
                            <p>Location:</p>
                        </div>
                        <div class="input">
                            <button class="yellow">Select</button>
                        </div>
                    </div>

                    <button class="reset" type="button" onClick={handleReset}>Reset Filters</button>
                </div>


            </div>
        </div>);
    }
    render() {
        return (<div style={{ textAlignment: "center" }}>In progress...</div>);
    }
}

const SettingsConnector = connect(
    baseViewStateMap,
    baseViewDispatcherMap)(Settings);

export default SettingsConnector;