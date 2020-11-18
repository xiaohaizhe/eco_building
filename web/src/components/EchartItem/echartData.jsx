import React, { useEffect,useState} from 'react';
import { Link, connect, history, FormattedMessage, formatMessage, useParams } from 'umi';
import { Card, Result, Button, Form, Upload, Input, Spin, DatePicker, message, Modal} from 'antd';
import { FullscreenOutlined } from '@ant-design/icons';
import echarts from 'echarts';
import moment from 'moment';
const { RangePicker } = DatePicker;
const EchartData = props => {   
    const {dispatch , name , format , echartId , dataType , timeType} = props;
    const params = useParams()
    const { id } = params;
    const start = '2020-01';
    const end = '2020-12';  
    // const [sum,setSum] = useState(0)
    const [visible, setVisible] = useState(false);
    const [result, setResult] = useState([]); 

    useEffect(() => {
        if (dispatch) {
            getData(start,end);
        }       
    }, [id]);

    const getData = function (start,end){
        dispatch({
            type: 'research/getEchartData',
            payload:{
                dataType: dataType,
                start: start,
                end: end,
                id: id,
            },
            callback: (response) => {
                // setSum(sum);
                renderChart(response);
                window[`${echartId}_big`] = response;
            },
        });        
    }

    const renderChart = function (result){
        if(result.length == 0){
            message.success(`${name} 数据为空`);
        }
        var xAxisData = [];
        var seriesData = [];
        result.map((item,index)=>{
            xAxisData.push(item["actualDate"].split(" ")[0]);
            seriesData.push(item["value"]);
        })
        var myChart = echarts.init(document.getElementById(echartId));
        let option = {
            tooltip: {
                trigger: 'axis',
            },
            xAxis: {
                type: 'category',
                data: xAxisData,
                axisLabel: {
                    formatter: function (value) {
                            return value.substring(0,7)
                    },
                }
            },
            yAxis: {
                type: 'value'
            },
            itemStyle:{
                color:['#26B99A']
            },
            lineStyle:{
                color:['#26B99A']
            },
            series: [{
                data: seriesData,
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
        if(e){     
            var start = e[0].format(format);
            var end = e[1].format(format);
            getData(start,end);
        }
    }

    const fullPage = function (e) {
        setVisible(e);
        console.log(props);
        if(e){
            setTimeout(() => {
                var xAxisData = [];
                var seriesData = [];
                window[`${echartId}_big`].map((item,index)=>{
                    xAxisData.push(item["actualDate"].split(" ")[0]);
                    seriesData.push(item["value"]);
                })
                var myChart = echarts.init(document.getElementById(`${echartId}_big`));
                let option = {
                    xAxis: {
                        type: 'category',
                        data: xAxisData,
                        axisLabel: {
                            formatter: function (value) {
                                    return value.substring(0,7)
                            },
                        }
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [{
                        data: seriesData,
                        type: 'line',
                        // smooth: true
                    }]
                };
                myChart.setOption(option);
                window.addEventListener('resize', function () {
                    myChart.resize();
                })
            }, 100);            
        }
    }

    const chartModal = function (e) {
        return (
            <div id={`${echartId}_big`} style={{width: '1000px',height: '600px'}}>
                
            </div>
        )
    }

    return (
        <div>
            <Card
                bordered={false}
                bodyStyle={{
                    padding: '20px'
                }} 
                style={{height: `${window.innerHeight-400}px`}}
                className="chartItem"
            >
                <div>
                    <h3 className="title">
                        {name}
                        <FullscreenOutlined style={{fontWeight: 'blod', float: 'right' ,cursor: 'pointer'}} onClick={()=>{fullPage(true)}}/>
                    </h3>
                    <div className="date">
                        <span style={{marginRight: 10}}>选择日期：</span>
                        <RangePicker
                            picker={"month"}
                            defaultValue={[moment(start, format), moment(end, format)]}
                            format={format}
                            onChange={onChange}
                        />
                        {/* <span style={{margin: '0 10px 0 20px'}}>共计：{sum}</span> */}
                    </div>
                    <div id={echartId} style={{width: '100%' ,height: `${window.innerHeight-500}px`}}></div>
                </div>           
            </Card>
            <Modal
                title=""
                visible={visible}
                onCancel={()=>{fullPage(false)}}
                footer={[]}
                width={1080}
            >
               {chartModal()}
            </Modal>
        </div>        
    )
}

export default connect(({ research, loading }) => ({
    research,
    loading
}))(EchartData);