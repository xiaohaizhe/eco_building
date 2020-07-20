import React, { useEffect,useState} from 'react';
import { Link, connect, history, FormattedMessage, formatMessage, useParams } from 'umi';
import { Card, Result, Button, Form, Upload, Input, Spin, DatePicker, message, Modal, Table} from 'antd';
import { FullscreenOutlined } from '@ant-design/icons';
import echarts from 'echarts';
import moment from 'moment';
const { RangePicker } = DatePicker;


const TableItem = props => {   
    const {dispatch , name , format , echartId , dataType , timeType} = props;
    const params = useParams()
    const { id } = params;
    const start = '2014-01-01';
    const end = '2018-01-01';  

    const [data, setData] = useState([]); 

    useEffect(() => {
        if (dispatch) {
            getData(start,end);
        }       
    }, []);

    const getData = function (start,end){
        dispatch({
            type: 'projectManage/getDataByTime',
            payload:{
                dataType: dataType,
                timeType: timeType,
                start: start,
                end: end,
                projectId: id,
            },
            callback: (response) => {
               setData(response.result)
            },
        });        
    }

    const columns = [
        {
            title: '日期',
            dataIndex: 'actualDate',
            editable: true,
            render: (_, record) => (
                <span>
                    {record["actualDate"].split(" ")[0]}
                </span>
            )
        },
        {
            title: '消耗量',
            dataIndex: 'value',
            editable: true,
        },
    ]
    const components = {
      body: {
        row: EditableRow,
        cell: EditableCell,
      },
    };
    const columnsReal = columns.map(col => {
      if (!col.editable) {
        return col;
      }
      return {
        ...col,
        onCell: record => ({
          record,
          editable: col.editable,
          dataIndex: col.dataIndex,
          title: col.title,
          handleSave: this.handleSave,
        }),
      };
    });
    return (
        <Card
            bordered={false}
            bodyStyle={{
                padding: '20px'
            }} 
            className="tableItem"
        >   
            <div className="title">{name}</div>
            <Table            
                bordered
                dataSource={data}
                columns={columnsReal}
            />
        </Card>        
    )
}

export default connect(({ projectManage, loading }) => ({
  projectManage,
  loading
}))(TableItem);