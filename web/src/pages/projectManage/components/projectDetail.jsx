import React ,{useEffect }from 'react';
import { Row, Col ,Descriptions,Avatar,Button } from 'antd';
import { connect,useParams,history } from 'umi';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import EchartItem from '@/components/EchartItem/echartItem';
import Map from '../../proDetail/components/map'
import nopic from '../../../assets/nopic.png'
const energySavingStandard = ['不执行节能标准','50%','65%','75%以上','未知'];
const energySavingTransformationOrNot = ['是','否','未知'];
const gbes = ['0星','1星','2星','3星','未知'];
const coolingMode = ['集中供冷','分户供冷','无供冷','未知'];
const heatingMode = ['集中供暖', '分户采暖',  '无采暖', '未知'];
const whetherToUseRenewableResources =['否','浅层地热能', '太阳能', '未知'];

const projectDetail = props => {
    const { dispatch ,detail } =props;
    const params = useParams()
    const { id } = params;

    useEffect(() => {
        if (dispatch) {
          dispatch({
            type: 'projectManage/getProjectDetail',
            payload:{projectId:id}
          });
        }
      }, [id]);
      
    const extra = (
      <Avatar shape="square" size={150} src={detail.imgUrl?detail.imgUrl:nopic} />
    );
    // const onBack = ()=>{
    //   history.goBack();
    // }
    // const toEdit =()=>{
    //   history.push('/projectManage/edit/'+id)
    // }
    
    const description = (
      <Descriptions title={detail.name?detail.name:""}>
        <Descriptions.Item label="地址" span={3}>{detail.province?detail.province:''}{detail.city?detail.city:''}{detail.district?detail.district:''}{detail.street?detail.street:''}<span style={{marginLeft:'8px'}}>{detail.address?detail.address:''}</span></Descriptions.Item>
        <Descriptions.Item label="建筑面积">{detail.area?detail.area:''}㎡</Descriptions.Item>
        <Descriptions.Item label="层数">{detail.floor?detail.floor:''}</Descriptions.Item>
        <Descriptions.Item label="建成时间">{detail.builtTime?detail.builtTime:''}</Descriptions.Item>
        <Descriptions.Item label="建筑类型">{detail.architecturalType?detail.architecturalType:''}</Descriptions.Item>
        <Descriptions.Item label="绿建星级">{gbes[detail.gbes?detail.gbes:0]}</Descriptions.Item>
        <Descriptions.Item label="节能标准">{energySavingStandard[detail.energySavingStandard?detail.energySavingStandard:0]}</Descriptions.Item>
        <Descriptions.Item label="是否经过节能改造">{energySavingTransformationOrNot[detail.energySavingTransformationOrNot?detail.energySavingTransformationOrNot:0]}</Descriptions.Item>
        <Descriptions.Item label="供冷方式">{coolingMode[detail.coolingMode?detail.coolingMode:0]}</Descriptions.Item>
        <Descriptions.Item label="供暖方式">{heatingMode[detail.heatingMode?detail.heatingMode:0]}</Descriptions.Item>
        <Descriptions.Item label="是否利用可再生能源">{whetherToUseRenewableResources[detail.whetherToUseRenewableResources?detail.whetherToUseRenewableResources:0]}</Descriptions.Item>
    </Descriptions>
    );
    const gutter = [16];
    return(
      <PageHeaderWrapper
        title={false}
        content={description}
        extraContent={extra}
        >
          <Row gutter={gutter}>
              <Col span={12}>
                <Map/>
              </Col>
              <Col span={12}>
                  <EchartItem name = "电耗趋势/按月" format = 'YYYY-MM' echartId = "power1" dataType="电" timeType="月"/>
              </Col>
          </Row>
          <Row gutter={gutter}>
              <Col span={12}>                
                  <EchartItem name = "气耗趋势/按月" format = 'YYYY-MM' echartId = "gas1" dataType="气" timeType="月"/>
              </Col>
              <Col span={12}>
                  <EchartItem name = "水耗趋势/按月" format = 'YYYY-MM' echartId = "water1" dataType="水" timeType="月"/>
              </Col>
          </Row>
          {/* <Row gutter={gutter} style={{justifyContent: 'center'}}>
              <Button type="primary"  onClick={toEdit} style={{marginRight:'20px'}}>
              编辑
              </Button>
              <Button onClick={onBack}>
              返回
              </Button>
          </Row>  */}
        </PageHeaderWrapper>
      )
  
}

export default connect(({ projectManage, loading }) => ({
  detail:projectManage.detail,
  loading
}))(projectDetail);