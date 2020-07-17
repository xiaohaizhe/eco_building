import React ,{useEffect}from 'react';
// import { Card, Typography, Alert } from 'antd';
import { useParams  } from 'umi';
import { getProjectPage } from '@/services/projectManage';
import { connect } from 'umi';
import ProTable from '@ant-design/pro-table';


const more = props => {
    const params = useParams()
    
    const { name } = params;
    const fetchData =async (params, sort, filter) =>{
        const res =await getProjectPage({...params,number:params.current,size:params.pageSize,name:name});
        return {
          data:res.result,
          current: 1,
          total:res.size,
          pageSize:10
        }
      }
    const paginationProps = {
        current: 1,
        pageSize: 10
    }
    const columns = [
        {
          title: '项目名称',
          dataIndex: 'name'
        }
      ];
    return (
        <div>
            <h4>搜索名称：{name}</h4>
            <ProTable
                headerTitle="项目管理"
                // actionRef={actionRef}
                rowKey="id"
                options={false} 
                search={false}
                pagination={paginationProps}
                toolBarRender={false}
                request={fetchData}
                columns={columns}
                rowSelection={false}
                />
        </div>
)}
  
export default connect(({ loading }) => ({
    loading
  }))(more);