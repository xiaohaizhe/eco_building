import React, { useState, useRef, useEffect} from 'react';
import { getUserPage } from '@/services/userManage';
import OperationModal from './components/OperationModal';
import { Link, connect } from 'umi';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { PlusOutlined } from '@ant-design/icons';
import { Button, Divider, Dropdown, Menu, message, Input,Modal } from 'antd';
import ProTable from '@ant-design/pro-table';
import { isUser } from '@/utils/authority';

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
    const { dispatch } = props;
    const [done, setDone] = useState(false);
    const [visible, setVisible] = useState(false);
    const [current, setCurrent] = useState(undefined);
    useEffect(() => {
      isUser();
    }, []);
    
    const showModal = () => {
      setVisible(true);
      setCurrent(undefined);
    };
    const showEditModal = item => {
      setVisible(true);
      setCurrent(item);
    };
    const deleteUser = (id) => {
        let temp = new FormData();
        temp.append('userId',id)
        Modal.confirm({
          title: '删除用户',
          content: '确定删除该用户吗？',
          okText: '确认',
          cancelText: '取消',
          onOk: () => dispatch({
            type: 'userManage/deleteUser',
            payload: temp,
            callback: (res) => {
              if (res.code==0) {
                message.success('删除成功');
                if (actionRef.current) {
                  actionRef.current.reload();
                }
              }else{
                message.success('删除失败');
              }
            }
          })
        });
    };
    const handleDone = () => {
      setDone(false);
      setVisible(false);
    };
    
    const handleCancel = () => {
      setVisible(false);
    };
    
    const handleSubmit = values => {
      const id = current ? current.id : '';
      setDone(true);
      dispatch({
        type: 'userManage/submit',
        payload: {
          id,
          ...values,
        },
        callback: (res) => {
          if (res.code==0) {
            setVisible(false);
            message.success('操作成功');
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }else{
            message.success('操作失败');
          }
        }
      });
    };
    const columns = [
      {
        title: '用户名',
        dataIndex: 'username',
        hideInSearch:'true'
      },
      {
        title: '操作',
        dataIndex: 'option',
        width: 150,
        valueType: 'option',
        render: (_, record) => (
          <>
            <a
              key="edit"
              onClick={e => {
                e.preventDefault();
                showEditModal(record);
              }}
            >
              编辑
            </a>
            <Divider type="vertical" />
            <a
              key="delete"
              onClick={e => {
                e.preventDefault();
                deleteUser(record.id);
              }}
            >
              删除
            </a>
          </>
        ),
      },
    ];
    const paginationProps = {
      current: 1,
      pageSize: 10
    }
    return (
      
      <PageHeaderWrapper title={false}>
        <ProTable
          headerTitle="用户表格"
          actionRef={actionRef}
          rowKey="key"
          options={false} 
          search={false}
          pagination={paginationProps}
          toolBarRender={(action, { selectedRows }) => [
            <Button type="primary" onClick={showModal}>
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
          rowSelection={false}
        />
        <OperationModal
          done={done}
          current={current}
          visible={visible}
          onDone={handleDone}
          onCancel={handleCancel}
          onSubmit={handleSubmit}
          
        />
      </PageHeaderWrapper>
    );
  };
  
  export default connect(({ userManage, loading }) => ({
    userManage,
    loading: loading.models.userManage,
  }))(userManage);