/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import {connect} from 'react-redux';

import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import ReviewsDialog from '../../../dialogs/reviews/ReviewsDialog';
import {
    BUTTON_TYPE_ADD,
    BUTTON_TYPE_EDIT_ON_SELECTION,
    DEFAULT_CELL_STYLE,
    SELECTION_MODE_SINGLE
} from '../../../../../utils/Constants';
import {PermanentPostingJob} from "../postings-in-progress/PermanentJob";
import AddPermanentPosting, {dialogInfo} from "../../../dialogs/postings/AddPermanentPosting";
import Renderer from "../../../../../utils/Renderer";

class Professionals extends PermanentPostingJob {
    initView(props) {
        let me = this;

        let configuration = {
            requestInfo: {
                fetchQueryName: 'previouslyHiredProfessionals',
                addQueryName: me.generateAddQueryName,
                updateQueryName: "updatePracticeOwnerGeneral",
                //deleteQueryName: "deletePracticeLocation",
                getResponseModel: "PracticeModel",
                //addResponseModel: "PracticeLocationModel",
                updateResponseModel: undefined,
                reloadAfterClose: true
            },
            columns: [
                {
                    dataIndex: 'id', name: 'id', title: 'Id', hidden: true, cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'lastName',
                    name: 'lastName',
                    title: 'Professional Last Name',
                    orderName: 'LAST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'firstName',
                    name: 'firstName',
                    title: 'Professional First Name',
                    orderName: 'FIRST_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'lastEmploymentDate',
                    name: 'lastEmploymentDate',
                    title: 'Last Worked Date',
                    orderName: 'LAST_EMPLOYMENT_DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRendererMonthDayYear
                },
                {
                    dataIndex: 'blackListed',
                    name: 'blackListed',
                    title: 'Banned',
                    orderName: 'BLACKLISTED',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getRequiredRenderer()
                },
                {
                    dataIndex: 'totalRating',
                    name: 'totalRating',
                    title: 'Total Rating',
                    orderName: 'TOTAL_RATING',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getRatingRenderer
                },
            ],
            actions: [
                {label: "Post/See Review ", type: BUTTON_TYPE_EDIT_ON_SELECTION, dialog: ReviewsDialog, dialogInfo: [], name:"seereview"},
                {
                    label: "Hire for Permanent Job",
                    type: BUTTON_TYPE_ADD,
                    dialog: AddPermanentPosting,
                    dialogInfo: dialogInfo
                },
                {
                    label: "Unban",
                    onClick: me.onBlackList,
                    allowedValues: [true],
                    allowedByFieldName: "blackListed",
                    hasSelectedRows: 1
                },
                {
                    label: "Ban",
                    onClick: me.onUnBlackList,
                    allowedValues: [false],
                    allowedByFieldName: "blackListed",
                    hasSelectedRows: 1
                }
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    prepareManagedObject(loadedObject, target) {
        let me = this;

        if(target.getAttribute('action-name')==='seereview') {
            return loadedObject;
        }

        let result = super.prepareManagedObject(loadedObject, target);

        let selection = me.state.selection;

        if (selection.length > 0) {
            result.preferredCandidateId = selection[0].id;
        }

        return result;
    }

    isEnabledAction(element, selection) {
        let result = super.isEnabledAction(element, selection);
        if (result) {
            result = selection && selection.length === 1
        }
        return result;
    }

    onBlackList(btn) {
        let me = this;
        me.blackUnBlackList(btn, true);
    }

    onUnBlackList(btn) {
        let me = this;
        me.blackUnBlackList(btn, false);
    }

    blackUnBlackList(btn, blackList) {
        let me = this,
          headerSubStr = blackList ? 'remove from banned list': 'add to banned list' ;

        let classHeader = 'header ' +  headerSubStr;

        let selection = me.state.selection;
        if (selection.length > 0) {
            const subStr = blackList ? `remove ${selection[0].firstName}, ${selection[0].lastName} from banned list`: `add ${selection[0].firstName}, ${selection[0].lastName} to banned list`;
            me.onExecuteOperation(btn, {
                    professionalId: selection[0].id
                },
                <div class="modal57">
                    <div className={classHeader}><h2>{headerSubStr}</h2></div>
                    <div class="body">Are you sure you want to {subStr}?</div></div>,
                blackList?'unBlackListProfessional':'blackListProfessional', undefined, function (returnObject, selectedObject) {
                    let updatedObject = Object.assign({}, selectedObject);
                    updatedObject.blackListed = !selectedObject.blackListed;
                    me.baseGrid.updateRow(selectedObject, updatedObject);
                });
        }
    }
}

const ProfessionalsConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {
            userId: state.context.currentUser ? state.context.currentUser.id : undefined
        });
    },
    baseViewDispatcherMap)(Professionals);

export default ProfessionalsConnector;

