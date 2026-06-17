import React, {Component} from 'react';
import {EditorState, convertToRaw, ContentState} from 'draft-js';
import {Editor} from 'react-draft-wysiwyg';
import draftToHtml from 'draftjs-to-html';
import htmlToDraft from 'html-to-draftjs';
import 'react-draft-wysiwyg/dist/react-draft-wysiwyg.css';

export default class RichEditor extends Component {

    constructor(props) {
        super(props);
        if (props.input.value) {
            const contentBlock = htmlToDraft(props.input.value);
            if (contentBlock) {
                const contentState = ContentState.createFromBlockArray(contentBlock.contentBlocks);
                const editorState = EditorState.createWithContent(contentState);
                this.state = {
                    editorState,
                };
            }
        } else {
            this.state = {
                editorState: EditorState.createEmpty(),
            };
        }
    }

    onEditorStateChange: Function = (editorState) => {
        this.setState({
            editorState,
        });
    };

    getError(meta) {
        return meta.active && meta.touched && meta.error && <span data-style="">{meta.error}</span>
    }

    render() {
        const {editorState} = this.state;
        const {input, meta, required, readOnly = false} = this.props;
        let className = undefined;
        if ((input.value && required) || meta.error) {
            className = (meta.error || (input.value && input.value.trim() === "<p></p>")) ? `input__false` : `input__true`;
        }
        return (<div class={className}>
            <Editor
                editorState={editorState}
                wrapperClassName="wrapper-class"
                editorClassName="editor-class"
                toolbarClassName="toolbar-class"
                onEditorStateChange={this.onEditorStateChange}
                onChange={this.changeValue.bind(this)}
            />
        </div>);
    }


    changeValue(ev) {
        this.props.input.onChange(draftToHtml(ev));
        this.props.input.onBlur();
    }


}