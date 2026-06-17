import React from 'react';
import {connect, Provider} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {reduxForm} from 'redux-form';
import BaseGrid from '../../common/BaseGrid';

import UiView from '../../../../utils/UiView';
import {
    DEFAULT_CELL_STYLE,
    REST_API_PREFIX,
    SELECTION_MODE_SINGLE
} from "../../../../utils/Constants";
import Renderer from "../../../../utils/Renderer";
import {getDataPromise} from "../../../../actions/common/getData";
import Error from "../../../../utils/Error";
import Remote from "../../../../utils/Remote";
import ProfessionalReviewsEdit from "./ProfessionalReviewsEdit";
import {saveDataPromise} from "../../../../actions/common/saveData";

export const reviewsDialogInfo = {
    references: []
}

let Form = props => {
    const {handleSubmit, handleCancel, onPostViewReview, dialog} = props;
    const managedObject = props.initialValues.managedObject;
    const columns = [
        {
            dataIndex: 'jobPostingApplicationId',
            name: 'id',
            title: 'id',
            hidden: true,
            cellClass: DEFAULT_CELL_STYLE
        },

        {
            dataIndex: 'jobPostingName',
            name: 'jobPostingName',
            title: 'Posting',
            orderName: 'JOB_POSTING_NAME',
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
            dataIndex: 'startDate',
            name: 'startDate',
            title: 'Start Date',
            orderName: 'START_DATE',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: Renderer.getDateRenderer
        },
        {
            dataIndex: 'endDate',
            name: 'endDate',
            title: 'End Date',
            orderName: 'END_DATE',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: Renderer.getDateRenderer
        },
        {
            dataIndex: 'hasReview',
            name: 'hasReview',
            title: 'Review',
            cellClass: DEFAULT_CELL_STYLE,
            renderer: function (value, cellClass, row, columnIndex, rowIndex) {
                if (value) {
                    return <div>
                        <button className="blue" type="button" onClick={onPostViewReview.bind(dialog, row, false)}> See
                            review
                        </button>
                    </div>;
                } else {
                    return <div>
                        <button className="blue" type="button" onClick={onPostViewReview.bind(dialog, row, true)}>Post
                            review
                        </button>
                    </div>;
                }
            }
        }];
    return (
        <form onSubmit={handleSubmit}>

            <div class="modal__gray-main modal52">
                <div class="modal__table">
                    <BaseGrid
                        columns={columns}
                        selectionMode={SELECTION_MODE_SINGLE}
                        url={REST_API_PREFIX} queryName="professionalPreviousJobsForEmployer"
                        additionalFields={[]}
                        params={{professionalId: managedObject.id}}
                        onRef={ref => (dialog.applicantsGrid = ref)}
                        hasPagination={true}
                        localOrdering={false}
                        key="base_view_base_grid"/>
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
    form: 'location'
})(Form);


class ReviewsDialogInfo extends BaseDialog {
    dialogProps() {
        return {
            width: 1024,
            height: 800,
            className: "modal__gray",
            title: "See Reviews"
        }
    }


    onPostViewReview(row, post, event) {
        let me = this;
        try {
            let requestFields = Remote.getFieldsByModel(me.props.metaInfo, "LocationToProfessionalReview", [], false);

            getDataPromise("professionalReview", {id: row.jobPostingApplicationId}, requestFields).then(
                function (loadedObject) {

                    let managedObject = {
                        professionalReview: loadedObject,
                        postingInfo: row,
                        professionalInfo: me.props.managedObject,
                        wouldHire: loadedObject ? loadedObject.wouldHire : false
                    }

                    managedObject.professionalism = [{_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}];
                    managedObject.communication = [{_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}];
                    managedObject.workQuality = [{_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}];
                    managedObject.punctuality = [{_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}];
                    managedObject.appearance = [{_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}, {_enabled: false}];

                    if (loadedObject) {
                        let initRate = function (parentTo, parentFrom, field) {
                            let fromField = `${field}Rate`;
                            if (parentFrom[fromField]) {
                                for (let i = 0; i < parentFrom[fromField]; i++) {
                                    parentTo[field][i]._enabled = true;
                                }
                            }
                        }
                        initRate(managedObject, loadedObject, 'professionalism');
                        initRate(managedObject, loadedObject, 'communication');
                        initRate(managedObject, loadedObject, 'workQuality');
                        initRate(managedObject, loadedObject, 'punctuality');
                        initRate(managedObject, loadedObject, 'appearance');

                        managedObject.comment = loadedObject.comment;
                    }

                    let actions = {
                        save: function (editor, newManagedObject, successfulCallBack) {
                            saveDataPromise({
                                queryName: post ? 'createProfessionalReview' : 'updateProfessionalReview',
                                showLoader: true
                            }, newManagedObject).then(function (result) {
                                row.hasReview = true;
                                me.applicantsGrid.setState({selection: []});
                                editor.close();
                            });
                        }
                    }


                    UiView.showDialog(<Provider store={UiView.createDialogStore()}><ProfessionalReviewsEdit
                        managedObject={managedObject}
                        references={me.props.references}
                        metaInfo={me.props.metaInfo}
                        actions={actions}
                        readOnly={!post}/></Provider>);
                });
        } catch (ex) {
            Error.showErrors(ex)
        }
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.onClose} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}}
                      references={this.props.references} onPostViewReview={this.onPostViewReview}/>);
    }
}


const ReviewsDialogInfoDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(ReviewsDialogInfo);

export default ReviewsDialogInfoDialogConnector;