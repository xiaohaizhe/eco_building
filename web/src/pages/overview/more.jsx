import React ,{useEffect}from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { useParams,history  } from 'umi';
import { getProjectScreenPage } from '@/services/projectManage';
import { connect } from 'umi';
import ProTable from '@ant-design/pro-table';


const more = props => {
    const params = useParams()
    
    const { name } = params;
    const fetchData =async (params, sort, filter) =>{
        const res =await getProjectScreenPage({...params,number:params.current,size:params.pageSize,name:name});
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
          dataIndex: 'name',
          render: (_,record) => <span
            onClick={e => {
              history.push({
                pathname: '/overview/proDetail/'+record.id
              })
            }}
          >
          {_}
        </span>
        }
      ];
    return (
        <PageHeaderWrapper title={false}>
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
        </PageHeaderWrapper>
)}
  
export default connect(({ loading }) => ({
    loading
  }))(more);