import React, { useEffect,useState} from 'react';
import { Link, connect, history, FormattedMessage, formatMessage } from 'umi';
import { Modal, Result, Button, Form, Upload, Input, Spin} from 'antd';
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
  const { dispatch,loading } = props;
  const { done, visible, current, onDone, onCancel, onSubmit } = props;
  const [fileList, setFileList] = useState([]);
  
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
    
      let temp = new FormData();
      temp.append("file",values.file.file)
       dispatch({
        type: 'projectManage/submit',
        payload: temp,
        callback: (res) => {
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

  // const modalFooter = done
  //   ? {
  //       footer: null,
  //       onCancel: onDone,
  //     }
  //   : {
  //       okText: '保存',
  //       onOk: handleSubmit,
  //       onCancel,
  //     };

    const beforeUpload = file=>{
      setFileList([].concat(file) )
      return false;
    }

  const getModalContent = () => {
    // if (done) {
    //   return (
    //     <Spin></Spin>
    //   );
    // }
    return (
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
        
      </Form>
    );
  };

  return (
    <Modal
      title={done ? null : '上传文件'}
      width={640}
      bodyStyle={
        done
          ? {
              padding: '72px 0',
            }
          : {
              padding: '28px 0 0',
            }
      }
      destroyOnClose
      visible={visible}
      okText={loading.effects['projectManage/submit'] }
      // onOk={handleOk}
      onCancel={onCancel}
    >
      {getModalContent()}
    </Modal>
  );
};

export default connect(({ projectManage, loading }) => ({
  projectManage,
  loading
}))(ImportModal);