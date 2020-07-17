import React, { useState, useRef,useEffect } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { PlusOutlined } from '@ant-design/icons';
import { connect } from 'umi';
import { Radio } from 'antd';
import ProTable from '@ant-design/pro-table';
import { getActionPage,getActionPageByUserId } from '@/services/log';
import { getAuthority } from '@/utils/authority';
const log = [
  { label: '全部', value: '-1' },
  { label: '登入/登出', value: '0' },
  { label: '删除', value: '1' },
  { label: '上传', value: '3' }
];

const paginationProps = {
  current: 1,
  pageSize: 10
}

  const Log = (props) => {
    const {dispatch,currentUser} = props;
    /**
     * constructor
     */

    // useEffect(() => {
    //   if (dispatch) {
    //     dispatch({
    //       type: 'log/fetchCurrent',
    //     });
    //   }
    // }, []);
    
    const actionRef = useRef();
    const fetchData =async (params, sort, filter) =>{
      const anthority = getAuthority();
      let res;
      if(anthority[0] == 'USER'){
        res =await getActionPageByUserId({number:params.current,size:params.pageSize,actionType:params.actionDesc|| -1,userId:currentUser.id,start:params.actionTime?params.actionTime[0]:'',end:params.actionTime?params.actionTime[1]:''});
      }else{
        res =await getActionPage({number:params.current,size:params.pageSize,actionType:params.actionDesc|| -1,start:params.actionTime?params.actionTime[0]:'',end:params.actionTime?params.actionTime[1]:''});
      }
      
      return {
        data:res.result,
        current: 1,
        total:res.size,
        pageSize:20
      }
    }
    const columns = [
      {
        title: '操作类型',
        dataIndex: 'actionDesc',
        // valueType:'option',
        renderFormItem: (_, { type, defaultRender,onChange,...rest }, form) => {
          if (type === 'form') {
            return null;
          }
          const status = form.getFieldValue('state');
          if (status !== 'open') {
            return  <Radio.Group options={log} {...rest} defaultValue="-1" buttonStyle="solid" onChange={onChange}></Radio.Group>
          }
          return defaultRender(_);
        }
      },
      {
        title: '操作人',
        dataIndex: 'userName',
        hideInSearch:'true'
      },
      {
        title: '操作时间',
        dataIndex: 'actionTime',
        valueType:'dateRange',
      },
    ];
    return (
      <PageHeaderWrapper>
        <ProTable
          headerTitle="查询表格"
          actionRef={actionRef}
          rowKey="actionDesc"
          options={false}
          request={fetchData}
          columns={columns}
          pagination={paginationProps}
          // rowSelection={{}}
        />
      </PageHeaderWrapper>
    );
  };
  
  export default connect(({ user,log }) => ({
    log,
    currentUser: user.currentUser,
  }))(Log);

