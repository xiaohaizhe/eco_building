import React, { useState, useRef, useEffect} from 'react';
import { Row, Table,Col ,Descriptions,Card,Avatar,Button } from 'antd';
import { connect,useParams,history } from 'umi';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import EchartData from '@/components/EchartItem/echartData';
import '../index.less'

const EnergyData = props => {
    const { dispatch ,researchName } =props;
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
          type: 'research/getElectrical',
          payload:{id:id},
          callback:function (data) {
            setLoading(true);
          }
        });
      }
    }, [id]);
    const gutter = [16,16];
    const name = (<p className="ant-descriptions-title">{researchName}</p>)
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
                      <Col span={12}>                
                          <EchartData name = "用电量" format = {'YYYY-MM'} echartId = "energy" dataType="电" timeType={'月'}/>
                      </Col>
                      <Col span={12}>
                          <EchartData name = "用水量" format = {'YYYY-MM'} echartId = "water" dataType="水" timeType={'月'}/>
                      </Col>
                  </Row>
                  <Row gutter={gutter}>
                      <Col span={12}>                
                          <EchartData name = "用气量" format = {'YYYY-MM'} echartId = "gas" dataType="气" timeType={'月'}/>
                      </Col>
                      <Col span={12}>
                          <EchartData name = "用热量" format = {'YYYY-MM'} echartId = "heat" dataType="热" timeType={'月'}/>
                      </Col>
                  </Row>
                </PageHeaderWrapper>
              }
            </>
      )
  
}

export default connect(({ research, loading }) => ({
  researchName:research.detail.name,
  loading
}))(EnergyData);