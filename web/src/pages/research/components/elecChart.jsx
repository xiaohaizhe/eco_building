import React, { useEffect,useState} from 'react';
import { Link, connect, history, FormattedMessage, formatMessage, useParams } from 'umi';
import { Card, Result, Button, Form, Upload, Input, Spin, DatePicker, message, Modal} from 'antd';
import echarts from 'echarts';
const ElecChart = props => {   
    const {dispatch , name , format , echartId , dataType , timeType} = props;
    const params = useParams()
    const { id } = params;

    useEffect(() => {
        if (dispatch) {
            getData();
        }       
    }, [id]);

    const getData = function (){
        dispatch({
            type: 'research/get3YearsElecData',
            payload:{
                id: id,
            },
            callback: (response) => {
                // setSum(sum);
                renderChart(response.result);
            },
        });        
    }

    const renderChart = function (result){
        if(result.length == 0){
            message.success(`${name} 数据为空`);
        }
        var myChart = echarts.init(document.getElementById('echartElec'));
        let option = {
            tooltip: {
                trigger: 'axis',
            },
            xAxis: {
                name: '月',
                type: 'category',
                data:['01','02','03','04','05','06','07','08','09','10','11','12'],
                // axisLabel: {
                //     formatter: function (value) {
                //         return value+''
                //     },
                // }
            },
            grid:{
                top:30,
                bottom:50
            },
            yAxis: {
                name: 'kWh',
                type: 'value'
            },
            series: [{
                data: result[0],
                name: '2017',
                type: 'line',
                // smooth: true
              },{
                data: result[1],
                name: '2018',
                type: 'line',
                // smooth: true
              },{
              data: result[2],
              name: '2019',
              type: 'line',
              // smooth: true
            }]
        };
        myChart.setOption(option);
        window.addEventListener('resize', function () {
            myChart.resize();
        })
    }
    return (
        <div>
            <div id='echartElec' style={{width: '100%' ,height:'300px'}}></div>
        </div>        
    )
}

export default connect(({ research, loading }) => ({
    research,
    loading
}))(ElecChart);