/*eslint-disable no-unused-vars*/
import React from 'react';
/*eslint-enable no-unused-vars*/
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {
    CANDIDATE_ANY,
    POSTING_TYPE_COMPLEX,
    POSTING_TYPE_SIMPLE,
    POSTING_TYPE_WEEKLY
} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import BaseWizard from "../../common/BaseWizard";
import PostingPage1 from "./PostingPage1";
import PostingSimplePage from "./PostingSimplePage";
import PostingWeeklyPage from "./PostingWeeklyPage";
import PostingComplexPage from "./PostingComplexPage";
import {PublishSimpleTemporaryJobPostingInput} from "../../../../models/postings/PublishSimpleTemporaryJobPostingInput";
import {PublishWeeklyTemporaryJobPostingInput} from "../../../../models/postings/PublishWeeklyTemporaryJobPostingInput";
import {PublishComplexTemporaryJobPostingInput} from "../../../../models/postings/PublishComplexTemporaryJobPostingInput";
import {DAYS_OF_WEEK} from "../../../../data/DaysOfWeek";


export const dialogInfo = {
    references: ['categories', 'languages', 'educations', 'academicDegrees']
}

class AddPosting extends BaseWizard {
    dialogProps() {
        return {
            width: 560,
            height: 740,
            className: "modal__gray",
            title: "Posting"
        }
    }

    initPages(managedObject) {
        return {
            page: PostingPage1,
            nextPage(currentNode, managedObject) {
                return new Promise((resolve) => {
                    if (currentNode.children) {
                        switch (managedObject.postingType) {
                            case POSTING_TYPE_SIMPLE:
                                resolve(currentNode.children[0]);
                                break;
                            case POSTING_TYPE_COMPLEX:
                                resolve(currentNode.children[2]);
                                break;
                            case POSTING_TYPE_WEEKLY:
                                managedObject['workSchedules'] = managedObject['workSchedules'] || [];
                                let workSchedules = managedObject['workSchedules'];
                                DAYS_OF_WEEK.forEach(function (day, index) {
                                    let row = workSchedules.find(function (v) {
                                        return v.weekDay === day.code
                                    });
                                    if (!row) {
                                        workSchedules.splice(index, 0, {
                                            weekDay: day.code,
                                            _enabled: false,
                                            startTime: '',
                                            endTime: ''
                                        });
                                    } else {
                                        row._enabled = true;
                                    }
                                });
                                resolve(currentNode.children[1]);
                                break;
                            default:
                                break;

                        }
                        resolve(null);
                    }
                });
            },

            children: [
                {
                    page: PostingSimplePage
                },
                {
                    page: PostingWeeklyPage
                },
                {
                    page: PostingComplexPage
                }
            ]
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

        Logger.of('App.AddPosting.beforeSave').info('ManagedObject data:', managedData);
        delete managedData.managedObject['practice'];
        delete managedData.managedObject['category'];
        if (managedData.managedObject['candidate'] === CANDIDATE_ANY) {
            delete managedData.managedObject['preferredCandidateId'];
        }
        delete managedData.managedObject['candidate'];

        switch (managedData.managedObject.postingType) {
            case POSTING_TYPE_SIMPLE:
                delete managedData.managedObject['workSchedules'];
                delete managedData.managedObject['jobDays'];
                managedData.managedObject = {jobPosting: new PublishSimpleTemporaryJobPostingInput(managedData.managedObject)};
                break;
            case POSTING_TYPE_COMPLEX:
                delete managedData.managedObject['workSchedules'];
                delete managedData.managedObject['startTime'];
                delete managedData.managedObject['endTime'];
                managedData.managedObject = {jobPosting: new PublishComplexTemporaryJobPostingInput(managedData.managedObject)};
                break;
            case POSTING_TYPE_WEEKLY:
                delete managedData.managedObject['jobDays'];
                delete managedData.managedObject['startTime'];
                delete managedData.managedObject['endTime'];
                if (managedData.managedObject.workSchedules) {

                    for (var i = managedData.managedObject.workSchedules.length - 1; i >= 0; i--) {
                        if (!managedData.managedObject.workSchedules[i]._enabled) {
                            managedData.managedObject.workSchedules.splice(i, 1);
                        } else {
                            delete managedData.managedObject.workSchedules[i]['_enabled'];
                        }
                    }
                }

                managedData.managedObject = {jobPosting: new PublishWeeklyTemporaryJobPostingInput(managedData.managedObject)};

                break;
            default:
                break;
        }

        delete managedData.managedObject.jobPosting['postingType'];

        return true;
    }
}

const AddPostingDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {
            metaInfo: ownProps.metaInfo
        });
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(AddPosting);

export default AddPostingDialogConnector;
