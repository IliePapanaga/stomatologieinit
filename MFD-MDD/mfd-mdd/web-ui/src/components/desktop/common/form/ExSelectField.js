import React, {Component} from 'react';
import ObjectHelper from '../../../../utils/Object';
import onClickOutside from 'react-onclickoutside';

class ExSelectField extends Component {

    onChangeSelection(value) {
        let me = this;
        const multiple = me.props.multiple;
        let selection = me.props.input.value || [];
        selection = ObjectHelper.copyObject(selection);
        let index = selection.indexOf(value);
        if (multiple) {
            if (index < 0) {
                selection.push(value);
            } else {

                selection.splice(index, 1);

            }
        } else {
            selection = [value];
        }
        this.setState({value: selection});
        this.props.input.onChange(selection);
    }

    getError(meta) {
        return meta.active && meta.touched && meta.error && <span data-style="">{meta.error}</span>
    }

    showMenu() {
        if (!this.props.readOnly) {
            this.baseMenu.classList.toggle("multiSelect-list-show");
        }
    }

    handleClickOutside = () => {
        this.baseMenu.classList.remove("multiSelect-list-show");
    }


    render() {
        const {input, meta, menuItems, required} = this.props;
        const value = input.value || [];
        let error = this.getError(meta);
         let className = undefined;
         if (error || required) {
             className = meta.error ? `mSelect input__false` : `mSelect input__true`;
         }
        let displayValue = `Select Values`;
        let classN = "";

        if (value.length > 0) {
            displayValue = '';
            value.forEach(function (selectedValue, index) {
                let selectedItem = menuItems.filter(function (item) {
                    return item.id === selectedValue || item.code === selectedValue;
                });
                if (selectedItem.length > 0) {
                    displayValue += (index === 0 ? selectedItem[0].name : `, ${selectedItem[0].name}`);
                }
            }, this);
        }


        return (

            classN = (this.props.readOnly) ? "disabled__multiSelect-list multiSelect-list" : "multiSelect-list",

            <div class="input board__input-wrapper select__input">
                <div class={classN} ref={(div) => {
                    this.baseMenu = div
                }} onBlur={this.showMenu.bind(this)}>
                    <ul>
                        {menuItems.map((menuItem, index) => (
                            <li onClick={this.onChangeSelection.bind(this, menuItem.code || menuItem.id)}>
                                <input type="checkbox" name={menuItem.code} id={menuItem.code || menuItem.id}
                                       checked={value.indexOf(menuItem.code || menuItem.id) >= 0}/>
                                <label>{menuItem.name}</label>
                            </li>
                        ))}
                    </ul>
                    {false && <div class="multiSelect-btn">
                        <button type="button" onClick={this.showMenu.bind(this)}>Ok</button>
                    </div>}
                </div>
                <input class={className} placeholder="Select" readonly="true" value={displayValue}
                       onClick={this.showMenu.bind(this)}/>
            </div>
        );
    }
}

export default onClickOutside(ExSelectField)