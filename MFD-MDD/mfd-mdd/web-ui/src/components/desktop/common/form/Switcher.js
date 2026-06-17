import React, {Component} from 'react';

export default class Switcher extends Component {
    handleChange(value) {
        this.props.input.onChange(!this.props.input.value);
    }
    render() {
        const { input, classWrapper = 'input' } = this.props;
        let checkPointClass = input.value ? 'check__point-true' : 'check__point';
        let checkLineClass = input.value ? 'check__line-true' : 'check__line';

        return (<div class={classWrapper}>
            <div class="input" onClick={this.handleChange.bind(this)}>
                <div class="modal__main-item-check">
                    <div class={checkPointClass}></div>
                    <div class={checkLineClass}></div>
                </div>
            </div>
        </div>
        );
    }
}