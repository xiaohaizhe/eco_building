import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { history } from 'umi';
import { Row,Col,Select ,Spin  } from 'antd';
import { connect } from 'umi';
import Map from './components/map.jsx'
import Treemap from './components/treemap.jsx'
import './index.less'
const { Option } = Select;


class overview extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: undefined,
      data:[],
      fetching: false,
      searchName:''
    };
    this.lastFetchId = 0;
  }

  handleSearch = value => {
    let that = this;
    console.log('fetching user', value);
    this.lastFetchId += 1;
    const fetchId = this.lastFetchId;
    this.setState({ data: [], fetching: true ,searchName:value});
    const { dispatch} = this.props;
    dispatch({
      type: 'projectManage/getProjectPage',
      payload:{number:1,size:10,name:value},
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
    if(value && value!='more'){
      //跳转详情或者更多
      history.push({
        pathname: '/proDetail/'+value
      })
    }else{
      //跳转更多
      history.push({
        pathname: '/more/'+this.state.searchName
      })
    }
    
    
  };

  render(){
    const { fetching, data, value } = this.state;
    
    return (
      <PageHeaderWrapper>
        <Select
          showSearch
          value={value}
          placeholder={'请输入项目名称'}
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
          {data.length>0 && <Option style={{color:'#26B99A'}} key="more" value="more">更多</Option>}
        </Select>
        <Row gutter={{ xs: 8, sm: 16, md: 24, lg: 32 }}>
          <Col span={12}>
            <Map ></Map>
          </Col>
          <Col span={12}>
            <div>
              <Treemap></Treemap>
            </div>
          </Col>
        </Row>
      </PageHeaderWrapper>
    )
    
  }
}

export default connect(({ projectManage, loading }) => ({
  projectManage,
  loading
}))(overview);
