import React, { useState, useRef, useEffect} from 'react';
import { Row, Table,Col ,Descriptions,Card,Avatar,Button } from 'antd';
import { connect,useParams,history } from 'umi';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import nopic from '../../../assets/nopic.png'
import '../index.less'

const HVAC = props => {
    const { dispatch ,HVAC,researchName} =props;
    const params = useParams()
    const { id } = params;
    const [ loading, setLoading] = useState(false);

    useEffect(() => {
      if (dispatch) {
        dispatch({
          type: 'research/getDetail',
          payload:{id:id}
        });
        dispatch({
          type: 'research/getHVAC',
          payload:{id:id},
          callback:function (data) {
            setLoading(true);
          }
        });
      }
    }, [id]);
    const name = (<p className="ant-descriptions-title">{researchName}</p>)
    const mainEngineCol = [
      {
        title: '型号',
        dataIndex: 'model',
        key: 'model',
      },
      {
        title: '数量',
        dataIndex: 'number',
        key: 'number',
      },
      {
        title: '额定制冷量/kW',
        dataIndex: 'ratedCoolingCapacity',
        key: 'ratedCoolingCapacity',
      },
      {
        title: '功率/kW',
        dataIndex: 'power',
        key: 'power',
      },
      {
        title: '额定制热量/kW',
        dataIndex: 'ratedHeatingCapacity',
        key: 'ratedHeatingCapacity',
      },
      {
        title: '功率/kW',
        dataIndex: 'power',
        key: 'power',
      },
      {
        title: '热回收量/kW',
        dataIndex: 'heatRecovery',
        key: 'heatRecovery',
      },
      {
        title: '厂商',
        dataIndex: 'manufacturer',
        key: 'manufacturer',
      },
      {
        title: '机组种类',
        dataIndex: 'unitType',
        key: 'unitType',
      },
    ];
    const waterPumpCol = [
      {
        title: '型号',
        dataIndex: 'model',
        key: 'model',
      },
      {
        title: '数量',
        dataIndex: 'number',
        key: 'number',
      },
      {
        title: '流量/(m³/h)',
        dataIndex: 'ratedFlow',
        key: 'ratedFlow',
      },
      {
        title: '扬程/m',
        dataIndex: 'lift',
        key: 'lift',
      },
      {
        title: '功率/kW',
        dataIndex: 'power',
        key: 'power',
      },
      {
        title: '转速/rpm',
        dataIndex: 'speed',
        key: 'speed',
      },
      {
        title: '效率/%',
        dataIndex: 'efficiency',
        key: 'efficiency',
      },
      {
        title: '厂商',
        dataIndex: 'manufacturer',
        key: 'manufacturer',
      },
      {
        title: '用途',
        dataIndex: 'purpose',
        key: 'purpose',
      },
    ];
    const coolingTowerCol = [
      {
        title: '型号',
        dataIndex: 'model',
        key: 'model',
      },
      {
        title: '数量',
        dataIndex: 'number',
        key: 'number',
      },
      {
        title: '冷却能力/(kcal/h)',
        dataIndex: 'coolingCapacity',
        key: 'coolingCapacity',
      },
      {
        title: '冷却水量/(m³/h)',
        dataIndex: 'coolingWaterQuantity',
        key: 'coolingWaterQuantity',
      },
      {
        title: '配套管径（进/出）/mm',
        dataIndex: 'matchingPipeDiameter',
        key: 'matchingPipeDiameter',
      },
      {
        title: '风机功率/kW',
        dataIndex: 'fanPower',
        key: 'fanPower',
      },
      {
        title: '厂商',
        dataIndex: 'manufacturer',
        key: 'manufacturer',
      },
      {
        title: '类型',
        dataIndex: 'type',
        key: 'type',
      },
    ];
    const airConditionBoxCol = [
      {
        title: '型号',
        dataIndex: 'model',
        key: 'model',
      },
      {
        title: '数量',
        dataIndex: 'number',
        key: 'number',
      },
      {
        title: '冷却能力/(kcal/h)',
        dataIndex: 'coolingCapacity',
        key: 'coolingCapacity',
      },
      {
        title: '冷却水量/(m³/h)',
        dataIndex: 'coolingWaterQuantity',
        key: 'coolingWaterQuantity',
      },
      {
        title: '配套管径（进/出）/mm',
        dataIndex: 'matchingPipeDiameter',
        key: 'matchingPipeDiameter',
      },
      {
        title: '风机功率/kW',
        dataIndex: 'fanPower',
        key: 'fanPower',
      },
      {
        title: '厂商',
        dataIndex: 'manufacturer',
        key: 'manufacturer',
      },
      {
        title: '类型',
        dataIndex: 'type',
        key: 'type',
      },
    ];
    const terminalEquipmentCol = [
      {
        title: '设备编号',
        dataIndex: 'model',
        key: 'model',
      },
      {
        title: '数量',
        dataIndex: 'number',
        key: 'number',
      },
      {
        title: '额定功率/kW',
        dataIndex: 'ratedPower',
        key: 'ratedPower',
      },
      {
        title: '额定风量/(m³/h)',
        dataIndex: 'ratedAirVolume',
        key: 'ratedAirVolume',
      },
      {
        title: '额定制冷量/kW',
        dataIndex: 'ratedCoolingCapacity',
        key: 'ratedCoolingCapacity',
      },
      {
        title: '额定制热量/kW',
        dataIndex: 'ratedHeatingCapacity',
        key: 'ratedHeatingCapacity',
      },
      {
        title: '其他',
        dataIndex: 'other',
        key: 'other',
      }
    ];
    const gutter = [{ xs: 8, sm: 16, md: 24, lg: 32 },{ xs: 8, sm: 16, md: 24, lg: 32 }];
    return(   
            <>
              {
              loading
              &&
                <PageHeaderWrapper
                  title={false}
                  content={name}
                  >
                  <Row gutter={gutter}>
                    <Col span={18}>
                      <Table size="small" dataSource={HVAC.mainEngine.data} columns={mainEngineCol} title={() => '主机设备信息'} pagination={false}/>
                    </Col>
                    <Col span={6}>
                      <img shape="square" src={HVAC.mainEngine.photo==='/'?nopic:HVAC.mainEngine.photo} style={{height:'200px',width:'200px'}}/>
                    </Col>
                  </Row>
                  
                  <Row gutter={gutter}>
                    <Col span={18}>
                      <Table size="small" dataSource={HVAC.waterPump.data} columns={waterPumpCol} title={() => '水泵（含热水泵）设备信息'} pagination={false}/>
                    </Col>
                    <Col span={6}>
                      <img shape="square" src={HVAC.waterPump.photo==='/'?nopic:HVAC.waterPump.photo} style={{height:'200px',width:'200px'}}/>
                    </Col>
                  </Row>

                  <Row gutter={gutter}>
                    <Col span={18}>
                      <Table size="small" dataSource={HVAC.coolingTower.data} columns={coolingTowerCol} title={() => '冷却塔（在辅楼屋顶）设备信息'} pagination={false}/>
                    </Col>
                    <Col span={6}>
                      <img shape="square" src={HVAC.coolingTower.photo==='/'?nopic:HVAC.coolingTower.photo} style={{height:'200px',width:'200px'}}/>
                    </Col>
                  </Row>

                  <Row gutter={gutter}>
                    <Col span={18}>
                      <Table size="small" dataSource={HVAC.airConditionBox.data} columns={airConditionBoxCol} title={() => '空调箱设备信息'} pagination={false}/>
                    </Col>
                    <Col span={6}>
                      <img shape="square" src={HVAC.airConditionBox.photo==='/'?nopic:HVAC.airConditionBox.photo} style={{height:'200px',width:'200px'}}/>
                    </Col>
                  </Row>

                  <Row gutter={gutter}>
                    <Col span={18}>
                      <Table size="small" dataSource={HVAC.terminalEquipment.data} columns={terminalEquipmentCol} title={() => '末端设备信息'} pagination={false}/>
                    </Col>
                    <Col span={6}>
                      <img shape="square" src={HVAC.terminalEquipment.photo==='/'?nopic:HVAC.terminalEquipment.photo} style={{height:'200px',width:'200px'}}/>
                    </Col>
                  </Row>

                </PageHeaderWrapper>
              }
            </>
      )
  
}

export default connect(({ research, loading }) => ({
  HVAC:research.HVAC,
  researchName:research.detail.name,
  loading
}))(HVAC);