import React, { useState, useRef, useEffect} from 'react';
import { Row, Table,Col ,Descriptions,Card,Avatar,Button } from 'antd';
import { connect,useParams,history } from 'umi';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import nopic from '../../../assets/nopic.png'
import '../index.less'

const Electrical = props => {
    const { dispatch ,electrical,researchName } =props;
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
    const name = (<p className="ant-descriptions-title">{researchName}</p>)
    const electricalCol = [
      {
        title: '名称',
        dataIndex: 'name',
        key: 'name',
      },
      {
        title: '种类',
        dataIndex: 'type',
        key: 'type',
      },
      {
        title: '额定功率/W',
        dataIndex: 'ratedPower',
        key: 'ratedPower',
      },
      {
        title: '安装方式',
        dataIndex: 'installationMode',
        key: 'installationMode',
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
                  <Row style={{margin:'10px 0'}}>
                      <Table style={{width:'100%'}}dataSource={electrical.lightingEquipment} columns={electricalCol} title={() => '照明设备'} pagination={false}/>
                  </Row>
                  <Row style={{margin:'10px 0'}}>
                      <Table style={{width:'100%'}} dataSource={electrical.electricalLoad} columns={electricalCol} title={() => '基本电器负载设备'} pagination={false}/>
                  </Row>
                </PageHeaderWrapper>
              }
            </>
      )
  
}

export default connect(({ research, loading }) => ({
  electrical:research.electrical,
  researchName:research.detail.name,
  loading
}))(Electrical);