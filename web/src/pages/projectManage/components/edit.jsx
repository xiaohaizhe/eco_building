import React ,{useEffect,useState}from 'react';
import { Row, Col ,Cascader,Avatar,Form,Input,Select,DatePicker,Button,message,Upload} from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import { connect,useParams,history } from 'umi';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import Map from './map';
import moment from 'moment';
import TableForm from './TableForm';
import '../index.less'
import psca from './pcas-code.json'

const energySavingStandard = ['不执行节能标准','50%','65%','75%以上','未知'];
const energySavingTransformationOrNot = ['是','否','未知'];
const gbes = ['0星','1星','2星','3星','未知'];
const coolingMode = ['集中供冷','分户供冷','无供冷','未知'];
const heatingMode = ['集中供暖', '分户采暖',  '无采暖', '未知'];
const whetherToUseRenewableResources =['否','浅层地热能', '太阳能', '未知'];
const format = 'YYYY-MM-DD';
const architecturalType = [ '商场', '酒店','办公','医院','餐饮','文化教育','其他'];
// var marker, map;
const Edit = props => {
    const { dispatch,detail } =props;
    const [form] = Form.useForm();
    const params = useParams();
    const { id } = params;
    const [fileList, setFileList] = useState([]);
    const [tableData, setTableData] = useState({});  

    useEffect(() => {
      if (dispatch) {
        // dispatch({
        //   type: 'display/getAddressOnMap',
        // });
        dispatch({
          type: 'projectManage/getProjectDetail',
          payload:{projectId:id},
          callback:(res)=>{
            res.result.buildTime = moment(res.result.builtTime);
            form.setFieldsValue(res.result)
          }
        });
      }
    }, [id]);

    useEffect(() => {
      form.setFieldsValue({
        latitude: detail.latitude,
        longitude: detail.longitude,
      });
    },[detail.longitude])

    const formLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 16,
      },
    };
    const onBack = ()=>{
      history.goBack();
    }
    const onFinish = function (values) {
      values.builtTime = values.buildTime.format(format);
      if(values.imgUrl){
        values.imgUrl = values.imgUrl.fileList?values.imgUrl.fileList[0].response.result:'';
      }
      values.architecturalType = architecturalType[values.architecturalType];
      if(values.division){
        if(values.division.length==3){
          values.province = values.division[0];
          values.city = values.division[1];
          values.district = values.division[2];
        }else if(values.division.length==2){
          values.province = values.division[0];
          values.city = values.division[1];
        }else if(values.division.length==1){
          values.province = values.division[0];
        }
      }
      dispatch({
        type: 'projectManage/update',
        payload:{
          id: id,
          ...values
        },
        callback: (res) => {
          if (res.code==0) {
            message.success('修改成功');
            history.push('/projectManage')
          }else{
            message.success('修改失败');
          }
        }
      });

      var array = [];
      for(var key in tableData){
        array.push(tableData[key]);
      }
      dispatch({
          type: 'projectManage/updateData',
          payload: array,
          callback: (response) => {
            
          },
      });  

    };

    const renderSelect = function (data) {
      return(
        <Select>
          {
            data.map((item,index)=>{
              return <Select.Option value={index} key={`${item}_${index}`}>{item}</Select.Option>
            })
          }
        </Select>
      )
    }
    
    const onUploading = function (info) {
      const { status, response, thumbUrl } = info.file;
      setFileList([].concat(info.file));
      if (status !== 'uploading') {
        // console.log(info.file, info.fileList);
      }
      if (status === 'done') {
        if (response.code === 0 ) {
          message.success(`图片上传成功`);
        } else {
          setFileList([]);
          message.error(response.message);
        }
      } else if (status === 'error') {
        if(response.code == 401 ){
          message.success('请重新登陆！');
          history.push("/user/login");
        }else{
          setFileList([]);
          message.error(`图片上传失败`);
        }
        
        
      }
    }

    const onRemove = function (file) {
      setFileList([]);
      form.setFieldsValue({ imgUrl: null});
      return false;
    }

    const onPreview = function (file) {
      const { response} = file;
      window.open(response.result);
    }

    const longChange = (val)=>{
      dispatch({
        type: 'projectManage/changeLongLat',
        payload:{longitude:val.target.value}
      });
    }

    const latChange = (val)=>{
      dispatch({
        type: 'projectManage/changeLongLat',
        payload:{latitude:val.target.value}
      });
    }

    const onChange = function ( dataType,newData) {
      console.log( dataType,newData);
      tableData[ dataType] = newData;
      setTableData(tableData);
    }

    const gutter = [16];
      return(
        <PageHeaderWrapper title={false}>
            <Form {...formLayout} className='editProForm' form={form} onFinish={onFinish}>
              <Row gutter={gutter}>
                  <Col span={10}>
                    <Form.Item
                      name="name"
                      label="项目名称"
                      rules={[
                        {
                          required: true,
                          message: '请输入',
                        },
                      ]}
                    >
                      <Input/>
                    </Form.Item>
                  </Col>
                  <Col span={10}>
                    <Form.Item
                      name="projectName"
                      label="工程名称"
                      rules={[
                        {
                          required: true,
                          message: '请输入',
                        },
                      ]}
                    >
                      <Input/>
                    </Form.Item>
                  </Col>
              </Row> 
              <Row gutter={gutter}>
                <Col span={10}>
                    <Form.Item
                        name="division"
                        label="行政区划"
                        rules={[
                          {
                            required: true,
                            message: '请输入',
                          },
                        ]}
                      >
                        <Cascader options={psca} fieldNames={{ label: 'name', value: 'name', children: 'children' }} changeOnSelect placeholder="请选择地址" />
                      </Form.Item>
                  </Col>
                  <Col span={10}>
                    <Form.Item
                        name="address"
                        label="地址"
                        rules={[
                          {
                            required: true,
                            message: '请输入',
                          },
                        ]}
                      >
                        <Input />
                      </Form.Item>
                  </Col>
              </Row>
              <Row gutter={gutter}>
                <Col span={10}>
                    <Form.Item
                        name="architecturalType"
                        label="建筑类型"
                        rules={[
                          {
                            required: true,
                            message: '请输入',
                          },
                        ]}
                      >
                        {renderSelect(architecturalType)}
                      </Form.Item>
                  </Col>
                <Col span={10}>
                      <Form.Item
                        name="buildTime"
                        label="建成时间"
                        rules={[
                          {
                            required: true,
                            message: '请输入',
                          },
                        ]}
                      >
                        <DatePicker style={{width:'100%'}}/>
                      </Form.Item>
                  </Col>
                  
                  </Row>
              <Row gutter={gutter}>
                    <Col span={10}>
                      <Form.Item
                        name="floor"
                        label="层数"
                        // rules={[
                        //   {
                        //     required: true,
                        //     message: '请输入',
                        //   },
                        // ]}
                      >
                        <Input/>
                      </Form.Item>
                    </Col>
                    <Col span={10}>
                        <Form.Item
                          name="energySavingStandard"
                          label="节能标准"
                          // rules={[
                          //   {
                          //     required: true,
                          //     message: '请输入',
                          //   },
                          // ]}
                        >
                          {renderSelect(energySavingStandard)}
                        </Form.Item>
                    </Col>
                  
                  </Row>  
              <Row gutter={gutter}>
                  <Col span={10}>
                    <Form.Item
                      name="coolingMode"
                      label="供冷方式"
                      // rules={[
                      //   {
                      //     required: true,
                      //     message: '请输入',
                      //   },
                      // ]}
                    >
                      {renderSelect(coolingMode)}
                    </Form.Item>
                  </Col>
                  <Col span={10}>
                    <Form.Item
                        name="gbes"
                        label="绿建星级"
                        // rules={[
                        //   {
                        //     required: true,
                        //     message: '请输入',
                        //   },
                        // ]}
                      >
                        {renderSelect(gbes)}
                      </Form.Item>
                  </Col>
              </Row>
              <Row gutter={gutter}>
                <Col span={10}>
                <Form.Item
                        name="heatingMode"
                        label="供暖方式"
                        // rules={[
                        //   {
                        //     required: true,
                        //     message: '请输入',
                        //   },
                        // ]}
                      >
                        {renderSelect(heatingMode)}
                      </Form.Item>
                  </Col>
                  <Col span={10}>
                      <Form.Item
                        name="energySavingTransformationOrNot"
                        label="是否经过节能改造"
                        // rules={[
                        //   {
                        //     required: true,
                        //     message: '请输入',
                        //   },
                        // ]}
                      >
                        {renderSelect(energySavingTransformationOrNot)}
                      </Form.Item>
                  </Col>
              </Row>
              <Row gutter={gutter}>
                <Col span={10}>
                    <Form.Item
                        name="longitude"
                        label="经度"
                        rules={[
                          {
                            required: true,
                            message: '请输入经度',
                          },
                        ]}
                      >
                        <Input onChange={longChange}/>
                      </Form.Item>
                      <Form.Item
                          name="latitude"
                          label="纬度"
                          rules={[
                            {
                              required: true,
                              message: '请输入纬度',
                            },
                          ]}
                        >
                          <Input onChange={latChange}/>
                        </Form.Item>
                        <Form.Item
                          label="地图">
                            <Map />
                        </Form.Item>
                        
                    </Col>
                    <Col span={10}>
                        <Form.Item
                          name="whetherToUseRenewableResources"
                          label="是否利用可再生能源"
                          // rules={[
                          //   {
                          //     required: true,
                          //     message: '请输入',
                          //   },
                          // ]}
                        >
                          {renderSelect(whetherToUseRenewableResources)}
                        </Form.Item>
                        <Form.Item
                          label="图片"
                        >
                          <Row>
                            {fileList.length==0 && detail.imgUrl && <img src={detail.imgUrl} style={{width:'100px',height:'100px',marginRight:'10px'}} />}
                            <Form.Item name="imgUrl">
                              <Upload
                                listType="picture-card"
                                accept="image/*"
                                name="file"
                                fileList={fileList}
                                action={`/api/project/uploadPic`}
                                onChange={info => onUploading(info)}
                                onRemove={file => onRemove(file)}
                                onPreview={file => onPreview(file)}
                              >
                                <div>
                                  <UploadOutlined />点击上传
                                </div>
                              </Upload>
                            </Form.Item>
                          </Row>
                        </Form.Item>
                    </Col>
                  
              </Row>
              
              
              <Row>
                  <TableForm props={props} onChange={onChange} name = "逐月电耗" format = 'YYYY-MM-DD' dataType="电" timeType="月"/>
              </Row>
              <Row>
                  <TableForm props={props} onChange={onChange} name = "逐月气耗" format = 'YYYY-MM-DD' dataType="气" timeType="月"/>
              </Row>  
              <Row>
                  <TableForm props={props} onChange={onChange} name = "逐月水耗" format = 'YYYY-MM-DD' dataType="水" timeType="月"/>
              </Row>        
              <Row gutter={gutter} style={{justifyContent: 'center'}}>
                <Form.Item style={{marginRight:'20px'}}>
                  <Button type="primary" htmlType="submit">
                  提交
                  </Button>
                </Form.Item>
                <Form.Item >
                  <Button onClick={onBack}>
                  返回
                  </Button>
                </Form.Item>
              </Row>        
            </Form>             
        </PageHeaderWrapper>        
      )
}

export default connect(({ projectManage, loading }) => ({
  // division:display.address,
  detail:projectManage.detail,
  loading
}))(Edit);