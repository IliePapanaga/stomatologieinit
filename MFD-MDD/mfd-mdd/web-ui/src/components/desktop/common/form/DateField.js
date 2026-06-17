import React from 'react';
import PropTypes from 'prop-types';
import DateChooser from './DateChooser';
import DateHelper from '../../../../utils/DateHelper';
import moment from 'moment';

import 'react-datepicker/dist/react-datepicker.css';

class DateField extends React.Component {
    static propTypes = {
        input: PropTypes.shape({
            onChange: PropTypes.func.isRequired,
            value: PropTypes.string.isRequired,
        }).isRequired,
        meta: PropTypes.shape({
            touched: PropTypes.bool,
            error: PropTypes.bool,
        }),
        placeholder: PropTypes.string,
    }

    static defaultProps = {
        placeholder: ''
    }

    constructor(props) {
        super(props)
        this.handleChange = this.handleChange.bind(this)
    }

    handleChange(date) {
        this.props.input.onChange(date ? moment(date).format(this.props.dateFormat) : date)
    }


    render() {
        const {
            input, placeholder, required, initiallyChoosenDate,
            meta: {error},
            ...other
        } = this.props;

        let className = undefined;
        if ((input.value && required) || error) {
            className = error ? `input__false` : `input__true`;
            if (error) {
                input.value = '';
            }
        }
        const initiallyDate = initiallyChoosenDate ? moment(DateHelper.convertServerDateStringToString(initiallyChoosenDate, this.props.dateFormat), this.props.dateFormat) : null;
        const selectedDate = input.value ? moment(DateHelper.convertServerDateStringToString(input.value, this.props.dateFormat), this.props.dateFormat) : initiallyDate;

        return (
            <div>
                <DateChooser
                    {...input} {...other}
                    placeholder={placeholder} class={className}
                    dateFormat={this.props.dateFormat}
                    openToDate={selectedDate}
                    dropdownMode="select" showMonthDropdown={true} showYearDropdown={true}
                    todayButton={<button type="button">Today</button>}
                    onChange={this.handleChange}
                />
            </div>
        )
    }
}

export default DateField