import React, { useState, useRef, useEffect} from 'react';
import { Row, Col ,Descriptions,Card,Avatar,Button } from 'antd';
import { connect,useParams,history } from 'umi';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import nopic from '../../../assets/nopic.png'
import '../index.less'

const researchDetail = props => {
    const { dispatch ,detail } =props;
    const params = useParams()
    const { id } = params;
    const [ loading, setLoading] = useState(false);
    const [ timeType, setTimeType] = useState("月");
    const [ format, setFormat] = useState("YYYY-MM");
    useEffect(() => {
      if (dispatch) {
        dispatch({
          type: 'research/getDetail',
          payload:{id:id},
          callback:function (data) {
            setLoading(true);
          }
        });
      }
    }, [id]);
      
    // const toEdit =()=>{
    //   history.push('/projectManage/edit/'+id)
    // }
    
    const description = (
      <div style={{display:'flex'}}>
        <Descriptions title={detail.name?detail.name:""} style={{width:'75%'}}>
          <Descriptions.Item label="地址" span={3}>{detail.province?detail.province:''}{detail.city?detail.city:''}{detail.district?detail.district:''}<span style={{marginLeft:'8px'}}>{detail.address?detail.address:''}</span></Descriptions.Item>
          <Descriptions.Item label="建设单位">{detail.constractor?detail.constractor:''}</Descriptions.Item>
          <Descriptions.Item label="总建筑面积">{detail.totalArea?detail.totalArea:''}</Descriptions.Item>
          <Descriptions.Item label="有无楼宇自动控制">{detail.hasBas?'是':'否'}</Descriptions.Item>   
          <Descriptions.Item label="设计单位">{detail.designer?detail.designer:''}</Descriptions.Item>    
          <Descriptions.Item label="地上建筑面积">{detail.abovegroundArea?detail.abovegroundArea:''}</Descriptions.Item>
          <Descriptions.Item label="有无能耗计量/远传电表数据">{detail.hasEnergyonsumptionMeasurement?'是':'否'}</Descriptions.Item>
          <Descriptions.Item label="施工单位">{detail.constructor?detail.constructor:''}</Descriptions.Item>
          <Descriptions.Item label="地下建筑面积">{detail.undergroundArea?detail.undergroundArea:''}</Descriptions.Item>
          <Descriptions.Item label="有无用水计量/远传水表数据">{detail.hasWaterMetering?'是':'否'}</Descriptions.Item>
          <Descriptions.Item label="监理单位">{detail.supervisor?detail.supervisor:''}</Descriptions.Item>
          <Descriptions.Item label="空调面积">{detail.airConditionArea?detail.airConditionArea:''}</Descriptions.Item>
          <Descriptions.Item label="窗户/幕墙是否可开启">{detail.windowIsOpen?'是':'否'}</Descriptions.Item>
          <Descriptions.Item label="建成时间">{detail.builtTime?detail.builtTime:''}</Descriptions.Item>
          <Descriptions.Item label="地上建筑层数">{detail.abovegroundFloor?detail.abovegroundFloor:''}</Descriptions.Item>
          <Descriptions.Item label="空调系统形式">{detail.airConditioningSystem?detail.airConditioningSystem:''}</Descriptions.Item>
          <Descriptions.Item label="建筑楼栋信息">{detail.buildingInfo?detail.buildingInfo:''}</Descriptions.Item>
          <Descriptions.Item label="地下建筑层数">{detail.undergroundFloor?detail.undergroundFloor:''}</Descriptions.Item>
          <Descriptions.Item label="新风系统形式">{detail.cDOAS?detail.cDOAS:''}</Descriptions.Item>
          <Descriptions.Item label="建筑高度">{detail.height?detail.height:''}</Descriptions.Item>
          <Descriptions.Item label="主要遮阳方式">{detail.sunshade?detail.sunshade:''}</Descriptions.Item>
          <Descriptions.Item label="末端用户数量">{detail.numberOfEndUsers?detail.numberOfEndUsers:''}</Descriptions.Item>
        </Descriptions>
        <div className="detailPic" style={{width:'25%',height:'355px',background:`url(${detail.overallPhotoUrl?detail.overallPhotoUrl:nopic}) no-repeat center`,backgroundSize:'cover'}}></div>
        {/* <img shape="square" style={{width:'25%',height:'240px'}} src={detail.imgUrl?detail.imgUrl:nopic} /> */}
      </div>
      
    
    );
    const gutter = [{ xs: 8, sm: 16, md: 24, lg: 32 },{ xs: 8, sm: 16, md: 24, lg: 32 }];
    return(   
            <>
              {
              loading
              &&
                <PageHeaderWrapper
                  title={false}
                  content={description}
                  extraContent={false}
                  >
                    <Row gutter={gutter}>
                      <Col span={12}>
                        <Card
                            title='项目暖通设备概况及信息'
                            bordered={false}
                            bodyStyle={{
                                padding: '20px'
                            }} 
                        >
                          <p>{detail.overviewOfHVACEquipment?detail.overviewOfHVACEquipment:''}</p>
                          <p className="moreInfo" onClick={()=>{history.push('/research/detail/'+detail.id+'/HVAC')}}>更多信息</p>
                        </Card>
                      </Col>
                      <Col span={12}>
                      <Card
                            title='项目电气设备概况及信息'
                            bordered={false}
                            bodyStyle={{
                                padding: '20px'
                            }} 
                        >
                          <p>{detail.overviewOfElectricalEquipment?detail.overviewOfElectricalEquipment:''}</p>
                          <p className="moreInfo" onClick={()=>{history.push('/research/detail/'+detail.id+'/electrical')}}>更多信息</p>
                        </Card>
                      </Col>
                    </Row>
                    <Row gutter={gutter}>
                      <Col span={12}>
                        <Card
                            title='项目给排水系统概况'
                            bordered={false}
                            bodyStyle={{
                                padding: '20px'
                            }} 
                        >
                          <p>{detail.overviewOfWaterSupplyAndDrainageSystem?detail.overviewOfWaterSupplyAndDrainageSystem:''}</p>
                        </Card>
                      </Col>
                      <Col span={12}>
                      <Card
                            title='项目近三年能耗数据'
                            bordered={false}
                            bodyStyle={{
                                padding: '20px'
                            }} 
                        >
                          <p>{detail.energyConsumptionDataInRecentThreeYears?detail.energyConsumptionDataInRecentThreeYears:''}</p>
                          <p className="moreInfo" onClick={()=>{history.push('/research/detail/'+detail.id+'/energyData')}}>更多信息</p>
                        </Card>
                      </Col>
                    </Row>
                    <Row gutter={gutter}>
                      <Col span={24}>
                        <Card
                              title='本项目改造建议'
                              bordered={false}
                              bodyStyle={{
                                  padding: '20px'
                              }} 
                          >
                            <p>{detail.transformationSuggestions?detail.transformationSuggestions:''}</p>
                          </Card>
                      </Col>
                    </Row>
                </PageHeaderWrapper>
              }
            </>
      )
  
}

export default connect(({ research, loading }) => ({
  detail:research.detail,
  loading
}))(researchDetail);