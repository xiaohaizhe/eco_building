import React, { useEffect,useState} from 'react';
import { Link, connect, history, FormattedMessage, formatMessage } from 'umi';
import { Modal, Result, Button, Form, Upload, message, Spin} from 'antd';
import { UploadOutlined } from '@ant-design/icons';
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

  const handleFinish = values => {
    debugger
      let temp = new FormData();
      temp.append("file",values.file.file)
      setLoading(true);
       dispatch({
        type: 'projectManage/submit',
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
      setFileList([].concat(file) )
      return false;
    }

    const downloadExcel=()=>{
        //loading
          // const loading = this.$loading({
          //     lock: true,
          //     text: 'Loading',
          //     spinner: 'el-icon-loading',
          //     background: 'rgba(0, 0, 0, 0.7)'
          // });
          window.URL = window.URL || window.webkitURL;  // Take care of vendor prefixes.
          var xhr = new XMLHttpRequest();
          xhr.open('GET', '/api/project/downloadExample', true);
          xhr.responseType = 'blob';

          xhr.onload = function(e) {
              // loading.close();
              if (this.status == 200) {
                  var blob = this.response;
                  var URL = window.URL || window.webkitURL;  //兼容处理
                  // for ie 10 and later
                  if (window.navigator.msSaveBlob) {
                      try { 
                          window.navigator.msSaveBlob(blob, 'example.xls');
                      }
                      catch (e) {
                          console.log(e);
                      }
                  }else{
                        let blobUrl = URL.createObjectURL(blob);
                        const a = document.createElement('a');
                        a.style.display = 'none';
                        a.download = 'example.xls';
                        a.href = blobUrl;
                        a.click();
                        // document.body.removeChild(a);
                  }
              }
              
          };

          xhr.send();
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
              <Upload
                  fileList={fileList}
                  name="file"
                  // onChange={onUploading}
                  // onRemove={onRemove}
                  beforeUpload={beforeUpload}
                >
                  <Button>
                    <UploadOutlined />点击上传
                  </Button>
                </Upload>
            </Form.Item>
            <Form.Item
              label="下载模板"
            >
              <a onClick={downloadExcel}>点击下载excel模板</a>
            </Form.Item>
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