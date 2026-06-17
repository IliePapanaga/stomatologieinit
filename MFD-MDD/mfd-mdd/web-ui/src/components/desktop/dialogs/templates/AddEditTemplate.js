import React, {Component} from 'react';
import {connect} from 'react-redux';
//import Dialog from 'material-ui/Dialog';
import TextArea from '../../common/form/TextArea';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import {formValueSelector, reduxForm} from 'redux-form';
import Field from '../../common/form/Field';
import TextField from '../../common/form/TextField';
import SelectField from '../../common/form/SelectField';
import {TEXTFIELD_WITH_SPACE_PATTERN_REGEX, TRANSPORT_TYPE_SMS} from '../../../../utils/Constants';
import {Logger} from 'react-logger-lib';
import {TRANSPORT_TYPES} from '../../../../data/TransportType';
//import RichEditor from "../../common/form/RichEditor";

export const dialogInfo = {
    references: ["notificationTypes"]
}

class Form extends Component {
    render() {
        const {handleSubmit, handleCancel, change, invalid, references, transport, type, placeholder, content, dialog} = this.props;
        const contentRef=this.contentRef;
        const notificationTypes=[];
        const placeholders = [];
        if (references.notificationTypes && references.notificationTypes.nodes) {
            references.notificationTypes.nodes.forEach(function(n){
                if (n.type === type) {
                    if (n.variables) {
                        n.variables.forEach(function (v) {
                            placeholders.push({code: v.variable, name: v.name,});
                        });
                    }
                }
                notificationTypes.push({code: n.type, name: n.name});
            })
        }
        return (
            <form onSubmit={handleSubmit}>

            <div class="modal__gray-main modal31"  ref={(el) => this.contentRef = el}>
            <div class="item__box">
            <div class="text">
            <p>Notification Type<span class="star">*</span>:</p>
        </div>
        <div class="input board__input-wrapper select__input">
            <Field name="managedObject.type"
        component={SelectField} menuItems={notificationTypes}
        required={true}/>
        </div>
        </div>

        <div class="item__box">
            <div class="text">
            <p>Transport Type<span class="star">*</span>:</p>
        </div>
        <div class="input board__input-wrapper">
            <Field name="managedObject.transport" component={SelectField} menuItems={TRANSPORT_TYPES}
        required={true}/>
        </div>
        </div>

        {transport !== TRANSPORT_TYPE_SMS && <div class="item__box">
            <div class="text">
            <p>Subject<span class="star">*</span>:</p>
        </div>
        <Field name="managedObject.subject" autocomplete="off" component={TextField} required={true}
            minLength={2} maxLength={60} regexPattern={TEXTFIELD_WITH_SPACE_PATTERN_REGEX}/>
        </div>}

            {placeholders.length > 0 && <div class="item__box item__box-btn">
                <div class="text">
                <p>Placeholders:</p>
            </div>
            <div class="item__box">
                <div class="input board__input-wrapper">
                <Field name="placeholder" component={SelectField} menuItems={placeholders}
                required={false}/>
            </div>
            <button class="blue" type="button" onClick={function(){
                        dialog.insertPlaceholder(change, content, placeholder, contentRef)
                        //change('managedObject.content', (content || '') + placeholder);
                    }}>Add</button>
                </div>
                </div>}

                <div class="item__box item__box-textarea">
                <div class="text">
                <p>Message<span class="star">*</span>:</p>
            </div>
                    {/*<Field name="managedObject.content" component={transport == 'EMAIL' ? RichEditor : TextArea}*/}
                           {/*required={true}/>*/}
                    <Field name="managedObject.content" component={TextArea}
                           required={true}/>
            </div>

            <div class="item__box">
                <p class="mandatory">Fields marked* are mandatory</p>
            </div>

            </div>

            <div class="footer__btn-wrapper">
                <div class="footer__btn">
                <button className="blue white" type="button" onClick={handleCancel}>Cancel</button>
                <button className="blue" type="button" onClick={handleSubmit} disabled={invalid}>Ok</button>
                </div>
                </div>

                </form>)
            }
}

Form = reduxForm({
    form: 'addedittemplate'
})(Form);

const selector = formValueSelector('addedittemplate');

Form = connect(state => {
    //const certificateDetails = selector(state, 'cprDetails');
    const transport = selector(state, 'managedObject.transport');
    const type = selector(state, 'managedObject.type');
    const placeholder = selector(state, 'placeholder');
    const content = selector(state, 'managedObject.content');

    return {
        //certificateDetails: certificateDetails,
        transport: transport,
        type: type,
        placeholder: placeholder,
        content: content
    }
})(Form)

class AddEditTemplate extends BaseDialog {
    dialogProps() {
        return {
            width: 640,
            height: 800,
            className: "modal__gray",
            title: "Add/Edit Template"
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
        managedData.managedObject = {template: managedData.managedObject};
        Logger.of('App.AddEditLocation.beforeSave').info('ManagedObject data:', managedData);
        return true;
    }

    renderDialogContent() {
        return (<Form onSubmit={this.onSave} handleCancel={this.close} dialog={this}
                      initialValues={{managedObject: this.props.managedObject}} references={this.props.references}/>);
    }

    insertPlaceholder(changeFn, content, placeholder, contentRef){
        let selectionEnd = contentRef.querySelector("textarea").selectionEnd;
        let oldContent = content || '';
        changeFn('managedObject.content', oldContent.substring(0, selectionEnd) + placeholder + oldContent.substring(selectionEnd));
    }
}

const AddEditTemplateDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {});
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
            onSend: (sendData, d, form) => {
                // dispatch(requestResetPassword(sendData.email, form.recaptchaHash)).then(function (result) {
                //     if (result) {
                //            toastr.success("Resset Password", "Please, check your email, we sent you request for reset your password.");
                //         dispatch({ type: EVENT_CHANGED_RECAPTCHA_HASH, recaptchaHash: undefined });
                //         form.handleCancel();
                //     }
                // });
            }
        });
    })(AddEditTemplate);

export default AddEditTemplateDialogConnector;
