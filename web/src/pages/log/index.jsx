import React, { useState, useRef } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { PlusOutlined } from '@ant-design/icons';
import { Button, Divider, Dropdown, Menu, message, Input } from 'antd';
import ProTable from '@ant-design/pro-table';
import { queryRule, updateRule, addRule, removeRule } from '../../pages/ListTableList/service';

const Log = () => {
    const actionRef = useRef();
    const columns = [
      {
        title: '操作类型',
        dataIndex: 'type',
        // valueType:'option',
        valueEnum:{
                  0: {
                    text: '登入',
                    status: '0',
                  },
                  1: {
                    text: '登出',
                    status: '1',
                  },
                  2: {
                    text: '上传',
                    status: '2',
                  },
                  3: {
                    text: '删除',
                    status: '3',
                  },
                  4: {
                    text: '全部',
                    status: '4',
                  }
               },
      },
      {
        title: '操作人',
        dataIndex: 'name',
        hideInSearch:'true'
      },
      {
        title: '操作时间',
        dataIndex: 'time',
        valueType:'dateTimeRange',
      },
    ];
    return (
      <PageHeaderWrapper>
        <ProTable
          headerTitle="查询表格"
          actionRef={actionRef}
          rowKey="key"
          options={false}
          request={(params, sorter, filter) => queryRule({ ...params, sorter, filter })}
          columns={columns}
          // rowSelection={{}}
        />
      </PageHeaderWrapper>
    );
  };
  
  export default Log;

