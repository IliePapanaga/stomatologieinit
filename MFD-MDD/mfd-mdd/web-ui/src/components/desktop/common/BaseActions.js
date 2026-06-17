import React, {Component} from 'react';
import {connect} from 'react-redux';
import {
    BUTTON_TYPE_ADD,
    BUTTON_TYPE_DELETE,
    BUTTON_TYPE_EDIT,
    BUTTON_TYPE_EDIT_ON_SELECTION,
    BUTTON_TYPE_EXPORT,
} from '../../../utils/Constants';

export class Actions extends Component {

    static running = false;

    componentDidMount() {
    }


    actionWrapper(view, action, element) {
        let origin = undefined;
        switch (element.type) {
            case BUTTON_TYPE_ADD:
            case BUTTON_TYPE_EDIT:
            case BUTTON_TYPE_EDIT_ON_SELECTION:
                origin = action.bind(view, element.dialog, element.dialogInfo);
                break;
            case BUTTON_TYPE_DELETE:
                origin = action.bind(view, element.dialogInfo);
                break;
            default:
                origin = action.bind(view);


        }
        return function () {
            try {
                if (Actions.running === true) {
                    return false;
                }
                Actions.running = true;
                origin.call(view, ...arguments);
                Actions.running = false;
            } catch (ex) {
            }
            finally {
                Actions.running = false;
            }

        };
    }

    render() {
        let elements = [];
        let elementsDownload = [];
        let {selection, actions, baseView} = this.props;
        if (actions) {
            actions.forEach(function (element, index) {
                let props = {};
                if (element.name) {
                    props['action-name'] = element.name;
                }
                switch (element.type) {
                    case BUTTON_TYPE_ADD:
                        elements.push(<div class="board__action-item" key={`action-${index}`} {...props}>
                            <button {...props} type="button"
                                    onClick={this.actionWrapper(baseView, baseView.onAddObject, element)}
                                    disabled={!baseView.isEnabledAction(element, selection)}>
                                <p>{element.label}</p>
                            </button>
                        </div>)
                        break;
                    case BUTTON_TYPE_EDIT:
                        elements.push(<div class="board__action-item" key={`action-${index}`} {...props}>
                            <button {...props} type="button"
                                    onClick={this.actionWrapper(baseView, baseView.onEditObject, element)}
                                    disabled={!baseView.isEnabledAction(element, selection)}>
                                <p>{element.label}</p>
                            </button>
                        </div>)
                        break;
                    case BUTTON_TYPE_EDIT_ON_SELECTION:
                        elements.push(<div class="board__action-item" key={`action-${index}`} {...props}>
                            <button {...props} type="button"
                                    onClick={this.actionWrapper(baseView, baseView.onEditOnSelectionObject, element)}
                                    disabled={!baseView.isEnabledAction(element, selection)}>
                                <p>{element.label}</p>
                            </button>
                        </div>)
                        break;
                    case BUTTON_TYPE_EXPORT:
                        elements.push(
                            <div class="board__action-item-export board__action-item-export-wrapper"></div>
                        )
                        break;
                    case BUTTON_TYPE_DELETE:
                        elements.push(<div class="board__action-item" key={`action-${index}`} {...props}>
                            <button {...props} type="button"
                                    onClick={this.actionWrapper(baseView, baseView.onDeleteObject, element)}
                                    disabled={!baseView.isEnabledAction(element, selection)}>
                                <p>{element.label}</p>
                            </button>
                        </div>)
                        break;
                    default:
                        elements.push(<div class="board__action-item" key={`action-${index}`} {...props}>
                            <button {...props} type="button"
                                    onClick={this.actionWrapper(baseView, element.onClick, element)}
                                    disabled={!baseView.isEnabledAction(element, selection)}>
                                <p>{element.label}</p>
                            </button>
                        </div>)
                }

            }, this);
        }

        if (actions) {
            actions.forEach(function (elementDownload, index) {
                let props = {};
                if (elementDownload.name) {
                    props['action-name'] = elementDownload.name;
                }
                switch (elementDownload.type) {
                    case BUTTON_TYPE_EXPORT:
                        elementsDownload.push(
                            <div class="board__action-item board__action-item-export" key={`action-${index}`}>
                                <button {...props} type="button"
                                        onClick={this.actionWrapper(baseView, elementDownload.onClick, elementDownload)}
                                        disabled={!baseView.isEnabledAction(elementDownload, selection)}>
                                    <p>{elementDownload.label}</p>
                                </button>
                            </div>
                        )
                        break;
                    default:
                        break;
                }
            }, this);
        }

        return (<div class="board__action" key="base_view_board__action">
            <div class="board__action-item-wrapper">
                {elements.map((element, index) => (
                    element
                ))}
            </div>

            <div class="board__action-item-wrapper">
                {elementsDownload.map((elementDownload, index) => (
                    elementDownload
                ))}
            </div>
        </div>);
    }
}

class BaseActions extends Actions {
}


const BaseActionsConnector = connect(
    (state, ownProps) => ({
        //selection: state.view?state.view.selection:[]
    }),
    dispatch => ({}))(BaseActions);

export default BaseActionsConnector;