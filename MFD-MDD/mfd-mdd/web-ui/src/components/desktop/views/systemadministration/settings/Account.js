import React, {Component} from 'react';
import {connect, Provider} from 'react-redux';
import {reduxForm} from 'redux-form';
import Field from '../../../common/form/Field';
import TextField from '../../../common/form/TextField';
import {validateEmail} from '../../../../../utils/Validators';
import saveData, {saveDataPromise} from '../../../../../actions/common/saveData';
import {EVENT_VIEW_SAVE_DATA} from '../../../../../utils/Constants';
import UiView from '../../../../../utils/UiView';
import {baseViewDispatcherMap, baseViewStateMap} from '../../../common/BaseView';
import ChangePassword from '../../../dialogs/account/ChangePassword';
import ChangeUserName from '../../../dialogs/account/ChangeUserName';
import {changePassword} from '../../../../../actions/common/security/password';
import requestChangeUserName from '../../../../../actions/common/security/username';
import {toastr} from 'react-redux-toastr';
import {getDataPromise} from "../../../../../actions/common/getData";
import Remote from "../../../../../utils/Remote";
import {phone} from "../../../../../utils/Normalizers";

let AccountForm = props => {
    const {handleSubmit, onChangeUserName, onChangePassword, /*onChangePhone,*/ invalid} = props;
    return (
        <form onSubmit={handleSubmit}>


            <div class="board__header">
            </div>


            <div class="board__action">

            </div>


            <div class="content pro__setting sys__setting-acc">

                <div class="view__header">
                    <h2>Account Settings</h2>
                </div>

                <div class="sys__setting-acc-item-wrapper">
                    <div class="sys__setting-acc-item">
                        <div class="item__box">
                            <div class="text">
                                <p>Email Address<span class="star">*</span>:</p>
                            </div>
                            <Field name="systemUser.contact.email" readonly="true" setting__general autocomplete="off"
                                   component={TextField} validate={validateEmail} required={true}
                                   minLength={6} maxLength={254}/>
                            <div class="eye__wrapper hidden">
                                <div class="eye-show"></div>
                            </div>
                            <div class="btn">
                                <button class="blue" type="button" onClick={onChangeUserName}>Change
                                </button>
                            </div>
                        </div>


                        <div class="item__box">
                            <div class="text">
                                <p>Phone<span class="star">*</span>:</p>
                            </div>
                            <Field name="systemUser.contact.phone" readonly="true" setting__general autocomplete="off"
                                   component={TextField} required={true} mask="(999) 999-9999" maskChar=" " normalize={phone}/>
                            <div class="eye__wrapper hidden">
                                <div class="eye-show"></div>
                            </div>
                            <div class="btn">
                                <button class="blue" type="button"onClick={handleSubmit} disabled={invalid}>Change
                                </button>
                            </div>
                        </div>


                        <div class="item__box">
                            <div class="text">
                                <p>Password<span class="star">*</span>:</p>
                            </div>
                            <Field name="password" type="password" readonly="true" setting__general
                                   autocomplete="off" component={TextField} required={true} minLength={6}
                                   maxLength={254}/>

                            <div class="eye__wrapper hidden">
                                <div class="eye-show"></div>
                            </div>

                            <div class="btn">
                                <button class="blue" type="button" onClick={onChangePassword}>Change
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="sys__setting-acc-help">
                    <p class="mandatory">Fields marked* are mandatory</p>
                </div>
                {false && <div class="footer__btn">
                    <button class="blue" type="button" onClick={handleSubmit} disabled={invalid}>Save</button>
                </div>}

            </div>
        </form>)
}

const Form = reduxForm({
    form: 'payments'
})(AccountForm);

class Account extends Component {

    state = {username: undefined, practice: undefined}


    async componentDidMount() {
        let me = this,
            username = me.props.username,
            userId = me.props.userId,
            fields = Remote.getFieldsByModel(me.props.metaInfo, "SystemUserModel", []);

        let systemUser = await getDataPromise('systemUser', userId, fields);

        me.setState({
            managedObject: {
                username: username,
                systemUser: systemUser,
                password: "123456789",
                userId: userId
            }
        });

    }

    submit(data) {
        saveDataPromise({
            queryName: 'updateSystemUser',
            showLoader: true
        }, {
            contact: data.systemUser.contact, id: data.userId
        }).then(function (result) {
            toastr.success("Change User Phone", "User Phone Number was changed successfuly.");
        });
    }

    changeUserName() {
        UiView.showDialog(<Provider store={UiView.createDialogStore()}><ChangeUserName managedObject={{}} actions={{
            reset: function (data, dialog) {
                requestChangeUserName(data.oldpassword, data.username).then(function (result) {
                    if (result) {
                        toastr.success("Change User Name", "Request for changing User Name was sent successfuly.");
                        dialog.close();
                    } else {
                        toastr.error("Change User Name", "Request for changing User Name wasn't sent successfuly.");
                    }
                });
            }
        }}/></Provider>);
    }

    changePassword() {
        UiView.showDialog(<Provider store={UiView.createDialogStore()}><ChangePassword managedObject={{}} actions={{
            reset: function (data, dialog) {
                changePassword(data.oldpassword, data.password).then(function (result) {
                    if (result) {
                        toastr.success("Change Password", "Password was changed successfuly.");
                        dialog.close();
                    } else {
                        toastr.error("Change Password", "Password wasn't changed successfuly.");
                    }
                });
            }
        }}/></Provider>);

    }


    render() {
        if (!this.state.managedObject) {
            return false;
        }
        return ([
            <div class="board__header"></div>,
            <div class="board__action"></div>,
            <Form onSubmit={this.submit.bind(this)} onChangeUserName={this.changeUserName.bind(this)}
                  onChangePassword={this.changePassword.bind(this)}
                  initialValues={this.state.managedObject}/>
        ]);
    }
}

const AccountConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseViewStateMap(state, ownProps), {
            userId: state.context.currentUser ? state.context.currentUser.id : undefined,
            username: state.context.currentUser ? state.context.currentUser.username : undefined
        });
    },
    function (dispatch) {
        return Object.assign(baseViewDispatcherMap(dispatch), {
            onSave: (managedObject) => {
                dispatch(saveData({queryName: 'updatePracticeOwnerGeneral'}, managedObject)).then(
                    function (result) {

                        dispatch({type: EVENT_VIEW_SAVE_DATA, result});
                        toastr.success("Change General Data", "\n" +
                            "The data has been changed successfully.");

                    });
            }
        });
    })(Account);

export default AccountConnector;