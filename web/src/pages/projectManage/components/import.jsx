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
      let fileend = file.name.substring(file.name.indexOf("."))
      if (['.xls','.xlsx','.csv'].indexOf(fileend.toLowerCase()) === -1) {
        message.error("只支持上传文件类型为csv,xls,xlsx");
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
                          window.navigator.msSaveBlob(blob, 'example.xlsx');
                      }
                      catch (e) {
                          console.log(e);
                      }
                  }else{
                        let blobUrl = URL.createObjectURL(blob);
                        const a = document.createElement('a');
                        a.style.display = 'none';
                        a.download = 'example.xlsx';
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