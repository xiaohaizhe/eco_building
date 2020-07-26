import React, { useState, useRef, useEffect} from 'react';
import { getProjectPage } from '@/services/projectManage';
import ImportModal from './components/import';
import { Link, connect, history } from 'umi';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { PlusOutlined } from '@ant-design/icons';
import { Button, Divider, Cascader, Menu, message, Input,Modal, Spin } from 'antd';
import ProTable from '@ant-design/pro-table';
import { isUser,getAuthority } from '@/utils/authority';

const fetchData =async (params, sort, filter) =>{
  if(params.address){
    if(params.address.length==3){
      params.province = params.address[0];
      params.city = params.address[1];
      params.district = params.address[2];
    }else if(params.address.length==2){
      params.province = params.address[0];
      params.city = params.address[1];
    }else if(params.address.length==1){
      params.province = params.address[0];
    }
  }
  const res =await getProjectPage({...params,number:params.current,size:params.pageSize});
  return {
    data:res.result,
    current: 1,
    total:res.size,
    pageSize:10
  }
}

const projectManage = props => {
    const actionRef = useRef();
    const { dispatch,loading } = props;
    const anthority = getAuthority();
    const [done, setDone] = useState(false);
    const [visible, setVisible] = useState(false);
    const [current, setCurrent] = useState(undefined);
    const address = props.address;
    useEffect(() => {
      isUser();
      if (dispatch) {
        dispatch({
          type: 'display/getAddressOnMap',
        });
      }
    }, []);
    
    const showModal = () => {
      setVisible(true);
      setCurrent(undefined);
    };

    const toEdit = id => {
      history.push({
        pathname: `/projectManage/edit/${id}`,
      });
    };

    const toDetail = id => {
      history.push({
        pathname: `/projectManage/detail/${id}`,
      });
    };

    const deleteProject = (id) => {
        let temp = new FormData();
        temp.append('id',id)
        Modal.confirm({
          title: '删除用户',
          content: '确定删除该项目吗？',
          okText: '确认',
          cancelText: '取消',
          onOk: () => dispatch({
            type: 'projectManage/deleteProject',
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
    const handleCancel = () => {
      setVisible(false);
    };
    
    const handleSubmit = values => {
      setVisible(false);
      if (actionRef.current) {
          actionRef.current.reload();
        }
    };

    const columns = [
      {
        title: '项目名称',
        dataIndex: 'name',
        render: (_, record) => (
          <span
            key="detail"
            onClick={e => {
              e.preventDefault();
              toDetail(record.id);
            }}
            style={{cursor: "pointer"}}
          >
            {record.name}
          </span>
        )
      },
      {
        title: '建筑类型',
        dataIndex: 'architecturalType',
        valueEnum: {
          '办公': '办公',
          '商场': '商场',
          '文化教育': '文化教育',
          '餐饮': '餐饮',
          '医院': '医院',
          '酒店': '酒店',
          '其他': '其他'
        },
      },
      {
        title: '地址',
        dataIndex: 'address',
        // valueType:'option',
        renderFormItem: (_, { type, defaultRender,onChange,...rest }, form) => {
          
          if (type === 'form') {
            return null;
          }
          const status = form.getFieldValue('state');
          if (status !== 'open') {
            return  <Cascader options={address} onChange={onChange} changeOnSelect placeholder="请选择行政区划" />
          }
          return defaultRender(_);
        }
      },
      {
        title: '最后修改时间',
        dataIndex: 'lastModified',
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
                toEdit(record.id);
              }}
            >
              编辑
            </a>
            {anthority[0] == 'ADMIN' && <span>
            <Divider type="vertical" />
            <a
              key="delete"
              onClick={e => {
                e.preventDefault();
                deleteProject(record.id);
              }}
            >
              删除
            </a></span>}
            
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
          headerTitle="项目管理"
          actionRef={actionRef}
          rowKey="id"
          options={false} 
          // search={true}
          pagination={paginationProps}
          toolBarRender={
            (action, { selectedRows }) => [
            <Button type="primary" onClick={showModal}>
              <PlusOutlined /> 批量导入
            </Button>,
            // selectedRows && selectedRows.length > 0 && (
            //     <Button>
            //     批量删除
            //     </Button>
            // ),
          ]}
          // tableAlertRender={({ selectedRowKeys, selectedRows }) => (
          //   <div>
          //     已选择{' '}
          //     <a
          //       style={{
          //         fontWeight: 600,
          //       }}
          //     >
          //       {selectedRowKeys.length}
          //     </a>{' '}
          //     项&nbsp;&nbsp;
          //     {/* <span>
          //       服务调用次数总计 {selectedRows.reduce((pre, item) => pre + item.callNo, 0)} 万
          //     </span> */}
          //   </div>
          // )}
          request={fetchData}
          columns={columns}
          rowSelection={false}
        />
        
          <ImportModal
            done={done}
            current={current}
            visible={visible}
            onCancel={handleCancel}
            onSubmit={handleSubmit}
          />
        
        
      </PageHeaderWrapper>
    );
  };
  
  export default connect(({ projectManage, loading,display }) => ({
    projectManage,
    address:display.address,
    loading
  }))(projectManage);