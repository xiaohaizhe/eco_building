import React, { useEffect,useState} from 'react';
import { Link, connect, history, FormattedMessage, formatMessage } from 'umi';
import { Card, Result, Button, Form, Upload, Input, Spin, DatePicker} from 'antd';
import { FullscreenOutlined } from '@ant-design/icons';
import echarts from 'echarts';
import moment from 'moment';
const { RangePicker } = DatePicker;

const EchartItem = props => {   
    const {dispatch , name , format , echartId , dataType , timeType} = props;  
    useEffect(() => {
        if (dispatch) {
            getData('2019-01-01','2020-01-01');
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
                projectId: 10893214898716672,
                // projectId: props.location.query.id
            },
            callback: (response) => {
                
                renderChart(response.Result);
            },
        });
        
    }

    const renderChart = function (){
        var myChart = echarts.init(document.getElementById(echartId));
        let option = {
            xAxis: {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
            },
            yAxis: {
                type: 'value'
            },
            series: [{
                data: [820, 932, 901, 934, 1290, 1330, 1320],
                type: 'line',
                // smooth: true
            }]
        };
        myChart.setOption(option);
        window.addEventListener('resize', function () {
            myChart.resize();
        })
    }

    const onChange = function (e) {
        alert(JSON.stringify(e));
    }

    const fullPage = function () {
        alert(1111);
    }

    return (
        <Card 
        bordered={false}
        bodyStyle={{
            padding: '20px'
        }} 
        style={{height: `${window.innerHeight-400}px`}}
        className="chartItem">
            <div>
                <h3 className="title">
                    {name}
                    <FullscreenOutlined style={{fontWeight: 'blod', float: 'right' ,cursor: 'pointer'}} onClick={fullPage}/>
                </h3>
                <div className="date">
                    <span style={{marginRight: 10}}>选择日期：</span>
                    <RangePicker
                        defaultValue={[moment('2020/01/01', format), moment('2020/02/01', format)]}
                        format={format}
                        onChange={onChange}
                    />
                </div>
                <div id={echartId} style={{width: '100%' ,height: `${window.innerHeight-500}px`}}>

                </div>
            </div>           
        </Card>
    )
}

export default connect(({ projectManage, loading }) => ({
  projectManage,
  loading
}))(EchartItem);