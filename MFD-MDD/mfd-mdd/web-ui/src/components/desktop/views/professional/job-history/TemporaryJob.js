/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import {connect, Provider} from 'react-redux';

import BaseView, {baseViewDispatcherMap, baseViewStateMap, saveObject} from '../../../common/BaseView';
import {
    BUTTON_TYPE_EDIT,
    BUTTON_TYPE_EDIT_ON_SELECTION,
    DEFAULT_CELL_STYLE,
    FILTER_TYPE_DATE,
    SELECTION_MODE_SINGLE,
    NUMBER_CELL_STYLE
} from '../../../../../utils/Constants';
import {dialogInfo as temporaryApplyPosting} from '../../../dialogs/professionals/TemporaryApplyPosting';
import TemporaryAcceptPosting, {dialogInfo as  temporaryAcceptPosting} from '../../../dialogs/professionals/TemporaryAcceptPosting';
import PracticeReviewsEdit, {dialogInfo as  dialogInfoPracticeReviewsEdit} from "../../../dialogs/reviews/PracticeReviewsEdit";
import Error from "../../../../../utils/Error";
import Remote from "../../../../../utils/Remote";
import ObjectHelper from "../../../../../utils/Object";
import Renderer from '../../../../../utils/Renderer';

class TemporaryJob extends BaseView {

    initView(props) {
        let me = this;

        let configuration = {
            requestInfo: {
                fetchQueryName: 'professionalPreviousJobsForEmployee',
                // getQueryName: "professionalReview",
                // updateQueryName: "createProfessionalReview",
                // getResponseModel: "LocationToProfessionalReview",
                // updateResponseModel: undefined


                getQueryName: "locationReview",
                updateQueryName: "createLocationReview",
                getResponseModel: "ProfessionalToLocationReview",
                updateResponseModel: undefined//"updateLocationReview"
            },
            additionalFields: ['jobPostingId', "hasReview"],
            columns: [
                {
                    dataIndex: 'jobPostingApplicationId',
                    name: 'id',
                    title: 'Id',
                    hidden: true,
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'jobPostingName',
                    name: 'jobPostingName',
                    title: 'Name',
                    orderName: 'JOB_POSTING_NAME',
                    cellClass: DEFAULT_CELL_STYLE
                },
                {
                    dataIndex: 'startDate',
                    name: 'startDate',
                    title: 'Start Date',
                    orderName: 'START_DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRendererMonthDayYear
                },
                {
                    dataIndex: 'endDate',
                    name: 'endDate',
                    title: 'End Date',
                    orderName: 'END_DATE',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getDateRendererMonthDayYear
                },
                {
                    dataIndex: 'practiceName',
                    name: 'practiceName',
                    title: 'Office', orderName: 'PRACTICE_NAME',
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
                    dataIndex: 'distance',
                    name: 'distance',
                    title: 'Distance (mi)', orderName: 'DISTANCE',
                    cellClass: NUMBER_CELL_STYLE
                },
                {
                    dataIndex: 'locationRating',
                    name: 'locationRating',
                    title: 'Rating',
                    cellClass: DEFAULT_CELL_STYLE,
                    renderer: Renderer.getRatingRenderer
                }
            ],
            actions: [
                {
                    label: "Post Review",
                    type: BUTTON_TYPE_EDIT_ON_SELECTION,
                    dialog: PracticeReviewsEdit,
                    dialogInfo: dialogInfoPracticeReviewsEdit,
                    name: 'post'
                },
                {
                    label: "View Posting",
                    hasSelectedRows: 1,
                    dialog: TemporaryAcceptPosting,
                    dialogInfo: temporaryApplyPosting,
                    name: "viewPosting", onClick: me.onViewPosting
                },
                {
                    label: "View Review",
                    type: BUTTON_TYPE_EDIT,
                    dialog: PracticeReviewsEdit,
                    dialogInfo: dialogInfoPracticeReviewsEdit,
                    name: 'review'
                }
            ],
            filters: [
                {type: FILTER_TYPE_DATE, name: "startDate", placeholder: "Start Date", onlyDate: true}
            ],
            selectionMode: SELECTION_MODE_SINGLE
        }
        return configuration;
    }

    prepareManagedObject(loadedObject) {
        let rate = [{_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}];
        if (loadedObject.rate) {
            for (let i = 0; i < loadedObject.rate; i++) {
                rate[i]._enabled = true;
            }
        }
        loadedObject.rate = rate;

        return loadedObject;
    }


    onViewPosting(e) {
        let me = this;

        let editAction = me.baseActions.props.actions.find(a => a.type === BUTTON_TYPE_EDIT);

        const target = e.target ? e.target.parentElement : {
            getAttribute: function (name) {
                return editAction ? editAction.name : null;
            }
        };

        let selection = me.state.selection;

        if (selection.length > 0) {
            try {

                let requestFields = [Remote.getFieldsByModel(me.props.metaInfo, 'JobPosting'), Remote.getFieldsByModel(me.props.metaInfo, 'JobPostingApplication')];

                me.props.onLoadRecord({
                    queryName: ['jobPosting', 'application'],
                    requestFields: requestFields,
                    getQueryParameterName: [
                        {
                            parameterName: 'id',
                            idParameterName: 'jobPostingId'
                        },
                        {
                            parameterName: 'id', idParameterName: 'jobPostingApplicationId'
                        }
                    ]
                }, selection[0], function (objects) {
                    if (ObjectHelper.isObject(objects)) {
                        let object = objects.jobPosting;
                        object.application = objects.application;
                        me.props.onLoadRefs(temporaryAcceptPosting.references || [], me.props.references, me.props.metaInfo, function () {
                            let actions = {
                                save: saveObject(me.requestInfo).bind(me)
                            }
                            me.showDialog(me, TemporaryAcceptPosting, Provider, target, object, actions);
                        });

                    }
                });
            } catch (ex) {
                Error.showErrors(ex)
            }
        }
    }


    dialogProps(dlg, button) {
        switch (button.getAttribute('action-name')) {
            case 'review':
            case 'viewPosting':
                return {readOnly: true};
            default:
                return null;
        }
    }

    isEnabledAction(element, selection) {
        let result = super.isEnabledAction(element, selection);
        switch (element.name) {
            case 'post':
                result = selection && selection.length === 1 && !selection[0].hasReview;
                break;
            case 'review':
                result = selection && selection.length === 1 && selection[0].hasReview;
                break;
            default:
                break;
        }
        return result;
    }
}

const TemporaryJobConnector = connect(
    baseViewStateMap,
    baseViewDispatcherMap)(TemporaryJob);

export default TemporaryJobConnector;