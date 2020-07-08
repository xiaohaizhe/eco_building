import React, { useEffect,useState} from 'react';
import { Link, connect, history, FormattedMessage, formatMessage } from 'umi';
import moment from 'moment';
import { Modal, Result, Button, Form, DatePicker, Input, Select,Popover,Progress,message } from 'antd';
import styles from '../style.less';

const formLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 13,
  },
};
const passwordStatusMap = {
  ok: (
    <div className={styles.success}>
      强度：强
    </div>
  ),
  pass: (
    <div className={styles.warning}>
      强度：中
    </div>
  ),
  poor: (
    <div className={styles.error}>
      强度：太短
    </div>
  ),
};
const passwordProgressMap = {
  ok: 'success',
  pass: 'normal',
  poor: 'exception',
};
const OperationModal = props => {
  const [form] = Form.useForm();
  const { done, visible, current, onDone, onCancel, onSubmit } = props;
  const [popVisible, setPopVisible] = useState(false);
  const [popover, setPopover] = useState(false);
  const confirmDirty = false;
  
  useEffect(() => {
    if (form && !visible) {
      form.resetFields();
    }
  }, [props.visible]);
  
  useEffect(() => {
    if (current) {
      form.setFieldsValue({
        username:current.username,
        id:current.id?current.id:'',
        createdAt: current.createdAt ? moment(current.createdAt) : null,
      });
    }
  }, [props.current]);
  const getPasswordStatus = () => {
    const value = form.getFieldValue('password');

    if (value && value.length > 9) {
      return 'ok';
    }

    if (value && value.length > 5) {
      return 'pass';
    }

    return 'poor';
  };
  const handleSubmit = () => {
    if (!form) return;
    form.submit();
  };

  const handleFinish = values => {
    if (onSubmit) {
      onSubmit(values);
    }
  };

  const modalFooter = done
    ? {
        footer: null,
        onCancel: onDone,
      }
    : {
        okText: '保存',
        onOk: handleSubmit,
        onCancel,
      };

  const getModalContent = () => {
    // if (done) {
      // return (
      //   <Result
      //     status="success"
      //     title="操作成功"
      //     extra={
      //       <Button type="primary" onClick={onDone}>
      //         知道了
      //       </Button>
      //     }
      //     className={styles.formResult}
      //   />
      // );
    // }
    const checkConfirm = (_, value) => {
      const promise = Promise;
  
      if (value && value !== form.getFieldValue('password')) {
        return promise.reject('两次输入的密码不匹配!');
      }
  
      return promise.resolve();
    };
  
    const checkPassword = (_, value) => {
      const promise = Promise; // 没有值的情况
  
      // if (!value) {
      //   setPopVisible(!!value);
      //   return promise.reject('请输入密码！');
      // } // 有值的情况
  
      if (value && !popVisible) {
        setPopVisible(!!value);
      }
  
      setPopover(!popover);
  
      if (value.length < 6) {
        return promise.reject('');
      }
  
      if (value && confirmDirty) {
        form.validateFields(['confirm']);
      }
  
      return promise.resolve();
    };
  

      const renderPasswordProgress = () => {
        const value = form.getFieldValue('password');
        const passwordStatus = getPasswordStatus();
        return value && value.length ? (
          <div className={styles[`progress-${passwordStatus}`]}>
            <Progress
              status={passwordProgressMap[passwordStatus]}
              className={styles.progress}
              strokeWidth={6}
              percent={value.length * 10 > 100 ? 100 : value.length * 10}
              showInfo={false}
            />
          </div>
        ) : null;
      };

    return (
      <Form {...formLayout} form={form} onFinish={handleFinish}>
        <Form.Item
          name="username"
          label="用户名"
          rules={[
            {
              required: true,
              message: '请输入用户名',
            },
          ]}
        >
          <Input placeholder="请输入用户名" />
        </Form.Item>
        <Popover
          getPopupContainer={node => {
            if (node && node.parentNode) {
              return node.parentNode;
            }

            return node;
          }}
          content={
            popVisible && (
              <div
                style={{
                  padding: '4px 0',
                }}
              >
                {passwordStatusMap[getPasswordStatus()]}
                {renderPasswordProgress()}
                <div
                  style={{
                    marginTop: 10,
                  }}
                >
                  请至少输入 6 个字符。请不要使用容易被猜到的密码。
                </div>
              </div>
            )
          }
          overlayStyle={{
            width: 240,
          }}
          placement="right"
          visible={popVisible}
        >
          <Form.Item
            name="password"
            label="密码"
            className={
              form.getFieldValue('password') &&
              form.getFieldValue('password').length > 0 &&
              styles.password
            }
            rules={[
              {
                required: true,
                message: '请输入密码！'
              },
              {
                validator: checkPassword,
              },
            ]}
          >
            <Input
              type="password"
              placeholder='至少6位密码，区分大小写'
            />
          </Form.Item>
        </Popover>
        <Form.Item
          name="confirm"
          label="确认密码"
          rules={[
            {
              required: true,
              message: '请确认密码！'
            },
            {
              validator: checkConfirm,
            },
          ]}
        >
          <Input
            type="password"
            placeholder='确认密码'
          />
        </Form.Item>
      </Form>
    );
  };

  return (
    <Modal
      title={done ? null : `用户${current ? '编辑' : '添加'}`}
      className={styles.standardListForm}
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
      {...modalFooter}
    >
      {getModalContent()}
    </Modal>
  );
};

export default OperationModal;
