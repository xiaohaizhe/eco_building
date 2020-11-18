import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { history } from 'umi';
import { Row,Col,Select ,Spin,Button  } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { connect } from 'umi';
import Map from './components/map.jsx'
import ResearchTable from './components/researchTable.jsx'
import ImportModal from './components/import';
import './index.less'
const { Option } = Select;


class research extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: undefined,
      data:[],
      fetching: false,
      searchName:'',
      visible:false
    };
    this.lastFetchId = 0;
  }
  handleCancel = () => {
    this.setState({visible:false});
    
  };
  handleSubmit = values => {
    this.setState({visible:false});
    this.child()
  };
  handleSearch = value => {
    let that = this;
    console.log('fetching user', value);
    this.lastFetchId += 1;
    const fetchId = this.lastFetchId;
    this.setState({ data: [], fetching: true ,searchName:value});
    const { dispatch } = this.props;
    dispatch({
      type: 'research/getSearchData',
      payload:{name:value},
      callback:function(res){
        if (fetchId !== that.lastFetchId) {
          // for fetch callback order
          return;
        }
        if(res){
          that.setState({ data:res,fetching: false});
        }
        
      }
    })
  };
  handleChange = value => {
    history.push({
      pathname: '/research/detail/'+value,
    })
  };
  showModal = value => {
    this.setState({visible:true})
  }
  render(){
    const { fetching, data, value } = this.state;
    return (
      <PageHeaderWrapper title={false} key="research" className="research">
        <Row className="top">
          <Select
            showSearch
            value={value}
            placeholder={'请输入调研项目名称'}
            className="searchAll"
            defaultActiveFirstOption={false}
            showArrow={false}
            filterOption={false}
            onSearch={(e)=>this.handleSearch(e)}
            size="large"
            onChange={this.handleChange}
            notFoundContent={fetching ? <Spin size="small" /> : null}
          >
            {data.map(d => <Option key={d.id}>{d.name}</Option>)}
          </Select>
          <Button type="primary" onClick={()=>this.showModal()}>
              <PlusOutlined /> 批量导入调研项目
            </Button>
        </Row>
        
        <Row gutter={{ xs: 8, sm: 16, md: 24, lg: 32 }}>
          <Col span={10}>
            <Map></Map>
          </Col>
          <Col span={14}>
            <ResearchTable onRef={ref=>this.child=ref}></ResearchTable>
          </Col>
        </Row>
        <ImportModal
            // current={current}
            visible={this.state.visible}
            onCancel={()=>this.handleCancel()}
            onSubmit={()=>this.handleSubmit()}
          />
      </PageHeaderWrapper>
    )
    
  }
}

export default connect(({ projectManage, loading }) => ({
  projectManage,
  loading
}))(research);
