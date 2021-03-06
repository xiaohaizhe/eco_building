import React, { useEffect,useState} from 'react';
import { Link, connect, history, FormattedMessage, formatMessage } from 'umi';
import { Card, Col, Row, Form, Upload, Input, Spin,Table} from 'antd';
import echarts from 'echarts'
import styles from '../index.less';
class Treemap extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
      };
  
    }
  
    componentDidMount() {
        const { dispatch } = this.props;

        if (dispatch) {
            dispatch({
                type: 'overview/getExcel'
            })
            dispatch({
                type: 'overview/getTop10',
            });
            dispatch({
                type: 'overview/getTypeData',
            });
        }
        this.loadBarChart()
    }
    componentDidUpdate(preProps) {
        const { overview } = this.props;
        if (preProps && JSON.stringify(preProps.overview.excel) !== JSON.stringify(overview.excel)) {
            this.loadBarChart()
        }
      }
      //top5
    loadChart = () => {
        const { top5 } =this.props.overview;
        var myChart = echarts.init(document.getElementById('myChart'));
        let option = {
            series: [{
                name: 'ALL',
                breadcrumb: {show: false},
                type: 'treemap',
                top:15,
                left:20,
                right:20,
                bottom:20,
                label: {
                        formatter: function (params) {
                            var arr = [
                                '{name|' + params.name + '}',
                                '{value|' + params.data.realValue + '}',
                            ];
                            return arr.join('\n');
                        },
                        rich: {
                            value: {
                                fontSize: 14,
                                lineHeight: 30,
                                color: '#fff',
                                align:'center'
                            },
                            name: {
                                fontSize: 16,
                                color: '#fff'
                            },
                            
                        },
                },
                data:top5
            }]
        };
        myChart.setOption(option);
        window.addEventListener('resize', function () {
            myChart.resize();
        })
    }
    loadBarChart = () =>{
        const { excel } =this.props.overview;
        var myChart = echarts.init(document.getElementById('barChart'));
        let option = {
            tooltip: {},
            legend: {
                data: ["办公", "商场", "文化教育", "餐饮", "医院", "酒店", "其他"]
            },
            grid:{
                top:20,
                left:50,
                right:50,
                bottom:120,
            },
            xAxis: {
                type: 'category',
                data:excel.x,
                splitLine: {
                    show: false
                },
                axisLabel: {
                    rotate: 90
                },
            },
            yAxis: {
                type: 'value',
                splitLine: {
                    show: false
                },
            },
            series: [{
                data: excel.data,
                type: 'bar',
                itemStyle: {
                    color: function(params){
                        let color = '';
                        switch(params.data.type){
                            case "办公":color='#2f4554';break;
                            case "商场":color='#61a0a8';break;
                            case "文化教育":color='#d48265';break;
                            case "酒店":color='#bda29a';break;
                            case "医院":color='#ca8622';break;
                            case "餐饮":color='#6e7074';break;
                            case "其他":color='#91c7ae';break;
                            default:color='#e062ae';
                        }
                        return color
                    }
                },
            }]
        };
        console.log(excel)
        myChart.setOption(option);
        window.addEventListener('resize', function () {
            myChart.resize();
        })
    }
    render(){
        const {top10,typeData} = this.props.overview;
        
        const columns = [
            {
              title: '建筑类型',
              dataIndex: 'type',
              key: 'type',
              render: (value, row, index) => {
                const obj = {
                  children: value,
                  props: {},
                };
                if (index === 0) {
                  obj.props.rowSpan = 7;
                }
                // These two are merged into above cell
                if (index >0 && index<7) {
                  obj.props.rowSpan = 0;
                }
                return obj;
              },
            },
            {
              title: '项目数量',
              dataIndex: 'number',
              key: 'number',
            },
            {
              title: '楼栋数量',
              dataIndex: 'sum',
              key: 'sum',
            }
          ];
          const renderLi = function () {
            const legend = [{color:'#2f4554',name:'办公'},{color:'#61a0a8',name:'商场'},{color:'#d48265',name:'文化教育'},{color:'#bda29a',name:'酒店'},{color:'#ca8622',name:'医院'},{color:'#6e7074',name:'餐饮'},{color:'#91c7ae',name:'其他'}]
            let temp =[];
            legend.forEach(item=>{
                temp.push (<li key={item.name}>
                            <div className="len" style={{background:item.color}}></div>
                            <span>{item.name}</span>
                        </li>)
                })
            return temp;
          }
          const rendertr = function () {
            let temp =[];
            typeData.forEach((item,index)=>{
                if(index==0){
                    temp.push (<tr>
                        <td rowSpan={typeData.length-1}>公共建筑</td>
                        <td>{item.type}</td>
                        <td>{item.number}</td>
                        <td>{item.sum}</td>
                    </tr>)
                }else if(index<typeData.length-1){
                    temp.push (<tr>
                        <td>{item.type}</td>
                        <td>{item.number}</td>
                        <td>{item.sum}</td>
                    </tr>)
                }else if(index==typeData.length-1){
                    temp.push(<tr>
                        <td colSpan='2'>{item.type}</td>
                        <td>{item.number}</td>
                        <td>{item.sum}</td>
                    </tr>)
                }
            })
            return temp;
          }
        return (
            <Card
            // loading={loading}
            title="项目统计"
            bordered={false}
            bodyStyle={{
                padding: 0
            }}>
            <Row>
                <Col span={16}>
                    {/* <div id="myChart" style={{width: '100%', height: `${window.innerHeight-255}px`}}></div> */}
                    <Row className="typeTable">
                        {/* <Table dataSource={typeData} columns={columns} pagination={false} size="small" style={{width:'100%'}}/> */}
                        <table border='1'>
                            <thead>
                                <tr>
                                    <th colSpan="2">建筑类型</th>
                                    <th colSpan='1'>项目数量</th>
                                    <th colSpan='1'>楼栋数量</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                rendertr()
                                
                                }
                                
                            </tbody>
                        </table>
                    </Row>
                    <Row>
                        <ul className="typeLegend">
                            {renderLi()}
                        </ul>
                        <div id="barChart" style={{width: '100%', height: `${window.innerHeight-520}px`}}></div> 
                    </Row>
                </Col>
                <Col span={8} style={{padding:'10px 20px'}}>
                    <p className={styles.rankingTitle}>单位面积电耗排名kWh/㎡·a</p>
                    <ul className={styles.rankingList}>
                        {top10.map((item, i) => (
                        <li key={item.name}>
                            <span className={`${styles.rankingItemNumber} ${i < 3 ? styles.active : ''}`}>
                            {i + 1}
                            </span>
                            <span className={styles.rankingItemTitle} title={item.name}>
                            {item.name}
                            </span>
                            <span className={styles.rankingItemValue}>
                            {(item.powerConsumptionPerUnitArea|| 0).toFixed(1)}
                            </span>
                        </li>
                        ))}
                    </ul>
                </Col>
            </Row>
        </Card>)}
    
    }

export default connect(({ overview, loading }) => ({
  overview,
  loading
}))(Treemap);