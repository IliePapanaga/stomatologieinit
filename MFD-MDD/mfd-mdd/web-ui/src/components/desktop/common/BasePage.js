/*eslint-disable no-unused-vars*/
import React, {Component} from 'react';
/*eslint-enable no-unused-vars*/
import UiView from '../../../utils/UiView';

export default class BasePage extends Component {

    footerSelector = "div.footer";

    navigateTo(functionalAreaKey, viewSetKey, viewKey) {
        let url = undefined;
        if (!viewKey) {
            url = UiView.getDefaultFunctionalAreaUrl([this.props.functionalArea], functionalAreaKey, viewSetKey);
        } else {
            url = `/${functionalAreaKey}/${viewSetKey}/${viewKey}`;
        }
        this.props.router.navigateToUrl(url);
    }

    componentDidMount() {
        let me = this;
        let components = document.querySelectorAll('[bind-field]');

        for (var i = 0; i < components.length; i++) {
            let element = components[i];
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
                    me.setState({ managedObject: managedObject });
                }

            });

        }
        //UiView.initFooter(this.footerSelector);
    }

    componentDidUpdate() {
        //UiView.initFooter(this.footerSelector);
    }
}
export const basePageStateMap = (state, ownProps) => ({
    router: ownProps.router
});

export let basePageDispatcherMap = dispatch => ({
});
