import React ,{useEffect }from 'react';
import { Row, Col ,Descriptions,Avatar } from 'antd';
import { connect,useParams } from 'umi';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import EchartItem from '../../components/EchartItem/echartItem';
import Map from './components/map'
const energySavingStandard = ['不执行节能标准','50%','65%','75%以上','未知'];
const energySavingTransformationOrNot = ['是','否','未知'];
const gbes = ['0星','1星','2星','3星','未知'];
const coolingMode = ['集中供冷','分户供冷','无供冷','未知'];
const heatingMode = ['集中供暖', '分户采暖',  '无采暖', '未知'];
const whetherToUseRenewableResources =['否','浅层地热能', '太阳能', '未知'];

const proDetail = props => {
    const { dispatch } =props;
    const params = useParams()
    const { id } = params;
    useEffect(() => {
        if (dispatch) {
          dispatch({
            type: 'projectManage/getProjectDetail',
            payload:{projectId:id}
          });
        }
      }, []);
    const detail = props.detail;
    const extra = (
      <Avatar shape="square" size={150} src={detail.imgUrl} />
    );
    const description = (
                  <Descriptions
                  title={detail.name}
                >
                  {/* <Descriptions.Item label="项目名称">{detail.name}</Descriptions.Item> */}
                  <Descriptions.Item label="地址" span={3}>{detail.province||''}{detail.city||''}{detail.district||''}{detail.street||''}{detail.address||''}</Descriptions.Item>
                  <Descriptions.Item label="建筑类型">{detail.architecturalType}</Descriptions.Item>
                  <Descriptions.Item label="建成时间">{detail.builtTime}</Descriptions.Item>
                  <Descriptions.Item label="绿建星级">{gbes[detail.gbes]}</Descriptions.Item>
                  <Descriptions.Item label="节能标准">{energySavingStandard[detail.energySavingStandard]}</Descriptions.Item>
                  <Descriptions.Item label="是否经过节能改造">{energySavingTransformationOrNot[detail.energySavingTransformationOrNot]}</Descriptions.Item>
                  <Descriptions.Item label="供冷方式">{coolingMode[detail.coolingMode]}</Descriptions.Item>
                  <Descriptions.Item label="供暖方式">{heatingMode[detail.heatingMode]}</Descriptions.Item>
                  <Descriptions.Item label="是否利用可再生能源">{whetherToUseRenewableResources[detail.whetherToUseRenewableResources]}</Descriptions.Item>
                </Descriptions>
      );
    return(
      <PageHeaderWrapper
        title="项目详情"
        content={description}
        extraContent={extra}
        >
          <Row gutter={{ xs: 8, sm: 16, md: 24, lg: 32 }}>
            <Col span={12}>
              <Map></Map>
            </Col>
            <Col span={12}>
              <div> 
                <EchartItem name = "电耗趋势/按月" format = 'YYYY/MM' echartId = "powerConsumptionPerUnitArea1" dataType="电" timeType="月"/>
                {/* <EchartItem name = "气耗趋势/按月" format = 'YYYY/MM' echartId = "gasConsumptionPerUnitArea1" dataType="气" timeType="月"/>
                <EchartItem name = "水耗趋势/按月" format = 'YYYY/MM' echartId = "waterConsumptionPerUnitArea1" dataType="水" timeType="月"/> */}
              </div>
            </Col>
          </Row>
        </PageHeaderWrapper>
      )
  
}

export default connect(({ projectManage, loading }) => ({
    detail:projectManage.detail,
    loading
  }))(proDetail);