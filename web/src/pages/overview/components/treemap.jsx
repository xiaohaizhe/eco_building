import React, { useEffect,useState} from 'react';
import { Link, connect, history, FormattedMessage, formatMessage } from 'umi';
import { Card, Col, Row, Form, Upload, Input, Spin} from 'antd';
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
                type: 'overview/getTop10',
            });
            dispatch({
                type: 'overview/getTop5',
            });
        }
        this.loadChart()
    }
    componentDidUpdate(preProps) {
        const { overview } = this.props;
        if (preProps && JSON.stringify(preProps.overview) !== JSON.stringify(overview)) {
            this.loadChart()
        }
      }
    loadChart = () => {
        const { top5 } =this.props.overview;
        var myChart = echarts.init(document.getElementById('myChart'));
        let option = {
            series: [{
                name: 'ALL',
                type: 'treemap',
                top:52,
                label: {
                    normal: {
                        formatter: function (params) {
                            var arr = [
                                '{name|' + params.name + '}',
                                '{value|' + params.value + '}',
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
                        }
                    }
                },
                data:top5
            }]
        };
        myChart.setOption(option);
        window.addEventListener('resize', function () {
            myChart.resize();
        })
    }
    render(){
        const {top10} = this.props.overview
        return (
            <Card
            // loading={loading}
            title="项目统计"
            bordered={false}
            bodyStyle={{
            padding: 0,
            }}>
            <Row>
                <Col span={14}>
                    <div id="myChart" style={{width: '100%', height: `${window.innerHeight-310}px`}}></div>
                </Col>
                <Col span={10} style={{padding:'10px 20px'}}>
                    <p className={styles.rankingTitle}>单位面积电耗排名</p>
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
                            {item.powerConsumptionPerUnitArea}
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