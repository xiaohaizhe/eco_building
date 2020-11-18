import React, { useEffect,useState} from 'react';
import { Link, connect, history, FormattedMessage, formatMessage } from 'umi';
import { Modal, Result, Button, Form, Upload, message, Spin} from 'antd';
import { UploadOutlined } from '@ant-design/icons';
const { Dragger } = Upload;
import '../index.less'

const formLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 13,
  },
};
const ImportModal = props => {
  const [form] = Form.useForm();
  const { dispatch } = props;
  const {visible, onCancel, onSubmit } = props;
  const [fileList, setFileList] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (form && !visible) {
      setFileList([]);
      form.resetFields();
    }
  }, [props.visible]);
  
  const handleSubmit = () => {
    if (!form) return;
    form.submit();
  };

  const handleFinish = () => {
    let temp = new FormData();
      fileList.forEach(function (file) {
        temp.append('files', file, file.name);
       })
      
      setLoading(true);
       dispatch({
        type: 'research/submit',
        payload: temp,
        callback: (res) => {
          setLoading(false);
          if (res.code==0) {
            message.success('操作成功');
            if (onSubmit) {
            onSubmit();}
          }else{
            message.success('操作失败');
          }
        }
      });
      
    }

    const modalFooter = loading
    ? {
        footer: null,
        onCancel
      }
    : {
        okText: '确定',
        onOk: handleSubmit,
        onCancel,
      };


    const beforeUpload = file=>{
      let fileend = file.name.substring(file.name.indexOf("."))
      if (['.xls','.xlsx'].indexOf(fileend.toLowerCase()) === -1) {
        message.error("只支持上传文件类型为xls,xlsx");
      }else{
        setFileList(fileList.concat(file) )
      }
      return false;
    }
    const onRemove = file=>{
      
      let index = fileList.indexOf(file);
      let newFileList = fileList.slice();
      newFileList.splice(index, 1);
      setFileList(newFileList)
      return false;
    }
    const getModalContent = () => {
      return (
        <Spin spinning={loading}>
          <Form {...formLayout} form={form} onFinish={handleFinish}>
            <Form.Item
              name="file"
              label="上传文件"
              rules={[
                {
                  required: true,
                  message: '请选择文件',
                },
              ]}
            >
              {/* <Upload
                  fileList={fileList}
                  name="file"
                  // onChange={onUploading}
                  // onRemove={onRemove}
                  beforeUpload={beforeUpload}
                >
                  <Button>
                    <UploadOutlined />点击上传
                  </Button>
                </Upload> */}
                <div className="dragger">
                  <Dragger name="file" multiple={true} fileList={fileList} accept=".csv,.xls,.xlsx" beforeUpload={beforeUpload} onRemove={onRemove}>
                      <div>
                        <p className="ant-upload-drag-icon" >
                          <UploadOutlined />
                        </p>
                        <p className="ant-upload-text">单击或拖动文件到此区域以上载</p>
                      </div>
                  </Dragger>
                </div>
                
            </Form.Item>
            {/* <Form.Item
              label="下载模板"
            >
              <a onClick={downloadExcel}>点击下载excel模板</a>
            </Form.Item> */}
          </Form>
        </Spin>
      );
    };

  return (
    <Modal
      title={'上传文件'}
      width={640}
      bodyStyle={{padding: '28px 0 0'}}
      destroyOnClose
      visible={visible}
      {...modalFooter}
    >
      {getModalContent()}
    </Modal>
  );
};

export default connect(({ projectManage }) => ({
  projectManage
}))(ImportModal);