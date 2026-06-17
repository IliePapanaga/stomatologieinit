import React from 'react';
import Dialog from 'react-dialog';
import ReactDOM from 'react-dom';
import ObjectHelper from '../../../utils/Object';

var VisibilitySensor = require('react-visibility-sensor');
/**
 * A modal dialog can only be closed by selecting one of the actions.
 */

export const baseStateMap = (state, ownProps) => ({
    uid: new Date().getTime(),
    managedObject: ownProps.managedObject,
    actions: ownProps.actions
});

export let baseDispatcherMap = dispatch => ({});


export default class BaseDialog extends React.Component {
    static counter = 0;

    state = {};

    order=++BaseDialog.counter;

    constructor(props) {
        super(props);
        this.state['managedObject'] = props.managedObject;
        var actions = props.actions;

        if (actions) {
            if (actions.save) {
                this.executeSave = actions.save;
            }

            if (actions.close) {
                this.onClose = actions.close.bind(this);
            }
        }
    }

    onSave(data, d, form) {
        let me = form.dialog;
        let managedData = ObjectHelper.copyObject(data);

        if (me.beforeSave(me, managedData)) {

            me.executeSave(me, managedData.managedObject, function (dialog) {
                if (dialog.afterSave(dialog, managedData.managedObject)) {
                    dialog.onClose();
                }
            });
        }
    }

    beforeSave(dialog, managedObject) {
        return true;
    }

    afterSave(dialog, managedObject) {
        return true;
    }

    executeSave() {
    };

    onClose() {
        this.close();
    }

    getPosition(width, height) {

        return {x: -width / 2, y: -height / 2};
    }

    onShow(isVisible) {
        let me = this;
        if (isVisible) {
            let components = document.querySelectorAll('[bind-field]');

            for (var i = 0; i < components.length; i++) {
                let element = components[i];
                let value = ObjectHelper.getValue(me.state.managedObject, element.getAttribute('bind-field'));

                if (value) {
                    element.value = value;
                }

                element.addEventListener("change", function (el) {
                    var bindpath = el.target.getAttribute('bind-field');
                    if (bindpath) {
                        let bindpathparts = bindpath.split('.');
                        let managedObject = me.state.managedObject;
                        if (bindpathparts.length === 1) {
                            managedObject[bindpathparts[0]] = el.target.value;
                        } else {
                            managedObject[bindpathparts[0]] = managedObject[bindpathparts[0]] || {};
                            let ref = managedObject[bindpathparts[0]];
                            for (var i = 1; i < bindpathparts.length; i++) {
                                if (i < bindpathparts.length - 1) {
                                    ref[bindpathparts[i]] = ref[bindpathparts[i]] || {}
                                    ref = ref[bindpathparts[i]];
                                } else {
                                    ref[bindpathparts[i]] = el.target.value;
                                }
                            }
                        }
                        me.setState({managedObject: managedObject});
                    }

                });
            }

        }
    }

    close = (dialog) => {
        let mountNode = ReactDOM.findDOMNode(this.refs[this.props.uid]);
        if (mountNode) {
            let parentElement = mountNode.parentElement;
            if (mountNode.parentNode) {
                ReactDOM.unmountComponentAtNode(mountNode.parentNode);
            }
            if (parentElement) {
                parentElement.remove();
            }
        }
    };

    renderDialogContent() {
    }

    onVisible() {
        let mountNode = ReactDOM.findDOMNode(this);
        if (mountNode && mountNode.parentElement) {
            let uiDialogEl = mountNode.parentElement.querySelector('.ui-dialog-container');
            let overlayDialogEl = mountNode.parentElement.querySelector('.ui-dialog-overlay');
            let draggableDialogEl = mountNode.parentElement.querySelector('.react-draggable');
            if(!uiDialogEl.style['zIndex']){
                uiDialogEl.style['zIndex']=100+this.order;
                if(draggableDialogEl){
                    draggableDialogEl.style['zIndex']=100+this.order;
                }
                overlayDialogEl.style['zIndex']=99+this.order;

            }
        }

    }

    render() {
        const {width, height, title} = this.dialogProps();
        let header = title ? <div class="modal__header handle">
            <div class="modal__header-icon">
                <div>
                    <svg class="close__icon" onClick={this.close.bind(this)}>
                        <use xlinkHref="#Close"></use>
                    </svg>
                </div>
            </div>
            <h2>{title}</h2>
        </div> : '';

        return (
            <Dialog position={this.getPosition(width, height)} width={width} height={height} title={header} modal={true}
                    isDraggable={true} isResizable={false} ref={this.props.uid}>

                {this.renderDialogContent()}
                <VisibilitySensor onChange={this.onVisible.bind(this)}/>
            </Dialog>);
    }
}


