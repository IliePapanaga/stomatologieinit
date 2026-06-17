import React from 'react';
import {connect} from 'react-redux';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {FieldArray, formValueSelector, reduxForm} from 'redux-form';

import {Logger} from 'react-logger-lib';
import Stars from '../../common/form/Stars';
import Field from '../../common/form/Field';
import TextArea from "../../common/form/TextArea";
import Checkbox from "../../common/form/Checkbox";

export const dialogInfo = {}

let Form = props => {
    const {handleSubmit, handleCancel, invalid, readOnly} = props;
    return (
        <form onSubmit={handleSubmit}>

            <div class="modal__gray-main modal46">
                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="title">
                            <p>Rate the Office:</p>
                        </div>
                        <div class="data">
                            <FieldArray name="managedObject.rate" component={Stars} readOnly={readOnly}/>
                        </div>
                    </div>
                </div>

                <div class="modal__gray-main-item">
                    <p class="title header">Legend:</p>

                    <div class="item__box">
                        <div class="data">
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__default">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__default">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__default">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__default">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                        </div>
                        <div class="title">
                            <p>Will not go back, worst experience.</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="data">
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__default">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__default">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__default">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                        </div>
                        <div class="title">
                            <p>Don't want to go back.</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="data">
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__default">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__default">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                        </div>
                        <div class="title">
                            <p>Might go back if no other jobs are available.</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="data">
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__default">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                        </div>
                        <div class="title">
                            <p>I like working in that office.</p>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="data">
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                            <div>
                                <svg class="star__active">
                                    <use xlinkHref="#star"></use>
                                </svg>
                            </div>
                        </div>
                        <div class="title">
                            <p>Excellent office.</p>
                        </div>
                    </div>
                </div>
                <div class="modal__gray-main-item">
                <div className="item__box">
                    <Field name="managedObject.wouldWorkPermanently"
                           component={Checkbox} disabled={readOnly}/>
                    <div className="text">
                        <p> I would work here permanently</p>
                    </div>
                </div>
                </div>

                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="text">
                            <p>Comments:</p>
                        </div>
                        <Field name="managedObject.comment" component={TextArea} required={false} readOnly={readOnly}/>
                    </div>
                </div>

            </div>

            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                    <button className="blue white" type="button" onClick={handleCancel}>Cancel</button>
                    {!readOnly&&<button className="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>}
                </div>
            </div>

        </form>)
}

Form = reduxForm({
    form: 'practice_review'
})(Form);

const selector = formValueSelector('practice_review');

let rate = function (arr) {
    let result = 0;
    for (var i = 0; i < 5; i++) {
        if (arr[i]._enabled) {
            result++;
        }
    }
    return result;
}

Form = connect(state => {
    const managedObject = selector(state, 'managedObject');

    let rate = function (arr) {
        let result = 0;
        for (var i = 0; i < 5; i++) {
            if (arr[i]._enabled) {
                result++;
            }
        }
        return result;
    }
    let raiting = 0;
    if (managedObject) {
        raiting = rate(managedObject.rate) / 5;
    }
    return {
        raiting: raiting
    }
})(Form)


class PracticeReviewsEdit extends BaseDialog {
    dialogProps() {
        return {
            width: 490,
            height: 660,
            className: "modal__gray",
            title: "review"
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
        let managedObject = managedData.managedObject;
        managedData.managedObject = {
            review: {
                applicationId: managedObject.jobPostingApplicationId,
                rate: rate(managedObject.rate),
                comment: managedObject.comment,
                wouldWorkPermanently: managedObject.wouldWorkPermanently
            }
        };

        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}}
                      references={this.props.references} readOnly={this.props.readOnly}/>);
    }
}


const PracticeReviewsEditDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {});
    })(PracticeReviewsEdit);

export default PracticeReviewsEditDialogConnector;