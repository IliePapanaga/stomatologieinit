import React from 'react';
import {connect} from 'react-redux';
import Dialog from 'material-ui/Dialog';
import BaseDialog, {baseDispatcherMap, baseStateMap} from '../../common/BaseDialog';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';

class Chooser extends BaseDialog {
    goTo(url) {

        this.props.actions.goTo(url);
        this.close();
    }
    render() {
        const paperStyles = {
            borderRadius: 5,
        };

        return (
            <div ref={this.props.uid} >
                <MuiThemeProvider>
                    <Dialog className="modal__static"
                        modal={true} paperProps={{ style: paperStyles }} bodyClassName="modal signUp"
                        open={true} contentStyle={{ borderRadius: 5, width: 720 }} bodyStyle={{ borderRadius: 3, 'paddingTop': 0, paddingLeft: 0, paddingRight: 0 }}>
                        
                        <div class="modal__header">
                            <div class="modal__header-icon">
                                <div>
                                    <svg class="close__icon"  onClick={this.close.bind(this)}>
                                        <use xlinkHref="#Close"></use>
                                    </svg>
                                </div>
                            </div>
                            <h2>Sign Up</h2>
                        </div>
                        <div class="signUp__main">
                            <div class="signUp__main-item">
                                <div class="signUp__main-item-img">
                                    <div>
                                        <svg class="signUp__icon">
                                            <use xlinkHref="#want"></use>
                                        </svg>
                                    </div>
                                    <div>
                                        <svg class="signUp__icon">
                                            <use xlinkHref="#wantH"></use>
                                        </svg>
                                    </div>
                                </div>
                                <div class="signUp__main-item-btn">
                                    <button class="blue" onClick={this.goTo.bind(this, '/signup/practice-owner')}>I'm a Dental Practice owner</button>
                                </div>
                                <div class="signUp__main-item-text">
                                    <p>I want to hire professionals</p>
                                </div>
                            </div>
                            <div class="signUp__main-item">
                                <div class="signUp__main-item-img">
                                    <div>
                                        <svg class="signUp__icon">
                                            <use xlinkHref="#look"></use>
                                        </svg>
                                    </div>
                                    <div>
                                        <svg class="signUp__icon">
                                            <use xlinkHref="#lookH"></use>
                                        </svg>
                                    </div>
                                </div>
                                <div class="signUp__main-item-btn">
                                    <button class="blue" onClick={this.goTo.bind(this, '/signup/professional')}>I'm a Dental Professional</button>
                                </div>
                                <div class="signUp__main-item-text">
                                    <p>I'm looking for job</p>
                                </div>
                            </div>
                        </div>
                    </Dialog>
                </MuiThemeProvider>
            </div>
        );
    }
}

const ChooserDialogConnector = connect(
    function (state, ownProps) {
        return Object.assign(baseStateMap(state, ownProps), {
        });
    },
    function (dispatch) {
        return Object.assign(baseDispatcherMap(dispatch), {
        });
    })(Chooser);

export default ChooserDialogConnector;
