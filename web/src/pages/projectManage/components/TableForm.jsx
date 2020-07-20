import { PlusOutlined } from '@ant-design/icons';
import { Button, Divider, Input, Popconfirm, Table, message } from 'antd';
import React, { useEffect,useState} from 'react';
import { Link, connect, history, FormattedMessage, formatMessage, useParams } from 'umi';
import styles from '../index.less';

const TableForm = ({ props, onChange, name , format , dataType , timeType }) => {
  const [clickedCancel, setClickedCancel] = useState(false);
  const [loading, setLoading] = useState(false);
  const [index, setIndex] = useState(0);
  const [cacheOriginData, setCacheOriginData] = useState({});

  const {dispatch } = props;
  const params = useParams()
  const { id } = params;
  const start = '2014-01-01';
  const end = '2018-01-01';  

  const [data, setData] = useState([]); 

  useEffect(() => {
      if (dispatch) {
          getData(start,end);
      }       
  }, []);

  const getData = function (start,end){
      dispatch({
          type: 'projectManage/getDataByTime',
          payload:{
              dataType: dataType,
              timeType: timeType,
              start: start,
              end: end,
              projectId: id,
          },
          callback: (response) => {
              dataFormat(response.result);
          },
      });        
  }
  
  const emptyData = {
    "01": "","02": "","03": "","04": "",
    "05": "","06": "","07": "","08": "",
    "09": "","10": "","11": "","12": "",
  }
  const dataFormat = function (result) {
    var data = {};
    result.map((item,index)=>{
      var year = item["actualDate"].split(" ")[0].split("-")[0];
      var month = item["actualDate"].split(" ")[0].split("-")[1];
      data[year] = data[year] ? data[year] : {...emptyData}
      data[year][month] = item["value"] || "";
    })
    var tableData = [];
    for(var key in data){
      tableData.push({
        key: key,
        year: key, 
        ...data[key]
      })
    }
    setData(tableData);
  }

  const getRowByKey = (key, newData) => (newData || data)?.filter(item => item.key === key)[0];

  const toggleEditable = (e, key) => {
    e.preventDefault();
    const newData = data?.map(item => ({ ...item }));
    const target = getRowByKey(key, newData);

    if (target) {
      // 进入编辑状态时保存原始数据
      if (!target.editable) {
        cacheOriginData[key] = { ...target };
        setCacheOriginData(cacheOriginData);
      }

      target.editable = !target.editable;
      setData(newData);
    }
  };

  const newMember = () => {
    const newData = data?.map(item => ({ ...item })) || [];
    newData.push({
      key: `NEW_TEMP_ID_${index}`,
      year: '2020',
      ...emptyData
    });
    setIndex(index + 1);
    setData(newData);
  };

  const remove = key => {
    const newData = data?.filter(item => item.key !== key);
    setData(newData);

    if (onChange) {
      onChange(newData);
    }
  };

  const handleFieldChange = (e, fieldName, key) => {
    const newData = [...data];
    const target = getRowByKey(key, newData);

    if (target) {
      target[fieldName] = e.target.value;
      setData(newData);
    }
  };

  const saveRow = (e, key) => {
    e.persist();
    setLoading(true);
    setTimeout(() => {
      if (clickedCancel) {
        setClickedCancel(false);
        return;
      }

      const target = getRowByKey(key) || {};

      if (!target.year) {
        message.error('请填写完整信息。');
        e.target.focus();
        setLoading(false);
        return;
      }

      delete target.isNew;
      toggleEditable(e, key);

      //提交数据
      updateData(data);

      setLoading(false);
    }, 500);
  };

  const updateData = function (data) {
    var dataMap = [];
    data.map((item,index)=>{
      for(var key in item){
        if(key != 'key' && key != 'year' && key != 'editable'){
          var year = item['year'] + key;
          var value = item[key];
          dataMap.push({
            [year]: value
          })
        }
      }
    })
    console.log(dataMap);
    dispatch({
        type: 'projectManage/updateData',
        payload:{
            projectId: id,
            type: dataType,
            timeType: timeType,
            dataMap
        },
        callback: (response) => {
           
        },
    });   
  }

  const handleKeyPress = (e, key) => {
    if (e.key === 'Enter') {
      saveRow(e, key);
    }
  };

  const cancel = (e, key) => {
    setClickedCancel(true);
    e.preventDefault();
    const newData = [...data]; // 编辑前的原始数据

    let cacheData = [];
    cacheData = newData.map(item => {
      if (item.key === key) {
        if (cacheOriginData[key]) {
          const originItem = { ...item, ...cacheOriginData[key], editable: false };
          delete cacheOriginData[key];
          setCacheOriginData(cacheOriginData);
          return originItem;
        }
      }

      return item;
    });
    setData(cacheData);
    setClickedCancel(false);
  };

  const columns = [
    {
      title: '年份',
      dataIndex: 'year',
      key: 'year',
      render: (text, record) => {
        if (record.editable) {
          return (
            <Input
              value={text.split(" ")[0]}
              autoFocus
              onChange={e => handleFieldChange(e, 'year', record.key)}
              onKeyPress={e => handleKeyPress(e, record.key)}
              placeholder="日期"
            />
          );
        }
        return text;
      },
    }    
  ];
  const months = [
    {
      value: '一月',
      key: '01'
    },
    {
      value: '二月',
      key: '02'
    },
    {
      value: '三月',
      key: '03'
    },
    {
      value: '四月',
      key: '04'
    },
    {
      value: '五月',
      key: '05'
    },
    {
      value: '六月',
      key: '06'
    },
    {
      value: '七月',
      key: '07'
    },
    {
      value: '八月',
      key: '08'
    },
    {
      value: '九月',
      key: '09'
    },
    {
      value: '十月',
      key: '10'
    },
    {
      value: '十一月',
      key: '11'
    },
    {
      value: '十二月',
      key: '12'
    },
  ]
  months.map((item,index)=>{
    columns.push(
      {
        title: item.value,
        dataIndex: item.key,
        key: item.key,
        render: (text, record) => {
          if (record.editable) {
            return (
              <Input
                value={text}
                onChange={e => handleFieldChange(e, item.key, record.key)}
                onKeyPress={e => handleKeyPress(e, record.key)}
                placeholder="消耗量"
              />
            );
          }
          return text;
        },
      }
    )
  })
  columns.push(
    {
      title: '操作',
      key: 'action',
      width: 120,
      render: (text, record) => {
        if (!!record.editable && loading) {
          return null;
        }

        if (record.editable) {
          if (record.isNew) {
            return (
              <span>
                <a onClick={e => saveRow(e, record.key)}>添加</a>
                <Divider type="vertical" />
                <Popconfirm title="是否要删除此行？" onConfirm={() => remove(record.key)}>
                  <a>删除</a>
                </Popconfirm>
              </span>
            );
          }

          return (
            <span>
              <a onClick={e => saveRow(e, record.key)}>保存</a>
              <Divider type="vertical" />
              <a onClick={e => cancel(e, record.key)}>取消</a>
            </span>
          );
        }

        return (
          <span>
            <a onClick={e => toggleEditable(e, record.key)}>编辑</a>
            <Divider type="vertical" />
            <Popconfirm title="是否要删除此行？" onConfirm={() => remove(record.key)}>
              <a>删除</a>
            </Popconfirm>
          </span>
        );
      },
    },
  )  
  return (
    <div className="tableItem">
      <div className="title">
        {name}
      </div>
      <Table
        loading={loading}
        columns={columns}
        dataSource={data}
        pagination={false}
        rowClassName={record => (record.editable ? styles.editable : '')}
        style={{width: '100%'}}
      />
      <Button
        style={{
          width: '100%',
          marginTop: 4,
          marginBottom: 20,
        }}
        type="dashed"
        onClick={newMember}
      >
        <PlusOutlined />
        新增消耗量
      </Button>
    </div>
  );
};

export default TableForm;