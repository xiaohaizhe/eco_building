import React, { useState, useRef, useEffect} from 'react';
import { getList } from '@/services/research';
import { Link, connect, history } from 'umi';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { PlusOutlined } from '@ant-design/icons';
import {Card, Button, Divider, Cascader, Menu, message, Input,Modal, Spin } from 'antd';
import ProTable from '@ant-design/pro-table';

const fetchData =async (params, sort, filter) =>{
  const res =await getList({name:'',isItSuggestedToTransform:true});
  return {
    data:res.result
  }
}

const researchTable = props => {
    const actionRef = useRef();
    const { dispatch,loading } = props;
    
    const toDetail = id => {
      history.push({
        pathname: `/research/detail/${id}`,
      });
    };

    const reload = ()=>{
      if (actionRef.current) {
        actionRef.current.reload();
      }
    }
    props.onRef(reload);
    const columns = [
      {
        title: '建议改造项目名称',
        dataIndex: 'name',
        render: (_, record) => (
          <p
            key="detail"
            onClick={e => {
              e.preventDefault();
              toDetail(record.id);
            }}
            style={{cursor: "pointer",minWidth:'200px'}}
          >
            {record.name}
          </p>
        )
      },
      {
        title: '改造建议',
        dataIndex: 'transformationSuggestions',
      },
    ];
    return (<Card
        // loading={loading}
        title="建议改造项目及改造建议"
        bordered={false}
        bodyStyle={{
        padding: 0,
        }}
        style={{height: `${window.innerHeight-200}px`}}
      >
          <ProTable
                actionRef={actionRef}
                rowKey="id"
                options={false} 
                search={false}
                pagination={false}
                toolBarRender={false}
                request={fetchData}
                columns={columns}
                rowSelection={false}
              />
        </Card>      
        
    );
  };
  
  export default connect(({ research, loading }) => ({
    research,
    loading
  }))(researchTable);