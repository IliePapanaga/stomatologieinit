import React from 'react';
import {connect} from 'react-redux';
import TextArea from '../../common/form/TextArea';
import ExSelectField from '../../common/form/ExSelectField';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';
import {
    CANDIDATE_ANY,
    CANDIDATE_DIRECT_BOOKING,
    POSTING_TYPE_COMPLEX,
    POSTING_TYPE_SIMPLE,
    POSTING_TYPE_WEEKLY,
    SUBCATEGORY_ORDERS
} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import SelectField from "../../common/form/SelectField";
import CheckboxGroup from "../../common/form/CheckboxGroup";
import RadioGroup from "../../common/form/RadioGroup";
import ObjectHelper from "../../../../utils/Object";
import DirectBooking from "./DirectBooking";
import BrowseDialogButton from "../../common/BrowseDialogButton";


let PostingPage1 = props => {
    const {dialog, handleSubmit, handleNextPage, handlePreviousPage, renderNavigation, change, handleCancel, invalid, node, initialValues, references, readOnly, directBooking} = props;
    let managedObject = props.managedObject || initialValues.managedObject;
    let navigation = renderNavigation(dialog, handleSubmit, handleNextPage, handlePreviousPage, change, handleCancel, invalid, node, managedObject);
    let selectedCategories = references.categories.filter((item) => item.id === managedObject.category);
    let subCategories = undefined;
    if (selectedCategories.length) {
        subCategories = selectedCategories[0].subCategories;

        subCategories = subCategories.sort(function (a, b) {
            let value1 = SUBCATEGORY_ORDERS[selectedCategories[0].id].indexOf(a.id);
            let value2 = SUBCATEGORY_ORDERS[selectedCategories[0].id].indexOf(b.id);

            if (value2 > value1) {
                return -1;
            }
            if (value1 > value2) {
                return 1;
            }
            return 0;
        });

        let startToMoveIndex = Math.round(subCategories.length / 2);

        for (let i = 0; i + startToMoveIndex < subCategories.length; i++) {
            let from = i + startToMoveIndex;
            let to = -1 * (startToMoveIndex - 1 - i);
            console.log(from, to);
            ObjectHelper.move(subCategories, subCategories[from], to);
        }

    }

    let changedCategory = function (e, newValue, oldValue) {
        if (newValue !== oldValue) {
            Logger.of('App.PostingPage1').info('Category:', newValue);
            change(`managedObject.requiredSubcategories`, []);
        }
    };

    let generateName = function (e, newValue, oldValue) {
        let requiredSubcategories = ObjectHelper.isArray(newValue) ? newValue : managedObject.requiredSubcategories;
        let practiceLocationId = ObjectHelper.isArray(newValue) ? managedObject.practiceLocationId : newValue;
        if (requiredSubcategories && requiredSubcategories.length > 0 && practiceLocationId) {
            let subCats = requiredSubcategories.join("\\");
            let location = managedObject.practice.locations.find(function (l) {
                return l.id === practiceLocationId;
            });
            change(`managedObject.name`, `${subCats}\\${location.name}`);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <div class="modal__gray-main modal10">

                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="title">
                            <p>Posting Name:</p>
                        </div>
                        <div class="data">
                            <p>{managedObject.name}</p>
                        </div>
                    </div>
                </div>

                <div class="modal__gray-main-item">
                    <h3>Note: If you want to add a day, create a new assignment. Modifying the post will cause all booked
                            temps to be cancelled until they reapply.</h3>

                    <h3>Position</h3>
                    <div class="item__box">
                        <div class="text">
                            <p>Category<span class="star">*</span>:</p>
                        </div>
                        <div class="input board__input-wrapper">
                            <Field name="managedObject.category" component={SelectField}
                                   disabled={readOnly || !!managedObject.id}
                                   menuItems={references.categories} required={true} onChange={changedCategory}
                                   hideEmpty={true}/>
                        </div>
                    </div>

                    {selectedCategories.length > 0 &&
                    <div class="item__box check">
                        <Field itemRenderer={function (item, value, name, itemClassWrapper, scope) {
                            return <div class={itemClassWrapper}>
                                <input type="checkbox" checked={value.indexOf(item.id) > -1}
                                       id={`${item.id}`}
                                       value={name}
                                       onChange={(e) => scope.handleChange(e, item.id)}
                                       disabled={readOnly || !!managedObject.id}/>
                                <label for={`${item.id}`}>{item.name}</label>
                            </div>
                        }} name="managedObject.requiredSubcategories" items={subCategories} required={true}
                               component={CheckboxGroup} itemClassWrapper="check-item" classWrapper={"check"}
                               columns={1} onChange={generateName}/></div>}


                </div>


                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="text">
                            <p>Location<span class="star">*</span>:</p>
                        </div>
                        <div class="input board__input-wrapper">
                            <Field onChange={generateName} name="managedObject.practiceLocationId"
                                   component={SelectField}
                                   menuItems={managedObject.practice.locations}
                                   required={true} disabled={readOnly}/>
                        </div>
                    </div>
                    <div className="item__box description">
                        <p>Please note: You must have at least 1 location added to your account in order to create a
                            posting. To add a location click on "My Account" then "Locations".</p>
                    </div>


                    {managedObject.practiceLocationId &&
                    <div class="item__box">
                        <div class="item__box">
                            <div class="text">
                                <p>Candidate<span class="star">*</span>:</p>
                            </div>
                        </div>
                        <div class="item__box">
                            <div class="item__radio">
                                <Field name="managedObject.candidate"
                                       items={[
                                           {
                                               code: CANDIDATE_ANY,
                                               name: 'Any Available. This posting will be available to all qualified candidates. '
                                           },
                                           {
                                               code: CANDIDATE_DIRECT_BOOKING,
                                               name: 'Direct Booking. You can chose a specific candidate that worked for you in the past. If the candidate does not accept booking, please clone the posting and make it available for all candidates.'
                                           }
                                       ]}
                                       required={true}
                                       component={RadioGroup} itemClassWrapper="text" classWrapper={null}
                                       disabled={directBooking || !!managedObject.id}/>
                                {managedObject.candidate === CANDIDATE_DIRECT_BOOKING && !directBooking && !managedObject.id &&
                                <div class="text btn download">
                                    <Field name="preferredCandidateName" autocomplete="off"
                                           component={TextField} readonly="readonly"/>
                                    <BrowseDialogButton title="Browse" dialog={DirectBooking} actions={{
                                        save: function (editor, updatedManagedObject, successfulCallBack) {
                                            let directProfession = editor.applicantsGrid.state.selection[0];
                                            change('managedObject.preferredCandidateId', directProfession.id);
                                            change('preferredCandidateName', `${directProfession.firstName} ${directProfession.lastName}`);
                                            editor.close();
                                        }
                                    }} managedObject={managedObject} browseProps={{
                                        metaInfo: props.metaInfo,
                                        references: references
                                    }}/>
                                </div>}
                            </div>
                        </div>
                    </div>
                    }
                </div>


                <div class="modal__gray-main-item">
                    <h3>Posting Date & Time Type<span class="star">*</span>:</h3>
                    <input class="hidden__empty-input" readOnly={readOnly}></input>
                    <div class="item__box">
                        <Field name="managedObject.postingType"
                               items={[
                                   {
                                       code: POSTING_TYPE_SIMPLE,
                                       name: 'Simple'
                                   },
                                   {
                                       code: POSTING_TYPE_WEEKLY,
                                       name: 'Weekly'
                                   },
                                   {
                                       code: POSTING_TYPE_COMPLEX,
                                       name: 'Complex'
                                   }]}
                               required={true}
                               component={RadioGroup} itemClassWrapper="text" classWrapper={"item__box"}/></div>

                    <div class="posting__description">
                        <div class="posting__description-item"><p>Simple: Good for single days or schedule consecutive
                            days with same start/end time. Ex.: 5/29-5/31 8-5. Please note: Saturday and Sunday will be
                            included in this posting type. If you want to avoid them please click on "weekly"</p></div>
                        <div class="posting__description-item"><p>Weekly: Good for any schedule that is a week or more
                            of nonconsecutive days if start/end times are the same. Ex.: 5/29-5/31 7-4. </p></div>
                        <div class="posting__description-item"><p>Complex: Good for a schedule with more than a week
                            needed with different start/end. Ex.: 5/29 Monday 8-5, 6/4 Monday 10-6.</p></div>
                    </div>

                </div>


                <div class="modal__gray-main-item">
                    <div class="item__box">
                        <div class="text">
                            <p>Language:</p>
                        </div>
                        <div class="input board__input-wrapper select__input">
                            <Field name="managedObject.requiredLanguages" component={ExSelectField}
                                   menuItems={references.languages}
                                   required={true} multiple={true} readOnly={readOnly}/>
                        </div>
                    </div>

                    <div class="item__box">
                        <div class="text">
                            <p>Comments:</p>
                        </div>
                        <div class="input">
                            <Field name="managedObject.comment" component={TextArea} placeholder="Your text..."
                                   required={false} readOnly={readOnly}/>
                        </div>
                    </div>
                    <div class="text">
                        <p class="mandatory">Fields marked* are mandatory</p>
                    </div>
                </div>

            </div>

            <div class="footer__btn-wrapper">
                {navigation}
            </div>
        </form>)
};

PostingPage1 = reduxForm({
    form: 'addposting',
    destroyOnUnmount: false,
    forceUnregisterOnUnmount: true,
})(PostingPage1);


const selector = formValueSelector('addposting');

PostingPage1 = connect((state, ownProps) => {
    const managedObject = selector(state, 'managedObject')
    return {
        managedObject: managedObject,
        metaInfo: ownProps.dialog.props.metaInfo
    }
})(PostingPage1);

export default PostingPage1