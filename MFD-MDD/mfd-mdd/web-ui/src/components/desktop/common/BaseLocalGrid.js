import React, {Component} from 'react';
import {connect} from 'react-redux';
import {
    DEFAULT_CELL_STYLE,
    EVENT_CHANGE_SELECTION_IN_GRID,
    SELECTION_MODE_MULTY,
    SELECTION_MODE_NONE,
    STATUS_ACTIVE
} from '../../../utils/Constants';
import {Direction, Order} from '../../../models/core/QueryInfo';
import ObjectHelper from '../../../utils/Object';
import Renderer from '../../../utils/Renderer';

const SELECT_ALL = 'all';

export class LocalGrid extends Component {

    constructor(props) {
        super(props);

        let { onRef, columns, selectionMode, nodes } = props;

        if (onRef) {
            onRef(this);
        }

        this.state = {
            columns: columns,
            selection: [],
            selectionMode: selectionMode || SELECTION_MODE_MULTY,
            nodes: nodes,
            orders: this.configureOrders(columns),
            orderInfo: undefined
        }

    }

    configureOrders(columns) {
        let orders: Array<Order> = [];
        //let sortOrder = 0;

        columns.forEach(function (element, index) {
            if (element.sortable) {
                orders.push(new Order({ field: element.dataIndex, direction: Direction.ASC, nullsFirst: true/*, sortOrder: sortOrder++ */ }));
            }
        }, this);

        return orders;
    }

    getOrder(column) {
        return this.state.orders.filter((order) => order.field === column.dataIndex)[0];
    }

    onSort(column, columnIndex) {
        let me = this;
        let state = me.state;
        let orders = state.orders;

        orders.forEach(function (order) {
            order.nullsFirst = true;
        }, this);

        let order = me.getOrder(column);
        order.direction = order.direction === Direction.ASC ? Direction.DESC : Direction.ASC;
        order.nullsFirst = false;
        me.setState(...this.state, { orders: orders, orderInfo: { dataIndex: column.dataIndex, direction: order.direction } });
    }

    onChangeSelection(event) {
        let me = this;
        let checked = event.target.checked;
        let index = event.target.id.substr(event.target.id.indexOf('-') + 1);
        let rows = index === SELECT_ALL ? me.state.nodes : [me.state.nodes[index]];
        let oldSelection = me.state.selectionMode === SELECTION_MODE_MULTY ? me.state.selection : [];
        let newSelection = undefined;

        if (checked) {
            newSelection = oldSelection.concat(rows);
        } else {
            newSelection = oldSelection.filter(function (row) {
                return !rows.includes(row);
            });
        }
        me.setState({ selection: newSelection });
        if (me.props.changeSelection) {
            me.props.changeSelection(newSelection);
        }
    }
    onSelectRow(index, event) {
        let me = this;
        let target = event.target;
        let checked = !target.checked;
        let rows = index === SELECT_ALL ? me.state.nodes : [me.state.nodes[index]];
        let oldSelection = me.state.selectionMode === SELECTION_MODE_MULTY ? me.state.selection : [];
        let newSelection = undefined;

        if (checked) {
            newSelection = oldSelection.concat(rows);
        } else {
            newSelection = oldSelection.filter(function (row) {
                return !rows.includes(row);
            });
        }
        me.setState({ selection: newSelection });
        if (me.props.changeSelection) {
            me.props.changeSelection(newSelection);
        }
        event.stopPropagation();
        event.preventDefault();

    }

    defaultCellRenderer(value, cellClass, row, columnIndex, rowIndex) {
        return <div class={cellClass}>{value}</div>;
    }

    renderCell(value, row, column, columnIndex, rowIndex) {
        if (column.renderer) {
            return column.renderer(value, column.cellClass || DEFAULT_CELL_STYLE, row, columnIndex, rowIndex);
        } else {
            return this.defaultCellRenderer(value, column.cellClass || DEFAULT_CELL_STYLE, row, columnIndex, rowIndex);
        }
    }

    renderRow(columns, row, rowIndex, selection, selectionMode) {
        return [
            <tr class={"row__btn row__btn-" + Renderer.getRowStatusClassPrefix(row.status || STATUS_ACTIVE)+(selection.includes(row)?' pressed':'')} key={`row-${rowIndex}`} onClick={this.onSelectRow.bind(this, rowIndex)}>
                {selectionMode !== SELECTION_MODE_NONE && <td class="row">
                    <div class="row__check">
                        <input type="checkbox" checked={selection.includes(row)} name="check"/>
                        <label htmlFor={`check-${rowIndex}`}></label>
                    </div>

                </td>}
                {columns.map((column, columnIndex) => (
                    (!column.hidden) && <td key={`td-${columnIndex}`}>{this.renderCell(ObjectHelper.getValue(row, column.dataIndex), row, column, columnIndex, rowIndex)}</td>
                ))}
            </tr>,
        ];
    }

    render() {
        let me = this;
        const {
            columns, nodes, selection, selectionMode, orderInfo
        } = me.state;

        let items = nodes || [];

        if (orderInfo) {
            items = items.sort(function (a, b) {
                let value1 = ObjectHelper.getValue(a, orderInfo.dataIndex, true),
                    value2 = ObjectHelper.getValue(b, orderInfo.dataIndex, true);
                if (orderInfo.direction === Direction.DESC ? value1 > value2 : value2 > value1) {
                    return -1;
                }
                if (orderInfo.direction === Direction.DESC ? value2 > value1 : value1 > value2) {
                    return 1;
                }
                return 0;
            });
        }


        const getColumnOrderStyle = function (column) {
            let direction = me.getOrder(column).direction;
            let prefix = direction === Direction.DESC ? ' row__select-arrow-active' : '';
            return `row__select-arrow${prefix}`;
        }

        return (
            [<div class="board__table" key="base_grid_board__table">

                <table>
                    <thead>
                        <tr>
                            {selectionMode !== SELECTION_MODE_NONE && <th data-resizable-column-id="id" class="row" width="30" key="th-sel">
                                {selectionMode === SELECTION_MODE_MULTY &&
                                    <div class="row__check" key="row__check-sel">
                                        <input type="checkbox" checked={selection.length >= items.length} onChange={this.onChangeSelection.bind(this)} name="check" id={`check-${SELECT_ALL}`} />
                                        <label for={`check-${SELECT_ALL}`}></label>
                                    </div>
                                }
                            </th>}


                            {columns.map((column, columnIndex) => (
                                (!column.hidden) && <th data-resizable-column-id={`id${columnIndex}`} class="row" width={column.width} key={`th-${columnIndex}`}>
                                    <div class="row__select" onClick={column.sortable
                                        ? this.onSort.bind(this, column, columnIndex) : undefined} key={`row__select-${columnIndex}`}>
                                        <div>{column.title}</div>
                                        {column.sortable && <div class={getColumnOrderStyle(column)}></div>}
                                    </div>
                                </th>
                            ))}

                        </tr>
                    </thead>
                    <tbody>
                        {items.map((row, rowIndex) => (
                            this.renderRow(columns, row, rowIndex, selection, selectionMode)
                        ))}
                    </tbody>
                </table>
            </div>]
        );
    }
}

class BaseLocalGrid extends LocalGrid { }

const GridLocalConnector = connect(
    (state, ownProps) => ({

    }),
    dispatch => ({
        changeSelection(selection) {
            dispatch({ type: EVENT_CHANGE_SELECTION_IN_GRID, selection });
        }
    }))(BaseLocalGrid);

export default GridLocalConnector;