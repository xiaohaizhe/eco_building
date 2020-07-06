import React, { useState, useRef, useEffect} from 'react';
import { getUserPage } from '@/services/userManage';


import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { PlusOutlined } from '@ant-design/icons';
import { Button, Divider, Dropdown, Menu, message, Input } from 'antd';
import ProTable from '@ant-design/pro-table';
import { queryRule, updateRule, addRule, removeRule } from '../../pages/ListTableList/service';

const fetchData =async (params, sort, filter) =>{
  const res =await getUserPage({number:params.current,size:params.pageSize});
  return {
    data:res.result,
    current: 1,
    total:res.size,
    pageSize:20
  }
}

const userManage = props => {
    const actionRef = useRef();
    const columns = [
      {
        title: '用户名',
        dataIndex: 'username',
        hideInSearch:'true'
      },
      {
        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        render: (_, record) => (
          <>
            <a
              onClick={() => {
                handleUpdateModalVisible(true);
                setStepFormValues(record);
              }}
            >
              编辑
            </a>
            <Divider type="vertical" />
            <a href="">删除</a>
          </>
        ),
      },
    ];
    return (
      <PageHeaderWrapper>
        <ProTable
          headerTitle="用户表格"
          actionRef={actionRef}
          rowKey="key"
          options={false} 
          search={false}
          toolBarRender={(action, { selectedRows }) => [
            <Button type="primary" onClick={() => handleModalVisible(true)}>
              <PlusOutlined /> 新建
            </Button>,
            selectedRows && selectedRows.length > 0 && (
                <Button>
                批量删除
                </Button>
            ),
          ]}
          tableAlertRender={({ selectedRowKeys, selectedRows }) => (
            <div>
              已选择{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowKeys.length}
              </a>{' '}
              项&nbsp;&nbsp;
              {/* <span>
                服务调用次数总计 {selectedRows.reduce((pre, item) => pre + item.callNo, 0)} 万
              </span> */}
            </div>
          )}
          request={fetchData}
          columns={columns}
          rowSelection={{}}
        />
      </PageHeaderWrapper>
    );
  };
  
  export default userManage;