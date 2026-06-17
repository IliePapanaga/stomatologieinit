import React from 'react';
//import Dialog from 'material-ui/Dialog';
import BaseDialog from './BaseDialog';

export const dialogInfo = {}
export default class BaseWizard extends BaseDialog {

    constructor(props) {
        super(props);

        let pagesRoot = this.initPages(props.managedObject);

        this.applyPagesActions(pagesRoot);

        this.state = {
            node: pagesRoot,
            readOnly: props.readOnly
        }
    }

    applyPagesActions(node) {
        if (node) {
            if (!node.nextPage) {
                node.nextPage = this.nextPage.bind(this);
            } else {
                node.nextPage = node.nextPage.bind(this);
            }

            if (!node.previousPage) {
                node.previousPage = this.previousPage.bind(this);
            } else {
                node.previousPage = node.previousPage.bind(this);
            }
            if (node.children) {
                node.children.forEach(child => {
                    child.parent = node;
                    this.applyPagesActions(child);
                });
            }
        }
    }

    initPages(managedObject) { return undefined }

    async doNextPage(managedObject) {
        let me = this;
        const { node } = me.state;
        let nextPage = await node.nextPage(node, managedObject);
        this.setState({ node: nextPage })
    }

    async doPreviousPage(managedObject) {
        let me = this;
        const { node } = me.state;
        let previousPage = await node.previousPage(node, managedObject);
        this.setState({ node: previousPage })
    }

    nextPage(currentNode, managedObject) {
        return new Promise((resolve) => {
            let childNode = null;
            if (currentNode && currentNode.children && currentNode.children.length) {
                childNode = currentNode.children[0];
                if (childNode.allowed && !childNode.allowed(childNode, managedObject)) {
                    this.nextPage(childNode, managedObject).then(function (node) {
                        resolve(node);
                    });

                } else {
                    resolve(childNode);
                }
            } else {
                resolve(childNode);
            }

        });
    }

    previousPage(currentNode, managedObject) {
        return new Promise((resolve) => {
            let parentNode = null;
            if (currentNode && currentNode.parent) {
                parentNode = currentNode.parent;
                if (parentNode.allowed && !parentNode.allowed(parentNode, managedObject)) {
                    this.previousPage(parentNode, managedObject).then(function (node) {
                        resolve(node);
                    });

                } else {
                    resolve(parentNode);
                }
            } else {
                resolve(parentNode);
            }
        });
    }
    renderDialogContent() {
        const {references, pageProps} = this.props
        const {node, readOnly} = this.state
        const Page = node.page;
        return (
            <div>
                <Page
                    dialog={this}
                    {...pageProps}
                    handlePreviousPage={this.doPreviousPage}
                    handleNextPage={this.doNextPage}
                    handleCancel={this.close}
                    onSubmit={this.onSave}
                    node={node}
                    renderNavigation={this.renderNavigation}
                    references={references}
                    readOnly={readOnly}
                    initialValues={{
                        managedObject: this.convertData(this.props.managedObject)
                    }}/>
            </div>);
    }
    renderNavigation(wizard, handleSubmit, handleNextPage, handlePreviousPage, change, handleCancel, invalid, node, managedObject) {
        let back = node.parent,
            next = node.children,
            ok = !node.children,
            cancel = true;

        if (node.navigation) {
            let navigation = node.navigation(node, managedObject);
            back = navigation.back;
            next = navigation.next;
            ok = navigation.ok;
            cancel = navigation.cancel;
        }

        return (<div class="footer__btn">
            {back && <button class="blue white" type="button" onClick={handlePreviousPage.bind(wizard, managedObject)}>Back</button>}
            {cancel && <button class="blue white" type="button" onClick={handleCancel.bind(wizard)}>Close</button>}
            {next && <button class="blue" type="button" onClick={handleNextPage.bind(wizard, managedObject)} disabled={invalid}>Next</button>}
            {ok && <button class="blue" type="button" onClick={handleSubmit.bind(wizard)} disabled={invalid}>Ok</button>}
        </div>);
    }
}
